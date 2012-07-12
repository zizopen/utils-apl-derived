/*******************************************************************************
 * Copyright 2012 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.beans.replicator2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Builder;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Configuration;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectToString;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.map.MapBuilder;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class BeanReplicator<FROM, TO>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<FROM>                  sourceType;
  private final Class<TO>                    targetType;
  private final FactoryResolver              factoryResolver;
  private final InstanceAccessorResolver     instanceAccessorResolver;
  private final PreservedTypeInstanceManager preservedTypeInstanceManager;
  private final TypeToTypeMappingManager     typeToTypeMappingManager;
  private final ExceptionHandlerDelegate     exceptionHandler = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  public static interface TypeToTypeMappingDeclarer
  {
    public void addTypeMapping( Class<?> typeFrom, Class<?> typeTo );
    
    public void addPropertyNameMapping( String propertyNameFrom, String propertyNameTo );
    
    public void addPropertyNameMapping( String path, String propertyNameFrom, String propertyNameTo );
    
    public void addTypeAndPropertyNameMapping( Class<?> typeFrom, String propertyNameFrom, Class<?> typeTo, String propertyNameTo );
    
    public void addTypeAndPropertyNameMapping( String path,
                                               Class<?> typeFrom,
                                               String propertyNameFrom,
                                               Class<?> typeTo,
                                               String propertyNameTo );
    
    public void addTypeMappingForPath( String path, Class<?> typeFrom, Class<?> typeTo );
  }
  
  private static interface TypeToTypeMappingManager extends TypeToTypeMappingDeclarer
  {
    
    public PropertyNameAndType determineRemapping( String propertyName, Class<?> propertySourceType, Path path );
    
  }
  
  /**
   * @author Omnaest
   */
  private static class TypeAndPath implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = 2177856576905085345L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Path        path;
    private final Class<?>    type;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see TypeAndPath
     * @param path
     * @param type
     */
    public TypeAndPath( String path, Class<?> type )
    {
      super();
      this.path = new Path( path );
      this.type = type;
    }
    
    /**
     * @see TypeAndPath
     * @param path
     * @param type
     */
    public TypeAndPath( Path path, Class<?> type )
    {
      super();
      this.path = path;
      this.type = type;
    }
    
    public Path getPath()
    {
      return this.path;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TypeAndPath [path=" );
      builder.append( this.path );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.path == null ) ? 0 : this.path.hashCode() );
      result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TypeAndPath ) )
      {
        return false;
      }
      TypeAndPath other = (TypeAndPath) obj;
      if ( this.path == null )
      {
        if ( other.path != null )
        {
          return false;
        }
      }
      else if ( !this.path.equals( other.path ) )
      {
        return false;
      }
      if ( this.type == null )
      {
        if ( other.type != null )
        {
          return false;
        }
      }
      else if ( !this.type.equals( other.type ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  private static class PropertyNameAndPath implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = 2496126835885709834L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final String      propertyName;
    private final Path        path;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see PropertyNameAndPath
     * @param propertyName
     * @param path
     */
    public PropertyNameAndPath( String propertyName, String path )
    {
      super();
      this.propertyName = propertyName;
      this.path = new Path( path );
    }
    
    /**
     * @see PropertyNameAndPath
     * @param propertyName
     * @param path
     */
    public PropertyNameAndPath( String propertyName, Path path )
    {
      super();
      this.propertyName = propertyName;
      this.path = path;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "PropertyNameAndPath [propertyName=" );
      builder.append( this.propertyName );
      builder.append( ", path=" );
      builder.append( this.path );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.path == null ) ? 0 : this.path.hashCode() );
      result = prime * result + ( ( this.propertyName == null ) ? 0 : this.propertyName.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof PropertyNameAndPath ) )
      {
        return false;
      }
      PropertyNameAndPath other = (PropertyNameAndPath) obj;
      if ( this.path == null )
      {
        if ( other.path != null )
        {
          return false;
        }
      }
      else if ( !this.path.equals( other.path ) )
      {
        return false;
      }
      if ( this.propertyName == null )
      {
        if ( other.propertyName != null )
        {
          return false;
        }
      }
      else if ( !this.propertyName.equals( other.propertyName ) )
      {
        return false;
      }
      return true;
    }
    
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
    public Path getPath()
    {
      return this.path;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  private static class PropertyNameAndTypeAndPath implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = -9180764463885578940L;
    
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final String      propertyName;
    private final Class<?>    type;
    private final Path        path;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see PropertyNameAndTypeAndPath
     * @param propertyName
     * @param type
     * @param path
     */
    public PropertyNameAndTypeAndPath( String propertyName, Class<?> type, String path )
    {
      super();
      this.propertyName = propertyName;
      this.type = type;
      this.path = new Path( path );
    }
    
    /**
     * @see PropertyNameAndTypeAndPath
     * @param propertyName
     * @param type
     * @param path
     */
    public PropertyNameAndTypeAndPath( String propertyName, Class<?> type, Path path )
    {
      super();
      this.propertyName = propertyName;
      this.type = type;
      this.path = path;
    }
    
    public Path getPath()
    {
      return this.path;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "PropertyNameAndTypeAndPath [propertyName=" );
      builder.append( this.propertyName );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( ", path=" );
      builder.append( this.path );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.path == null ) ? 0 : this.path.hashCode() );
      result = prime * result + ( ( this.propertyName == null ) ? 0 : this.propertyName.hashCode() );
      result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof PropertyNameAndTypeAndPath ) )
      {
        return false;
      }
      PropertyNameAndTypeAndPath other = (PropertyNameAndTypeAndPath) obj;
      if ( this.path == null )
      {
        if ( other.path != null )
        {
          return false;
        }
      }
      else if ( !this.path.equals( other.path ) )
      {
        return false;
      }
      if ( this.propertyName == null )
      {
        if ( other.propertyName != null )
        {
          return false;
        }
      }
      else if ( !this.propertyName.equals( other.propertyName ) )
      {
        return false;
      }
      if ( this.type == null )
      {
        if ( other.type != null )
        {
          return false;
        }
      }
      else if ( !this.type.equals( other.type ) )
      {
        return false;
      }
      return true;
    }
    
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
  }
  
  private static class PropertyNameAndType implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = -1968836324042260832L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final String      propertyName;
    private final Class<?>    type;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see PropertyNameAndType
     * @param propertyName
     * @param type
     */
    public PropertyNameAndType( String propertyName, Class<?> type )
    {
      super();
      this.propertyName = propertyName;
      this.type = type;
    }
    
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.propertyName == null ) ? 0 : this.propertyName.hashCode() );
      result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof PropertyNameAndType ) )
      {
        return false;
      }
      PropertyNameAndType other = (PropertyNameAndType) obj;
      if ( this.propertyName == null )
      {
        if ( other.propertyName != null )
        {
          return false;
        }
      }
      else if ( !this.propertyName.equals( other.propertyName ) )
      {
        return false;
      }
      if ( this.type == null )
      {
        if ( other.type != null )
        {
          return false;
        }
      }
      else if ( !this.type.equals( other.type ) )
      {
        return false;
      }
      return true;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "PropertyNameAndType [propertyName=" );
      builder.append( this.propertyName );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * @author Omnaest
   */
  private static class Path implements Serializable
  {
    private static final long serialVersionUID = -6753433772711288639L;
    
    private final String[]    path;
    
    /**
     * @see Path
     */
    public Path()
    {
      super();
      this.path = new String[] {};
    }
    
    /**
     * @see Path
     * @param canonicalPath
     */
    public Path( String canonicalPath )
    {
      super();
      this.path = ListUtils.valueOf( Splitter.on( '.' ).split( canonicalPath ) ).toArray( new String[] {} );
    }
    
    /**
     * @see Path
     * @param path
     */
    public Path( String[] path )
    {
      super();
      this.path = path;
    }
    
    /**
     * @see Path
     * @param parentPath
     * @param propertyName
     */
    public Path( Path parentPath, String propertyName )
    {
      super();
      this.path = ArrayUtils.add( parentPath.getPath(), propertyName );
    }
    
    public String[] getPath()
    {
      return this.path;
    }
    
    public int size()
    {
      return this.path.length;
    }
    
    /**
     * Returns the path in the form "property1.property2.[...]"
     * 
     * @return
     */
    public String getCanonicalPath()
    {
      return Joiner.on( '.' ).join( this.path );
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Path [path=" );
      builder.append( Arrays.toString( this.path ) );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode( this.path );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof Path ) )
      {
        return false;
      }
      Path other = (Path) obj;
      if ( !Arrays.equals( this.path, other.path ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  private static class TypeToTypeMappingManagerImpl implements TypeToTypeMappingManager
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private Map<Class<?>, Class<?>>                              sourceToTargetTypeMap                       = new HashMap<Class<?>, Class<?>>();
    private Map<TypeAndPath, Class<?>>                           sourceToTargetTypeAndPathMap                = new HashMap<BeanReplicator.TypeAndPath, Class<?>>();
    private Map<PropertyNameAndType, PropertyNameAndType>        sourceToTargetPropertyNameAndTypeMap        = new HashMap<PropertyNameAndType, PropertyNameAndType>();
    private Map<PropertyNameAndTypeAndPath, PropertyNameAndType> sourceToTargetPropertyNameAndTypeAndPathMap = new HashMap<PropertyNameAndTypeAndPath, PropertyNameAndType>();
    private Map<String, String>                                  propertyNameToPropertyNameMap               = new HashMap<String, String>();
    private Map<PropertyNameAndPath, String>                     propertyNameAndPathToPropertyNameMap        = new HashMap<BeanReplicator.PropertyNameAndPath, String>();
    
    private Map<PropertyNameAndTypeAndPath, PropertyNameAndType> cache                                       = new ConcurrentHashMap<PropertyNameAndTypeAndPath, PropertyNameAndType>();
    
    /* *************************************************** Methods **************************************************** */
    @Override
    public PropertyNameAndType determineRemapping( String propertyName, Class<?> type, Path path )
    {
      Assert.isNotNull( propertyName, "propertyName must not be null" );
      Assert.isNotNull( type, "type must not be null" );
      Assert.isNotNull( path, "path must not be null" );
      
      PropertyNameAndType retval = null;
      {
        {
          final PropertyNameAndTypeAndPath key = new PropertyNameAndTypeAndPath( propertyName, type, path );
          retval = this.cache.get( key );
        }
        if ( retval == null )
        {
          {
            final PropertyNameAndTypeAndPath key = new PropertyNameAndTypeAndPath( propertyName, type, path );
            retval = this.sourceToTargetPropertyNameAndTypeAndPathMap.get( key );
          }
          if ( retval == null )
          {
            final PropertyNameAndType key = new PropertyNameAndType( propertyName, type );
            retval = this.sourceToTargetPropertyNameAndTypeMap.get( key );
          }
          if ( retval == null )
          {
            //
            Class<?> typeTo = null;
            {
              final TypeAndPath key = new TypeAndPath( path, type );
              typeTo = this.sourceToTargetTypeAndPathMap.get( key );
            }
            if ( typeTo == null )
            {
              typeTo = this.sourceToTargetTypeMap.get( type );
            }
            
            //
            String propertyNameTo = null;
            {
              final PropertyNameAndPath key = new PropertyNameAndPath( propertyName, path );
              propertyNameTo = this.propertyNameAndPathToPropertyNameMap.get( key );
            }
            if ( propertyNameTo == null )
            {
              propertyNameTo = this.propertyNameToPropertyNameMap.get( propertyName );
            }
            
            if ( propertyNameTo != null || typeTo != null )
            {
              if ( propertyNameTo == null )
              {
                propertyNameTo = propertyName;
              }
              if ( typeTo == null )
              {
                typeTo = type;
              }
              
              retval = new PropertyNameAndType( propertyNameTo, typeTo );
            }
          }
          
          //
          if ( retval != null )
          {
            this.cache.put( new PropertyNameAndTypeAndPath( propertyName, type, path ), retval );
          }
        }
      }
      return retval;
    }
    
    @Override
    public void addTypeMapping( Class<?> typeFrom, Class<?> typeTo )
    {
      Assert.isNotNull( typeFrom, "typeFrom must not be null" );
      Assert.isNotNull( typeTo, "typeTo must not be null" );
      
      this.sourceToTargetTypeMap.put( typeFrom, typeTo );
    }
    
    @Override
    public void addPropertyNameMapping( String propertyNameFrom, String propertyNameTo )
    {
      Assert.isNotNull( propertyNameFrom, "propertyNameFrom must not be null" );
      Assert.isNotNull( propertyNameTo, "propertyNameTo must not be null" );
      
      this.propertyNameToPropertyNameMap.put( propertyNameFrom, propertyNameTo );
    }
    
    @Override
    public void addPropertyNameMapping( String path, String propertyNameFrom, String propertyNameTo )
    {
      Assert.isNotNull( propertyNameFrom, "propertyNameFrom must not be null" );
      Assert.isNotNull( propertyNameTo, "propertyNameTo must not be null" );
      Assert.isNotNull( path, "path must not be null" );
      
      final PropertyNameAndPath key = new PropertyNameAndPath( propertyNameFrom, path );
      final String value = propertyNameTo;
      this.propertyNameAndPathToPropertyNameMap.put( key, value );
    }
    
    @Override
    public void addTypeAndPropertyNameMapping( Class<?> typeFrom, String propertyNameFrom, Class<?> typeTo, String propertyNameTo )
    {
      Assert.isNotNull( typeFrom, "typeFrom must not be null" );
      Assert.isNotNull( propertyNameFrom, "propertyNameFrom must not be null" );
      Assert.isNotNull( typeTo, "typeTo must not be null" );
      Assert.isNotNull( propertyNameTo, "propertyNameTo must not be null" );
      
      final PropertyNameAndType key = new PropertyNameAndType( propertyNameFrom, typeFrom );
      final PropertyNameAndType value = new PropertyNameAndType( propertyNameTo, typeTo );
      this.sourceToTargetPropertyNameAndTypeMap.put( key, value );
      
    }
    
    @Override
    public void addTypeAndPropertyNameMapping( String path,
                                               Class<?> typeFrom,
                                               String propertyNameFrom,
                                               Class<?> typeTo,
                                               String propertyNameTo )
    {
      Assert.isNotNull( typeFrom, "typeFrom must not be null" );
      Assert.isNotNull( propertyNameFrom, "propertyNameFrom must not be null" );
      Assert.isNotNull( typeTo, "typeTo must not be null" );
      Assert.isNotNull( propertyNameTo, "propertyNameTo must not be null" );
      Assert.isNotNull( path, "path must not be null" );
      
      final PropertyNameAndTypeAndPath key = new PropertyNameAndTypeAndPath( propertyNameFrom, typeFrom, path );
      final PropertyNameAndType value = new PropertyNameAndType( propertyNameTo, typeTo );
      this.sourceToTargetPropertyNameAndTypeAndPathMap.put( key, value );
    }
    
    @Override
    public void addTypeMappingForPath( String path, Class<?> typeFrom, Class<?> typeTo )
    {
      Assert.isNotNull( path, "path must not be null" );
      Assert.isNotNull( typeFrom, "typeFrom must not be null" );
      Assert.isNotNull( typeTo, "typeTo must not be null" );
      
      final TypeAndPath key = new TypeAndPath( path, typeFrom );
      final Class<?> value = typeTo;
      this.sourceToTargetTypeAndPathMap.put( key, value );
    }
  }
  
  private static interface PreservedTypeInstanceManager
  {
    public void removeAll( Collection<? extends Class<?>> typeCollection );
    
    public void retainAll( Collection<? extends Class<?>> typeCollection );
    
    public void addAll( Collection<? extends Class<?>> typeCollection );
    
    public void remove( Class<?> type );
    
    public void add( Class<?> type );
    
    public boolean contains( Class<?> type );
  }
  
  private static class PreservedTypeInstanceManagerImpl implements PreservedTypeInstanceManager
  {
    private Set<Class<?>> typeSet = ImmutableSet.<Class<?>> of();
    
    @Override
    public boolean contains( Class<?> type )
    {
      return this.typeSet.contains( type );
    }
    
    @Override
    public void add( Class<?> type )
    {
      this.typeSet = ImmutableSet.<Class<?>> builder().addAll( this.typeSet ).add( type ).build();
    }
    
    @Override
    public void remove( Class<?> type )
    {
      this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.remove( this.typeSet, type ) ).add( type ).build();
    }
    
    @Override
    public void addAll( Collection<? extends Class<?>> typeCollection )
    {
      this.typeSet = ImmutableSet.<Class<?>> builder().addAll( this.typeSet ).addAll( typeCollection ).build();
    }
    
    @Override
    public void retainAll( Collection<? extends Class<?>> typeCollection )
    {
      this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.retainAll( this.typeSet, typeCollection ) ).build();
    }
    
    @Override
    public void removeAll( Collection<? extends Class<?>> typeCollection )
    {
      this.typeSet = ImmutableSet.<Class<?>> builder().addAll( SetUtils.removeAll( this.typeSet, typeCollection ) ).build();
    }
    
  }
  
  private static interface PropertyAccessor
  {
    public String getPropertyName();
    
    public void setValue( Object value );
    
    public Object getValue();
    
    public Class<?> getType();
  }
  
  private static interface InstanceAccessor
  {
    public PropertyAccessor getPropertyAccessor( String propertyName, Object instance );
    
    public Set<String> getPropertyNameSet( Object instance );
    
    public Class<?> getType();
  }
  
  /**
   * {@link InstanceAccessor} for {@link Map} instances
   * 
   * @see InstanceAccessor
   * @author Omnaest
   */
  private static class InstanceAccessorForMap implements InstanceAccessor
  {
    
    @Override
    public PropertyAccessor getPropertyAccessor( final String propertyName, final Object instance )
    {
      return new PropertyAccessor()
      {
        @Override
        public void setValue( Object value )
        {
          Map<Object, Object> map = instanceAsMap( instance );
          if ( map != null )
          {
            map.put( propertyName, value );
          }
        }
        
        @SuppressWarnings("unchecked")
        private Map<Object, Object> instanceAsMap( Object instance )
        {
          return instance instanceof Map ? (Map<Object, Object>) instance : null;
        }
        
        @Override
        public Object getValue()
        {
          Object retval = null;
          {
            Map<Object, Object> map = instanceAsMap( instance );
            if ( map != null )
            {
              retval = map.get( propertyName );
            }
          }
          return retval;
        }
        
        @Override
        public Class<?> getType()
        {
          Class<?> retval = null;
          {
            Object value = this.getValue();
            if ( value != null )
            {
              retval = value.getClass();
            }
          }
          return retval;
        }
        
        @Override
        public String getPropertyName()
        {
          return propertyName;
        }
      };
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Set<String> getPropertyNameSet( Object instance )
    {
      return instance instanceof Map ? SetUtils.convert( ( (Map) instance ).keySet(), new ElementConverterObjectToString() )
                                    : SetUtils.emptySet();
    }
    
    @Override
    public Class<?> getType()
    {
      return Map.class;
    }
    
  }
  
  /**
   * @see InstanceAccessor
   * @author Omnaest
   */
  private static class InstanceAccessorArbitraryObject implements InstanceAccessor
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Map<String, BeanPropertyAccessor<?>> propertyNameToBeanPropertyAccessorMap;
    private final Class<?>                             type;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    public static final class PropertyForArbitraryObject implements PropertyAccessor
    {
      private final String                       propertyName;
      private final BeanPropertyAccessor<Object> beanPropertyAccessor;
      private final Object                       instance;
      
      public PropertyForArbitraryObject( String propertyName, BeanPropertyAccessor<Object> beanPropertyAccessor, Object instance )
      {
        this.propertyName = propertyName;
        this.beanPropertyAccessor = beanPropertyAccessor;
        this.instance = instance;
      }
      
      @Override
      public void setValue( Object value )
      {
        this.beanPropertyAccessor.setPropertyValue( this.instance, value );
      }
      
      @Override
      public Object getValue()
      {
        return this.beanPropertyAccessor.getPropertyValue( this.instance );
      }
      
      @Override
      public Class<?> getType()
      {
        return this.beanPropertyAccessor.getDeclaringPropertyType();
      }
      
      @Override
      public String getPropertyName()
      {
        return this.propertyName;
      }
    }
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see InstanceAccessorArbitraryObject
     * @param type
     */
    public InstanceAccessorArbitraryObject( Class<?> type )
    {
      super();
      this.type = type;
      this.propertyNameToBeanPropertyAccessorMap = ImmutableMap.<String, BeanPropertyAccessor<?>> copyOf( BeanUtils.propertyNameToBeanPropertyAccessorMap( type ) );
    }
    
    @Override
    public PropertyAccessor getPropertyAccessor( final String propertyName, final Object instance )
    {
      @SuppressWarnings("unchecked")
      final BeanPropertyAccessor<Object> beanPropertyAccessor = (BeanPropertyAccessor<Object>) this.propertyNameToBeanPropertyAccessorMap.get( propertyName );
      return beanPropertyAccessor == null ? null : new PropertyForArbitraryObject( propertyName, beanPropertyAccessor, instance );
    }
    
    @Override
    public Set<String> getPropertyNameSet( Object instance )
    {
      return this.propertyNameToBeanPropertyAccessorMap.keySet();
    }
    
    @Override
    public Class<?> getType()
    {
      return this.type;
    }
    
  }
  
  private static interface InstanceAccessorResolver
  {
    public InstanceAccessor resolveInstanceAccessor( Class<?> type );
  }
  
  private static class InstanceAccessorResolverImpl implements InstanceAccessorResolver
  {
    private Map<Class<?>, InstanceAccessor> typeToInstanceAccessorMap = new HashMap<Class<?>, BeanReplicator.InstanceAccessor>();
    
    public InstanceAccessorResolverImpl()
    {
      super();
      
    }
    
    @Override
    public InstanceAccessor resolveInstanceAccessor( Class<?> type )
    {
      InstanceAccessor retval = this.typeToInstanceAccessorMap.get( type );
      if ( retval == null )
      {
        if ( Map.class.isAssignableFrom( type ) )
        {
          retval = new InstanceAccessorForMap();
        }
        else
        {
          retval = new InstanceAccessorArbitraryObject( type );
        }
        
        this.typeToInstanceAccessorMap.put( type, retval );
      }
      return retval;
    }
    
  }
  
  private static interface InstanceManager
  {
    
    public void addReplicaInstance( Class<?> targetType, Object value, Object valueReplica );
    
    public Object getReplicaInstance( Class<?> targetType, Object value );
    
  }
  
  private static class InstanceManagerImpl implements InstanceManager
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private Map<TypeAndInstance, Object> typeAndInstanceToReplicaInstanceMap = new ConcurrentHashMap<TypeAndInstance, Object>();
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @author Omnaest
     */
    private static class TypeAndInstance
    {
      private final Class<?> type;
      private final Object   instance;
      
      public TypeAndInstance( Class<?> type, Object instance )
      {
        super();
        this.type = type;
        this.instance = instance;
      }
      
      @Override
      public int hashCode()
      {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( this.instance == null ) ? 0 : this.instance.hashCode() );
        result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
        return result;
      }
      
      @Override
      public boolean equals( Object obj )
      {
        if ( this == obj )
        {
          return true;
        }
        if ( obj == null )
        {
          return false;
        }
        if ( !( obj instanceof TypeAndInstance ) )
        {
          return false;
        }
        TypeAndInstance other = (TypeAndInstance) obj;
        if ( this.instance == null )
        {
          if ( other.instance != null )
          {
            return false;
          }
        }
        else if ( this.instance != other.instance )
        {
          return false;
        }
        if ( this.type == null )
        {
          if ( other.type != null )
          {
            return false;
          }
        }
        else if ( !this.type.equals( other.type ) )
        {
          return false;
        }
        return true;
      }
    }
    
    /* *************************************************** Methods **************************************************** */
    
    @Override
    public void addReplicaInstance( Class<?> type, Object instance, Object replicaInstance )
    {
      if ( replicaInstance != null )
      {
        final TypeAndInstance key = new TypeAndInstance( type, instance );
        this.typeAndInstanceToReplicaInstanceMap.put( key, replicaInstance );
      }
    }
    
    @Override
    public Object getReplicaInstance( Class<?> type, Object instance )
    {
      final TypeAndInstance key = new TypeAndInstance( type, instance );
      return this.typeAndInstanceToReplicaInstanceMap.get( key );
    }
    
  }
  
  private static interface FactoryResolver
  {
    public Factory<Object> resolveFactory( Class<?> type );
  }
  
  private static class FactoryResolverImpl implements FactoryResolver
  {
    private static final class LinkedHashMapFactory implements Factory<Object>
    {
      @Override
      public Object newInstance()
      {
        return new LinkedHashMap<Object, Object>();
      }
    }
    
    private static final class HashMapFactory implements Factory<Object>
    {
      @Override
      public Object newInstance()
      {
        return new HashMap<Object, Object>();
      }
    }
    
    private Map<Class<?>, Factory<Object>> typeToFactoryMap = new MapBuilder<Class<?>, Factory<Object>>().linkedHashMap()
                                                                                                         .put( HashMap.class,
                                                                                                               new HashMapFactory() )
                                                                                                         .put( LinkedHashMap.class,
                                                                                                               new LinkedHashMapFactory() )
                                                                                                         .put( Map.class,
                                                                                                               new LinkedHashMapFactory() )
                                                                                                         .build();
    
    @Override
    public Factory<Object> resolveFactory( final Class<?> type )
    {
      final Factory<Object> factory = this.typeToFactoryMap.get( type );
      if ( factory != null )
      {
        return factory;
      }
      if ( type.isInterface() )
      {
        return new Factory<Object>()
        {
          private Builder<?> builder = PropertynameMapToTypeAdapter.builder( type, new Configuration() );
          
          @Override
          public Object newInstance()
          {
            return this.builder.newTypeAdapter( new HashMap<String, Object>() );
          }
        };
      }
      return new Factory<Object>()
      {
        @Override
        public Object newInstance()
        {
          return ReflectionUtils.newInstanceOf( type );
        }
      };
    }
    
  }
  
  public static interface DeclarationSupport extends TypeToTypeMappingDeclarer
  {
  }
  
  public static interface Declaration
  {
    public void declare( DeclarationSupport support );
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see BeanReplicator
   * @param sourceType
   * @param targetType
   */
  public BeanReplicator( Class<FROM> sourceType, Class<TO> targetType )
  {
    this.sourceType = sourceType;
    this.targetType = targetType;
    
    this.factoryResolver = new FactoryResolverImpl();
    this.instanceAccessorResolver = new InstanceAccessorResolverImpl();
    this.preservedTypeInstanceManager = new PreservedTypeInstanceManagerImpl();
    this.preservedTypeInstanceManager.add( String.class );
    this.typeToTypeMappingManager = new TypeToTypeMappingManagerImpl();
    
    this.typeToTypeMappingManager.addTypeMappingForPath( "", sourceType, targetType );
  }
  
  @SuppressWarnings("unchecked")
  public TO clone( FROM source )
  {
    TO retval = null;
    
    final Factory<Object> factory = this.factoryResolver.resolveFactory( this.sourceType );
    if ( factory != null )
    {
      retval = (TO) factory.newInstance();
      this.copy( source, retval );
    }
    
    return retval;
  }
  
  public void copy( FROM source, TO target )
  {
    final InstanceManager instanceManager = new InstanceManagerImpl();
    final Class<?> sourceType = this.sourceType;
    final Class<?> targetType = this.targetType;
    final Path path = new Path();
    this.copy( source, target, instanceManager, sourceType, targetType, path );
  }
  
  private void copy( Object source,
                     Object target,
                     InstanceManager instanceManager,
                     Class<?> sourceType,
                     Class<?> targetType,
                     Path path )
  {
    if ( source != null && sourceType != null && target != null && targetType != null )
    {
      final InstanceAccessor instanceAccessorSource = this.instanceAccessorResolver.resolveInstanceAccessor( sourceType );
      final InstanceAccessor instanceAccessorTarget = this.instanceAccessorResolver.resolveInstanceAccessor( targetType );
      if ( instanceAccessorSource != null && instanceAccessorTarget != null )
      {
        for ( String propertyName : instanceAccessorSource.getPropertyNameSet( source ) )
        {
          final PropertyAccessor propertySource = instanceAccessorSource.getPropertyAccessor( propertyName, source );
          if ( propertySource != null )
          {
            Object valueReplica = null;
            {
              final Class<?> propertySourceType = propertySource.getType();
              final PropertyNameAndType remapping = this.typeToTypeMappingManager.determineRemapping( propertyName,
                                                                                                      propertySourceType, path );
              final String propertyNameWithinTarget = determinePropertyNameWithinTarget( propertyName, remapping );
              final PropertyAccessor propertyTarget = instanceAccessorTarget.getPropertyAccessor( propertyNameWithinTarget,
                                                                                                  target );
              if ( propertyTarget != null )
              {
                final Class<?> propertyTargetType = determinePropertyTargetType( propertyTarget, propertySourceType, remapping );
                final Object value = propertySource.getValue();
                if ( value != null )
                {
                  if ( ObjectUtils.isPrimitiveOrPrimitiveWrapperType( propertySourceType )
                       || this.preservedTypeInstanceManager.contains( propertySourceType ) )
                  {
                    valueReplica = value;
                  }
                  else
                  {
                    valueReplica = instanceManager.getReplicaInstance( propertyTargetType, value );
                    if ( valueReplica == null )
                    {
                      final Factory<Object> factory = this.factoryResolver.resolveFactory( propertyTargetType );
                      if ( factory != null )
                      {
                        valueReplica = factory.newInstance();
                        this.copy( value, valueReplica, instanceManager, propertySourceType, propertyTargetType,
                                   new Path( path, propertyName ) );
                        instanceManager.addReplicaInstance( propertyTargetType, value, valueReplica );
                      }
                    }
                  }
                }
                propertyTarget.setValue( valueReplica );
              }
            }
          }
        }
      }
    }
  }
  
  private static String determinePropertyNameWithinTarget( String propertyName, final PropertyNameAndType remapping )
  {
    return remapping != null ? remapping.getPropertyName() : propertyName;
  }
  
  private static Class<?> determinePropertyTargetType( final PropertyAccessor propertyTarget,
                                                       final Class<?> propertySourceType,
                                                       PropertyNameAndType remapping )
  {
    Class<?> retval = null;
    if ( remapping == null )
    {
      retval = propertyTarget.getType();
      if ( retval == null )
      {
        retval = propertySourceType;
      }
    }
    else
    {
      retval = remapping.getType();
    }
    return retval;
  }
  
  public void declare( Declaration declaration )
  {
    final TypeToTypeMappingManager typeToTypeMappingManager = this.typeToTypeMappingManager;
    if ( declaration != null )
    {
      
      final DeclarationSupport support = new DeclarationSupport()
      {
        
        @Override
        public void addTypeMapping( Class<?> typeFrom, Class<?> typeTo )
        {
          typeToTypeMappingManager.addTypeMapping( typeFrom, typeTo );
        }
        
        @Override
        public void addTypeAndPropertyNameMapping( Class<?> typeFrom,
                                                   String propertyNameFrom,
                                                   Class<?> typeTo,
                                                   String propertyNameTo )
        {
          typeToTypeMappingManager.addTypeAndPropertyNameMapping( typeFrom, propertyNameFrom, typeTo, propertyNameTo );
        }
        
        @Override
        public void addPropertyNameMapping( String propertyNameFrom, String propertyNameTo )
        {
          typeToTypeMappingManager.addPropertyNameMapping( propertyNameFrom, propertyNameTo );
          
        }
        
        @Override
        public void addPropertyNameMapping( String path, String propertyNameFrom, String propertyNameTo )
        {
          typeToTypeMappingManager.addPropertyNameMapping( path, propertyNameFrom, propertyNameTo );
        }
        
        @Override
        public void addTypeAndPropertyNameMapping( String path,
                                                   Class<?> typeFrom,
                                                   String propertyNameFrom,
                                                   Class<?> typeTo,
                                                   String propertyNameTo )
        {
          typeToTypeMappingManager.addTypeAndPropertyNameMapping( path, typeFrom, propertyNameFrom, typeTo, propertyNameTo );
        }
        
        @Override
        public void addTypeMappingForPath( String path, Class<?> typeFrom, Class<?> typeTo )
        {
          typeToTypeMappingManager.addTypeMappingForPath( path, typeFrom, typeTo );
        }
        
      };
      declaration.declare( support );
      
      this.prepare();
    }
  }
  
  private void prepare()
  {
  }
  
  public BeanReplicator<FROM, TO> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler.setExceptionHandler( ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() ) );
    return this;
  }
}
