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

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Pattern;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.beans.replicator.adapter.helper.BeanPropertiesAutowireHelper;
import org.omnaest.utils.beans.replicator.adapter.helper.DTOPackage;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.strings.StringReplacementBuilder;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * This {@link Adapter} allows to specify a replacement pattern.<br>
 * <br>
 * The replacement pattern can use the <code>{type}</code> and <code>{package}</code> placeholders, which will be replaced with
 * the {@link Class} type and {@link Package} name of the source type.<br>
 * <br>
 * Be aware of the fact, that this is a <b>VERY SLOW</b> operation. Initially when creating the {@link Adapter} every
 * {@link Package} of the {@link ClassLoader} has to be scanned for {@link Package} {@link Annotation}s. Additionally for every
 * {@link org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal.Handler} invocation the {@link Package}s has to be
 * checked, if any has a matching {@link Class} which can be quite a lot of work. (Rough cost is about 50ms per handler
 * invocation)<br>
 * <br>
 * Examples: <br>
 * <table border="1">
 * <tr>
 * <td>Replacement</td>
 * <td>Description</td>
 * </tr>
 * <tr>
 * <td>org.example.package.dto.{type}DTO</td>
 * <td>Searches within the org.example.package.dto after the name of the orignal type appended by 'DTO'</td>
 * </tr>
 * <tr>
 * <td>{package}.dto.{type}</td>
 * <td>Searches within the 'dto' subpackage of the original package the name of the orignal type</td>
 * </tr>
 * </table>
 * 
 * @see DTOPackage
 * @author Omnaest
 */
public class AdapterTypePatternBased implements AdapterInternal
{
  /* ********************************************** Variables ********************************************** */
  protected final Set<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory> patternBasedTargetTypeFactorySet;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   */
  protected static class PackageAndPatternBasedTargetTypeFactory
  {
    /* ********************************************** Variables ********************************************** */
    private final Package package_;
    private final String  replacement;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see PackageAndPatternBasedTargetTypeFactory
     * @param package_
     * @param replacement
     */
    public PackageAndPatternBasedTargetTypeFactory( Package package_, String replacement )
    {
      super();
      this.package_ = package_;
      this.replacement = replacement;
    }
    
    /**
     * Returns a new {@link FactoryTypeAware} instance for the given source type, if the
     * {@link PackageAndPatternBasedTargetTypeFactory} can resolve a {@link Class} based on the given type and the internal
     * {@link Package} and replacement {@link String} information.<br>
     * 
     * @param type
     * @return
     */
    public FactoryTypeAware<Object> newFactory( Class<?> type )
    {
      //
      FactoryTypeAware<Object> retval = null;
      
      //
      final Class<?> resolvedClass = this.resolveClassFor( type );
      final boolean hasDefaultConstructor = ReflectionUtils.hasDefaultConstructorFor( resolvedClass );
      if ( hasDefaultConstructor )
      {
        retval = new FactoryTypeAware<Object>()
        {
          @Override
          public Object newInstance()
          {
            return ReflectionUtils.createInstanceOf( resolvedClass );
          }
          
          @Override
          public Class<?> getInstanceType()
          {
            return resolvedClass;
          }
        };
      }
      
      return retval;
    }
    
