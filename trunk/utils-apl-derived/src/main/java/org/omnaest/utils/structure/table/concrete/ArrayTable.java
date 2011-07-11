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
package org.omnaest.utils.structure.table.concrete;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;
import org.omnaest.utils.structure.table.concrete.internal.CellAndStripeResolverImpl;
import org.omnaest.utils.structure.table.concrete.internal.StripeImpl;
import org.omnaest.utils.structure.table.concrete.internal.StripeListContainerImpl;
import org.omnaest.utils.structure.table.concrete.internal.TableSizeImpl;
import org.omnaest.utils.structure.table.concrete.selection.SelectionImpl;
import org.omnaest.utils.structure.table.internal.TableInternal;
import org.omnaest.utils.xml.XMLHelper;

/**
 * Implementation of {@link Table} that uses two array lists as row and column data structure.
 * 
 * @see Table
 * @author Omnaest
 */
public class ArrayTable<E> extends TableAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long          serialVersionUID      = 1763808639838518679L;
  
  /* ********************************************** Variables ********************************************** */
  protected Object                   tableName             = null;
  protected TableContent<E>          tableContent          = new StripeListContainerImpl<E>( this );
  protected CellAndStripeResolver<E> cellAndStripeResolver = new CellAndStripeResolverImpl<E>( this.tableContent );
  protected TableSize                tableSize             = new TableSizeImpl( this.tableContent );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Transfer data class for XML serialization
   * 
   * @author Omnaest
   * @param <E>
   */
  @XmlRootElement
  protected static class XMLDataContainer<E>
  {
    /* ********************************************** Variables ********************************************** */
    
    @XmlElement
    protected Object            tableName            = null;
    
    @XmlAttribute
    protected Integer           rowSize              = null;
    
    @XmlAttribute
    protected Integer           columnSize           = null;
    
    @XmlElementWrapper
    protected ArrayList<Object> rowTitleValueList    = new ArrayList<Object>();
    
    @XmlElementWrapper
    protected ArrayList<Object> columnTitleValueList = new ArrayList<Object>();
    
    @XmlElementWrapper
    protected ArrayList<E>      cellElementList      = new ArrayList<E>();
    
    protected XMLDataContainer()
    {
      super();
    }
    
    /**
     * @param rowTitleValueList
     * @param columnTitleValueList
     * @param cellElementList
     * @param columnSize
     * @param rowSize
     * @param tableName
     */
    public XMLDataContainer( ArrayList<Object> rowTitleValueList, ArrayList<Object> columnTitleValueList,
                             ArrayList<E> cellElementList, Integer columnSize, Integer rowSize, Object tableName )
    {
      super();
      this.rowTitleValueList = rowTitleValueList;
      this.columnTitleValueList = columnTitleValueList;
      this.cellElementList = cellElementList;
      this.columnSize = columnSize;
      this.rowSize = rowSize;
      this.tableName = tableName;
    }
    
    public Integer getRowSize()
    {
      return this.rowSize;
    }
    
    public Integer getColumnSize()
    {
      return this.columnSize;
    }
    
    public ArrayList<Object> getRowTitleValueList()
    {
      return this.rowTitleValueList;
    }
    
    public ArrayList<Object> getColumnTitleValueList()
    {
      return this.columnTitleValueList;
    }
    
    public ArrayList<E> getCellElementList()
    {
      return this.cellElementList;
    }
    
    public Object getTableName()
    {
      return this.tableName;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  public ArrayTable()
  {
    super();
  }
  
  @Override
  public Table<E> setRowTitleValue( Object titleValue, int rowIndexPosition )
  {
    //
    StripeData<E> rowStripeData = this.cellAndStripeResolver.resolveOrCreateRowStripeData( rowIndexPosition );
    rowStripeData.getTitleInternal().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowTitleValues( List<?> titleValueList )
  {
    //
    this.setStripeTitleValueList( titleValueList, StripeType.ROW );
    
    //
    return this;
  }
  
  /**
   * Sets the {@link Title} elements of the {@link Row}s or {@link Column}s
   * 
   * @param titleValueList
   * @param stripeType
   */
  protected void setStripeTitleValueList( List<? extends Object> titleValueList, StripeType stripeType )
  {
    //
    if ( titleValueList != null && stripeType != null )
    {
      //
      for ( int indexPosition = 0; indexPosition < titleValueList.size(); indexPosition++ )
      {
        //
        StripeData<E> stripe = this.cellAndStripeResolver.resolveOrCreateStripeData( stripeType, indexPosition );
        
        //
        Title title = stripe.getTitleInternal();
        title.setValue( titleValueList.get( indexPosition ) );
      }
    }
  }
  
  @Override
  public Table<E> setColumnTitleValue( Object titleValue, int columnIndexPosition )
  {
    //
    StripeData<E> columnStripeData = this.cellAndStripeResolver.resolveOrCreateColumnStripeData( columnIndexPosition );
    columnStripeData.getTitleInternal().setValue( titleValue );
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnTitleValues( List<?> titleValueList )
  {
    //
    this.setStripeTitleValueList( titleValueList, StripeType.COLUMN );
    
    //
    return this;
  }
  
  @Override
  public List<Object> getRowTitleValueList()
  {
    return this.getStripeTitleValueList( StripeType.ROW );
  }
  
  /**
   * Returns the {@link Title#getValue()} elements within a {@link List}
   * 
   * @param stripeType
   * @return
   */
  protected List<Object> getStripeTitleValueList( StripeType stripeType )
  {
    //
    List<Object> retlist = new ArrayList<Object>();
    
    //
    if ( stripeType != null )
    {
      //
      StripeDataList<E> stripeDataList = this.tableContent.getStripeDataList( stripeType );
      
      //
      for ( StripeData<E> stripeData : stripeDataList )
      {
        //
        retlist.add( stripeData.getTitleInternal().getValue() );
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public Object getRowTitleValue( int rowIndexPosition )
  {
    return this.getStripeTitleValue( StripeType.ROW, rowIndexPosition );
  }
  
  /**
   * Returns the {@link Title#getValue()} object for the given index position within the respective
   * {@link TableContent#getStripeDataList(StripeType)}
   * 
   * @param stripeType
   * @param indexPosition
   * @return
   */
  protected Object getStripeTitleValue( StripeType stripeType, int indexPosition )
  {
    //
    Object retval = null;
    
    //
    if ( stripeType != null && indexPosition >= 0 )
    {
      //
      StripeData<E> stripe = this.cellAndStripeResolver.resolveStripeData( stripeType, indexPosition );
      
      //
      if ( stripe != null )
      {
        retval = stripe.getTitleInternal().getValue();
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public List<Object> getColumnTitleValueList()
  {
    return this.getStripeTitleValueList( StripeType.COLUMN );
  }
  
  @Override
  public Object getColumnTitleValue( int columnIndexPosition )
  {
    return this.getStripeTitleValue( StripeType.COLUMN, columnIndexPosition );
  }
  
  @Override
  public TableSize getTableSize()
  {
    return this.tableSize;
  }
  
  @Override
  public Table<E> setCellElement( int rowIndexPosition, int columnIndexPosition, E element )
  {
    //
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCell( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    if ( rowCellElementList != null )
    {
      for ( int columnIndexPosition = 0; columnIndexPosition < rowCellElementList.size(); columnIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, rowCellElementList.get( columnIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> setColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //
    if ( columnCellElementList != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < columnCellElementList.size(); rowIndexPosition++ )
      {
        this.setCellElement( rowIndexPosition, columnIndexPosition, columnCellElementList.get( rowIndexPosition ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( rowIndexPosition, columnIndexPosition );
  }
  
  @Override
  public E getCellElement( int rowIndexPosition, int columnIndexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( rowIndexPosition, columnIndexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> addColumnCellElements( List<? extends E> columnCellElementList )
  {
    //
    int columnIndexPosition = this.tableSize.getColumnSize();
    this.setColumnCellElements( columnIndexPosition, columnCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addColumnCellElements( int columnIndexPosition, List<? extends E> columnCellElementList )
  {
    //    
    StripeData<E> newStripe = this.tableContent.getColumnList().addNewStripe( columnIndexPosition );
    if ( newStripe != null )
    {
      this.setColumnCellElements( columnIndexPosition, columnCellElementList );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRowCellElements( List<? extends E> rowCellElementList )
  {
    //
    int rowIndexPosition = this.tableSize.getRowSize();
    this.setRowCellElements( rowIndexPosition, rowCellElementList );
    
    //
    return this;
  }
  
  @Override
  public Table<E> addRowCellElements( int rowIndexPosition, List<? extends E> rowCellElementList )
  {
    //
    StripeData<E> newStripe = this.tableContent.getRowList().addNewStripe( rowIndexPosition );
    if ( newStripe != null )
    {
      this.setRowCellElements( rowIndexPosition, rowCellElementList );
    }
    
    // 
    return this;
  }
  
  @Override
  public List<E> removeRow( int rowIndexPosition )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    StripeData<E> rowStripeData = this.cellAndStripeResolver.resolveRowStripeData( rowIndexPosition );
    if ( rowStripeData != null )
    {
      //
      retlist.addAll( rowStripeData.getCellElementList() );
      
      //
      this.tableContent.getRowList().removeStripeDataAndDetachCellsFromTable( rowIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public List<E> removeColumn( int columnIndexPosition )
  {
    //
    List<E> retlist = new ArrayList<E>();
    
    //
    StripeData<E> columnStripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnIndexPosition );
    if ( columnStripeData != null )
    {
      //
      retlist.addAll( columnStripeData.getCellElementList() );
      
      //
      this.tableContent.getColumnList().removeStripeDataAndDetachCellsFromTable( columnIndexPosition );
    }
    
    //
    return retlist;
  }
  
  @Override
  public Row<E> getRow( int rowIndexPosition )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveRowStripeData( rowIndexPosition );
    TableInternal<E> tableInternal = this;
    return new StripeImpl<E>( tableInternal, stripeData );
  }
  
  @Override
  public Row<E> getRow( Object rowTitleValue )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveRowStripeData( rowTitleValue );
    TableInternal<E> tableInternal = this;
    return new StripeImpl<E>( tableInternal, stripeData );
  }
  
  @Override
  public Column<E> getColumn( int columnIndexPosition )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnIndexPosition );
    TableInternal<E> tableInternal = this;
    return new StripeImpl<E>( tableInternal, stripeData );
  }
  
  @Override
  public Column<E> getColumn( Object columnTitleValue )
  {
    //
    StripeData<E> stripeData = this.cellAndStripeResolver.resolveColumnStripeData( columnTitleValue );
    TableInternal<E> tableInternal = this;
    return new StripeImpl<E>( tableInternal, stripeData );
  }
  
  @Override
  public Table<E> clear()
  {
    //
    this.tableName = null;
    this.tableContent.clear();
    
    //
    return this;
  }
  
  @Override
  public Table<E> cloneTableStructure()
  {
    //TODO
    
    //
    return null;
  }
  
  @Override
  public Table<E> clone()
  {
    //TODO
    //
    return null;
  }
  
  @XmlTransient
  @Override
  public Object getTableName()
  {
    return this.tableName;
  }
  
  @Override
  public Table<E> setTableName( Object tableName )
  {
    //
    this.tableName = tableName;
    
    //
    return this;
  }
  
  @Override
  public Iterator<Cell<E>> iteratorCell()
  {
    return new Iterator<Cell<E>>()
    {
      /* ********************************************** Variables ********************************************** */
      protected int cellIndexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return this.cellIndexPosition + 1 < ArrayTable.this.tableSize.getCellSize();
      }
      
      @Override
      public Cell<E> next()
      {
        return ArrayTable.this.getCell( ++this.cellIndexPosition );
      }
      
      @Override
      public void remove()
      {
        Cell<E> cell = ArrayTable.this.getCell( this.cellIndexPosition-- );
        if ( cell != null )
        {
          cell.setElement( null );
        }
      }
    };
  }
  
  @Override
  public Cell<E> getCell( int cellIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( cellIndexPosition );
  }
  
  @Override
  public Table<E> setCellElement( int cellIndexPosition, E element )
  {
    //
    Cell<E> cell = this.cellAndStripeResolver.resolveOrCreateCell( cellIndexPosition );
    
    //
    if ( cell != null )
    {
      cell.setElement( element );
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> processTableCells( TableCellVisitor<E> tableCellVisitor )
  {
    //
    if ( tableCellVisitor != null )
    {
      for ( int rowIndexPosition = 0; rowIndexPosition < this.getTableSize().getRowSize(); rowIndexPosition++ )
      {
        for ( int columnIndexPosition = 0; columnIndexPosition < this.getTableSize().getColumnSize(); columnIndexPosition++ )
        {
          tableCellVisitor.process( rowIndexPosition, columnIndexPosition, this.getCell( rowIndexPosition, columnIndexPosition ) );
        }
      }
    }
    
    //
    return this;
  }
  
  @Override
  public <TO> Table<TO> convert( final TableCellConverter<E, TO> tableCellConverter )
  {
    //
    final Table<TO> table = new ArrayTable<TO>();
    
    //
    this.processTableCells( new TableCellVisitor<E>()
    {
      @Override
      public void process( int rowIndexPosition, int columnIndexPosition, Cell<E> cell )
      {
        table.setCellElement( rowIndexPosition, columnIndexPosition, tableCellConverter.convert( cell.getElement() ) );
      }
    } );
    
    //
    return table;
  }
  
  @Override
  public Iterator<Row<E>> iterator()
  {
    return this.iteratorRow();
  }
  
  public Iterable<Cell<E>> cells()
  {
    return new Iterable<Cell<E>>()
    {
      @Override
      public Iterator<Cell<E>> iterator()
      {
        return ArrayTable.this.iteratorCell();
      }
    };
  }
  
  @Override
  public boolean contains( E element )
  {
    //
    boolean retval = false;
    
    //TODO can this be optimized by indexes?
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      if ( cell.hasElement( element ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public String toString()
  {
    //TODO take the implementation of the tablehelper to enhance this
    //
    StringBuilder sb = new StringBuilder();
    
    //
    String rowDelimiter = "";
    for ( Row<E> row : this )
    {
      //
      sb.append( rowDelimiter + "[" );
      
      //
      String elementDelimiter = "";
      for ( Cell<E> cell : row )
      {
        //
        E element = cell.getElement();
        
        //
        String elementValue = String.valueOf( element );
        sb.append( elementDelimiter + elementValue );
        elementDelimiter = ",";
      }
      
      //
      sb.append( "]" );
      rowDelimiter = "\n";
    }
    
    return sb.toString();
  }
  
  @Override
  public TableContent<E> getTableContent()
  {
    return this.tableContent;
  }
  
  @Override
  public CellAndStripeResolver<E> getCellAndStripeResolver()
  {
    return this.cellAndStripeResolver;
  }
  
  @Override
  public Table<E> putTable( Table<E> table, int rowIndexPosition, int columnIndexPosition )
  {
    //
    if ( table != null )
    {
      //
      for ( int ii = 0; ii < table.getTableSize().getRowSize(); ii++ )
      {
        for ( int jj = 0; jj < table.getTableSize().getColumnSize(); jj++ )
        {
          this.setCellElement( ii + rowIndexPosition, jj + columnIndexPosition, table.getCellElement( ii, jj ) );
        }
      }
      
    }
    
    //
    return this;
  }
  
  @Override
  public Table<E> transpose()
  {
    //
    this.tableContent.switchRowAndColumnStripeList();
    
    //
    return this;
  }
  
  @Override
  public boolean equals( Table<E> table )
  {
    //
    boolean retval = this.tableSize.equals( table.getTableSize() );
    
    //
    if ( retval )
    {
      //      
      Iterator<Cell<E>> iteratorCellThis = this.iteratorCell();
      Iterator<Cell<E>> iteratorCellOther = table.iteratorCell();
      
      //
      while ( retval && iteratorCellThis.hasNext() && iteratorCellOther.hasNext() )
      {
        //
        Cell<E> cellThis = iteratorCellThis.next();
        Cell<E> cellOther = iteratorCellOther.next();
        
        //
        retval &= ( cellThis == null && cellOther == null )
                  || ( cellThis != null && cellOther != null && cellThis.hasElement( cellOther.getElement() ) );
      }
      
      //
      retval &= !iteratorCellThis.hasNext() && !iteratorCellOther.hasNext();
    }
    
    //
    return retval;
  }
  
  @Override
  public Table<E> putArray( E[][] elementArray, int rowIndexPosition, int columnIndexPosition )
  {
    //
    for ( int ii = 0; ii < elementArray.length; ii++ )
    {
      for ( int jj = 0; jj < elementArray[ii].length; jj++ )
      {
        this.setCellElement( rowIndexPosition + ii, columnIndexPosition + jj, elementArray[ii][jj] );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public Cell<E> getCell( String rowTitleValue, String columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveCell( rowTitleValue, columnTitleValue );
  }
  
  @Override
  public Cell<E> getCell( Object rowTitleValue, int columnIndexPosition )
  {
    return this.cellAndStripeResolver.resolveCell( rowTitleValue, columnIndexPosition );
  }
  
  @Override
  public Cell<E> getCell( int rowIndexPosition, Object columnTitleValue )
  {
    return this.cellAndStripeResolver.resolveCell( rowIndexPosition, columnTitleValue );
  }
  
  @Override
  public List<Cell<E>> getCellList()
  {
    return ListUtils.createListFrom( this.iteratorCell() );
  }
  
  @Override
  public List<E> getCellElementList()
  {
    //    
    List<E> retlist = new ArrayList<E>();
    
    //
    for ( Cell<E> cell : this.cells() )
    {
      retlist.add( cell != null ? cell.getElement() : null );
    }
    
    // 
    return retlist;
  }
  
  @Override
  public Iterator<Row<E>> iteratorRow()
  {
    return new Iterator<Row<E>>()
    {
      protected int rowIndexPosition = -1;
      
      @Override
      public boolean hasNext()
      {
        return this.rowIndexPosition + 1 < ArrayTable.this.getTableSize().getRowSize();
      }
      
      @Override
      public Row<E> next()
      {
        return ArrayTable.this.getRow( ++this.rowIndexPosition );
      }
      
      @Override
      public void remove()
      {
        ArrayTable.this.removeRow( this.rowIndexPosition-- );
      }
    };
  }
  
  @Override
  public E getCellElement( int cellIndexPosition )
  {
    //
    E retval = null;
    
    //
    Cell<E> cell = this.getCell( cellIndexPosition );
    if ( cell != null )
    {
      retval = cell.getElement();
    }
    
    // 
    return retval;
  }
  
  @Override
  public Selection<E> select()
  {
    return new SelectionImpl<E>( this );
  }
  
  /**
   * Creates a {@link XMLDataContainer} from the {@link Table} content
   * 
   * @return
   */
  protected XMLDataContainer<E> createXMLDataContainerFromTableContent()
  {
    //
    ArrayList<Object> rowTitleValueList = new ArrayList<Object>( this.getRowTitleValueList() );
    ArrayList<Object> columnTitleValueList = new ArrayList<Object>( this.getColumnTitleValueList() );
    ArrayList<E> cellElementList = new ArrayList<E>( this.getCellElementList() );
    Integer columnSize = this.tableSize.getColumnSize();
    Integer rowSize = this.tableSize.getRowSize();
    Object tableName = this.tableName;
    XMLDataContainer<E> dataContainer = new XMLDataContainer<E>( rowTitleValueList, columnTitleValueList, cellElementList,
                                                                 columnSize, rowSize, tableName );
    
    //
    return dataContainer;
  }
  
  @Override
  public Table<E> writeAsXMLTo( Appendable appendable )
  {
    //
    XMLHelper.storeObjectAsXML( this.createXMLDataContainerFromTableContent(), appendable );
    
    // 
    return this;
  }
  
  @Override
  public Table<E> writeAsXMLTo( OutputStream outputStream )
  {
    //
    XMLHelper.storeObjectAsXML( this.createXMLDataContainerFromTableContent(), outputStream );
    
    //
    return this;
  }
  
  @SuppressWarnings({ "unchecked" })
  @Override
  public Table<E> parseXMLFrom( String xmlContent )
  {
    //
    try
    {
      //
      XMLDataContainer<E> xmlDataContainer = XMLHelper.loadObjectFromXML( xmlContent, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public Table<E> parseXMLFrom( InputStream inputStream )
  {
    //
    try
    {
      //
      XMLDataContainer xmlDataContainer = XMLHelper.loadObjectFromXML( inputStream, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this;
  }
  
  /**
   * Clears the {@link Table} and writes the data of a {@link XMLDataContainer} to it
   * 
   * @param xmlDataContainer
   */
  protected void writeXMLDataContainerToTableContent( XMLDataContainer<E> xmlDataContainer )
  {
    //
    this.clear();
    
    //
    {
      int rowIndexPosition = xmlDataContainer.getRowSize() - 1;
      int columnIndexPosition = xmlDataContainer.getColumnSize() - 1;
      E element = null;
      this.setCellElement( rowIndexPosition, columnIndexPosition, element );
    }
    
    //
    int cellIndexPosition = 0;
    for ( E element : xmlDataContainer.getCellElementList() )
    {
      this.setCellElement( cellIndexPosition++, element );
    }
    
    //
    this.setColumnTitleValues( xmlDataContainer.getColumnTitleValueList() );
    this.setRowTitleValues( xmlDataContainer.getRowTitleValueList() );
    this.tableName = xmlDataContainer.getTableName();
  }
  
  @Override
  public Table<E> parseXMLFrom( CharSequence charSequence )
  {
    //
    try
    {
      //      
      @SuppressWarnings("unchecked")
      XMLDataContainer<E> xmlDataContainer = XMLHelper.loadObjectFromXML( charSequence, XMLDataContainer.class );
      
      //
      this.writeXMLDataContainerToTableContent( xmlDataContainer );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    // 
    return this;
  }
  
}
