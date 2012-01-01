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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal.Handler;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.proxy.BeanProperty;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterBeanPropertyAccessorToProperty;
import org.omnaest.utils.structure.element.converter.ElementConverterRegistration;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor.TraversalConfiguration;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTree;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNavigator;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode.ObjectModel;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * The {@link BeanReplicator} allows to make a deep copy of an {@link Object} graph
 * 
 * @author Omnaest
 */
public class BeanReplicator
{
  /* ********************************************** Variables ********************************************** */
  protected final Set<AdapterInternal> adapterSet = new LinkedHashSet<AdapterInternal>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Marker interface for all {@link Adapter} used in combination with the {@link BeanReplicator}
   * 
   * @see BeanReplicator
   * @see #canHandle(Class)
   * @see #createNewTargetObjectInstance()
   * @see #copyPropertiesAndReturnBeanPropertyAccessorsForUnhandledProperties(Object, Object)
   * @author Omnaest
   */
  public static interface Adapter
  {
  }
  
  /**
   * Internal extension of the {@link Adapter} interface
   * 
   * @author Omnaest
   */
  public static interface AdapterInternal extends Adapter
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
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
       * Creates a new target object instance
       * 
       * @see #copyPropertiesAndReturnBeanPropertyAccessorsForUnhandledProperties(Object, Object)
       * @return
       */
      public Object createNewTargetObjectInstance();
      
