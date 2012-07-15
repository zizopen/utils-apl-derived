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
package org.omnaest.utils.table2.impl;

import java.util.BitSet;
import java.util.Iterator;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.Row;
import org.omnaest.utils.table2.Rows;
import org.omnaest.utils.table2.StripesTransformer;

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
   * @param table
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
    final Iterable<Row<E>> rowIterable = this.rowIterable;
    return new StripesTransformer<E>()
    {
      private static final long serialVersionUID = 2110974282111579037L;
      
      @Override
      public <T> Iterable<T> types( final Class<T> type )
      {
        final ElementConverter<Row<E>, T> elementConverter = new ElementConverterSerializable<Row<E>, T>()
        {
          private static final long serialVersionUID = -4832586956691329459L;
          
          @Override
          public T convert( Row<E> row )
          {
            return row.to().type( type );
          }
        };
        return IterableUtils.adapter( rowIterable, elementConverter );
      }
    };
  }
  
}
