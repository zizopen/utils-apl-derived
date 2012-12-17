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
package org.omnaest.utils.cache;

import java.util.Map;

/**
 * Helper which is related to {@link Cache} implementations
 * 
 * @see Cache
 * @author Omnaest
 */
public class CacheUtils
{
  
  /**
   * Returns an adapter from {@link Map} which acts as a {@link Cache}
   * 
   * @see MapToCacheAdapter
   * @param map
   * @return
   */
  public static <K, V> Cache<K, V> adapter( final Map<? extends K, ? extends V> map )
  {
    return map == null ? null : new MapToCacheAdapter<K, V>( map );
  }
}