      /**
       * Copies all properties of the source object to the target object for which the {@link Adapter} knows how to handle them.
       * For all other properties which are present within the source type but not handled by the {@link Adapter}
       * {@link BeanPropertyAccessors} will be returned.
       * 
       * @see #createNewTargetObjectInstance()
       * @param sourceObject
       * @param targetObject
       * @return
       */
      public BeanPropertyAccessors<Object> copyPropertiesAndReturnBeanPropertyAccessorsForUnhandledProperties( Object sourceObject,
                                                                                                               Object targetObject );
      
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns the {@link Handler} of the {@link Adapter}
     * 
     * @param transitiveBeanReplicationInvocationHandler
     *          TODO
     * @return
     */
    public Handler newHandler( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler );
  }
  
  /**
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static abstract class AdapterDeclarableBindings<FROM, TO> implements AdapterInternal
  {
    /* ********************************************** Variables ********************************************** */
    private final BeanProperty                                                                                      beanProperty;
    private final Map<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>> bindingMap               = new LinkedHashMap<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>>();
    private ElementConverterResolver                                                                                elementConverterResolver = new ElementConverterResolver();
    private final Class<FROM>                                                                                       sourceType;
    private final Class<TO>                                                                                         targetType;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see AdapterDeclarableBindings
     * @param sourceType
     * @param targetType
     */
    public AdapterDeclarableBindings( Class<FROM> sourceType, Class<TO> targetType )
    {
      super();
      this.sourceType = sourceType;
      this.targetType = targetType;
      this.beanProperty = new BeanProperty();
      
      final FROM source = this.beanProperty.newInstanceOfCapturedType( sourceType );
      final TO target = this.beanProperty.newInstanceOfCapturedType( targetType );
      
      //
      Assert.isNotNull( "source and target type have to have a default constructor", source, target );
      
      //
      this.declareBindings( source, target );
    }
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see #resolveElementConverterFor(Class, Class)
     * @author Omnaest
     */
    protected static class ElementConverterResolver
    {
      
      /**
       * Resolves an {@link ElementConverter} for a given source and target type
       * 
       * @param sourceType
       * @param targetType
       * @return
       */
      public <FROM, TO> ElementConverter<FROM, TO> resolveElementConverterFor( Class<FROM> sourceType, Class<TO> targetType )
      {
        return ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      }
    }
    
    /**
     * Marker {@link ElementConverter} which indicates that a {@link TransitiveBeanReplicationInvocationHandler} should be used to
     * convert a given {@link Object}
     * 
     * @author Omnaest
     */
    protected static class ElementConverterOngoingBeanReplication implements ElementConverter<Object, Object>
    {
      @Override
      public Object convert( Object element )
      {
        throw new UnsupportedOperationException();
      }
    }
    
    /**
     * @author Omnaest
     */
    protected class BindingMultipleTo
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @see BindingMultipleTo
       * @param beanPropertyAccessorsFrom
       */
      protected BindingMultipleTo( BeanPropertyAccessors<Object> beanPropertyAccessorsFrom )
      {
        super();
        this.beanPropertyAccessorsFrom = beanPropertyAccessorsFrom;
      }
      
      /**
       * @param to
       * @return {@link BindingMultipleToSingleUsing}
       */
      public BindingMultipleToSingleUsing to( Object to )
      {
        //
        final BeanPropertyAccessor<Object> beanPropertyAccessorTo = AdapterDeclarableBindings.this.beanProperty.accessor.of( to );
        
        //
        return new BindingMultipleToSingleUsing( this.beanPropertyAccessorsFrom, beanPropertyAccessorTo );
      }
    }
    
    /**
     * @author Omnaest
     */
    protected class BindingSingleTo
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessor<Object> beanPropertyAccessorFrom;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @see BindingSingleTo
       * @param beanPropertyAccessorFrom
       */
      protected BindingSingleTo( BeanPropertyAccessor<Object> beanPropertyAccessorFrom )
      {
        super();
        this.beanPropertyAccessorFrom = beanPropertyAccessorFrom;
      }
      
      /**
       * @param to
       * @return
       */
      public BindingSingleToSingleUsing to( Object to )
      {
        //
        final BeanPropertyAccessor<Object> beanPropertyAccessorTo = AdapterDeclarableBindings.this.beanProperty.accessor.of( to );
        return new BindingSingleToSingleUsing( this.beanPropertyAccessorFrom, beanPropertyAccessorTo );
      }
      
      /**
       * @param to
       * @return
       */
      public BindingSingleToMultipleUsing to( Object... to )
      {
        //
        final BeanPropertyAccessors<Object> beanPropertyAccessorsTo = AdapterDeclarableBindings.this.beanProperty.accessor.of( to );
        return new BindingSingleToMultipleUsing( this.beanPropertyAccessorFrom, beanPropertyAccessorsTo );
      }
    }
    
    /**
     * @author Omnaest
     */
    protected class BindingSingleToMultipleUsing
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessor<Object>  beanPropertyAccessorFrom;
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsTo;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @see BindingSingleToMultipleUsing
       * @param beanPropertyAccessorFrom
       * @param beanPropertyAccessorsTo
       */
      protected BindingSingleToMultipleUsing( BeanPropertyAccessor<Object> beanPropertyAccessorFrom,
                                              BeanPropertyAccessors<Object> beanPropertyAccessorsTo )
      {
        super();
        this.beanPropertyAccessorFrom = beanPropertyAccessorFrom;
        this.beanPropertyAccessorsTo = beanPropertyAccessorsTo;
      }
      
      /**
       * @param elementConverter
       */
      public void using( ElementConverter<?, ?> elementConverter )
      {
        //
        Assert.isNotNull( "elementConverter must no be null for binding", elementConverter );
        
        //
        for ( BeanPropertyAccessor<Object> beanPropertyAccessorTo : this.beanPropertyAccessorsTo )
        {
          TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>> key = new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                                               this.beanPropertyAccessorFrom,
                                                                                                                                                               beanPropertyAccessorTo );
          ElementConverter<?, ?> value = elementConverter;
          AdapterDeclarableBindings.this.bindingMap.put( key, value );
        }
      }
      
      /**
       * Tries to autodetect an {@link ElementConverter} based on the return type of the property getter at the source type and
       * the parameter type of the setter at the target type.<br>
       * <br>
       * 
       * @throws IllegalArgumentException
       *           if autodetection fails
       */
      public void usingAutodetectedElementConverter()
      {
        //
        final Class<?> sourceType = this.beanPropertyAccessorFrom.getDeclaringPropertyType();
        for ( BeanPropertyAccessor<Object> beanPropertyAccessorTo : this.beanPropertyAccessorsTo )
        {
          //
          final Class<?> targetType = beanPropertyAccessorTo.getDeclaringPropertyType();
          
          //
          final ElementConverter<?, ?> elementConverter = AdapterDeclarableBindings.this.elementConverterResolver.resolveElementConverterFor( sourceType,
                                                                                                                                              targetType );
          
          //
          AdapterDeclarableBindings.this.bindingMap.put( new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                   this.beanPropertyAccessorFrom,
                                                                                                                                   beanPropertyAccessorTo ),
                                                         elementConverter );
          
          //
          Assert.isNotNull( elementConverter, "No element converter could be autodetected for source type: " + sourceType
                                              + " and target type: " + targetType );
        }
      }
      
    }
    
    /**
     * @author Omnaest
     */
    protected class BindingSingleToSingleUsing
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessor<Object> beanPropertyAccessorFrom;
      private final BeanPropertyAccessor<Object> beanPropertyAccessorTo;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @see BindingSingleToSingleUsing
       * @param beanPropertyAccessorFrom
       * @param beanPropertyAccessorsTo
       */
      protected BindingSingleToSingleUsing( BeanPropertyAccessor<Object> beanPropertyAccessorFrom,
                                            BeanPropertyAccessor<Object> beanPropertyAccessorTo )
      {
        super();
        this.beanPropertyAccessorFrom = beanPropertyAccessorFrom;
        this.beanPropertyAccessorTo = beanPropertyAccessorTo;
      }
      
      /**
       * @param elementConverter
       */
      public void using( ElementConverter<?, ?> elementConverter )
      {
        //
        Assert.isNotNull( "elementConverter must no be null for binding", elementConverter );
        
        //
        TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>> key = new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                                             this.beanPropertyAccessorFrom,
                                                                                                                                                             this.beanPropertyAccessorTo );
        ElementConverter<?, ?> value = elementConverter;
        AdapterDeclarableBindings.this.bindingMap.put( key, value );
      }
      
      /**
       * Tries to autodetect an {@link ElementConverter} based on the return type of the property getter at the source type and
       * the parameter type of the setter at the target type.<br>
       * <br>
       * 
       * @throws IllegalArgumentException
       *           if autodetection fails
       */
      public void usingAutodetectedElementConverter()
      {
        //
        final Class<?> sourceType = this.beanPropertyAccessorFrom.getDeclaringPropertyType();
        final Class<?> targetType = this.beanPropertyAccessorTo.getDeclaringPropertyType();
        
        //
        final ElementConverter<?, ?> elementConverter = AdapterDeclarableBindings.this.elementConverterResolver.resolveElementConverterFor( sourceType,
                                                                                                                                            targetType );
        
        //
        AdapterDeclarableBindings.this.bindingMap.put( new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                 this.beanPropertyAccessorFrom,
                                                                                                                                 this.beanPropertyAccessorTo ),
                                                       elementConverter );
        
        //
        Assert.isNotNull( elementConverter, "No element converter could be autodetected for source type: " + sourceType
                                            + " and target type: " + targetType );
        
      }
      
      /**
       * Applies a transitive bean replication process to the selected bean properties
       */
      public void usingOngoingBeanReplication()
      {
        //
        final ElementConverter<?, ?> elementConverter = new ElementConverterOngoingBeanReplication();
        
        //
        AdapterDeclarableBindings.this.bindingMap.put( new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                 this.beanPropertyAccessorFrom,
                                                                                                                                 this.beanPropertyAccessorTo ),
                                                       elementConverter );
      }
      
    }
    
    /**
     * @author Omnaest
     */
    protected class BindingMultipleToSingleUsing
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom;
      private final BeanPropertyAccessor<Object>  beanPropertyAccessorTo;
      
      /* ********************************************** Methods ********************************************** */
      /**
       * @see BindingMultipleToSingleUsing
       * @param beanPropertyAccessorsFrom
       * @param beanPropertyAccessorTo
       */
      protected BindingMultipleToSingleUsing( BeanPropertyAccessors<Object> beanPropertyAccessorsFrom,
                                              BeanPropertyAccessor<Object> beanPropertyAccessorTo )
      {
        super();
        this.beanPropertyAccessorsFrom = beanPropertyAccessorsFrom;
        this.beanPropertyAccessorTo = beanPropertyAccessorTo;
      }
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @param elementConverter
       */
      public void using( ElementConverter<?, ?> elementConverter )
      {
        //
        Assert.isNotNull( "elementConverter must no be null for binding", elementConverter );
        
        //
        for ( BeanPropertyAccessor<Object> beanPropertyAccessorFrom : this.beanPropertyAccessorsFrom )
        {
          TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>> key = new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                                               beanPropertyAccessorFrom,
                                                                                                                                                               this.beanPropertyAccessorTo );
          ElementConverter<?, ?> value = elementConverter;
          AdapterDeclarableBindings.this.bindingMap.put( key, value );
        }
      }
      
      /**
       * Tries to autodetect an {@link ElementConverter} based on the return type of the property getter at the source type and
       * the parameter type of the setter at the target type.<br>
       * <br>
       * 
       * @throws IllegalArgumentException
       *           if autodetection fails
       */
      public void usingAutodetectedElementConverter()
      {
        //
        final Class<?> targetType = this.beanPropertyAccessorTo.getDeclaringPropertyType();
        for ( BeanPropertyAccessor<Object> beanPropertyAccessorFrom : this.beanPropertyAccessorsFrom )
        {
          //
          final Class<?> sourceType = beanPropertyAccessorFrom.getDeclaringPropertyType();
          
          //
          final ElementConverter<?, ?> elementConverter = AdapterDeclarableBindings.this.elementConverterResolver.resolveElementConverterFor( sourceType,
                                                                                                                                              targetType );
          
          //
          AdapterDeclarableBindings.this.bindingMap.put( new TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>(
                                                                                                                                   beanPropertyAccessorFrom,
                                                                                                                                   this.beanPropertyAccessorTo ),
                                                         elementConverter );
          
          //
          Assert.isNotNull( elementConverter, "No element converter could be autodetected for source type: " + sourceType
                                              + " and target type: " + targetType );
        }
      }
    }
    
    /**
     * {@link Handler} implementation for {@link AdapterDeclarableBindings}
     * 
     * @author Omnaest
     */
    @SuppressWarnings("hiding")
    private class HandlerForAdapterDeclarableBindings implements Handler
    {
      /* ********************************************** Variables ********************************************** */
      private final Class<FROM>                                                                                       sourceType;
      private final Class<TO>                                                                                         targetType;
      private final Map<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>> bindingMap;
      private final TransitiveBeanReplicationInvocationHandler                                                        transitiveBeanReplicationInvocationHandler;
      
      /* ********************************************** Methods ********************************************** */
      /**
       * @see HandlerForAdapterDeclarableBindings
       * @param sourceType
       * @param targetType
       * @param bindingMap
       * @param transitiveBeanReplicationInvocationHandler
       */
      public HandlerForAdapterDeclarableBindings( Class<FROM> sourceType,
                                                  Class<TO> targetType,
                                                  Map<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>> bindingMap,
                                                  TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
      {
        super();
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.bindingMap = bindingMap;
        this.transitiveBeanReplicationInvocationHandler = transitiveBeanReplicationInvocationHandler;
      }
      
      /**
       * Returns true if the current {@link AdapterDeclarableBindings} can handle the given source type
       * 
       * @param sourceObjectType
       * @return
       */
      @Override
      public boolean canHandle( Class<? extends Object> sourceObjectType )
      {
        return sourceObjectType != null && sourceObjectType.isAssignableFrom( this.sourceType );
      }
      
      @Override
      public Object createNewTargetObjectInstance()
      {
        return ReflectionUtils.createInstanceOf( this.targetType );
      }
      
      @Override
      public BeanPropertyAccessors<Object> copyPropertiesAndReturnBeanPropertyAccessorsForUnhandledProperties( Object sourceObject,
                                                                                                               Object targetObject )
      {
        //
        final Collection<BeanPropertyAccessor<Object>> beanPropertyAccessorCollection = new ArrayList<BeanPropertyAccessor<Object>>();
        
        //
        final Set<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>> sourceAndTargetBindingSet = this.bindingMap.keySet();
        for ( TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>> sourceAndTargetBinding : sourceAndTargetBindingSet )
        {
          //
          @SuppressWarnings("unchecked")
          final ElementConverter<Object, Object> elementConverter = (ElementConverter<Object, Object>) this.bindingMap.get( sourceAndTargetBinding );
          final BeanPropertyAccessor<Object> beanPropertyAccessorSource = sourceAndTargetBinding.getValueFirst();
          final BeanPropertyAccessor<Object> beanPropertyAccessorTarget = sourceAndTargetBinding.getValueSecond();
          
          //
          Object propertyValueSource = beanPropertyAccessorSource.getPropertyValue( sourceObject );
          Object propertyValueTarget = null;
          {
            //
            if ( elementConverter instanceof ElementConverterOngoingBeanReplication )
            {
              propertyValueTarget = this.transitiveBeanReplicationInvocationHandler.replicate( propertyValueSource );
            }
            else
            {
              propertyValueTarget = elementConverter.convert( propertyValueSource );
            }
          }
          beanPropertyAccessorTarget.setPropertyValue( targetObject, propertyValueTarget );
        }
        
        // 
        return new BeanPropertyAccessors<Object>( beanPropertyAccessorCollection );
      }
    }
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Binds a single property to one or many others
     * 
     * @param from
     * @return
     */
    protected BindingSingleTo bind( Object from )
    {
      final BeanPropertyAccessor<Object> beanPropertyAccessorFrom = this.beanProperty.accessor.of( from );
      Assert.isNotNull( beanPropertyAccessorFrom, "There was no property method of the called source proxy" );
      return new BindingSingleTo( beanPropertyAccessorFrom );
    }
    
    /**
     * Binds multiple properties to one other
     * 
     * @param from
     * @return
     */
    protected BindingMultipleTo bind( Object... from )
    {
      final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom = this.beanProperty.accessor.of( from );
      Assert.isFalse( beanPropertyAccessorsFrom.isEmpty(), "There was no property method of the called source proxy" );
      return new BindingMultipleTo( beanPropertyAccessorsFrom );
    }
    
    /**
     * @param source
     * @param target
     */
    public abstract void declareBindings( FROM source, TO target );
    
    @Override
    public Handler newHandler( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
    {
      return new HandlerForAdapterDeclarableBindings( this.sourceType, this.targetType, this.bindingMap,
                                                      transitiveBeanReplicationInvocationHandler );
    }
    
  }
  
  /**
   * Callback for transitive replication invocations through {@link Adapter} instances
   * 
   * @author Omnaest
   */
  protected static interface TransitiveBeanReplicationInvocationHandler
  {
    /**
     * Replicates the given {@link Object}
     * 
     * @param object
     * @return
     */
    public Object replicate( Object object );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see BeanReplicator
   * @param adapters
   */
  public BeanReplicator( AdapterDeclarableBindings<?, ?>... adapters )
  {
    super();
    this.adapterSet.addAll( Arrays.asList( adapters ) );
  }
  
  /**
   * @see BeanReplicator
   */
  public BeanReplicator()
  {
    super();
  }
  
  /**
   * Copies the given Java bean transitively
   * 
   * @param bean
   * @return
   */
  public <R> R copy( final Object bean )
  {
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
      final ObjectTreeNavigator treeNavigator = new ObjectTreeNavigator( bean );
      
      final TreeNodeVisitor<ObjectTree, ObjectTreeNode> treeNodeVisitor = new TreeNodeVisitor<ObjectTree, ObjectTreeNode>()
      {
        /* ********************************************** Variables ********************************************** */
        private Set<String> unhandledBeanPropertyNameSet = new HashSet<String>();
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public TraversalControl visit( ObjectTreeNode treeNode, TreeNavigator<ObjectTree, ObjectTreeNode> treeNavigator )
        {
          //
          TraversalControl traversalControl = TraversalControl.SKIP_CHILDREN;
          
          //
          final ObjectModel objectModel = treeNode.getModel();
          final Object sourceObject = objectModel.getObject();
          
          //
          Object targetObject = null;
          if ( sourceObjectToTargetObjectMap.containsKey( sourceObject ) )
          {
            targetObject = sourceObjectToTargetObjectMap.get( sourceObject );
          }
          else
          {
            //
            boolean isParentNode = sourceObject == bean;
            if ( isParentNode )
            {
              //
              Handler matchingAdapterHandler = null;
              final Class<? extends Object> sourceObjectType = sourceObject.getClass();
              final TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler = new TransitiveBeanReplicationInvocationHandler()
              {
                @Override
                public Object replicate( Object bean )
                {
                  return copy( bean, sourceObjectToTargetObjectMap );
                }
              };
              for ( AdapterInternal adapter : BeanReplicator.this.adapterSet )
              {
                final Handler handler = adapter.newHandler( transitiveBeanReplicationInvocationHandler );
                if ( handler.canHandle( sourceObjectType ) )
                {
                  matchingAdapterHandler = handler;
                  break;
                }
              }
              
              //
              if ( matchingAdapterHandler != null )
              {
                //
                targetObject = matchingAdapterHandler.createNewTargetObjectInstance();
                
                //
                final BeanPropertyAccessors<Object> beanPropertyAccessorsForUnhandledProperties = matchingAdapterHandler.copyPropertiesAndReturnBeanPropertyAccessorsForUnhandledProperties( sourceObject,
                                                                                                                                                                                             targetObject );
                
                //
                this.unhandledBeanPropertyNameSet.addAll( SetUtils.convert( beanPropertyAccessorsForUnhandledProperties,
                                                                            new ElementConverterBeanPropertyAccessorToProperty<Object>() ) );
                
                //
                traversalControl = TraversalControl.GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES;
              }
            }
            else
            {
              //
              final String propertyName = objectModel.getPropertyName();
              if ( this.unhandledBeanPropertyNameSet.contains( propertyName ) )
              {
                //
                targetObject = copy( sourceObject, sourceObjectToTargetObjectMap );
              }
            }
            
            //
            sourceObjectToTargetObjectMap.put( sourceObject, targetObject );
          }
          
          // 
          return traversalControl;
        }
      };
      boolean includingAlreadyTraversedNodes = true;
      boolean includingCurrentNode = true;
      boolean includingChildren = true;
      treeNavigator.traverse( new TraversalConfiguration( includingCurrentNode, includingAlreadyTraversedNodes, includingChildren ),
                              treeNodeVisitor );
    }
    
    //
    retval = (R) sourceObjectToTargetObjectMap.get( bean );
    
    //
    return retval;
  }
}
