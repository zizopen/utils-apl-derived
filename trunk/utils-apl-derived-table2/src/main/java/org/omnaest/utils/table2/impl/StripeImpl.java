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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.ImmutableStripe;
import org.omnaest.utils.table2.Stripe;
import org.omnaest.utils.table2.StripeTransformer;
import org.omnaest.utils.table2.Table;

/**
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
public abstract class StripeImpl<E> implements Stripe<E>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected volatile boolean isDeleted  = false;
  protected volatile boolean isModified = false;
  
  protected final Table<E>   table;
  
  /* *************************************************** Methods **************************************************** */
  
  public StripeImpl( Table<E> table )
  {
    super();
    this.table = table;
  }
  
  public Iterator<E> iterator()
  {
    final int indexMax = this.size() - 1;
    return new Iterator<E>()
    {
      private int index = -1;
      
      @Override
      public boolean hasNext()
      {
        return this.index + 1 <= indexMax;
      }
      
      @Override
      public E next()
      {
        return getCellElement( ++this.index );
      }
      
      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
  
  @Override
  public boolean isDeleted()
  {
    return this.isDeleted;
  }
  
  @Override
  public boolean isModified()
  {
    return this.isModified;
  }
  
  @Override
  public Iterable<Cell<E>> cells()
  {
    final List<Cell<E>> retlist = new ArrayList<Cell<E>>();
    final int size = this.size();
    for ( int ii = 0; ii < size; ii++ )
    {
      retlist.add( this.getCell( ii ) );
    }
    return retlist;
  }
  
  @Override
  public Table<E> table()
  {
    return this.table;
  }
  
  @Override
  public boolean equalsInContent( ImmutableStripe<E> stripe )
  {
    boolean retval = stripe != null;
    
    if ( retval )
    {
      final int sizeOther = stripe.size();
      retval &= this.size() == sizeOther;
      if ( retval )
      {
        for ( int ii = 0; ii < sizeOther; ii++ )
        {
          final E cellElementOther = stripe.getCellElement( ii );
          final E cellElementThis = this.getCellElement( ii );
          if ( !ObjectUtils.equals( cellElementThis, cellElementOther ) )
          {
            retval = false;
            break;
          }
        }
      }
    }
    
    return retval;
  }
  
  @Override
  public StripeTransformer<E> to()
  {
    final ImmutableStripe<E> stripe = this;
    final Table<E> table = this.table;
    return new StripeTransformer<E>()
    {
      @Override
      public Set<E> set()
      {
        return SetUtils.valueOf( stripe );
      }
      
      @Override
      public List<E> list()
      {
        return ListUtils.valueOf( stripe );
      }
      
      @Override
      public E[] array()
      {
        return ArrayUtils.valueOf( stripe, table.elementType() );
      }
    };
  }
  
}
