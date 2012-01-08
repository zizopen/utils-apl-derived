/*******************************************************************************
 * Copyright 2011 Danny Kunz
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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal.Handler;
import org.omnaest.utils.beans.replicator.BeanReplicator.Configuration.AdditionType;
import org.omnaest.utils.beans.replicator.adapter.AdapterComposite;
import org.omnaest.utils.beans.replicator.adapter.AdapterForArrayTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForBigDecimalType;
import org.omnaest.utils.beans.replicator.adapter.AdapterForBigIntegerType;
import org.omnaest.utils.beans.replicator.adapter.AdapterForIterableTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForListTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForMapTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForPrimitiveTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForSetTypes;
import org.omnaest.utils.beans.replicator.adapter.AdapterForStringType;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.concrete.LinkedHashSetFactory;
import org.omnaest.utils.structure.map.MapUtils;

import com.rits.cloning.Cloner;

/**
 * The {@link BeanReplicator} allows to make a <b>deep copy</b> or a <b>deep clone</b> of an {@link Object} graph. <br>
 * For the {@link #copy(Object)} method the {@link BeanReplicator} allows to use one or multiple {@link Adapter} instances,<br>
 * whereby the {@link #clone(Object)} will try to clone the given source object without transformation.<br>
 * <br>
 * As an <b>example</b> of a source {@link Object} take a CGLIB proxy instance implementing some getter and setter methods
 * representing some property methods.<br>
 * The {@link #copy(Object)} method allows to create e.g. a data transfer {@link Object} which has a complete other {@link Class}
 * type, but which gets all the single properties copied one by one.<br>
 * On the other hand the {@link #clone(Object)} method would try to create another CGLIB proxy instance which does exactly the
 * same as the original proxy {@link Object} but being a new instance.<br>
 * <br>
 * A good decision hint can be, if the <b>state of objects</b> should be replicated, for example for a <b>history function</b>,
 * the use of {@link #clone(Object)} should be considered first.<br>
 * On the other hand for creating <b>data transfer objects (DTOs)</b> the {@link #copy(Object)} method in conjunction with a given
 * set of {@link Adapter}s is more appropriate most of the time. <br>
 * <br>
 * The {@link BeanReplicator} uses a default {@link Configuration}s.
 * 
 * @see #copy(Object)
 * @see #clone(Object)
 * @see Adapter
 * @see Configuration
 * @author Omnaest
 */
public class BeanReplicator
{
  /* ********************************************** Variables ********************************************** */
  protected final AdapterInternal               adapter;
  protected final Configuration                 configuration;
  protected final BeanPropertyAccessors<Object> unhandledBeanPropertyAccessors = new BeanPropertyAccessors<Object>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Configuration of the {@link BeanReplicator}
   * 
   * @see BeanReplicator
   * @author Omnaest
   */
  public static class Configuration
  {
    /* ********************************************** Variables ********************************************** */
    private boolean      failingOnUnhandledProperties = false;
    private AdditionType addAdapterForListTypes       = AdditionType.PREPEND;
    private AdditionType addAdapterForMapTypes        = AdditionType.PREPEND;
    private AdditionType addAdapterForSetTypes        = AdditionType.PREPEND;
    private AdditionType addAdapterForIterableTypes   = AdditionType.PREPEND;
    private AdditionType addAdapterForPrimitiveTypes  = AdditionType.PREPEND;
    private AdditionType addAdapterForStringType      = AdditionType.PREPEND;
    private AdditionType addAdapterForBigIntegerType  = AdditionType.PREPEND;
    private AdditionType addAdapterForBigDecimalType  = AdditionType.PREPEND;
    private AdditionType addAdapterForArrayTypes      = AdditionType.PREPEND;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * Defines the type how a {@link Adapter} is added
     * 
     * @author Omnaest
     */
    public static enum AdditionType
    {
      PREPEND,
      APPEND,
      DO_NOT_ADD
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Configuration
     */
    public Configuration()
    {
      super();
    }
    
