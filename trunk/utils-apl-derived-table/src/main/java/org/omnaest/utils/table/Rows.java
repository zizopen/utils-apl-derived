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

import java.util.BitSet;

import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * {@link Iterable} over {@link Row}s
 * 
 * @author Omnaest
 * @param <E>
 */
public interface Rows<E, R extends ImmutableRow<E>> extends Stripes<E, R>
{
  
  /**
   * Returns new {@link Rows} filtered by the given {@link BitSet} filter.
   * 
   * @param filter
   * @return new {@link Rows}
   */
  @Override
  public Rows<E, R> filtered( BitSet filter );
  
  /**
   * Returns a new {@link StripesTransformer} instance
   * 
   * @return
   */
  @Override
  public StripesTransformer<E> to();
  
  /**
   * Applies the given {@link ElementConverter} to all the underlying {@link Row}s
   * 
   * @param elementConverter
   *          {@link ElementConverter}
   * @return this
   */
  @Override
  public Rows<E, R> apply( ElementConverter<E, E> elementConverter );
}
