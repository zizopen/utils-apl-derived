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
package org.omnaest.utils.structure.table.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.MapToTypeAdapter;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Row;
import org.omnaest.utils.structure.table.subspecification.TableAdaptable.TableAdapterProvider;

/**
 * <br>
 * A {@link TableToTypeListAdapter} treats the horizontal {@link Row}s of the {@link Table} as Java Beans. The {@link Column}
 * titles of the {@link Table} have to be equal to (all or a subset) of the property names of the given {@link Table}, since the
 * property values of the Java Bean object will be written to the {@link Column} with the respective title.
 * 
 * @see TableAdapter
 * @see #newInstance(Table, Class)
 * @author Omnaest
 * @param <B>
 *          Java Bean type
 */
public class TableToTypeListAdapter<B> extends ListAbstract<B> implements TableAdapter<List<B>, Object>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -5895678029582731115L;
  /* ********************************************** Variables ********************************************** */
  protected Table<Object>   table            = null;
  protected Class<B>        beanClass        = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new {@link TableToTypeListAdapter} with the given {@link Table} as underlying {@link Table} object.
   * 
   * @param table
   * @param beanClass
   * @see #newInstance(Table, Class)
   */
  @SuppressWarnings("unchecked")
  protected TableToTypeListAdapter( Table<? extends Object> table, Class<? extends B> beanClass )
  {
    super();
    this.table = (Table<Object>) table;
    this.beanClass = (Class<B>) beanClass;
  }
  
  /**
   * Creates a new {@link TableAdapter} for use with {@link TableAdapterProvider#adapter(TableAdapter)} only. For normal usage see
   * {@link #newInstance(Table, Class)} instead.
   * 
   * @see #newInstance(Table, Class)
   * @see #initializeAdapter(Table)
   * @param table
   * @param beanClass
   * @see #newInstance(Table, Class)
   */
  @SuppressWarnings("unchecked")
  public TableToTypeListAdapter( Class<? extends B> beanClass )
  {
    super();
    this.beanClass = (Class<B>) beanClass;
  }
  
  /**
   * Factory methods which creates a new {@link TableToTypeListAdapter} instance for the given Java Bean {@link Class} and
   * {@link Table} .
   * 
   * @param table
   * @param beanClass
   * @param <B>
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> TableToTypeListAdapter<B> newInstance( Table<?> table, Class<? extends B> beanClass )
  {
    //    
    TableToTypeListAdapter<B> retval = null;
    
    //
    if ( table != null && beanClass != null )
    {
      retval = new TableToTypeListAdapter<B>( table, beanClass );
      retval.initializeAdapter( (Table<Object>) table );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the underlying {@link Table} data structure.
   * 
   * @return
   */
  public Table<Object> getTable()
  {
    return this.table;
  }
  
  @Override
  public int size()
  {
    return this.table.getTableSize().getRowSize();
  }
  
  @Override
  public boolean add( B e )
  {
    return this.addRow( this.size(), e ) != null;
  }
  
  /**
   * Adds a new {@link Row} to the underlying {@link Table} based on the values of the given Java Bean at the given row index
   * position.
   * 
   * @param rowIndexPosition
   * @param bean
   * @return managed Java Bean instance, which reflects all changes to the new created {@link Row} immediately and vice versa.
   */
  public B addRow( int rowIndexPosition, B bean )
  {
    //
    B rowBean = null;
    
    //
    if ( bean != null )
    {
      
      //
      List<Object> rowCellElementList = new ArrayList<Object>();
      {
        //
        List<Object> columnTitleValueList = this.table.getColumnTitleValueList();
        
        //
        Map<String, Object> propertyNameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( bean );
        for ( Object columnTitleValue : columnTitleValueList )
        {
          if ( propertyNameToBeanPropertyValueMap.containsKey( columnTitleValue ) )
          {
            Object value = propertyNameToBeanPropertyValueMap.get( columnTitleValue );
            rowCellElementList.add( value );
          }
          else
          {
            rowCellElementList.add( null );
          }
        }
        
        //
        Collection<String> remainingPropertyNameList = propertyNameToBeanPropertyValueMap.keySet();
        remainingPropertyNameList.removeAll( columnTitleValueList );
        for ( String propertyName : remainingPropertyNameList )
        {
          Object value = propertyNameToBeanPropertyValueMap.get( propertyName );
          rowCellElementList.add( value );
        }
      }
      
      //
      this.table.addRowCellElements( rowIndexPosition, rowCellElementList );
      
      //
      rowBean = this.get( rowIndexPosition );
    }
    
    //
    return rowBean;
  }
  
  @Override
  public void clear()
  {
    this.table.clear();
  }
  
  /**
   * Returns the {@link Row} for the given row index position as Java Bean. All changes to this Java Bean will be reflected within
   * the {@link Row} of the {@link Table}
   * 
   * @param rowIndexPosition
   * @return
   */
  @Override
  public B get( int rowIndexPosition )
  {
    //
    B retval = null;
    
    //
    Row<Object> row = this.table.getRow( rowIndexPosition );
    if ( row != null )
    {
      Map<Object, Object> map = new RowToMapAdapter<Object>( row );
      retval = MapToTypeAdapter.newInstance( map, this.beanClass );
    }
    
    return retval;
  }
  
  /**
   * Sets the {@link Row} of the underling {@link Table} to the values of the given Java Bean.
   * 
   * @param rowIndexPosition
   * @param bean
   * @return a new managed bean instance with the same values which act as proxy for the addressed {@link Row}. This means changes
   *         to this instance will be reflected immediately to the {@link Row} and vice versa.
   */
  @Override
  public B set( int rowIndexPosition, B bean )
  {
    //
    B rowBean = this.get( rowIndexPosition );
    
    //
    if ( rowBean != null && bean != null )
    {
      BeanUtils.copyPropertyValues( bean, rowBean );
    }
    
    //
    return rowBean;
  }
  
  @Override
  public void add( int index, B element )
  {
    this.addRow( index, element );
  }
  
  @Override
  public B remove( int index )
  {
    //
    B retval = null;
    
    //
    B element = this.get( index );
    if ( element != null )
    {
      //
      Map<String, Object> propertyNameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( element );
      retval = MapToTypeAdapter.newInstance( propertyNameToBeanPropertyValueMap, this.beanClass );
      
      //
      this.table.removeRow( index );
    }
    
    //
    return retval;
  }
  
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
  public List<B> initializeAdapter( Table<Object> table )
  {
    this.table = table;
    return this;
  }
}
