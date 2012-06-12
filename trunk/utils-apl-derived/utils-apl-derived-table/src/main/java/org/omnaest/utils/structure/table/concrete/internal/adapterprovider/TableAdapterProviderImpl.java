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
package org.omnaest.utils.structure.table.concrete.internal.adapterprovider;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.TableSize;
import org.omnaest.utils.structure.table.adapter.TableAdapter;
import org.omnaest.utils.structure.table.adapter.TableToMapAdapter;
import org.omnaest.utils.structure.table.adapter.TableToResultSetAdapter;
import org.omnaest.utils.structure.table.adapter.TableToTypeListAdapter;
import org.omnaest.utils.structure.table.subspecification.TableAdaptable.TableAdapterProvider;

/**
 * @see TableAdapterProvider
 * @author Omnaest
 * @param <E>
 */
public class TableAdapterProviderImpl<E> implements TableAdapterProvider<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 4437703836682659106L;
  /* ********************************************** Variables ********************************************** */
  protected Table<E>        table            = null;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @param table
   */
  public TableAdapterProviderImpl( Table<E> table )
  {
    super();
    this.table = table;
  }
  
  @Override
  public ResultSet resultSet()
  {
    return new TableToResultSetAdapter( this.table );
  }
  
  @Override
  public <T> List<T> listOfType( Class<? extends T> beanClass )
  {
    return TableToTypeListAdapter.<T> newInstance( this.table, beanClass );
  }
  
  @Override
  public <A> A adapter( TableAdapter<A, E> tableAdapter )
  {
    //
    A retval = null;
    
    //
    if ( tableAdapter != null )
    {
      retval = tableAdapter.initializeAdapter( this.table );
    }
    
    //
    return retval;
  }
  
  @Override
  public Object[][] array()
  {
    //
    Object[][] retvals = null;
    
    //
    if ( this.table != null )
    {
      //
      final TableSize tableSize = this.table.getTableSize();
      retvals = new Object[tableSize.getRowSize()][tableSize.getColumnSize()];
      
      //
      this.fillArrayFromTableContent( retvals );
    }
    
    // 
    return retvals;
  }
  
  /**
   * Fills the given array with the content of the internal {@link Table}
   * 
   * @param array
   */
  private void fillArrayFromTableContent( Object[][] array )
  {
    if ( array != null && this.table != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < this.table.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        for ( int columnIndexPosition = 0; columnIndexPosition < this.table.getTableSize().getColumnSize(); columnIndexPosition++ )
        {
          array[rowIndexPosition][columnIndexPosition] = this.table.getCellElement( rowIndexPosition, columnIndexPosition );
        }
      }
    }
  }
  
  @Override
  @SuppressWarnings("unchecked")
  public E[][] array( Class<? extends E> clazz )
  {
    //    
    E[][] retvals = null;
    
    //
    if ( this.table != null )
    {
      //
      try
      {
        //
        final TableSize tableSize = this.table.getTableSize();
        
        //
        Object newInstance = Array.newInstance( clazz, tableSize.getRowSize(), tableSize.getColumnSize() );
        retvals = (E[][]) newInstance;
        
        //
        this.fillArrayFromTableContent( retvals );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
    
    // 
    return retvals;
  }
  
  @Override
  public Map<E, E> map( Column<E> columnForKeys, Column<E> columnForValues )
  {
    return new TableToMapAdapter<E, E, E>( columnForKeys, columnForValues ).initializeAdapter( this.table );
  }
  
  @Override
  public <K, V> Map<K, V> map( Column<E> columnForKeys,
                               Column<E> columnForValues,
                               ElementConverter<E, K> elementConverterForKeys,
                               ElementConverter<E, V> elementConverterForValues )
  {
    return new TableToMapAdapter<E, K, V>( columnForKeys, columnForValues, elementConverterForKeys, elementConverterForValues ).initializeAdapter( this.table );
  }
}
