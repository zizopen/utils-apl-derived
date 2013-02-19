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
package org.omnaest.utils.structure.element.converter;

import java.util.Collections;
import java.util.Set;

/**
 * {@link ElementBidirectionalConverter} which makes use of {@link Collections#unmodifiableSet(Set)} within its
 * {@link #convert(Set)} method but not in its {@link #convertBackwards(Set)} method
 * 
 * @author Omnaest
 * @param <E>
 */
public class ElementBidirectionalConverterSetToUnmodifiableSet<E> implements
                                                                  ElementBidirectionalConverterSerializable<Set<E>, Set<E>>
{
  private static final long serialVersionUID = -8723454285457115920L;
  
  @Override
  public Set<E> convertBackwards( Set<E> element )
  {
    return element;
  }
  
  @Override
  public Set<E> convert( Set<E> element )
  {
    return Collections.unmodifiableSet( element );
  }
}
