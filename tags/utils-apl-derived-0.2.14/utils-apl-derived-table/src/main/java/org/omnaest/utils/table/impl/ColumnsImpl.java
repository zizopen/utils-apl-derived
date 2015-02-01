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
package org.omnaest.utils.table.impl;

import java.util.BitSet;
import java.util.Iterator;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table.Column;
import org.omnaest.utils.table.Columns;
import org.omnaest.utils.table.Stripe;
import org.omnaest.utils.table.StripesTransformer;

/**
 * @author Omnaest
 * @param <E>
 * @param <R>
 */
class ColumnsImpl<E> implements Columns<E, Column<E>>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Iterable<Column<E>> columnIterable;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see ColumnsImpl
   * @param columnIterable
   */
  ColumnsImpl( Iterable<Column<E>> columnIterable )
  {
    super();
    this.columnIterable = columnIterable;
  }
  
  @Override
  public Iterator<Column<E>> iterator()
  {
    return this.columnIterable.iterator();
  }
  
  @Override
  public Columns<E, Column<E>> filtered( BitSet filter )
  {
    final Iterable<Column<E>> rowIterable = IterableUtils.filtered( this.columnIterable, filter );
    return new ColumnsImpl<E>( rowIterable );
  }
  
  @Override
  public StripesTransformer<E> to()
  {
    final Iterable<? extends Stripe<E>> stripeIterable = this.columnIterable;
    return new StripesTransformerImpl<E>( stripeIterable );
  }
  
  @Override
  public Columns<E, Column<E>> apply( ElementConverter<E, E> elementConverter )
  {
    for ( Column<E> column : this.columnIterable )
    {
      column.apply( elementConverter );
    }
    return this;
  }
  
}
