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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.table2.Cell;
import org.omnaest.utils.table2.ImmutableStripe;
import org.omnaest.utils.table2.Stripe;
import org.omnaest.utils.table2.StripeEntity;
import org.omnaest.utils.table2.StripeTransformer;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableEventHandler;

/**
 * @see Stripe
 * @author Omnaest
 * @param <E>
 */
abstract class StripeImpl<E> implements Stripe<E>, TableEventHandler<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long  serialVersionUID = 3138389285052519615L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected volatile boolean isDeleted        = false;
  protected volatile boolean isModified       = false;
  protected volatile boolean isDetached       = false;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  protected final Table<E>   table;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see StripeImpl
   * @param table
   * @param isDetached
   */
  StripeImpl( Table<E> table, boolean isDetached )
  {
    super();
    this.table = table;
    this.isDetached = isDetached;
  }
  
  /**
   * 
   */
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
        return getElement( ++this.index );
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
      retlist.add( this.cell( ii ) );
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
          final E cellElementOther = stripe.getElement( ii );
          final E cellElementThis = this.getElement( ii );
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
    final String[] orthogonalTitles = this.getOrthogonalTitles();
    return new StripeTransformer<E>()
    {
      private static final long serialVersionUID = 4473192340081669345L;
      
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
      
      @Override
      public Map<String, E> map()
      {
        Map<String, E> retmap = new LinkedHashMap<String, E>();
        for ( String title : orthogonalTitles )
        {
          final E element = stripe.getElement( title );
          retmap.put( title, element );
        }
        return retmap;
      }
      
      @Override
      public StripeEntity<E> entity()
      {
        final String title = getTitle();
        final E[] elements = getElements();
        return new StripeEntity<E>( title, elements );
      }
      
      @Override
      public <T> T instanceOf( Class<T> type )
      {
        return table.transformStripeInto( type, stripe );
      }
      
      @Override
      public <T> T instance( T instance )
      {
        return table.transformStripeInto( instance, StripeImpl.this );
      }
    };
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public E[] getElements()
  {
    final E[] retvals = (E[]) Array.newInstance( this.table.elementType(), this.size() );
    for ( int ii = 0; ii < retvals.length; ii++ )
    {
      retvals[ii] = this.getElement( ii );
    }
    return retvals;
  }
  
  protected abstract String[] getOrthogonalTitles();
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "StripeImpl [isDeleted=" );
    builder.append( this.isDeleted );
    builder.append( ", isModified=" );
    builder.append( this.isModified );
    builder.append( ", elements=" );
    builder.append( Arrays.deepToString( this.getElements() ) );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public boolean isDetached()
  {
    return this.isDetached;
  }
  
  @Override
  public Stripe<E> detach()
  {
    this.table.tableEventHandlerRegistration().detach( this );
    this.isDetached = true;
    return this;
  }
}