    /**
     * @return the failingOnUnhandledProperties
     */
    protected boolean isFailingOnUnhandledProperties()
    {
      return this.failingOnUnhandledProperties;
    }
    
    /**
     * @param failingOnUnhandledProperties
     *          the failingOnUnhandledProperties to set
     */
    public void setFailingOnUnhandledProperties( boolean failingOnUnhandledProperties )
    {
      this.failingOnUnhandledProperties = failingOnUnhandledProperties;
    }
    
    /**
     * @return the addAdapterForListTypes
     */
    protected AdditionType getAddAdapterForListTypes()
    {
      return this.addAdapterForListTypes;
    }
    
    /**
     * @param addAdapterForListTypes
     *          the addAdapterForListTypes to set
     */
    public void setAddAdapterForListTypes( AdditionType addAdapterForListTypes )
    {
      this.addAdapterForListTypes = addAdapterForListTypes;
    }
    
    /**
     * @return the addAdapterForMapTypes
     */
    protected AdditionType getAddAdapterForMapTypes()
    {
      return this.addAdapterForMapTypes;
    }
    
    /**
     * @param addAdapterForMapTypes
     *          the addAdapterForMapTypes to set
     */
    public void setAddAdapterForMapTypes( AdditionType addAdapterForMapTypes )
    {
      this.addAdapterForMapTypes = addAdapterForMapTypes;
    }
    
    /**
     * @return the addAdapterForSetTypes
     */
    protected AdditionType getAddAdapterForSetTypes()
    {
      return this.addAdapterForSetTypes;
    }
    
    /**
     * @param addAdapterForSetTypes
     *          the addAdapterForSetTypes to set
     */
    public void setAddAdapterForSetTypes( AdditionType addAdapterForSetTypes )
    {
      this.addAdapterForSetTypes = addAdapterForSetTypes;
    }
    
    /**
     * @return the addAdapterForIterableTypes
     */
    protected AdditionType getAddAdapterForIterableTypes()
    {
      return this.addAdapterForIterableTypes;
    }
    
    /**
     * @param addAdapterForIterableTypes
     *          the addAdapterForIterableTypes to set
     */
    public void setAddAdapterForIterableTypes( AdditionType addAdapterForIterableTypes )
    {
      this.addAdapterForIterableTypes = addAdapterForIterableTypes;
    }
    
    /**
     * @return the addAdapterForPrimitiveTypes
     */
    protected AdditionType getAddAdapterForPrimitiveTypes()
    {
      return this.addAdapterForPrimitiveTypes;
    }
    
    /**
     * @param addAdapterForPrimitiveTypes
     *          the addAdapterForPrimitiveTypes to set
     */
    public void setAddAdapterForPrimitiveTypes( AdditionType addAdapterForPrimitiveTypes )
    {
      this.addAdapterForPrimitiveTypes = addAdapterForPrimitiveTypes;
    }
    
    /**
     * @return the addAdapterForStringType
     */
    protected AdditionType getAddAdapterForStringType()
    {
      return this.addAdapterForStringType;
    }
    
    /**
     * @param addAdapterForStringType
     *          the addAdapterForStringType to set
     */
    public void setAddAdapterForStringType( AdditionType addAdapterForStringType )
    {
      this.addAdapterForStringType = addAdapterForStringType;
    }
    
    /**
     * @return the addAdapterForArrayTypes
     */
    protected AdditionType getAddAdapterForArrayTypes()
    {
      return this.addAdapterForArrayTypes;
    }
    
    /**
     * @param addAdapterForArrayTypes
     *          the addAdapterForArrayTypes to set
     */
    public void setAddAdapterForArrayTypes( AdditionType addAdapterForArrayTypes )
    {
      this.addAdapterForArrayTypes = addAdapterForArrayTypes;
    }
    
    /**
     * @param addAdapterForBigIntegerType
     *          the addAdapterForBigIntegerType to set
     */
    public void setAddAdapterForBigIntegerType( AdditionType addAdapterForBigIntegerType )
    {
      this.addAdapterForBigIntegerType = addAdapterForBigIntegerType;
    }
    
