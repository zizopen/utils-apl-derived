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
package org.omnaest.utils.structure.table.concrete.internal.cloner;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Cell;
import org.omnaest.utils.structure.table.Table.TableCellVisitor;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.concrete.internal.helper.TableInternalHelper;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.structure.table.subspecification.TableCloneable.TableCloner;

import com.rits.cloning.Cloner;

/**
 * @see TableCloner
 * @author Omnaest
 * @param <E>
 */
public class TableClonerImpl<E> implements TableCloner<E>
{
  /* ********************************************** Variables ********************************************** */
  protected Table<E> table = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param table
   */
  public TableClonerImpl( Table<E> table )
  {
    this.table = table;
  }
  
  @Override
  public Table<E> structureOnly()
  {
    //
    final Table<E> table = this.table;
    
    //
    ArrayTable<E> retval = new ArrayTable<E>();
    
    //
    TableInternal<E> tableInternalFromRetval = TableInternalHelper.extractTableInternalFromTable( retval );
    TableInternal<E> tableInternalFromTable = TableInternalHelper.extractTableInternalFromTable( table );
    
    tableInternalFromRetval.setTableContent( tableInternalFromTable.getTableContent().cloneStructure() );
    retval.setTableName( table.getTableName() );
    
    //
    return retval;
  }
  
  @Override
  public Table<E> structureAndContent()
  {
    //
    final Table<E> table = this.table;
    
    //
    ArrayTable<E> retval = new ArrayTable<E>();
    retval.setNumberOfColumns( table.getTableSize().getColumnSize() );
    retval.setNumberOfRows( table.getTableSize().getRowSize() );
    TableCellVisitor<E> tableCellVisitor = new TableCellVisitor<E>()
    {
      /* ********************************************** Variables ********************************************** */
      private Cloner cloner = new Cloner();
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell )
      {
        //
        E elementClone = null;
        
        //
        E cellElement = table.getCellElement( rowIndexPosition, columnIndexPosition );
        if ( cellElement != null )
        {
          //
          elementClone = this.cloner.deepClone( cellElement );
        }
        
        //
        cell.setElement( elementClone );
      }
    };
    retval.processTableCells( tableCellVisitor );
    retval.setColumnTitleValues( table.getColumnTitleValueList() );
    retval.setRowTitleValues( table.getRowTitleValueList() );
    retval.setTableName( table.getTableName() );
    
    //
    return retval;
  }
  
}
