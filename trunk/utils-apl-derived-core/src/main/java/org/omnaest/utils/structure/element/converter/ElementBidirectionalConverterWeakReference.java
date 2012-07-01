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

import java.lang.ref.WeakReference;


public class ElementBidirectionalConverterWeakReference<E> implements ElementBidirectionalConverter<WeakReference<E>, E>
{
  @Override
  public E convert( WeakReference<E> element )
  {
    return element != null ? element.get() : null;
  }
  
  @Override
  public WeakReference<E> convertBackwards( E element )
  {
    return new WeakReference<E>( element );
  }
}
