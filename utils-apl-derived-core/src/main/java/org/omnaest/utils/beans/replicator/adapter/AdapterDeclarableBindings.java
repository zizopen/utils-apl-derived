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
package org.omnaest.utils.beans.replicator.adapter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.proxy.BeanProperty;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterRegistration;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * The abstract {@link AdapterDeclarableBindings} allows to declare bindings based on capture instances of the given types. To do
 * this the {@link #declareBindings(Object, Object)} method has to be implemented like following example:<br>
 * 
 * <pre>
 * final AdapterDeclarableBindings&lt;TestClass, TestClassDTO&gt; adapter;
 * adapter = new AdapterDeclarableBindings&lt;TestClass, TestClassDTO&gt;( TestClass.class, TestClassDTO.class )
 * {
 *   &#064;Override
 *   public void declareBindings( TestClass source, TestClassDTO target )
 *   {
 *     this.bind( source.getFieldString() ).to( target.getFieldInteger() ).using( new ElementConverterStringToInteger() );
 *     this.bind( source.getFieldInteger() ).to( target.getFieldString() ).usingAutodetectedElementConverter();
 *     this.bind( source.getTestClassSub() ).to( target.getTestClassSub() ).usingOngoingBeanReplication();
 *   }
 * };
 * </pre>
 * 
 * <br>
 * This allows a faster replication (less than 1 ms per call), since the reflection based adapter has only to be created once
 * (high cost about 200 ms).
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public abstract class AdapterDeclarableBindings<FROM, TO> implements AdapterInternal
{
  /* ********************************************** Variables ********************************************** */
  private final BeanProperty                                                                                      beanProperty;
  private final Map<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>> bindingMap               = new LinkedHashMap<TupleTwo<BeanPropertyAccessor<Object>, BeanPropertyAccessor<Object>>, ElementConverter<?, ?>>();
  private AdapterDeclarableBindings.ElementConverterResolver                                                      elementConverterResolver = new ElementConverterResolver();
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
     * Tries to autodetect an {@link ElementConverter} based on the return type of the property getter at the source type and the
     * parameter type of the setter at the target type.<br>
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
    public Object createNewTargetObjectInstance( Class<?> sourceObjectType, Object sourceObject )
    {
      //
      final Object retval = ReflectionUtils.newInstanceOf( this.targetType );
      
      //
      this.copyProperties( sourceObject, retval );
      
      //
      return retval;
    }
    
    public void copyProperties( Object sourceObject, Object targetObject )
    {
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
          if ( elementConverter instanceof AdapterDeclarableBindings.ElementConverterOngoingBeanReplication )
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
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "HandlerForAdapterDeclarableBindings [sourceType=" );
      builder.append( this.sourceType );
      builder.append( ", targetType=" );
      builder.append( this.targetType );
      builder.append( ", bindingMap=" );
      builder.append( this.bindingMap );
      builder.append( ", transitiveBeanReplicationInvocationHandler=" );
      builder.append( this.transitiveBeanReplicationInvocationHandler );
      builder.append( "]" );
      return builder.toString();
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
   * @param source
   * @param target
   */
  public abstract void declareBindings( FROM source, TO target );
  
  @Override
  public Set<Handler> newHandlerSet( TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    return SetUtils.valueOf( (Handler) new HandlerForAdapterDeclarableBindings( this.sourceType, this.targetType,
                                                                                this.bindingMap,
                                                                                transitiveBeanReplicationInvocationHandler ) );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AdapterDeclarableBindings [beanProperty=" );
    builder.append( this.beanProperty );
    builder.append( ", bindingMap=" );
    builder.append( this.bindingMap );
    builder.append( ", elementConverterResolver=" );
    builder.append( this.elementConverterResolver );
    builder.append( ", sourceType=" );
    builder.append( this.sourceType );
    builder.append( ", targetType=" );
    builder.append( this.targetType );
    builder.append( "]" );
    return builder.toString();
  }
  
}
