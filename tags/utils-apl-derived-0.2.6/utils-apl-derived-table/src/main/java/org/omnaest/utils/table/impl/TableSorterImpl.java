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

import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.collections.ComparatorUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.element.cached.CachedElement;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;
import org.omnaest.utils.table.ImmutableColumn;
import org.omnaest.utils.table.StripeEntity;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.TableExecution;
import org.omnaest.utils.table.TableSorter;

/**
 * {@link TableSorter} implementation
 * 
 * @author Omnaest
 * @param <E>
 */
final class TableSorterImpl<E> implements TableSorter<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 7658083106342074763L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private boolean           useTableLock     = false;
  @SuppressWarnings("unchecked")
  private Comparator<E>     comparator       = ComparatorUtils.NATURAL_COMPARATOR;
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  private final Table<E>    table;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static final class SortExecution<E> implements TableExecution<Table<E>, E>
  {
    private final Comparator<StripeEntity<E>> rowIndexComparator;
    private final int                         rowSize;
    
    SortExecution( Comparator<StripeEntity<E>> rowIndexComparator, int rowSize )
    {
      this.rowIndexComparator = rowIndexComparator;
      this.rowSize = rowSize;
    }
    
    @Override
    public void execute( final Table<E> table )
    {
      Collections.sort( new ListAbstract<StripeEntity<E>>()
      {
        private static final long serialVersionUID = -4874801955229108199L;
        
        @Override
        public int indexOf( Object o )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public int lastIndexOf( Object o )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public int size()
        {
          return SortExecution.this.rowSize;
        }
        
        @Override
        public boolean add( StripeEntity<E> e )
        {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public StripeEntity<E> get( int index )
        {
          return table.row( index ).to().entity();
        }
        
        @Override
        public StripeEntity<E> set( int index, StripeEntity<E> stripeEntity )
        {
          StripeEntity<E> retval = this.get( index );
          table.setRowElements( index, stripeEntity.getElements() );
          table.setRowTitle( index, stripeEntity.getTitle() );
          return retval;
        }
        
        @Override
        public void add( int index, StripeEntity<E> element )
        {
          throw new UnsupportedOperationException();
          
        }
        
        @Override
        public StripeEntity<E> remove( int index )
        {
          throw new UnsupportedOperationException();
        }
        
      }, this.rowIndexComparator );
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableSorterImpl
   * @param table
   */
  TableSorterImpl( Table<E> table )
  {
    this.table = table;
  }
  
  @Override
  public Table<E> by( final ImmutableColumn<E> column )
  {
    
    final int rowSize = this.table.rowSize();
    final Comparator<StripeEntity<E>> rowIndexComparator = new Comparator<StripeEntity<E>>()
    {
      private CachedElement<Integer> columnIndex = new CachedElement<Integer>( new ValueResolver<Integer>()
                                                 {
                                                   @Override
                                                   public Integer resolveValue()
                                                   {
                                                     return column.index();
                                                   }
                                                 } );
      
      @Override
      public int compare( StripeEntity<E> stripeEntity1, StripeEntity<E> stripeEntity2 )
      {
        final int columnIndex = this.columnIndex.getValue();
        E element1 = stripeEntity1.getElements()[columnIndex];
        E element2 = stripeEntity2.getElements()[columnIndex];
        
        return TableSorterImpl.this.comparator.compare( element1, element2 );
      }
    };
    
    final SortExecution<E> tableExecution = new SortExecution<E>( rowIndexComparator, rowSize );
    if ( this.useTableLock )
    {
      this.table.executeWithWriteLock( tableExecution );
    }
    else
    {
      tableExecution.execute( this.table );
    }
    
    return this.table;
  }
  
  @Override
  public Table<E> by( final int columnIndex )
  {
    return this.by( this.table.column( columnIndex ) );
  }
  
  @Override
  public TableSorter<E> using( Comparator<E> comparator )
  {
    Assert.isNotNull( comparator, "Comparator must not be null" );
    this.comparator = comparator;
    return this;
  }
  
  @Override
  public TableSorter<E> withTableLock()
  {
    this.useTableLock = true;
    return this;
  }
}
