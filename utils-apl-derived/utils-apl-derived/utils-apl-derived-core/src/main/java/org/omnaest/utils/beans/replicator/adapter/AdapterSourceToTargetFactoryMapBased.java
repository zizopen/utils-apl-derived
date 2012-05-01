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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.omnaest.utils.beans.replicator.BeanReplicator;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterInternal;
import org.omnaest.utils.beans.replicator.BeanReplicator.TransitiveBeanReplicationInvocationHandler;
import org.omnaest.utils.beans.replicator.adapter.helper.BeanPropertiesAutowireHelper;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;
import org.omnaest.utils.structure.map.SimpleEntry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * This {@link Adapter} uses pairs of {@link FactoryTypeAware} instances to create a binding. Each
 * {@link FactoryTypeAware#getInstanceType()} will be matched to a source type and the opposite
 * {@link FactoryTypeAware#newInstance()} method is used to create a new translated {@link Object} instance.<br>
 * <br>
 * This works in both directions by default but can be overridden using {@link #setUsingBothDirections(boolean)}.<br>
 * <br>
 * This method uses reflection and takes approximately <b>0.5-5.0 ms</b> to perform.
 * 
 * @see AdapterSourceToTargetTypeMapBased
 * @see BeanReplicator
 * @author Omnaest
 */
public class AdapterSourceToTargetFactoryMapBased implements AdapterInternal
{
  /* ********************************************** Variables ********************************************** */
  protected final BiMap<FactoryTypeAware<?>, FactoryTypeAware<?>> sourceToTargetFactoryMap;
  protected boolean                                               usingBothDirections = true;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see AdapterSourceToTargetFactoryMapBased
   * @param sourceToTargetFactoryMap
   */
  public AdapterSourceToTargetFactoryMapBased( Map<FactoryTypeAware<?>, FactoryTypeAware<?>> sourceToTargetFactoryMap )
  {
    super();
    this.sourceToTargetFactoryMap = HashBiMap.<FactoryTypeAware<?>, FactoryTypeAware<?>> create( sourceToTargetFactoryMap );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<Handler> newHandlerSet( final TransitiveBeanReplicationInvocationHandler transitiveBeanReplicationInvocationHandler )
  {
    //
    final Set<Handler> retset = new LinkedHashSet<Handler>();
    
    //
    for ( final Entry<FactoryTypeAware<?>, FactoryTypeAware<?>> sourceToTargetTypeEntryOriginal : this.sourceToTargetFactoryMap.entrySet() )
    {
      //
      final List<Entry<FactoryTypeAware<?>, FactoryTypeAware<?>>> sourceToTargetTypeEntryList = this.usingBothDirections ? Arrays.asList( sourceToTargetTypeEntryOriginal,
                                                                                                                                          new SimpleEntry<FactoryTypeAware<?>, FactoryTypeAware<?>>(
                                                                                                                                                                                                     sourceToTargetTypeEntryOriginal ).inverted() )
                                                                                                                        : Arrays.asList( sourceToTargetTypeEntryOriginal );
      for ( final Entry<FactoryTypeAware<?>, FactoryTypeAware<?>> sourceToTargetTypeEntry : sourceToTargetTypeEntryList )
      {
        //
        retset.add( new Handler()
        {
          @Override
          public Object createNewTargetObjectInstance( Class<?> sourceObjectType, Object sourceObject )
          {
            //
            Object retval = sourceToTargetTypeEntry.getValue().newInstance();
            
            //              
            BeanPropertiesAutowireHelper.copyProperties( sourceObject, retval, transitiveBeanReplicationInvocationHandler );
            
            //
            return retval;
          }
          
          @Override
          public boolean canHandle( Class<? extends Object> sourceObjectType )
          {
            //
            return ReflectionUtils.isAssignableFrom( sourceToTargetTypeEntry.getKey().getInstanceType(), sourceObjectType );
          }
        } );
      }
    }
    
    //
    return retset;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "AdapterSourceToTargetFactoryMapBased [sourceToTargetFactoryMap=" );
    builder.append( this.sourceToTargetFactoryMap );
    builder.append( "]" );
    return builder.toString();
  }
  
  /**
   * @return the usingBothDirections
   */
  public boolean isUsingBothDirections()
  {
    return this.usingBothDirections;
  }
  
  /**
   * @param usingBothDirections
   *          the usingBothDirections to set
   * @return this
   */
  public AdapterSourceToTargetFactoryMapBased setUsingBothDirections( boolean usingBothDirections )
  {
    this.usingBothDirections = usingBothDirections;
    return this;
  }
  
}
