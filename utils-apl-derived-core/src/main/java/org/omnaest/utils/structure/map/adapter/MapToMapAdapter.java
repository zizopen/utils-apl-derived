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
package org.omnaest.utils.structure.map.adapter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterToBidirectionalConverterAdapter;
import org.omnaest.utils.structure.map.MapAbstract;

/**
 * The {@link MapToMapAdapter} is a decorator and adapter which allows to access a given {@link Map} using other types for the key
 * as well as for the value. <br>
 * <br>
 * The adapter instance is always backed by the original {@link Map} and changes on both sides will be traversed to each other
 * side.
 * 
 * @author Omnaest
 * @param <KEY_FROM>
 * @param <VALUE_FROM>
 * @param <KEY_TO>
 * @param <VALUE_TO>
 */
public class MapToMapAdapter<KEY_FROM, VALUE_FROM, KEY_TO, VALUE_TO> extends MapAbstract<KEY_TO, VALUE_TO>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                   serialVersionUID = -1538459740184023592L;
  
  /* ********************************************** Variables ********************************************** */
  private Map<KEY_FROM, VALUE_FROM>                           sourceMap        = null;
  
  private ElementBidirectionalConverter<KEY_FROM, KEY_TO>     elementBidirectionalConverterKey;
  private ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue;
  
  /* ********************************************** Methods ********************************************** */
  
  public MapToMapAdapter( Map<KEY_FROM, VALUE_FROM> sourceMap,
                          ElementConverter<KEY_FROM, KEY_TO> elementConverterKeySourceToAdapter,
                          ElementConverter<KEY_TO, KEY_FROM> elementConverterKeyAdapterToSource,
                          ElementConverter<VALUE_FROM, VALUE_TO> elementConverterValueSourceToAdapter,
                          ElementConverter<VALUE_TO, VALUE_FROM> elementConverterValueAdapterToSource )
  {
    super();
    
    Assert.isNotNull( sourceMap, elementConverterValueAdapterToSource, elementConverterValueSourceToAdapter,
                      elementConverterKeyAdapterToSource, elementConverterKeySourceToAdapter );
    
    this.sourceMap = sourceMap;
    this.elementBidirectionalConverterKey = new ElementConverterToBidirectionalConverterAdapter<KEY_FROM, KEY_TO>(
                                                                                                            elementConverterKeySourceToAdapter,
                                                                                                            elementConverterKeyAdapterToSource );
    this.elementBidirectionalConverterValue = new ElementConverterToBidirectionalConverterAdapter<VALUE_FROM, VALUE_TO>(
                                                                                                                  elementConverterValueSourceToAdapter,
                                                                                                                  elementConverterValueAdapterToSource );
    
  }
  
  public MapToMapAdapter( Map<KEY_FROM, VALUE_FROM> sourceMap,
                          ElementBidirectionalConverter<KEY_FROM, KEY_TO> elementBidirectionalConverterKey,
                          ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue )
  {
    super();
    
    Assert.isNotNull( elementBidirectionalConverterKey, elementBidirectionalConverterValue, sourceMap );
    
    this.sourceMap = sourceMap;
    this.elementBidirectionalConverterKey = elementBidirectionalConverterKey;
    this.elementBidirectionalConverterValue = elementBidirectionalConverterValue;
  }
  
  protected KEY_TO convertKeyFromToKeyTo( KEY_FROM key_FROM )
  {
    return this.elementBidirectionalConverterKey.convert( key_FROM );
  }
  
  protected KEY_FROM convertKeyToToKeyFrom( KEY_TO key_TO )
  {
    return this.elementBidirectionalConverterKey.convertBackwards( key_TO );
  }
  
  protected VALUE_TO convertValueFromToValueTo( VALUE_FROM value_FROM )
  {
    return this.elementBidirectionalConverterValue.convert( value_FROM );
  }
  
  protected VALUE_FROM convertValueToToValueFrom( VALUE_TO value_TO )
  {
    return this.elementBidirectionalConverterValue.convertBackwards( value_TO );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public VALUE_TO get( Object key )
  {
    //
    VALUE_TO retval = null;
    
    //
    if ( key != null && this.sourceMap != null )
    {
      try
      {
        //
        KEY_FROM convertedKey = this.convertKeyToToKeyFrom( (KEY_TO) key );
        if ( convertedKey != null )
        {
          VALUE_FROM value_FROM = this.sourceMap.get( convertedKey );
          retval = this.convertValueFromToValueTo( value_FROM );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public VALUE_TO put( KEY_TO key, VALUE_TO value )
  {
    //
    VALUE_TO retval = null;
    
    //
    if ( key != null && this.sourceMap != null )
    {
      //
      try
      {
        //
        @SuppressWarnings("cast")
        KEY_FROM convertedKey = this.convertKeyToToKeyFrom( (KEY_TO) key );
        VALUE_FROM convertedValue = this.convertValueToToValueFrom( value );
        if ( convertedKey != null )
        {
          VALUE_FROM value_FROM = this.sourceMap.put( convertedKey, convertedValue );
          retval = this.convertValueFromToValueTo( value_FROM );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public VALUE_TO remove( Object key )
  {
    //
    VALUE_TO retval = null;
    
    //
    if ( key != null && this.sourceMap != null )
    {
      //
      try
      {
        //
        @SuppressWarnings({ "unchecked" })
        KEY_FROM convertedKey = this.convertKeyToToKeyFrom( (KEY_TO) key );
        if ( convertedKey != null )
        {
          VALUE_FROM value_FROM = this.sourceMap.remove( convertedKey );
          retval = this.convertValueFromToValueTo( value_FROM );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public Set<KEY_TO> keySet()
  {
    return SetUtils.adapter( this.sourceMap.keySet(), this.elementBidirectionalConverterKey );
  }
  
  @Override
  public Collection<VALUE_TO> values()
  {
    return CollectionUtils.adapter( this.sourceMap.values(), this.elementBidirectionalConverterValue );
  }
  
}
