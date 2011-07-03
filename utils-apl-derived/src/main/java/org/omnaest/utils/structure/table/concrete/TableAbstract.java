package org.omnaest.utils.structure.table.concrete;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public abstract class TableAbstract<E> implements TableInternal<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 413819072598819746L;
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public abstract Table<E> clone();
  
  public E getCell( String rowTitle, String columnTitle )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitle ),
                         this.tableHeader.getColumnTitles().indexOf( columnTitle ) );
  }
  
  public E getCell( String rowTitle, int columnIndexPosition )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitle ), columnIndexPosition );
  }
  
  public E getCell( int rowIndexPosition, String columnTitle )
  {
    return this.getCell( rowIndexPosition, this.tableHeader.getColumnTitles().indexOf( columnTitle ) );
  }
  
  public E getCell( Enum<?> rowTitleEnum, Enum<?> columnTitleEnum )
  {
    return this.getCell( rowTitleEnum.name(), columnTitleEnum.name() );
  }
  
  public E getCell( Enum<?> rowTitleEnum, int columnIndexPosition )
  {
    return this.getCell( this.tableHeader.getRowTitles().indexOf( rowTitleEnum.name() ), columnIndexPosition );
  }
  
  public E getCell( int rowIndexPosition, Enum<?> columnTitleEnumeration )
  {
    return this.getCell( rowIndexPosition, this.tableHeader.getColumnTitles().indexOf( columnTitleEnumeration.name() ) );
  }
  
}