    /**
     * @param addAdapterForBigDecimalType
     *          the addAdapterForBigDecimalType to set
     */
    public void setAddAdapterForBigDecimalType( AdditionType addAdapterForBigDecimalType )
    {
      this.addAdapterForBigDecimalType = addAdapterForBigDecimalType;
    }
    
    /**
     * @return the addAdapterForBigIntegerType
     */
    protected AdditionType getAddAdapterForBigIntegerType()
    {
      return this.addAdapterForBigIntegerType;
    }
    
    /**
     * @return the addAdapterForBigDecimalType
     */
    protected AdditionType getAddAdapterForBigDecimalType()
    {
      return this.addAdapterForBigDecimalType;
    }
    
  }
  
  /**
   * Marker interface for all {@link Adapter} used in combination with the {@link BeanReplicator}<br>
   * <br>
   * An {@link Adapter} is thread safe and can be reused even by multiple {@link Thread}s or multiple {@link BeanReplicator}
   * instances. <br>
   * <br>
   * <small> Any concrete implementation has to implement {@link AdapterInternal} as well.</small>
   * 
   * @see BeanReplicator
   * @author Omnaest
   */
  public interface Adapter
  {
  }
  
  /**
   * Internal extension of the {@link Adapter} interface which provides a {@link Set} of {@link Handler} instances which can be
   * used to copy concrete source objects. <br>
   * <br>
   * {@link AdapterInternal} instances have to be thread safe. This allows to use a single {@link AdapterInternal} instance by
   * multiple {@link Thread}s
   * 
   * @author Omnaest
   */
  public interface AdapterInternal extends Adapter
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * Every {@link Handler} is specialized to handle a set of types. It allows to create a copy of a given source object using
     * {@link #createNewTargetObjectInstance(Class, Object)} if it supports its {@link Class} type returning true for
     * {@link #canHandle(Class)} <br>
     * <br>
     * {@link Handler} instances are created by an {@link AdapterInternal} for each copy request. This ensures that the
     * {@link AdapterInternal} itself can be reused even by multiple {@link Thread}s.
     * 
     * @see #canHandle(Class)
     * @see #createNewTargetObjectInstance(Class, Object)
     * @author Omnaest
     */
    public static interface Handler
    {
      /**
       * Returns true, if the {@link Adapter} can handle the given source object type
       * 
       * @param sourceObjectType
       * @return
       */
      public boolean canHandle( Class<? extends Object> sourceObjectType );
      
      /**
       * Creates a <b>new target object</b> instance and <b>copies</b> all properties of the source object to the target object
       * for which the {@link Adapter} knows how to handle them.
       * 
       * @param sourceObjectType
       * @param sourceObject
       * @return new instance
       */
      public Object createNewTargetObjectInstance( Class<?> sourceObjectType, Object sourceObject );
      
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns a {@link Set} of new {@link Handler} instances generated by the {@link Adapter}
     * 
     * @param transitiveBeanReplicationInvocationHandler
     * @return this
     */
    public Set<AdapterInternal.Handler> newHandlerSet( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler );
    
  }
  
  /**
   * Callback for transitive replication invocations through {@link Adapter} instances
   * 
   * @author Omnaest
   */
  public static interface TransitiveBeanReplicationInvocationHandler
  {
    /**
     * Replicates the given {@link Object}
     * 
     * @param object
     * @return
     */
    public Object replicate( Object object );
    
    /**
     * Notifies th {@link BeanReplicator} of unhandled bean properties
     * 
     * @param beanPropertyAccessors
     */
    public void notifyOfUnhandledProperties( BeanPropertyAccessors<Object> beanPropertyAccessors );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see BeanReplicator
   * @see Adapter
   * @param adapters
   */
  public BeanReplicator( Adapter... adapters )
  {
    this( null, adapters );
  }
  
  /**
   * @see BeanReplicator
   * @see Configuration
   * @see Adapter
   * @param configuration
   * @param adapters
   */
  public BeanReplicator( Configuration configuration, Adapter... adapters )
  {
    super();
    this.configuration = ObjectUtils.defaultIfNull( configuration, new Configuration() );
    this.adapter = BeanReplicator.initializeAdapterUsingConfiguration( this.configuration, adapters );
  }
  
  /**
   * @param configuration
   * @return {@link AdapterInternal}
   */
  private static AdapterInternal initializeAdapterUsingConfiguration( Configuration configuration, Adapter[] adapters )
  {
    //
    AdapterInternal retval = new AdapterComposite( adapters );
    
    //
    if ( configuration != null )
    {
      //
      final Map<AdditionType, Set<AdapterInternal>> additionTypeToAdapterInternalSetInitializedMap = MapUtils.initializedEnumMap( AdditionType.class,
                                                                                                                                  new LinkedHashSetFactory<AdapterInternal>() );
      
      //      
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForPrimitiveTypes();
        putToAdapterMap( additionType, new AdapterForPrimitiveTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForStringType();
        putToAdapterMap( additionType, new AdapterForStringType(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForBigIntegerType();
        putToAdapterMap( additionType, new AdapterForBigIntegerType(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForBigDecimalType();
        putToAdapterMap( additionType, new AdapterForBigDecimalType(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForArrayTypes();
        putToAdapterMap( additionType, new AdapterForArrayTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForListTypes();
        putToAdapterMap( additionType, new AdapterForListTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForSetTypes();
        putToAdapterMap( additionType, new AdapterForSetTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForMapTypes();
        putToAdapterMap( additionType, new AdapterForMapTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      {
        //
        final AdditionType additionType = configuration.getAddAdapterForIterableTypes();
        putToAdapterMap( additionType, new AdapterForIterableTypes(), additionTypeToAdapterInternalSetInitializedMap );
      }
      
      //
      for ( AdditionType additionType : additionTypeToAdapterInternalSetInitializedMap.keySet() )
      {
        //
        final Set<AdapterInternal> additionalAdapterInternalSet = additionTypeToAdapterInternalSetInitializedMap.get( additionType );
        retval = appendOrPrependAdapter( additionalAdapterInternalSet, additionType, retval );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * @param additionType
   * @param adapterInternal
   * @param additionTypeToAdapterInternalSetInitializedMap
   */
  private static void putToAdapterMap( final AdditionType additionType,
                                       AdapterInternal adapterInternal,
                                       final Map<AdditionType, Set<AdapterInternal>> additionTypeToAdapterInternalSetInitializedMap )
  {
    if ( additionType != null )
    {
      additionTypeToAdapterInternalSetInitializedMap.get( additionType ).add( adapterInternal );
    }
  }
  
  /**
   * @param additionalAdapterInternalSet
   * @param additionType
   * @param adapterInternalOriginal
   * @return
   */
  private static AdapterInternal appendOrPrependAdapter( Set<AdapterInternal> additionalAdapterInternalSet,
                                                         final AdditionType additionType,
                                                         AdapterInternal adapterInternalOriginal )
  {
    //
    AdapterInternal retval = adapterInternalOriginal;
    
    //
    if ( AdditionType.APPEND.equals( additionType ) )
    {
      retval = new AdapterComposite( adapterInternalOriginal,
                                     new AdapterComposite( additionalAdapterInternalSet.toArray( new AdapterInternal[0] ) ) );
    }
    else if ( AdditionType.PREPEND.equals( additionType ) )
    {
      retval = new AdapterComposite( new AdapterComposite( additionalAdapterInternalSet.toArray( new AdapterInternal[0] ) ),
                                     adapterInternalOriginal );
    }
    
    //
    return retval;
  }
  
  /**
   * Copies the given Java bean transitively
   * 
   * @param bean
   * @return
   */
  public <R> R copy( final Object bean )
  {
    //
    this.unhandledBeanPropertyAccessors.clear();
    
    //
    final Map<Object, Object> sourceObjectToTargetObjectMap = new IdentityHashMap<Object, Object>();
    return this.<R> copy( bean, sourceObjectToTargetObjectMap );
  }
  
  /**
   * @param bean
   * @param sourceObjectToTargetObjectMap
   * @return
   */
  @SuppressWarnings("unchecked")
  protected <R> R copy( final Object bean, final Map<Object, Object> sourceObjectToTargetObjectMap )
  {
    //
    R retval = null;
    
    //
    if ( bean != null )
    {
      //
      final Handler matchingAdapterHandler = resolveMatchingAdapterHandler( sourceObjectToTargetObjectMap, bean );
      if ( matchingAdapterHandler != null )
      {
        //
        final Class<? extends Object> sourceObjectType = bean.getClass();
        Object targetObject = null;
        if ( sourceObjectToTargetObjectMap.containsKey( bean ) )
        {
          targetObject = sourceObjectToTargetObjectMap.get( bean );
        }
        else
        {
          targetObject = matchingAdapterHandler.createNewTargetObjectInstance( sourceObjectType, bean );
          sourceObjectToTargetObjectMap.put( bean, targetObject );
        }
        
        //
        try
        {
          retval = (R) targetObject;
        }
        catch ( Exception e )
        {
          Assert.fails( "Generated instance object can not be cast to generic return type. (" + targetObject + ")", e );
        }
      }
    }
    
    //
    retval = (R) sourceObjectToTargetObjectMap.get( bean );
    
    //
    return retval;
  }
  
  /**
   * @param sourceObjectToTargetObjectMap
   * @param sourceObject
   * @return
   */
  private Handler resolveMatchingAdapterHandler( final Map<Object, Object> sourceObjectToTargetObjectMap,
                                                 final Object sourceObject )
  {
    //
    Handler matchingAdapterHandler = null;
    
    //
    if ( sourceObject != null )
    {
      //
      final Class<? extends Object> sourceObjectType = sourceObject.getClass();
      final TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler = new TransitiveBeanReplicationInvocationHandler()
      {
        @Override
        public Object replicate( Object bean )
        {
          return copy( bean, sourceObjectToTargetObjectMap );
        }
        
        @Override
        public void notifyOfUnhandledProperties( BeanPropertyAccessors<Object> beanPropertyAccessors )
        {
          //
          BeanReplicator.this.unhandledBeanPropertyAccessors.addAll( beanPropertyAccessors );
          
          //
          final boolean isFailingOnUnhandledProperties = BeanReplicator.this.configuration.isFailingOnUnhandledProperties();
          if ( isFailingOnUnhandledProperties )
          {
            Assert.fails( "There are unhandled source object properties, but unhandled properties are not allowed: "
                          + beanPropertyAccessors );
          }
          
        }
      };
      
      //
      final Set<Handler> handlerSet = this.adapter.newHandlerSet( transitiveBeanReplicationInvocationHandler );
      for ( Handler handler : handlerSet )
      {
        if ( handler.canHandle( sourceObjectType ) )
        {
          matchingAdapterHandler = handler;
          break;
        }
      }
    }
    
    return matchingAdapterHandler;
  }
  
  /**
   * Clones a given source object. This differs from {@link #copy(Object)} in the way, that the returned {@link Object} will have
   * the same type as the given one. The {@link #copy(Object)} method uses the {@link Adapter} instances which allow to generate
   * different type instances containing the same property values as the original given {@link Object} instance.<br>
   * <br>
   * 
   * @see BeanReplicator
   * @see #copy(Object)
   * @param sourceObject
   * @return new cloned instance
   */
  public <E> E clone( E sourceObject )
  {
    //
    E retval = null;
    
    try
    {
      //
      final Cloner cloner = new Cloner();
      retval = cloner.deepClone( sourceObject );
    }
    catch ( Exception e )
    {
      Assert.fails( "Cloning of " + sourceObject + " failed", e );
    }
    
    //
    return retval;
  }
  
  /**
   * @return the unhandledBeanPropertyAccessors
   */
  public BeanPropertyAccessors<?> getUnhandledBeanPropertyAccessors()
  {
    return this.unhandledBeanPropertyAccessors;
  }
}
