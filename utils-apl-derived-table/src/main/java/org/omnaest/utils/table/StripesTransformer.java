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
package org.omnaest.utils.table;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Omnaest
 */
public interface StripesTransformer<E> extends Serializable
{
  /**
   * @see StripeTransformer#instanceOf(Class)
   * @param type
   * @return
   */
  public <T> Iterable<T> instancesOf( Class<T> type );
  
  /**
   * Returns the {@link StripeTransformer#map()}s
   * 
   * @return {@link Iterable} of {@link Map}s
   */
  public Iterable<Map<String, E>> maps();
}