    /**
     * @param type
     * @return
     */
    private Class<?> resolveClassFor( Class<?> type )
    {
      //        
      Class<?> retval = null;
      
      //
      if ( type != null )
      {
        try
        {
          //
          final Package packageCurrent = this.package_ != null ? this.package_ : type.getPackage();
          String className = new StringReplacementBuilder().add( Pattern.quote( "{type}" ), type.getSimpleName() )
                                                           .add( Pattern.quote( "{package}" ), packageCurrent.getName() )
                                                           .process( this.replacement );
          retval = Class.forName( className );
        }
        catch ( Exception e )
        {
        }
      }
      
      //
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see AdapterTypePatternBased
   * @param replacement
   */
  public AdapterTypePatternBased( String replacement )
  {
    this( replacement, null );
  }
  
  /**
   * @see AdapterTypePatternBased
   * @param replacement
   * @param packageAnnotationType
   */
  public AdapterTypePatternBased( String replacement, Class<? extends Annotation> packageAnnotationType )
  {
    //
    super();
    Assert.isNotNull( replacement, "replacement must not be null" );
    
    //
    this.patternBasedTargetTypeFactorySet = determinePatternBasedTargetTypeFactorySet( replacement, packageAnnotationType );
  }
  
  /**
   * @param replacement
   * @param packageAnnotationType
   * @return
   */
  @SuppressWarnings("unchecked")
  private static Set<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory> determinePatternBasedTargetTypeFactorySet( String replacement,
                                                                                                                                 Class<? extends Annotation> packageAnnotationType )
  {
    final Set<Package> packageSet = packageAnnotationType != null ? ReflectionUtils.annotatedPackageSet( packageAnnotationType )
                                                                 : SetUtils.valueOf( Package.getPackages() );
    return determinePatternBasedTargetTypeFactorySet( replacement, packageSet );
  }
  
  /**
   * @param replacement
   * @param packageSet
   * @return
   */
  private static Set<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory> determinePatternBasedTargetTypeFactorySet( String replacement,
                                                                                                                                 Set<Package> packageSet )
  {
    //
    final Set<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory> retset = new LinkedHashSet<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory>();
    
    //
    Assert.isNotNull( packageSet, "packageSet must not be null" );
    for ( Package package_ : packageSet )
    {
      retset.add( new PackageAndPatternBasedTargetTypeFactory( package_, replacement ) );
    }
    
    //
    return retset;
  }
  
  @Override
  public Set<Handler> newHandlerSet( final TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    //
    final Set<Handler> retset = new LinkedHashSet<Handler>();
    
    //
    final Set<AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory> patternBasedTargetTypeFactorySet = this.patternBasedTargetTypeFactorySet;
    retset.add( new Handler()
    {
      /* ********************************************** Variables ********************************************** */
      private Map<Class<?>, FactoryTypeAware<Object>> sourceObjectTypeToFactoryMap = MapUtils.initializedMap( new WeakHashMap<Class<?>, FactoryTypeAware<Object>>(),
                                                                                                              new FactoryParameterized<FactoryTypeAware<Object>, Class<?>>()
                                                                                                              {
                                                                                                                @Override
                                                                                                                public FactoryTypeAware<Object> newInstance( Class<?>... arguments )
                                                                                                                {
                                                                                                                  //
                                                                                                                  FactoryTypeAware<Object> retval = null;
                                                                                                                  
                                                                                                                  //
                                                                                                                  if ( arguments.length == 1 )
                                                                                                                  {
                                                                                                                    //
                                                                                                                    final Class<?> type = arguments[0];
                                                                                                                    for ( AdapterTypePatternBased.PackageAndPatternBasedTargetTypeFactory packageAndPatternBasedTargetTypeFactory : patternBasedTargetTypeFactorySet )
                                                                                                                    {
                                                                                                                      //
                                                                                                                      retval = packageAndPatternBasedTargetTypeFactory.newFactory( type );
                                                                                                                      if ( retval != null )
                                                                                                                      {
                                                                                                                        break;
                                                                                                                      }
                                                                                                                    }
                                                                                                                  }
                                                                                                                  
                                                                                                                  //
                                                                                                                  return retval;
                                                                                                                }
                                                                                                              } );
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public Object createNewTargetObjectInstance( Class<?> sourceObjectType, Object sourceObject )
      {
        //
        Object retval = null;
        
        //
        if ( sourceObject != null )
        {
          final FactoryTypeAware<Object> factory = this.resolveFactoryFor( sourceObjectType );
          retval = factory.newInstance();
        }
        
        //
        BeanPropertiesAutowireHelper.copyProperties( sourceObject, retval, transitiveBeanReplicationInvocationHandler );
        
        //
        return retval;
      }
      
      @Override
      public boolean canHandle( Class<? extends Object> sourceObjectType )
      {
        return this.resolveFactoryFor( sourceObjectType ) != null;
      }
      
      private FactoryTypeAware<Object> resolveFactoryFor( Class<? extends Object> sourceObjectType )
      {
        return this.sourceObjectTypeToFactoryMap.get( sourceObjectType );
      }
      
    } );
    
    //
    return retset;
  }
}
