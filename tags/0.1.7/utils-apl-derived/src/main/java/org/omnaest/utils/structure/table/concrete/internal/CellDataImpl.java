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
package org.omnaest.utils.structure.table.concrete.internal;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.internal.TableInternal.CellData;
import org.omnaest.utils.structure.table.internal.TableInternal.CellInternal;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;

/**
 * Wrapper for an element, which can be shared between {@link Table}s to allow the modification from all of them. As a difference
 * to {@link CellImpl} this wrapper has no reference to any {@link StripeData} instance.
 * 
 * @see CellInternal
 * @see CellImpl
 * @see CellData
 * @author Omnaest
 * @param <E>
 */
public class CellDataImpl<E> implements CellData<E>
{
  /* ********************************************** Variables ********************************************** */
  protected E element = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public E getElement()
  {
    return this.element;
  }
  
  @Override
  public void setElement( E element )
  {
    this.element = element;
  }
  
  @Override
  public boolean hasElement( E element )
  {
    return this.element == element || ( this.element != null && this.element.equals( element ) );
  }
  
  @Override
  public String toString()
  {
    return String.valueOf( this.element );
  }
}
