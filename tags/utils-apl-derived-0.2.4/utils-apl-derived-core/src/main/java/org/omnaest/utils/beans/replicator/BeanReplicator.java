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

import java.awt.List;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.beanutils.BeanUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.copier.PreparedBeanCopier.NonMatchingPropertyException;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerDelegate;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;

/**
 * <h1>General</h1><br>
 * A {@link BeanReplicator} allows to {@link #copy(Object, Object)} or {@link #clone(Object)} instances from one type to another
 * type. <br>
 * <br>
 * By default the {@link BeanReplicator} maps similar named and typed properties to each other. The {@link #declare(Declaration)}
 * method allows to specify in more detail how the replication of a target instance takes place. <br>
 * <br>
 * <h1>Mappings</h1><br>
 * Mappings can be done from
 * <ul>
 * <li>Java bean to Java bean</li>
 * <li>Java bean to {@link Map}&lt;String,Object&gt;</li>
 * <li>{@link Map}&lt;String,Object&gt; to Java bean</li>
 * <li>{@link Map} to {@link Map} / {@link SortedMap}</li>
 * <li>{@link List} to {@link List} / {@link Set} / {@link Collection} / Array</li>
 * <li>{@link Set} to {@link List} / {@link Set} / {@link Collection} / Array</li>
 * <li>{@link Collection} to {@link List} / {@link Set} / {@link Collection} / Array</li>
 * <li>{@link Iterable} to {@link List} / {@link Set} / {@link Collection} / Array</li>
 * <li>Array to {@link List} / {@link Set} / {@link Collection} / Array</li>
 * </ul>
 * Some primitive type mappings like {@link String} to {@link Long} conversions are done automatically. <br>
 * <br>
 * <h2>Proxy generation</h2><br>
 * If a target type is an <b>interface</b>, the {@link BeanReplicator} will try to generate an proxy implementation on the fly
 * which will act like a normal bean. Of course such conversions are much slower, than normal bean to bean copy actions. <br>
 * <br>
 * <h1>Exception handling</h1><br>
 * If one ore more properties can not be matched, the {@link BeanReplicator} throws internally
 * {@link NoMatchingPropertiesException}s which are given to any {@link ExceptionHandler} set with
 * {@link #setExceptionHandler(ExceptionHandler)}.<br>
 * Unexpected exceptions will be catched and wrapped into a {@link CopyException} and given to the set {@link ExceptionHandler}.
 * The {@link BeanReplicator} methods itself will never throw any {@link Exception}.<br>
 * <br>
 * <h1>Thread safety</h1><br>
 * The {@link BeanReplicator} instance is thread safe in its {@link #copy(Object, Object)} and {@link #clone(Object)} methods. The
 * {@link #declare(Declaration)} method is NOT thread safe and the modification of any {@link Declaration} should be strongly
 * avoided during any {@link #copy(Object, Object)} or {@link #clone(Object)} invocation. <br>
 * <h1>Performance</h1><br>
 * Even with its very strong mapping capabilities the {@link BeanReplicator} is still <b>about 2-5 times faster</b> than the
 * Apache Commons {@link BeanUtils#copyProperties(Object, Object)} <br>
 * <br>
 * For about <b>1000000 clone</b> invocations it needs <b>about 5-10 seconds</b>. Based on simple beans with a hand full of
 * primitive properties.<br>
 * <br>
 * The direct getter setter invocation for the same amount of beans takes about 100 ms!!<br>
 * <h1>Code examples</h1> <br>
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
 * Remapping example: <br>
 * 
 * <pre>
 * Source bean:  
 * |--Bean0From bean0From
 *    |--Bean1From bean1From
 *       |--Bean2From bean2From
 *          |--String fieldForLong
 *          |--String fieldString1
 *          |--String fieldString2
 * 
 *  Target bean: 
 *  |-- Bean0To bean0To
 *    |--Bean1To bean1To
 *       |--Bean2To bean2To
 *          |--long fieldLong
 *          |--String fieldString1
 *          |--String fieldString2
 * </pre>
 * 
 * <pre>
 * beanReplicator.declare( new Declaration()
 * {
 *   &#064;Override
 *   public void declare( DeclarationSupport support )
 *   {
 *     support.addTypeMapping( Bean1From.class, Bean1To.class );
 *     support.addPropertyNameMapping( &quot;bean1From&quot;, &quot;bean1To&quot; );
 *     
 *     support.addTypeMappingForPath( &quot;bean1From&quot;, Bean2From.class, Bean2To.class );
 *     support.addPropertyNameMapping( &quot;bean1From&quot;, &quot;bean2From&quot;, &quot;bean2To&quot; );
 *     
 *     support.addTypeAndPropertyNameMapping( &quot;bean1From.bean2From&quot;, String.class, &quot;fieldForLong&quot;, Long.class, &quot;fieldLong&quot; );
 *   }
 * } );
 * </pre>
 * 
 * @see DeclarationSupport
 * @see Declaration
 * @see #declare(Declaration)
 * @see #setExceptionHandler(ExceptionHandler)
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class BeanReplicator<FROM, TO> implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                      serialVersionUID = -5403362205184966835L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<FROM>                      sourceType;
  private final Class<TO>                        targetType;
  private final FactoryResolver                  factoryResolver;
  private final InstanceAccessorResolver         instanceAccessorResolver;
  private final PreservationAndIgnorationManager preservationAndIgnorationManager;
  private final TypeToTypeMappingManager         typeToTypeMappingManager;
  private final ConverterPipeManager             converterPipeManager;
  private final ExceptionHandlerDelegate         exceptionHandler = new ExceptionHandlerDelegate( new ExceptionHandlerIgnoring() );
  private final AtomicBoolean                    hasCopiedOnce    = new AtomicBoolean( false );
  
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
   * The {@link DeclarationSupport} allows to add e.g. type to type or property name mappings, {@link ElementConverter} pipes,
   * preservations, ... <br>
   * <br>
   * 
   * @author Omnaest
   */
  public static interface DeclarationSupport extends TypeToTypeMappingDeclarer, ConverterPipeDeclarer,
                                            PreservationAndIgnorationDeclarer
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
  public static interface Declaration extends Serializable
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
  @SuppressWarnings("unchecked")
  public BeanReplicator( Class<? super FROM> sourceType, Class<? extends TO> targetType )
  {
    this.sourceType = (Class<FROM>) sourceType;
    this.targetType = (Class<TO>) targetType;
    
    this.factoryResolver = new FactoryResolverImpl();
    this.instanceAccessorResolver = new InstanceAccessorResolverImpl( this.exceptionHandler );
    
    this.preservationAndIgnorationManager = new PreservationAndIgnorationManagerImpl();
    
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
    try
    {
      final FactoryParameterized<Object, Map<String, Object>> factory = this.factoryResolver.resolveFactory( this.targetType );
      if ( factory != null )
      {
        retval = (TO) factory.newInstance( determineFactoryMetaInformation( this.sourceType, source ) );
        Assert.isNotNull( retval, "Failed to create instance of type " + this.sourceType );
        this.copy( source, retval );
      }
    }
    catch ( Exception e )
    {
      final String canonicalPath = "";
      this.exceptionHandler.handleException( new CopyException( e, canonicalPath ) );
    }
    return retval;
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
              final Path subPath = new Path( path, propertyName );
              if ( !this.preservationAndIgnorationManager.isIgnoredPath( subPath ) )
              {
                final PropertyAccessor propertySource = instanceAccessorSource.getPropertyAccessor( propertyName, source );
                if ( propertySource != null )
                {
                  Object valueReplica = null;
                  {
                    final Class<?> propertySourceType = propertySource.getType();
                    if ( propertySourceType != null && !this.preservationAndIgnorationManager.isIgnoredType( propertySourceType ) )
                    {
                      final PropertyNameAndType remapping = this.typeToTypeMappingManager.determineRemapping( propertyName,
                                                                                                              propertySourceType,
                                                                                                              path );
                      final String propertyNameWithinTarget = determinePropertyNameWithinTarget( propertyName, remapping );
                      final PropertyAccessor propertyTarget = instanceAccessorTarget.getPropertyAccessor( propertyNameWithinTarget,
                                                                                                          target );
                      final Class<?> propertyTargetType = determinePropertyTargetType( propertyTarget, propertySourceType,
                                                                                       remapping );
                      final Pipe<Object, Object> converterPipe = this.converterPipeManager.resolveConverterPipeFor( propertySourceType,
                                                                                                                    propertyTargetType );
                      
                      if ( propertyTargetType == null && converterPipe == null )
                      {
                        final String canonicalPath = subPath.getCanonicalPath();
                        final String propertyNameSource = propertyName;
                        final String propertyNameTarget = propertyNameWithinTarget;
                        throw new NoMatchingPropertiesException( canonicalPath, propertySourceType, propertyNameSource,
                                                                 targetType, propertyNameTarget );
                      }
                      
                      final Object value = propertySource.getValue();
                      if ( converterPipe != null )
                      {
                        valueReplica = converterPipe.convert( value );
                      }
                      else if ( value != null )
                      {
                        final boolean isPrimitiveOrPrimitiveWrapperOrStringType = ObjectUtils.isPrimitiveOrPrimitiveWrapperType( propertySourceType )
                                                                                  || String.class.equals( propertySourceType );
                        final boolean isPreservedInstance = this.preservationAndIgnorationManager.isPreservedType( propertySourceType )
                                                            || this.preservationAndIgnorationManager.isPreservedPath( subPath );
                        if ( isPrimitiveOrPrimitiveWrapperOrStringType || isPreservedInstance )
                        {
                          if ( isPreservedInstance )
                          {
                            valueReplica = value;
                          }
                          else
                          {
                            if ( propertyTargetType != null && propertyTargetType.isAssignableFrom( propertySourceType ) )
                            {
                              valueReplica = value;
                            }
                            else
                            {
                              valueReplica = ObjectUtils.castTo( propertyTargetType, value );
                            }
                          }
                        }
                        else
                        {
                          valueReplica = instanceCache.getReplicaInstance( propertyTargetType, value );
                          if ( valueReplica == null )
                          {
                            final FactoryParameterized<Object, Map<String, Object>> factory = this.factoryResolver.resolveFactory( propertyTargetType );
                            if ( factory != null )
                            {
                              final Map<String, Object> factoryMetaInformation = determineFactoryMetaInformation( propertySourceType,
                                                                                                                  value );
                              valueReplica = factory.newInstance( factoryMetaInformation );
                              {
                                this.copy( value, valueReplica, instanceCache, propertySourceType, propertyTargetType, subPath );
                              }
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
            }
            catch ( CopyException e )
            {
              this.exceptionHandler.handleException( e );
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
  
  private Map<String, Object> determineFactoryMetaInformation( final Class<?> propertySourceType, Object instance )
  {
    InstanceAccessor instanceAccessor = this.instanceAccessorResolver.resolveInstanceAccessor( propertySourceType );
    Map<String, Object> factoryMetaInformation = instanceAccessor != null ? instanceAccessor.determineFactoryMetaInformation( instance )
                                                                         : null;
    return factoryMetaInformation;
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
      if ( propertyTarget != null )
      {
        retval = propertyTarget.getType();
        if ( retval == null )
        {
          retval = propertySourceType;
        }
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
        
        @Override
        public void addAllPreservedTypes( Iterable<? extends Class<?>> typeIterable )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addAllPreservedTypes( typeIterable );
        }
        
        @Override
        public void addPreservedType( Class<?> type )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addPreservedType( type );
        }
        
        @Override
        public void addPreservedPath( String path )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addPreservedPath( path );
        }
        
        @Override
        public void setPreservedDeepnessLevel( int deepnessLevel )
        {
          BeanReplicator.this.preservationAndIgnorationManager.setPreservedDeepnessLevel( deepnessLevel );
        }
        
        @Override
        public void setIgnoredDeepnessLevel( int deepnessLevel )
        {
          BeanReplicator.this.preservationAndIgnorationManager.setIgnoredDeepnessLevel( deepnessLevel );
        }
        
        @Override
        public void addAllIgnoredTypes( Iterable<? extends Class<?>> typeIterable )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addAllIgnoredTypes( typeIterable );
        }
        
        @Override
        public void addIgnoredType( Class<?> type )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addIgnoredType( type );
        }
        
        @Override
        public void addIgnoredPath( String path )
        {
          BeanReplicator.this.preservationAndIgnorationManager.addIgnoredPath( path );
        }
        
      };
      declaration.declare( support );
    }
    
    return this;
  }
  
  /**
   * The given {@link ExceptionHandler} should handle any {@link CopyException} and {@link NonMatchingPropertyException}
   * 
   * @param exceptionHandler
   * @return this
   */
  public BeanReplicator<FROM, TO> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler.setExceptionHandler( ObjectUtils.defaultIfNull( exceptionHandler, new ExceptionHandlerIgnoring() ) );
    return this;
  }
}
