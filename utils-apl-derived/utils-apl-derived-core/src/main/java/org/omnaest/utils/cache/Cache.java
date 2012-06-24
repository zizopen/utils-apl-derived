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

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Cache} is a container which allows to hold key value pairs. The makes it technically very similar to a {@link Map} so
 * it does actually implements the {@link Map} interface.<br>
 * <br>
 * Nevertheless a {@link Map} is more general than a {@link Cache}. A {@link Cache} has more a focus on the <b>lifecycle</b> of a
 * stored element. So some {@link Cache} implementations will e.g. clear elements from its store, which are not requested for a
 * given amount of time. <br>
 * <br>
 * Do to the possibly fast clearing of elements the {@link #keySet()}, {@link #values()} and {@link #entrySet()} returned are
 * unmodifiable and represent only a snapshot of the {@link Cache} state when the function call was made. The returned
 * {@link Collection}s are not updated when the {@link Cache} content changes. <br>
 * <br>
 * Note: this {@link Cache} represents a lightweight contract which is intended to be <b>easily implementable</b>. If a more
 * feature rich cache contract is needed take a look at {@link com.google.common.cache.Cache}.
 * 
 * @see CacheUtils
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public interface Cache<K, V> extends Map<K, V> , Serializable
{
  /**
   * Returns an unmodifiable {@link Set} of all keys within the {@link Cache} which only represents a snapshot in moment
   */
  @Override
  public Set<K> keySet();
  
  /**
   * Returns an unmodifiable {@link Collection} of all values within the {@link Cache} which only represents a snapshot in moment
   */
  @Override
  public Collection<V> values();
  
  /**
   * Returns an unmodifiable {@link Set} of all {@link java.util.Map.Entry}s within the {@link Cache} which only represents a
   * snapshot in moment
   */
  @Override
  public Set<Entry<K, V>> entrySet();
}
