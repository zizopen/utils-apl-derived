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
package org.omnaest.utils.structure.table.concrete.internal.iterator;

import java.util.ListIterator;

import org.omnaest.utils.structure.collection.list.iterator.ListIteratorIndexBased;
import org.omnaest.utils.structure.collection.list.iterator.ListIteratorIndexBased.ListIteratorIndexBasedSource;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Row;

/**
 * @see Table
 * @see ListIterator
 * @author Omnaest
 * @param <E>
 */
public class TableRowListIterator<E> implements ListIterator<Row<E>>
{
  /* ********************************************** Variables ********************************************** */
  protected ListIteratorIndexBasedSource<E> listIteratorIndexBasedSource = null;
  protected ListIterator<Row<E>>            listIterator                 = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Adapter for a {@link Table} to act as a {@link ListIteratorIndexBasedSource}
   * 
   * @see TableRowListIterator
   * @author Omnaest
   * @param <E>
   */
  protected static class TableToListIteratorIndexBasedSourceAdapter<E> implements ListIteratorIndexBasedSource<Row<E>>
  {
    /* ********************************************** Variables ********************************************** */
    protected Table<E> table = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @param table
     */
    public TableToListIteratorIndexBasedSourceAdapter( Table<E> table )
    {
      super();
      this.table = table;
    }
    
    @Override
    public int size()
    {
      return this.table.tableSize().getRowSize();
    }
    
    @Override
    public void remove( int indexPosition )
    {
      this.table.removeRow( indexPosition );
    }
    
    @Override
    public Row<E> get( int indexPosition )
    {
      return this.table.getRow( indexPosition );
    }
    
    @Override
    public void set( int indexPosition, Row<E> row )
    {
      if ( row != null )
      {
        this.table.setRowCellElements( indexPosition, row.asNewListOfCellElements() );
      }
    }
    
    @Override
    public void add( int indexPosition, Row<E> row )
    {
      if ( row != null )
      {
        this.table.addRowCellElements( indexPosition, row.asNewListOfCellElements() );
      }
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param table
   */
  public TableRowListIterator( Table<E> table )
  {
    super();
    ListIteratorIndexBasedSource<Row<E>> listIteratorSource = new TableToListIteratorIndexBasedSourceAdapter<E>( table );
    this.listIterator = new ListIteratorIndexBased<Table.Row<E>>( listIteratorSource );
  }
  
  public boolean hasNext()
  {
    return this.listIterator.hasNext();
  }
  
  public Row<E> next()
  {
    return this.listIterator.next();
  }
  
  public boolean hasPrevious()
  {
    return this.listIterator.hasPrevious();
  }
  
  public Row<E> previous()
  {
    return this.listIterator.previous();
  }
  
  public int nextIndex()
  {
    return this.listIterator.nextIndex();
  }
  
  public int previousIndex()
  {
    return this.listIterator.previousIndex();
  }
  
  public void remove()
  {
    this.listIterator.remove();
  }
  
  public void set( Row<E> e )
  {
    this.listIterator.set( e );
  }
  
  public void add( Row<E> e )
  {
    this.listIterator.add( e );
  }
  
}
