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
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.Rows;
import org.omnaest.utils.table.Stripe;
import org.omnaest.utils.table.StripesTransformer;

/**
 * @author Omnaest
 * @param <E>
 * @param <R>
 */
class RowsImpl<E> implements Rows<E, Row<E>>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Iterable<Row<E>> rowIterable;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see RowsImpl
   * @param rowIterable
   */
  RowsImpl( Iterable<Row<E>> rowIterable )
  {
    super();
    this.rowIterable = rowIterable;
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.rowIterable.iterator();
  }
  
  @Override
  public Rows<E, Row<E>> filtered( BitSet filter )
  {
    final Iterable<Row<E>> rowIterable = IterableUtils.filtered( this.rowIterable, filter );
    return new RowsImpl<E>( rowIterable );
  }
  
  @Override
  public StripesTransformer<E> to()
  {
    final Iterable<? extends Stripe<E>> stripeIterable = this.rowIterable;
    return new StripesTransformerImpl<E>( stripeIterable );
  }
  
  @Override
  public Rows<E, Row<E>> apply( ElementConverter<E, E> elementConverter )
  {
    for ( Row<E> row : this.rowIterable )
    {
      row.apply( elementConverter );
    }
    return this;
  }
  
}
