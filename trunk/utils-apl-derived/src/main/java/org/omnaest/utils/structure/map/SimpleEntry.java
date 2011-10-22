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
package org.omnaest.utils.structure.map;

import java.util.Map.Entry;

/**
 * Simple entity implementation for an {@link Entry}
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class SimpleEntry<K, V> implements Entry<K, V>
{
  /* ********************************************** Variables ********************************************** */
  protected K key   = null;
  protected V value = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param key
   * @param value
   */
  public SimpleEntry( K key, V value )
  {
    super();
    this.key = key;
    this.value = value;
  }
  
  @Override
  public K getKey()
  {
    return this.key;
  }
  
  @Override
  public V getValue()
  {
    return this.value;
  }
  
  @Override
  public V setValue( V value )
  {
    //
    V retval = this.value;
    
    //
    this.value = value;
    
    //
    return retval;
  }
  
}
