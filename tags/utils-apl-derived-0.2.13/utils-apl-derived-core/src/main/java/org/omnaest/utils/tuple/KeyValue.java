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
package org.omnaest.utils.tuple;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Two arguments {@link Tuple} supporting {@link #hashCode()}, {@link #equals(Object)} and {@link #toString()} using the values of
 * the given elements.
 * 
 * @see Tuple2
 * @author Omnaest
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class KeyValue<K, V> implements Tuple
{
  private static final long serialVersionUID = 7098063453458668698L;
  
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected K               key              = null;
  
  @XmlElement
  protected V               value            = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see KeyValue
   */
  public KeyValue()
  {
  }
  
  /**
   * @see KeyValue
   * @param key
   * @param value
   */
  public KeyValue( K key, V value )
  {
    this.key = key;
    this.value = value;
  }
  
  /**
   * Creates a new {@link KeyValue} instance based on the values of an already existing instance
   * 
   * @see KeyValue
   * @param keyValue
   */
  public KeyValue( KeyValue<K, V> keyValue )
  {
    if ( keyValue != null )
    {
      this.key = keyValue.key;
      this.value = keyValue.value;
    }
  }
  
  /**
   * Returns a new {@link KeyValue} instance with inverted first and second value
   * 
   * @return {@link KeyValue}
   */
  public KeyValue<V, K> newInvertedInstance()
  {
    return new KeyValue<V, K>( this.value, this.key );
  }
  
  /**
   * Returns the key
   * 
   * @return
   */
  public K getKey()
  {
    return this.key;
  }
  
  /**
   * Sets the key
   * 
   * @param key
   */
  public void setKey( K key )
  {
    this.key = key;
  }
  
  /**
   * Returns the value
   * 
   * @return
   */
  public V getValue()
  {
    return this.value;
  }
  
  /**
   * Sets the value
   * 
   * @param value
   */
  public void setValue( V value )
  {
    this.value = value;
  }
  
  /**
   * Returns a {@link Map} containing an entry based on this {@link KeyValue}
   * 
   * @return
   */
  public Map<K, V> asMap()
  {
    //
    Map<K, V> retmap = new HashMap<K, V>();
    retmap.put( this.key, this.value );
    return retmap;
  }
  
  @Override
  public String toString()
  {
    return "[" + this.key + "," + this.value + "]";
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.key == null ) ? 0 : this.key.hashCode() );
    result = prime * result + ( ( this.value == null ) ? 0 : this.value.hashCode() );
    return result;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    if ( this == obj )
    {
      return true;
    }
    if ( obj == null )
    {
      return false;
    }
    if ( !( obj instanceof KeyValue ) )
    {
      return false;
    }
    @SuppressWarnings("rawtypes")
    KeyValue other = (KeyValue) obj;
    if ( this.key == null )
    {
      if ( other.key != null )
      {
        return false;
      }
    }
    else if ( !this.key.equals( other.key ) )
    {
      return false;
    }
    if ( this.value == null )
    {
      if ( other.value != null )
      {
        return false;
      }
    }
    else if ( !this.value.equals( other.value ) )
    {
      return false;
    }
    return true;
  }
  
}
