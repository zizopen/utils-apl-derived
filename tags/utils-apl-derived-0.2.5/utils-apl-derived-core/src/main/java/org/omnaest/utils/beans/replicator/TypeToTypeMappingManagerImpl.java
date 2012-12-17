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
package org.omnaest.utils.beans.replicator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omnaest.utils.assertion.Assert;

/**
 * @see TypeToTypeMappingManager
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class TypeToTypeMappingManagerImpl implements TypeToTypeMappingManager
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                    serialVersionUID                            = -2633024229759544479L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<Class<?>, Class<?>>                              sourceToTargetTypeMap                       = new HashMap<Class<?>, Class<?>>();
  private Map<TypeAndPath, Class<?>>                           sourceToTargetTypeAndPathMap                = new HashMap<TypeAndPath, Class<?>>();
  private Map<PropertyNameAndType, PropertyNameAndType>        sourceToTargetPropertyNameAndTypeMap        = new HashMap<PropertyNameAndType, PropertyNameAndType>();
  private Map<PropertyNameAndTypeAndPath, PropertyNameAndType> sourceToTargetPropertyNameAndTypeAndPathMap = new HashMap<PropertyNameAndTypeAndPath, PropertyNameAndType>();
  private Map<String, String>                                  propertyNameToPropertyNameMap               = new HashMap<String, String>();
  private Map<PropertyNameAndPath, String>                     propertyNameAndPathToPropertyNameMap        = new HashMap<PropertyNameAndPath, String>();
  
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
