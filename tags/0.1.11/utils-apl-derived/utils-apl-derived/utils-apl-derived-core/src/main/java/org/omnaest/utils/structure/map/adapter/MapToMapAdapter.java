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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
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
  
  /* ********************************************** Variables ********************************************** */
  protected Map<KEY_FROM, VALUE_FROM>              sourceMap                            = null;
  
  protected ElementConverter<KEY_FROM, KEY_TO>     elementConverterKeySourceToAdapter   = null;
  protected ElementConverter<KEY_TO, KEY_FROM>     elementConverterKeyAdapterToSource   = null;
  protected ElementConverter<VALUE_FROM, VALUE_TO> elementConverterValueSourceToAdapter = null;
  protected ElementConverter<VALUE_TO, VALUE_FROM> elementConverterValueAdapterToSource = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public MapToMapAdapter( Map<KEY_FROM, VALUE_FROM> sourceMap,
                          ElementConverter<KEY_FROM, KEY_TO> elementConverterKeySourceToAdapter,
                          ElementConverter<KEY_TO, KEY_FROM> elementConverterKeyAdapterToSource,
                          ElementConverter<VALUE_FROM, VALUE_TO> elementConverterValueSourceToAdapter,
                          ElementConverter<VALUE_TO, VALUE_FROM> elementConverterValueAdapterToSource )
  {
    super();
    this.sourceMap = sourceMap;
    this.elementConverterKeySourceToAdapter = elementConverterKeySourceToAdapter;
    this.elementConverterKeyAdapterToSource = elementConverterKeyAdapterToSource;
    this.elementConverterValueSourceToAdapter = elementConverterValueSourceToAdapter;
    this.elementConverterValueAdapterToSource = elementConverterValueAdapterToSource;
  }
  
  protected KEY_TO convertKeyFromToKeyTo( KEY_FROM key_FROM )
  {
    //    
    KEY_TO retval = null;
    
    //
    if ( this.elementConverterKeySourceToAdapter != null )
    {
      retval = this.elementConverterKeySourceToAdapter.convert( key_FROM );
    }
    
    //
    return retval;
  }
  
  protected KEY_FROM convertKeyToToKeyFrom( KEY_TO key_TO )
  {
    //    
    KEY_FROM retval = null;
    
    //
    if ( this.elementConverterKeyAdapterToSource != null )
    {
      retval = this.elementConverterKeyAdapterToSource.convert( key_TO );
    }
    
    //
    return retval;
  }
  
  protected VALUE_TO convertValueFromToValueTo( VALUE_FROM value_FROM )
  {
    //    
    VALUE_TO retval = null;
    
    //
    if ( this.elementConverterValueSourceToAdapter != null )
    {
      retval = this.elementConverterValueSourceToAdapter.convert( value_FROM );
    }
    
    //
    return retval;
  }
  
  protected VALUE_FROM convertValueToToValueFrom( VALUE_TO value_TO )
  {
    //    
    VALUE_FROM retval = null;
    
    //
    if ( this.elementConverterValueAdapterToSource != null )
    {
      retval = this.elementConverterValueAdapterToSource.convert( value_TO );
    }
    
    //
    return retval;
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
    return new LinkedHashSet<KEY_TO>( ListUtils.convert( this.sourceMap.keySet(), this.elementConverterKeySourceToAdapter ) );
  }
  
  @Override
  public Collection<VALUE_TO> values()
  {
    return ListUtils.convert( this.sourceMap.values(), this.elementConverterValueSourceToAdapter );
  }
  
}
