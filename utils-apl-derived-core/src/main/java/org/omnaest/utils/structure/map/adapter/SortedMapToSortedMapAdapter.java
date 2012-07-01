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

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * The {@link SortedMapToSortedMapAdapter} is a decorator and adapter which allows to access a given {@link SortedMap} using other
 * types for the values. <br>
 * Since a {@link SortedMap} is sorted by the keys it is not possible to create an adapter for the keys.<br>
 * The adapter instance is always backed by the original {@link SortedMap} and changes on both sides will be traversed to each
 * other side.
 * 
 * @author Omnaest
 * @param <KEY>
 * @param <VALUE_FROM>
 * @param <VALUE_TO>
 */
public class SortedMapToSortedMapAdapter<KEY, VALUE_FROM, VALUE_TO> implements SortedMap<KEY, VALUE_TO>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long                                   serialVersionUID = -1538459740184023592L;
  
  /* ********************************************** Variables ********************************************** */
  private SortedMap<KEY, VALUE_FROM>                          sourceMap        = null;
  
  private ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SortedMapToSortedMapAdapter
   * @param sourceMap
   * @param elementBidirectionalConverterValue
   */
  public SortedMapToSortedMapAdapter( SortedMap<KEY, VALUE_FROM> sourceMap,
                                      ElementBidirectionalConverter<VALUE_FROM, VALUE_TO> elementBidirectionalConverterValue )
  {
    super();
    
    Assert.isNotNull( sourceMap, elementBidirectionalConverterValue );
    
    this.sourceMap = sourceMap;
    this.elementBidirectionalConverterValue = elementBidirectionalConverterValue;
  }
  
  protected VALUE_TO convertValueFromToValueTo( VALUE_FROM value_FROM )
  {
    return this.elementBidirectionalConverterValue.convert( value_FROM );
  }
  
  protected VALUE_FROM convertValueToToValueFrom( VALUE_TO value_TO )
  {
    return this.elementBidirectionalConverterValue.convertBackwards( value_TO );
  }
  
  @Override
  public VALUE_TO get( Object key )
  {
    //
    VALUE_TO retval = null;
    
    //
    if ( key != null && this.sourceMap != null )
    {
      VALUE_FROM value_FROM = this.sourceMap.get( key );
      retval = this.convertValueFromToValueTo( value_FROM );
    }
    
    //
    return retval;
  }
  
  @Override
  public VALUE_TO put( KEY key, VALUE_TO value )
  {
    //
    VALUE_TO retval = null;
    
    //
    if ( key != null && this.sourceMap != null )
    {
      //
      VALUE_FROM convertedValue = this.convertValueToToValueFrom( value );
      VALUE_FROM value_FROM = this.sourceMap.put( key, convertedValue );
      retval = this.convertValueFromToValueTo( value_FROM );
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
      VALUE_FROM value_FROM = this.sourceMap.remove( key );
      retval = this.convertValueFromToValueTo( value_FROM );
    }
    
    //
    return retval;
  }
  
  @Override
  public Set<KEY> keySet()
  {
    return this.sourceMap.keySet();
  }
  
  @Override
  public Collection<VALUE_TO> values()
  {
    return CollectionUtils.adapter( this.sourceMap.values(), this.elementBidirectionalConverterValue );
  }
  
  @Override
  public int size()
  {
    return this.sourceMap.size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.sourceMap.isEmpty();
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.sourceMap.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    try
    {
      @SuppressWarnings("unchecked")
      VALUE_FROM value_FROM = this.convertValueToToValueFrom( (VALUE_TO) value );
      return this.sourceMap.containsValue( value_FROM );
    }
    catch ( Exception e )
    {
      return false;
    }
  }
  
  @Override
  public void putAll( Map<? extends KEY, ? extends VALUE_TO> map )
  {
    if ( map != null )
    {
      Set<? extends Entry<? extends KEY, ? extends VALUE_TO>> entrySet = map.entrySet();
      if ( entrySet != null )
      {
        for ( Entry<? extends KEY, ? extends VALUE_TO> entry : entrySet )
        {
          if ( entry != null )
          {
            VALUE_TO value = entry.getValue();
            KEY key = entry.getKey();
            this.put( key, value );
          }
        }
      }
    }
  }
  
  @Override
  public void clear()
  {
    this.sourceMap.clear();
  }
  
  @Override
  public Comparator<? super KEY> comparator()
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public SortedMap<KEY, VALUE_TO> subMap( KEY fromKey, KEY toKey )
  {
    return MapUtils.adapter( this.sourceMap.subMap( fromKey, toKey ), this.elementBidirectionalConverterValue );
  }
  
  @Override
  public SortedMap<KEY, VALUE_TO> headMap( KEY toKey )
  {
    return MapUtils.adapter( this.sourceMap.headMap( toKey ), this.elementBidirectionalConverterValue );
  }
  
  @Override
  public SortedMap<KEY, VALUE_TO> tailMap( KEY fromKey )
  {
    return MapUtils.adapter( this.sourceMap.tailMap( fromKey ), this.elementBidirectionalConverterValue );
  }
  
  @Override
  public KEY firstKey()
  {
    return this.sourceMap.firstKey();
  }
  
  @Override
  public KEY lastKey()
  {
    return this.sourceMap.lastKey();
  }
  
  @Override
  public Set<Map.Entry<KEY, VALUE_TO>> entrySet()
  {
    ElementBidirectionalConverter<Entry<KEY, VALUE_FROM>, Entry<KEY, VALUE_TO>> elementBidirectionalConverter = new ElementBidirectionalConverter<Map.Entry<KEY, VALUE_FROM>, Map.Entry<KEY, VALUE_TO>>()
    {
      @Override
      public java.util.Map.Entry<KEY, VALUE_TO> convert( final Map.Entry<KEY, VALUE_FROM> entry )
      {
        return new Map.Entry<KEY, VALUE_TO>()
        {
          
          @Override
          public KEY getKey()
          {
            return entry.getKey();
          }
          
          @Override
          public VALUE_TO getValue()
          {
            VALUE_FROM value_FROM = entry.getValue();
            VALUE_TO value_TO = convertValueFromToValueTo( value_FROM );
            return value_TO;
          }
          
          @Override
          public VALUE_TO setValue( VALUE_TO value )
          {
            VALUE_FROM value_FROM = convertValueToToValueFrom( value );
            VALUE_FROM setValue = entry.setValue( value_FROM );
            return convertValueFromToValueTo( setValue );
          }
        };
      }
      
      @Override
      public java.util.Map.Entry<KEY, VALUE_FROM> convertBackwards( java.util.Map.Entry<KEY, VALUE_TO> element )
      {
        throw new UnsupportedOperationException();
      }
    };
    return SetUtils.adapter( this.sourceMap.entrySet(), elementBidirectionalConverter );
  }
}
