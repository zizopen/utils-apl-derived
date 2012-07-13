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
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;

/**
 * A {@link BeanReplicator} allows to {@link #copy(Object, Object)} or {@link #clone(Object)} instances from one type to another
 * type. <br>
 * <br>
 * Copy example:
 * 
 * <pre>
 * {
 *   BeanReplicator&lt;BeanFrom, BeanTo&gt; beanReplicator = new BeanReplicator&lt;BeanFrom, BeanTo&gt;( BeanFrom.class, BeanTo.class );
 *   final BeanFrom simpleBean = new BeanFrom();
 *   final TestSimpleBeanTo clone = new TestSimpleBeanTo();
 *   beanReplicator.copy( simpleBean, clone );
 * }
 * </pre>
 * 
 * Clone example:
 * 
 * <pre>
 * {
 *   BeanReplicator&lt;BeanFrom, BeanTo&gt; beanReplicator = new BeanReplicator&lt;BeanFrom, BeanTo&gt;( BeanFrom.class, BeanTo.class );
 *   final BeanFrom simpleBean = new BeanFrom();
 *   final BeanTo clone = beanReplicator.clone( simpleBean );
 * }
 * </pre>
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class BeanReplicator<FROM, TO> implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                  serialVersionUID = -5403362205184966835L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<FROM>                  sourceType;
  private final Class<TO>                    targetType;
  private final FactoryResolver              factoryResolver;
  private final InstanceAccessorResolver     instanceAccessorResolver;
  private final PreservedTypeInstanceManager preservedTypeInstanceManager;
  private final TypeToTypeMappingManager     typeToTypeMappingManager;
  private final ConverterPipeManager         converterPipeManager;
  private final ExceptionHandlerDelegate     exceptionHandler = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  private final AtomicBoolean                hasCopiedOnce    = new AtomicBoolean( false );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * The {@link BeanReplicator.PipeBuilder} allows to construct a pipe from a given type to another over one or multiple
   * {@link ElementConverter} instances.
   * 
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static interface PipeBuilder<FROM, TO>
  {
    /**
     * Adds an additional {@link ElementConverter} to the converter pipe
     * 
     * @param elementConverter
     *          {@link ElementConverter}
     * @return
     */
    public <OVER> PipeBuilder<FROM, OVER> over( ElementConverter<FROM, OVER> elementConverter );
    
    /**
     * Closes the converter pipe and adds it to the {@link BeanReplicator}
     * 
     * @param typeTo
     */
    public void to( Class<TO> typeTo );
  }
  
  /**
   * @author Omnaest
   */
  public static interface ConverterPipeDeclarer extends Serializable
  {
    /**
     * Allows to declare a direct {@link ElementConverter} pipe from one type to another type. <br>
     * <br>
     * Any copy action invoked for the source and target type pair will use this chain of one or more {@link ElementConverter}s to
     * directly create the target property instances. Any generated instance will not be processed further in any way.
     * 
     * @param typeFrom
     * @return {@link BeanReplicator.PipeBuilder}
     */
    public <FROM> PipeBuilder<FROM, FROM> addConverterPipeFrom( Class<FROM> typeFrom );
  }
  
  /**
   * The {@link DeclarationSupport} allows to add e.g. type to type or property name mappings.
   * 
   * @author Omnaest
   */
  public static interface DeclarationSupport extends TypeToTypeMappingDeclarer, ConverterPipeDeclarer
  {
  }
  
  /**
   * A {@link Declaration} allows to declare several kinds of mappings a {@link BeanReplicator} should rely on.<br>
   * <br>
   * It is possible to declare subsequent {@link Declaration}s. They will be treated as one large {@link Declaration}. <br>
   * <br>
   * It is only possible to declare mappings as long as no {@link BeanReplicator#copy(Object, Object)} or as no
   * {@link BeanReplicator#clone(Object)} method has been called yet.
   * 
   * @see DeclarationSupport
   * @see #declare(DeclarationSupport)
   * @author Omnaest
   */
  public static interface Declaration
  {
    /**
     * @see DeclarationSupport
     * @param support
     */
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
    
    this.converterPipeManager = new ConverterPipeManagerImpl();
    initializeConverterPipeManager( this.converterPipeManager );
    
  }
  
  private static void initializeConverterPipeManager( final ConverterPipeManager converterPipeManager )
  {
    {
      ElementConverter<Date, Date> elementConverter = new ElementConverterSerializable<Date, Date>()
      {
        private static final long serialVersionUID = -8170416497828888204L;
        
        @Override
        public Date convert( Date date )
        {
          return new Date( date.getTime() );
        }
      };
      converterPipeManager.addConverterPipeFrom( Date.class ).over( elementConverter ).to( Date.class );
    }
    {
      ElementConverter<Calendar, Calendar> elementConverter = new ElementConverterSerializable<Calendar, Calendar>()
      {
        private static final long serialVersionUID = 8770104025185376953L;
        
        @Override
        public Calendar convert( Calendar calendarFrom )
        {
          final Calendar calendar = Calendar.getInstance();
          calendar.setTime( calendarFrom.getTime() );
          return calendar;
        }
      };
      converterPipeManager.addConverterPipeFrom( Calendar.class ).over( elementConverter ).to( Calendar.class );
    }
    {
      ElementConverter<Date, Calendar> elementConverter = new ElementConverterSerializable<Date, Calendar>()
      {
        private static final long serialVersionUID = -2711841719554089887L;
        
        @Override
        public Calendar convert( Date date )
        {
          final Calendar calendar = Calendar.getInstance();
          calendar.setTime( date );
          return calendar;
        }
      };
      converterPipeManager.addConverterPipeFrom( Date.class ).over( elementConverter ).to( Calendar.class );
    }
    {
      ElementConverter<Calendar, Date> elementConverter = new ElementConverterSerializable<Calendar, Date>()
      {
        private static final long serialVersionUID = 1110860863721796033L;
        
        @Override
        public Date convert( Calendar calendarFrom )
        {
          return calendarFrom.getTime();
        }
      };
      converterPipeManager.addConverterPipeFrom( Calendar.class ).over( elementConverter ).to( Date.class );
    }
  }
  
  /**
   * Clones a given source instance into its target instance representation
   * 
   * @see #copy(Object, Object)
   * @param source
   * @return
   */
  @SuppressWarnings("unchecked")
  public TO clone( FROM source )
  {
    TO retval = null;
    final FactoryParameterized<Object, Object> factory = this.factoryResolver.resolveFactory( this.targetType );
    if ( factory != null )
    {
      retval = (TO) factory.newInstance( determineRootFactoryParameter( source ) );
      this.copy( source, retval );
    }
    
    return retval;
  }
  
  private Object determineRootFactoryParameter( FROM source )
  {
    Object parameter = null;
    if ( ArrayUtils.isArray( source ) )
    {
      parameter = ArrayUtils.length( source );
    }
    return parameter;
  }
  
  /**
   * Copies from a given source instance to a given target instance
   * 
   * @see BeanReplicator#clone(Object)
   * @param source
   * @param target
   */
  public void copy( FROM source, TO target )
  {
    final InstanceCache instanceCache = new InstanceCacheImpl();
    final Class<?> sourceType = this.sourceType;
    final Class<?> targetType = this.targetType;
    final Path path = new Path();
    this.copy( source, target, instanceCache, sourceType, targetType, path );
  }
  
  /**
   * Internal {@link #copy(Object, Object)} method which is able to be called recursively
   * 
   * @param source
   * @param target
   * @param instanceCache
   * @param sourceType
   * @param targetType
   * @param path
   */
  private void copy( Object source,
                     Object target,
                     InstanceCache instanceCache,
                     Class<?> sourceType,
                     Class<?> targetType,
                     Path path )
  {
    try
    {
      if ( source != null && sourceType != null && target != null && targetType != null )
      {
        this.hasCopiedOnce.compareAndSet( false, true );
        
        final InstanceAccessor instanceAccessorSource = this.instanceAccessorResolver.resolveInstanceAccessor( sourceType );
        final InstanceAccessor instanceAccessorTarget = this.instanceAccessorResolver.resolveInstanceAccessor( targetType );
        if ( instanceAccessorSource != null && instanceAccessorTarget != null )
        {
          for ( String propertyName : instanceAccessorSource.getPropertyNameIterable( source ) )
          {
            try
            {
              final PropertyAccessor propertySource = instanceAccessorSource.getPropertyAccessor( propertyName, source );
              if ( propertySource != null )
              {
                Object valueReplica = null;
                {
                  final Object factoryParameter = propertySource.getFactoryParameter();
                  
                  final Class<?> propertySourceType = propertySource.getType();
                  final PropertyNameAndType remapping = this.typeToTypeMappingManager.determineRemapping( propertyName,
                                                                                                          propertySourceType,
                                                                                                          path );
                  final String propertyNameWithinTarget = determinePropertyNameWithinTarget( propertyName, remapping );
                  final PropertyAccessor propertyTarget = instanceAccessorTarget.getPropertyAccessor( propertyNameWithinTarget,
                                                                                                      target );
                  if ( propertyTarget != null )
                  {
                    final Class<?> propertyTargetType = determinePropertyTargetType( propertyTarget, propertySourceType,
                                                                                     remapping );
                    
                    final Pipe<Object, Object> converterPipe = this.converterPipeManager.resolveConverterPipeFor( propertySourceType,
                                                                                                                  propertyTargetType );
                    final Object value = propertySource.getValue();
                    if ( converterPipe != null )
                    {
                      valueReplica = converterPipe.convert( value );
                    }
                    else if ( value != null )
                    {
                      if ( ObjectUtils.isPrimitiveOrPrimitiveWrapperType( propertySourceType )
                           || this.preservedTypeInstanceManager.contains( propertySourceType ) )
                      {
                        valueReplica = value;
                      }
                      else
                      {
                        valueReplica = instanceCache.getReplicaInstance( propertyTargetType, value );
                        if ( valueReplica == null )
                        {
                          final FactoryParameterized<Object, Object> factory = this.factoryResolver.resolveFactory( propertyTargetType );
                          if ( factory != null )
                          {
                            valueReplica = factory.newInstance( factoryParameter );
                            this.copy( value, valueReplica, instanceCache, propertySourceType, propertyTargetType,
                                       new Path( path, propertyName ) );
                            instanceCache.addReplicaInstance( propertyTargetType, value, valueReplica );
                          }
                        }
                      }
                    }
                    propertyTarget.setValue( valueReplica );
                  }
                }
              }
            }
            catch ( Exception e )
            {
              final String canonicalPath = path.getCanonicalPath() + "." + propertyName;
              this.exceptionHandler.handleException( new CopyException( e, canonicalPath ) );
            }
          }
        }
      }
    }
    catch ( Exception e )
    {
      final String canonicalPath = path.getCanonicalPath();
      this.exceptionHandler.handleException( new CopyException( e, canonicalPath ) );
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
  
  /**
   * Allows the specify a mapping {@link Declaration} <br>
   * <br>
   * {@link Declaration} have to be made before any call to {@link #copy(Object, Object)} or {@link #clone(Object)}
   * 
   * @param declaration
   *          {@link Declaration}
   * @return this
   */
  public BeanReplicator<FROM, TO> declare( Declaration declaration )
  {
    Assert.isFalse( this.hasCopiedOnce.get(), "Declarations must be specified before any call to copy or clone" );
    
    final TypeToTypeMappingManager typeToTypeMappingManager = this.typeToTypeMappingManager;
    if ( declaration != null )
    {
      
      final DeclarationSupport support = new DeclarationSupport()
      {
        private static final long serialVersionUID = 3224104919381023723L;
        
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
        
        @Override
        public <FROM2> PipeBuilder<FROM2, FROM2> addConverterPipeFrom( Class<FROM2> typeFrom )
        {
          return BeanReplicator.this.converterPipeManager.addConverterPipeFrom( typeFrom );
        }
        
      };
      declaration.declare( support );
      
      this.prepare();
    }
    
    return this;
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
