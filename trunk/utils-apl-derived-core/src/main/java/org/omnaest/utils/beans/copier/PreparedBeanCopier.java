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
package org.omnaest.utils.beans.copier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.copier.PreparedBeanCopier.CopierFactory.Copier;
import org.omnaest.utils.beans.copier.PreparedBeanCopier.InstanceFactoryCreator.InstanceFactory;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.tuple.Tuple2;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A {@link PreparedBeanCopier} will prepare reflection based property copy actions for two given {@link Class} types in advance.<br>
 * <br>
 * This allows to execute the copy process about <b>5 - 10 times</b> faster than the Apache Commons
 * {@link org.apache.commons.beanutils.BeanUtils#copyProperties(Object, Object)} <br>
 * <br>
 * E.g. for bean copy actions including instantiations of {@link Map}s or {@link List}s this implementations is only about 5 - 10
 * times slower than regular getter and setter transfer constructs. <br>
 * <br>
 * The overall speed is about <b>5-10 seconds</b> for <b>about 10000000 copy operations</b> invoked by the same
 * {@link PreparedBeanCopier} instance. <br>
 * <br>
 * The {@link PreparedBeanCopier} can be based on interfaces when being constructed, which allows to copy a reduced set of
 * properties by giving an interface with a subset of the instance properties at construction time and the implementing instance
 * during copy method invocation. <br>
 * <br>
 * The {@link Configuration} allows to set various options, like:
 * <ul>
 * <li>{@link Configuration#setHandlingPrimitivesAndWrappers(boolean)}</li>
 * <li>{@link Configuration#setHandlingLists(boolean)}</li>
 * <li>{@link Configuration#setHandlingSets(boolean)}</li>
 * <li>{@link Configuration#setHandlingMaps(boolean)}</li>
 * <li>{@link Configuration#setHandlingArbitraryObjects(boolean)}</li>
 * </ul>
 * <br>
 * <br>
 * The {@link #deepCloneProperties(Object)} and {@link #deepCopyProperties(Object, Object)} instances are thread safe per default,
 * as long as any at {@link #setExceptionHandler(ExceptionHandler)} or at {@link Configuration#add(CopierFactory)} or
 * {@link Configuration#add(InstanceFactoryCreator)} provided instances are thread safe.
 * 
 * @author Omnaest
 */
public class PreparedBeanCopier<FROM, TO> implements Serializable
{
  
  /* ************************************************** Constants *************************************************** */
  private static final long                     serialVersionUID             = -7426881508083859649L;
  private static final ExceptionHandlerIgnoring DEFAULT_EXCEPTION_HANDLER    = new ExceptionHandlerIgnoring();
  private static final PropertyAccessType       DEFAULT_PROPERTY_ACCESS_TYPE = PropertyAccessType.PROPERTY;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final List<PreparedCopier>            preparedCopierList;
  private ExceptionHandler                      exceptionHandler             = PreparedBeanCopier.DEFAULT_EXCEPTION_HANDLER;
  private final InstanceFactory                 instanceFactoryForRoot;
  private final Class<FROM>                     typeFrom;
  private PropertyAccessType                    propertyAccessTypeFrom       = PreparedBeanCopier.DEFAULT_PROPERTY_ACCESS_TYPE;
  private PropertyAccessType                    propertyAccessTypeTo         = PreparedBeanCopier.DEFAULT_PROPERTY_ACCESS_TYPE;
  private final Transformer                     transformer;
  private final List<String>                    nonMatchingPropertyNameList;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see PreparedBeanCopier
   * @author Omnaest
   */
  public static class Configuration implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long                  serialVersionUID                = 5361292993967280460L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final List<InstanceFactoryCreator> instanceFactoryCreatorList      = new ArrayList<InstanceFactoryCreator>();
    private final List<CopierFactory>          copierFactoryList               = new ArrayList<CopierFactory>();
    
    private Map<Class<?>, Class<?>>            typeFromToTypeToMap             = ImmutableMap.<Class<?>, Class<?>> of();
    
    private boolean                            isHandlingPrimitivesAndWrappers = true;
    private boolean                            isHandlingLists                 = true;
    private boolean                            isHandlingSets                  = true;
    private boolean                            isHandlingCollections           = true;
    private boolean                            isHandlingMaps                  = true;
    private boolean                            isHandlingArbitraryObjects      = true;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @param instanceFactoryCreator
     *          {@link InstanceFactoryCreator}
     * @return this
     */
    public Configuration add( InstanceFactoryCreator instanceFactoryCreator )
    {
      this.instanceFactoryCreatorList.add( instanceFactoryCreator );
      return this;
    }
    
    /**
     * @param index
     * @param instanceFactoryCreator
     *          {@link InstanceFactoryCreator}
     * @return this
     */
    public Configuration add( int index, InstanceFactoryCreator instanceFactoryCreator )
    {
      this.instanceFactoryCreatorList.add( index, instanceFactoryCreator );
      return this;
    }
    
    /**
     * @param copierFactory
     *          {@link CopierFactory}
     * @return this
     */
    public Configuration add( CopierFactory copierFactory )
    {
      this.copierFactoryList.add( copierFactory );
      return this;
    }
    
    /**
     * @param index
     * @param copierFactory
     *          {@link CopierFactory}
     * @return this
     */
    public Configuration add( int index, CopierFactory copierFactory )
    {
      this.copierFactoryList.add( index, copierFactory );
      return this;
    }
    
    /**
     * @return
     */
    public List<InstanceFactoryCreator> getInstanceHandlerFactoryList()
    {
      return Collections.unmodifiableList( this.instanceFactoryCreatorList );
    }
    
    /**
     * @return
     */
    public List<CopierFactory> getCopierFactoryList()
    {
      return Collections.unmodifiableList( this.copierFactoryList );
    }
    
    /**
     * @param isHandlingPrimitivesAndWrappers
     * @return this
     */
    public Configuration setHandlingPrimitivesAndWrappers( boolean isHandlingPrimitivesAndWrappers )
    {
      this.isHandlingPrimitivesAndWrappers = isHandlingPrimitivesAndWrappers;
      return this;
    }
    
    /**
     * @param isHandlingArbitraryObjects
     * @return this
     */
    public Configuration setHandlingArbitraryObjects( boolean isHandlingArbitraryObjects )
    {
      this.isHandlingArbitraryObjects = isHandlingArbitraryObjects;
      return this;
    }
    
    /**
     * @return
     */
    public boolean isHandlingPrimitivesAndWrappers()
    {
      return this.isHandlingPrimitivesAndWrappers;
    }
    
    /**
     * @return
     */
    public boolean isHandlingArbitraryObjects()
    {
      return this.isHandlingArbitraryObjects;
    }
    
    /**
     * @return
     */
    public boolean isHandlingLists()
    {
      return this.isHandlingLists;
    }
    
    /**
     * @param isHandlingLists
     * @return this
     */
    public Configuration setHandlingLists( boolean isHandlingLists )
    {
      this.isHandlingLists = isHandlingLists;
      return this;
    }
    
    /**
     * Adds a mapping from one {@link Class} type to another {@link Class} type
     * 
     * @param typeFrom
     *          {@link Class}
     * @param typeTo
     *          {@link Class}
     * @return this
     */
    public Configuration addTypeToTypeMapping( Class<?> typeFrom, Class<?> typeTo )
    {
      this.typeFromToTypeToMap = ImmutableMap.<Class<?>, Class<?>> builder()
                                             .putAll( this.typeFromToTypeToMap )
                                             .put( typeFrom, typeTo )
                                             .build();
      return this;
    }
    
    /**
     * Similar to {@link #addTypeToTypeMapping(Class, Class)} but for both directions
     * 
     * @param type1
     *          {@link Class}
     * @param type2
     *          {@link Class}
     * @return this
     */
    public Configuration addBidirectionalTypeToTypeMapping( Class<?> type1, Class<?> type2 )
    {
      this.addTypeToTypeMapping( type1, type2 );
      this.addTypeToTypeMapping( type2, type1 );
      return this;
    }
    
    /**
     * @see #addTypeToTypeMapping(Class, Class)
     * @param typeFromToTypeToMap
     * @return this
     */
    public Configuration addTypeToTypeMapping( Map<? extends Class<?>, ? extends Class<?>> typeFromToTypeToMap )
    {
      this.typeFromToTypeToMap = ImmutableMap.<Class<?>, Class<?>> builder()
                                             .putAll( this.typeFromToTypeToMap )
                                             .putAll( typeFromToTypeToMap )
                                             .build();
      return this;
    }
    
    /**
     * @see #addBidirectionalTypeToTypeMapping(Class, Class)
     * @param typeFromToTypeToMap
     * @return
     */
    public Configuration addBidirectionalTypeToTypeMapping( Map<? extends Class<?>, ? extends Class<?>> typeFromToTypeToMap )
    {
      this.addTypeToTypeMapping( typeFromToTypeToMap );
      this.addTypeToTypeMapping( MapUtils.invertedBidirectionalMap( typeFromToTypeToMap ) );
      return this;
    }
    
    /**
     * @return
     */
    public Map<Class<?>, Class<?>> getTypeFromToTypeToMap()
    {
      return this.typeFromToTypeToMap;
    }
    
    /**
     * @return
     */
    public boolean isHandlingSets()
    {
      return this.isHandlingSets;
    }
    
    /**
     * @param isHandlingSets
     * @return this
     */
    public Configuration setHandlingSets( boolean isHandlingSets )
    {
      this.isHandlingSets = isHandlingSets;
      return this;
    }
    
    /**
     * @return
     */
    public boolean isHandlingMaps()
    {
      return this.isHandlingMaps;
    }
    
    /**
     * @param isHandlingMaps
     * @return this
     */
    public Configuration setHandlingMaps( boolean isHandlingMaps )
    {
      this.isHandlingMaps = isHandlingMaps;
      return this;
    }
    
    /**
     * @return
     */
    public boolean isHandlingCollections()
    {
      return this.isHandlingCollections;
    }
    
    /**
     * @param isHandlingCollections
     * @return this
     */
    public Configuration setHandlingCollections( boolean isHandlingCollections )
    {
      this.isHandlingCollections = isHandlingCollections;
      return this;
    }
    
  }
  
  private static class PreparedCopier implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long                  serialVersionUID = 9014411875272403900L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final BeanPropertyAccessor<Object> beanPropertyAccessorFrom;
    private final BeanPropertyAccessor<Object> beanPropertyAccessorTo;
    private final InstanceFactory              instanceFactory;
    private final Class<?>                     type;
    private final Copier                       copier;
    
    /* *************************************************** Methods **************************************************** */
    
    @SuppressWarnings("unchecked")
    public PreparedCopier( BeanPropertyAccessor<? extends Object> beanPropertyAccessorFrom,
                           BeanPropertyAccessor<? extends Object> beanPropertyAccessorTo, InstanceFactory instanceFactory,
                           Class<?> type, Copier copier )
    {
      super();
      this.copier = copier;
      this.beanPropertyAccessorFrom = (BeanPropertyAccessor<Object>) beanPropertyAccessorFrom;
      this.beanPropertyAccessorTo = (BeanPropertyAccessor<Object>) beanPropertyAccessorTo;
      this.instanceFactory = instanceFactory;
      this.type = type;
    }
    
    public Object getPropertyValue( Object bean, PropertyAccessType propertyAccessType, ExceptionHandler exceptionHandler )
    {
      return this.beanPropertyAccessorFrom.getPropertyValue( bean, propertyAccessType, exceptionHandler );
    }
    
    public boolean setPropertyValue( Object bean,
                                     Object value,
                                     PropertyAccessType propertyAccessType,
                                     ExceptionHandler exceptionHandler )
    {
      return this.beanPropertyAccessorTo.setPropertyValue( bean, value, propertyAccessType, exceptionHandler );
    }
    
    public Object newReplacementInstance()
    {
      return this.instanceFactory.newReplacementInstance( this.type );
    }
    
    /**
     * @param instanceFrom
     * @param instanceTo
     * @param transformer
     */
    public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
    {
      if ( this.copier != null )
      {
        this.copier.copy( instanceFrom, instanceTo, transformer );
      }
    }
    
  }
  
  public static interface InstanceFactoryCreator extends Serializable
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @author Omnaest
     */
    public static interface InstanceFactory extends Serializable
    {
      /**
       * Marker instance which indicates an immutable instance, which should not be cloned. E.g. an {@link InstanceFactory} should
       * return this for primitive types
       */
      public static final Object IMMUTABLE_INSTANCE = new Object();
      
      /**
       * Returns a new instance for the given {@link Class} type
       * 
       * @see #IMMUTABLE_INSTANCE
       * @param type
       * @return new instance
       */
      public Object newReplacementInstance( Class<?> type );
    }
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * Returns true if the given {@link Class} type can be handled
     * 
     * @param type
     * @return
     */
    public boolean isHandling( Class<?> type );
    
    /**
     * Returns a new {@link InstanceFactory} for the given {@link Class} type
     * 
     * @param type
     * @return
     */
    public InstanceFactory newInstanceFactory( Class<?> type );
  }
  
  /**
   * Factory for {@link Copier} instances
   * 
   * @author Omnaest
   */
  public static interface CopierFactory extends Serializable
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see #copy(Object, Object, Transformer)
     * @author Omnaest
     */
    public static interface Copier extends Serializable
    {
      /**
       * Copies all property values from one given instance to another
       * 
       * @param instanceFrom
       * @param instanceTo
       * @param transformer
       *          {@link Transformer}
       */
      public void copy( Object instanceFrom, Object instanceTo, Transformer transformer );
    }
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * Indicates if the given {@link Class} type can be handled
     * 
     * @param type
     * @return
     */
    public boolean isHandling( Class<?> type );
    
    /**
     * Returns a new {@link Copier} instance for the given {@link Class} type
     * 
     * @param typeFrom
     * @param typeTo
     * @param configuration
     *          {@link Configuration}
     * @param metaDataHandler
     *          {@link MetaDataHandler}
     * @return
     */
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler );
  }
  
  public static interface MetaDataHandler extends Serializable
  {
    
    public void reportNonMatchingPropertyNames( List<String> list );
  }
  
  /**
   * A {@link Transformer} will return new transformed instances for another given instance
   * 
   * @author Omnaest
   */
  public static interface Transformer extends Serializable
  {
    /**
     * Transforms one instance into another
     * 
     * @param instanceFrom
     * @return
     */
    public Object transform( Object instanceFrom );
  }
  
  private static class InstanceFactoryCreatorForPrimitives implements InstanceFactoryCreator, InstanceFactory
  {
    private static final long serialVersionUID = -8024424149443680755L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return ObjectUtils.isPrimitiveOrPrimitiveWrapperType( type ) || String.class.equals( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return this;
    }
    
    @Override
    public Object newReplacementInstance( Class<?> type )
    {
      return IMMUTABLE_INSTANCE;
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForDate implements InstanceFactoryCreator, InstanceFactory,
                                                                    CopierFactory
  {
    private static final long serialVersionUID = -8024424149443680755L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return Date.class.equals( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return this;
    }
    
    @Override
    public Object newReplacementInstance( Class<?> type )
    {
      return new Date();
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      return new Copier()
      {
        private static final long serialVersionUID = 858420964408231399L;
        
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          Date dateFrom = (Date) instanceFrom;
          Date dateTo = (Date) instanceTo;
          dateTo.setTime( dateFrom.getTime() );
        }
      };
    }
  }
  
  private static class InstanceFactoryCreatorForMappedTypes implements InstanceFactoryCreator, InstanceFactory
  {
    /* ************************************************** Constants *************************************************** */
    private static final long             serialVersionUID = -1621806923021530179L;
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Map<Class<?>, Class<?>> typeFromToTypeToMap;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see InstanceFactoryCreatorForMappedTypes
     * @param typeFromToTypeToMap
     */
    public InstanceFactoryCreatorForMappedTypes( Map<Class<?>, Class<?>> typeFromToTypeToMap )
    {
      super();
      this.typeFromToTypeToMap = typeFromToTypeToMap;
    }
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return this.typeFromToTypeToMap.containsKey( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return this;
    }
    
    @Override
    public Object newReplacementInstance( Class<?> type )
    {
      return ReflectionUtils.newInstanceOf( this.typeFromToTypeToMap.get( type ) );
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForArbitraryObjects implements InstanceFactoryCreator, CopierFactory
  {
    private static final long serialVersionUID = -5948935042135374462L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return !ObjectUtils.isPrimitiveOrPrimitiveWrapperType( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return new InstanceFactory()
      {
        private static final long serialVersionUID = -6795625336262144894L;
        
        @Override
        public Object newReplacementInstance( Class<?> type )
        {
          return ReflectionUtils.newInstanceOf( type );
        }
      };
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      final PreparedBeanCopier<Object, Object> preparedBeanCopier = new PreparedBeanCopier<Object, Object>( typeFrom, typeTo,
                                                                                                            configuration );
      metaDataHandler.reportNonMatchingPropertyNames( preparedBeanCopier.getNonMatchingPropertyNameList() );
      return new Copier()
      {
        private static final long serialVersionUID = -6415805520183068114L;
        
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          preparedBeanCopier.deepCopyProperties( instanceFrom, instanceTo );
        }
      };
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForList implements InstanceFactoryCreator, CopierFactory
  {
    private static final long serialVersionUID = 654229828154669671L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return List.class.isAssignableFrom( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return new InstanceFactory()
      {
        private static final long serialVersionUID = 312554307912581992L;
        
        @Override
        public Object newReplacementInstance( Class<?> type )
        {
          return new ArrayList<Object>();
        }
      };
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      return new Copier()
      {
        private static final long serialVersionUID = 7561447633309204450L;
        
        @SuppressWarnings("unchecked")
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          if ( instanceFrom instanceof List && instanceTo instanceof List )
          {
            //
            final List<? extends Object> listFrom = (List<Object>) instanceFrom;
            final List<? super Object> listTo = (List<Object>) instanceTo;
            
            for ( Object element : listFrom )
            {
              listTo.add( transformer.transform( element ) );
            }
          }
        }
      };
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForSet implements InstanceFactoryCreator, CopierFactory
  {
    private static final long serialVersionUID = 3615193483888509556L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return Set.class.isAssignableFrom( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return new InstanceFactory()
      {
        private static final long serialVersionUID = 5411662705527963151L;
        
        @Override
        public Object newReplacementInstance( Class<?> type )
        {
          return new LinkedHashSet<Object>();
        }
      };
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      return new Copier()
      {
        private static final long serialVersionUID = -4647789552559439022L;
        
        @SuppressWarnings("unchecked")
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          if ( instanceFrom instanceof Set && instanceTo instanceof Set )
          {
            //
            final Set<? extends Object> setFrom = (Set<Object>) instanceFrom;
            final Set<? super Object> setTo = (Set<Object>) instanceTo;
            
            for ( Object element : setFrom )
            {
              setTo.add( transformer.transform( element ) );
            }
          }
        }
      };
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForCollection implements InstanceFactoryCreator, CopierFactory
  {
    
    private static final long serialVersionUID = 218975866373111663L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return Collection.class.isAssignableFrom( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return new InstanceFactory()
      {
        private static final long serialVersionUID = 5411662705527963151L;
        
        @Override
        public Object newReplacementInstance( Class<?> type )
        {
          return new ArrayList<Object>();
        }
      };
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      return new Copier()
      {
        private static final long serialVersionUID = -4647789552559439022L;
        
        @SuppressWarnings("unchecked")
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          if ( instanceFrom instanceof Collection && instanceTo instanceof Collection )
          {
            //
            final Collection<? extends Object> collectionFrom = (Collection<Object>) instanceFrom;
            final Collection<? super Object> collectionTo = (Collection<Object>) instanceTo;
            
            for ( Object element : collectionFrom )
            {
              collectionTo.add( transformer.transform( element ) );
            }
          }
        }
      };
    }
  }
  
  private static class InstanceFactoryCreatorAndCopierFactoryForMap implements InstanceFactoryCreator, CopierFactory
  {
    private static final long serialVersionUID = -5806518949904168961L;
    
    @Override
    public boolean isHandling( Class<?> type )
    {
      return Map.class.isAssignableFrom( type );
    }
    
    @Override
    public InstanceFactory newInstanceFactory( Class<?> type )
    {
      return new InstanceFactory()
      {
        private static final long serialVersionUID = -2991059275809191118L;
        
        @Override
        public Object newReplacementInstance( Class<?> type )
        {
          return new LinkedHashMap<Object, Object>();
        }
      };
    }
    
    @Override
    public Copier newCopier( Class<?> typeFrom, Class<?> typeTo, Configuration configuration, MetaDataHandler metaDataHandler )
    {
      return new Copier()
      {
        private static final long serialVersionUID = -122066676367014959L;
        
        @SuppressWarnings("unchecked")
        @Override
        public void copy( Object instanceFrom, Object instanceTo, Transformer transformer )
        {
          if ( instanceFrom instanceof Map && instanceTo instanceof Map )
          {
            //
            final Map<Object, Object> mapFrom = (Map<Object, Object>) instanceFrom;
            final Map<Object, Object> mapTo = (Map<Object, Object>) instanceTo;
            
            for ( Object keyFrom : mapFrom.keySet() )
            {
              Object valueFrom = mapFrom.get( keyFrom );
              mapTo.put( transformer.transform( keyFrom ), transformer.transform( valueFrom ) );
            }
          }
        }
      };
    }
  }
  
  /**
   * @see PreparedBeanCopier
   * @author Omnaest
   */
  public static class NonMatchingPropertyException extends Exception
  {
    private static final long  serialVersionUID = 9045272214549608639L;
    private final List<String> nonMatchingPropertyNameList;
    
    public NonMatchingPropertyException( List<String> nonMatchingPropertyNameList )
    {
      super( "Properties are not matching: " + nonMatchingPropertyNameList );
      this.nonMatchingPropertyNameList = nonMatchingPropertyNameList;
    }
    
    public List<String> getNonMatchingPropertyNameList()
    {
      return this.nonMatchingPropertyNameList;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "NonMatchingPropertyException [nonMatchingPropertyNameList2=" );
      builder.append( this.nonMatchingPropertyNameList );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see PreparedBeanCopier
   * @param typeFrom
   * @param typeTo
   */
  public PreparedBeanCopier( Class<? extends FROM> typeFrom, Class<? extends TO> typeTo )
  {
    this( typeFrom, typeTo, new Configuration() );
  }
  
  /**
   * @see PreparedBeanCopier
   * @param typeFrom
   *          {@link Class}
   * @param typeTo
   *          {@link Class}
   * @param configuration
   *          {@link Configuration}
   */
  @SuppressWarnings("unchecked")
  public PreparedBeanCopier( Class<? extends FROM> typeFrom, Class<? extends TO> typeTo, Configuration configuration )
  {
    super();
    
    //
    this.typeFrom = (Class<FROM>) typeFrom;
    
    //
    final List<String> nonMatchingPropertyNameList = new ArrayList<String>();
    final Configuration configurationOrDefault = ObjectUtils.defaultIfNull( configuration, new Configuration() );
    this.preparedCopierList = PreparedBeanCopier.newPreparedCopierList( typeFrom, typeTo, configurationOrDefault,
                                                                        nonMatchingPropertyNameList );
    this.instanceFactoryForRoot = PreparedBeanCopier.newInstanceFactory( typeFrom, configurationOrDefault );
    this.nonMatchingPropertyNameList = ImmutableList.<String> copyOf( nonMatchingPropertyNameList );
    this.transformer = new Transformer()
    {
      private static final long serialVersionUID = -4846406160190255627L;
      
      @Override
      public Object transform( Object instanceFrom )
      {
        Object retval = null;
        if ( instanceFrom != null )
        {
          Class<?> propertyTypeFrom = instanceFrom.getClass();
          InstanceFactory instanceFactory = newInstanceFactory( propertyTypeFrom, configurationOrDefault );
          if ( instanceFactory != null )
          {
            //
            retval = instanceFactory.newReplacementInstance( propertyTypeFrom );
            if ( retval != null )
            {
              if ( retval != InstanceFactory.IMMUTABLE_INSTANCE )
              {
                final Class<?> propertyTypeTo = retval.getClass();
                final MetaDataHandler metaDataHandler = newMetaDataHandler( PreparedBeanCopier.this.nonMatchingPropertyNameList );
                Copier copier = newCopier( propertyTypeFrom, propertyTypeTo, configurationOrDefault, metaDataHandler );
                if ( copier != null )
                {
                  copier.copy( instanceFrom, retval, this );
                }
              }
              else
              {
                retval = instanceFrom;
              }
            }
          }
        }
        return retval;
      }
    };
  }
  
  private static <FROM, TO> List<PreparedCopier> newPreparedCopierList( Class<FROM> typeFrom,
                                                                        Class<TO> typeTo,
                                                                        Configuration configuration,
                                                                        final List<String> nonMatchingPropertyNameList )
  {
    //
    final List<PreparedCopier> retlist = new ArrayList<PreparedCopier>();
    
    //
    final Map<String, BeanPropertyAccessor<FROM>> propertyNameToBeanPropertyAccessorMapFrom = BeanUtils.propertyNameToBeanPropertyAccessorMap( typeFrom );
    final Map<String, BeanPropertyAccessor<TO>> propertyNameToBeanPropertyAccessorMapTo = BeanUtils.propertyNameToBeanPropertyAccessorMap( typeTo );
    Map<String, Tuple2<BeanPropertyAccessor<FROM>, BeanPropertyAccessor<TO>>> joinMapByPropertyName = MapUtils.innerJoinMapByKey( propertyNameToBeanPropertyAccessorMapFrom,
                                                                                                                                  propertyNameToBeanPropertyAccessorMapTo );
    {
      nonMatchingPropertyNameList.addAll( SetUtils.delta( propertyNameToBeanPropertyAccessorMapFrom.keySet(),
                                                          joinMapByPropertyName.keySet() ).getRemovedElementSet() );
      nonMatchingPropertyNameList.addAll( SetUtils.delta( propertyNameToBeanPropertyAccessorMapTo.keySet(),
                                                          joinMapByPropertyName.keySet() ).getRemovedElementSet() );
    }
    for ( String propertyName : joinMapByPropertyName.keySet() )
    {
      //
      final Tuple2<BeanPropertyAccessor<FROM>, BeanPropertyAccessor<TO>> tuple = joinMapByPropertyName.get( propertyName );
      final BeanPropertyAccessor<FROM> beanPropertyAccessorFrom = tuple.getValueFirst();
      final BeanPropertyAccessor<TO> beanPropertyAccessorTo = tuple.getValueSecond();
      if ( beanPropertyAccessorTo != null && beanPropertyAccessorFrom != null )
      {
        //
        final Class<?> propertyTypeFrom = beanPropertyAccessorFrom.getDeclaringPropertyType();
        final Class<?> propertyTypeTo = beanPropertyAccessorTo.getDeclaringPropertyType();
        if ( propertyTypeTo != null && propertyTypeFrom != null )
        {
          //
          final InstanceFactory instanceFactory = newInstanceFactory( propertyTypeFrom, configuration );
          final MetaDataHandler metaDataHandler = newMetaDataHandler( nonMatchingPropertyNameList );
          final Copier copier = newCopier( propertyTypeFrom, propertyTypeTo, configuration, metaDataHandler );
          if ( instanceFactory != null )
          {
            retlist.add( new PreparedCopier( beanPropertyAccessorFrom, beanPropertyAccessorTo, instanceFactory, propertyTypeFrom,
                                             copier ) );
          }
        }
      }
    }
    
    //
    return ImmutableList.<PreparedCopier> copyOf( retlist );
  }
  
  /**
   * @param nonMatchingPropertyNameList
   * @return new {@link MetaDataHandler} instance
   */
  private static MetaDataHandler newMetaDataHandler( final List<String> nonMatchingPropertyNameList )
  {
    return new MetaDataHandler()
    {
      private static final long serialVersionUID = -2755016787172736785L;
      
      @Override
      public void reportNonMatchingPropertyNames( List<String> propertyNameList )
      {
        if ( propertyNameList != null )
        {
          nonMatchingPropertyNameList.addAll( propertyNameList );
        }
      }
    };
  }
  
  private static InstanceFactory newInstanceFactory( Class<?> propertyTypeFrom, Configuration configuration )
  {
    InstanceFactory instanceHandler = null;
    {
      //
      final List<InstanceFactoryCreator> instanceHandlerFactoryList = new ArrayList<InstanceFactoryCreator>(
                                                                                                             configuration.getInstanceHandlerFactoryList() );
      {
        //
        if ( configuration.isHandlingPrimitivesAndWrappers() )
        {
          instanceHandlerFactoryList.add( 0, new InstanceFactoryCreatorForPrimitives() );
          instanceHandlerFactoryList.add( 1, new InstanceFactoryCreatorAndCopierFactoryForDate() );
        }
        
        if ( configuration.isHandlingLists() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForList() );
        }
        if ( configuration.isHandlingSets() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForSet() );
        }
        if ( configuration.isHandlingCollections() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForCollection() );
        }
        if ( configuration.isHandlingMaps() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForMap() );
        }
        
        final Map<Class<?>, Class<?>> typeFromToTypeToMap = configuration.getTypeFromToTypeToMap();
        if ( !typeFromToTypeToMap.isEmpty() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorForMappedTypes( typeFromToTypeToMap ) );
        }
        
        if ( configuration.isHandlingArbitraryObjects() )
        {
          instanceHandlerFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForArbitraryObjects() );
        }
      }
      
      //
      for ( InstanceFactoryCreator instanceHandlerFactory : instanceHandlerFactoryList )
      {
        if ( instanceHandlerFactory != null && instanceHandlerFactory.isHandling( propertyTypeFrom ) )
        {
          instanceHandler = instanceHandlerFactory.newInstanceFactory( propertyTypeFrom );
          break;
        }
      }
    }
    return instanceHandler;
  }
  
  private static Copier newCopier( Class<?> propertyTypeFrom,
                                   Class<?> propertyTypeTo,
                                   Configuration configuration,
                                   MetaDataHandler metaDataHandler )
  {
    Copier copier = null;
    {
      final List<CopierFactory> copierFactoryList = new ArrayList<CopierFactory>( configuration.getCopierFactoryList() );
      {
        if ( configuration.isHandlingPrimitivesAndWrappers() )
        {
          copierFactoryList.add( 0, new InstanceFactoryCreatorAndCopierFactoryForDate() );
        }
        if ( configuration.isHandlingLists() )
        {
          copierFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForList() );
        }
        if ( configuration.isHandlingSets() )
        {
          copierFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForSet() );
        }
        if ( configuration.isHandlingCollections() )
        {
          copierFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForCollection() );
        }
        if ( configuration.isHandlingMaps() )
        {
          copierFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForMap() );
        }
        if ( configuration.isHandlingArbitraryObjects() )
        {
          copierFactoryList.add( new InstanceFactoryCreatorAndCopierFactoryForArbitraryObjects() );
        }
      }
      for ( CopierFactory copierFactory : copierFactoryList )
      {
        if ( copierFactory != null && copierFactory.isHandling( propertyTypeFrom ) )
        {
          copier = copierFactory.newCopier( propertyTypeFrom, propertyTypeTo, configuration, metaDataHandler );
          break;
        }
      }
    }
    return copier;
  }
  
  /**
   * @param instanceFrom
   * @return new clone instance
   */
  public TO deepCloneProperties( FROM instanceFrom )
  {
    @SuppressWarnings("unchecked")
    final TO instanceTo = (TO) this.instanceFactoryForRoot.newReplacementInstance( this.typeFrom );
    this.deepCopyProperties( instanceFrom, instanceTo );
    return instanceTo;
  }
  
  /**
   * @param instanceFrom
   * @param instanceTo
   * @return this
   */
  public PreparedBeanCopier<FROM, TO> deepCopyProperties( FROM instanceFrom, TO instanceTo )
  {
    //
    for ( PreparedCopier preparedCopier : this.preparedCopierList )
    {
      //
      try
      {
        //
        Object value = preparedCopier.getPropertyValue( instanceFrom, this.propertyAccessTypeFrom, this.exceptionHandler );
        
        //
        Object targetValue = value;
        {
          //
          final Object replacementInstance = preparedCopier.newReplacementInstance();
          if ( replacementInstance != InstanceFactory.IMMUTABLE_INSTANCE )
          {
            //
            targetValue = replacementInstance;
            
            //
            preparedCopier.copy( value, targetValue, this.transformer );
          }
        }
        preparedCopier.setPropertyValue( instanceTo, targetValue, this.propertyAccessTypeTo, this.exceptionHandler );
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * @param exceptionHandler
   *          {@link ExceptionHandler}
   * @return this
   */
  public PreparedBeanCopier<FROM, TO> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = ObjectUtils.defaultIfNull( exceptionHandler, PreparedBeanCopier.DEFAULT_EXCEPTION_HANDLER );
    return this;
  }
  
  /**
   * @param propertyAccessTypeFrom
   *          {@link PropertyAccessType}
   * @return this
   */
  public PreparedBeanCopier<FROM, TO> setPropertyAccessTypeFrom( PropertyAccessType propertyAccessTypeFrom )
  {
    this.propertyAccessTypeFrom = ObjectUtils.defaultIfNull( propertyAccessTypeFrom,
                                                             PreparedBeanCopier.DEFAULT_PROPERTY_ACCESS_TYPE );
    return this;
  }
  
  /**
   * @param propertyAccessTypeTo
   *          {@link PropertyAccessType}
   * @return this
   */
  public PreparedBeanCopier<FROM, TO> setPropertyAccessTypeTo( PropertyAccessType propertyAccessTypeTo )
  {
    this.propertyAccessTypeTo = ObjectUtils.defaultIfNull( propertyAccessTypeTo, PreparedBeanCopier.DEFAULT_PROPERTY_ACCESS_TYPE );
    return this;
  }
  
  /**
   * Returns a {@link List} of all non matching property names
   * 
   * @see #hasNonMatchingProperties()
   * @return
   */
  public List<String> getNonMatchingPropertyNameList()
  {
    return Collections.unmodifiableList( this.nonMatchingPropertyNameList );
  }
  
  /**
   * Returns true if there are properties which could not be matched
   * 
   * @see #getNonMatchingPropertyNameList()
   * @return
   */
  public boolean hasNonMatchingProperties()
  {
    return !this.nonMatchingPropertyNameList.isEmpty();
  }
  
  /**
   * Throws an {@link NonMatchingPropertyException} if {@link #hasNonMatchingProperties()} is true
   * 
   * @see #hasNonMatchingProperties()
   * @return this
   * @throws NonMatchingPropertyException
   */
  public PreparedBeanCopier<FROM, TO> throwExceptionWhenAnyPropertiesAreNotMatching() throws NonMatchingPropertyException
  {
    if ( this.hasNonMatchingProperties() )
    {
      throw new NonMatchingPropertyException( this.getNonMatchingPropertyNameList() );
    }
    return this;
  }
}
