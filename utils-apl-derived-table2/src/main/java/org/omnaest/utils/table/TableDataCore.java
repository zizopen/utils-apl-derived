package org.omnaest.utils.table;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;

public class TableDataCore<E>
{
  private static final int INITIAL_ROW_SIZE    = 16;
  private static final int INITIAL_COLUMN_SIZE = 4;
  
  private E[][]            matrix;
  
  private int[]            nativeColumnIndices;
  private int[]            nativeRowIndices;
  
  private BitSet           activeRowBitSet     = new BitSet( INITIAL_ROW_SIZE );
  private BitSet           activeColumnBitSet  = new BitSet( INITIAL_COLUMN_SIZE );
  
  private int              rowSize             = 0;
  private int              columnSize          = 0;
  
  private final Class<E>   type;
  
  @SuppressWarnings("unchecked")
  public TableDataCore( Class<? extends E> type )
  {
    super();
    this.type = (Class<E>) type;
    
    this.matrix = (E[][]) Array.newInstance( type, INITIAL_ROW_SIZE, INITIAL_COLUMN_SIZE );
    this.nativeColumnIndices = new int[INITIAL_COLUMN_SIZE];
    this.nativeRowIndices = new int[INITIAL_ROW_SIZE];
  }
  
  public void set( E element, int rowIndex, int columnIndex )
  {
    this.ensureColumnSize( columnIndex + 1 );
    this.ensureRowSize( rowIndex + 1 );
    
    final int nativeColumnIndex = this.determineNativeColumnIndex( columnIndex );
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    if ( nativeColumnIndex >= 0 && nativeRowIndex >= 0 )
    {
      this.matrix[nativeRowIndex][nativeColumnIndex] = element;
    }
  }
  
  public int addRow( E... elements )
  {
    int retval = -1;
    
    final int rowIndex = this.rowSize;
    this.ensureColumnSize( elements.length );
    this.ensureRowSize( rowIndex + 1 );
    
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    if ( nativeRowIndex >= 0 )
    {
      for ( int ii = 0; ii < elements.length; ii++ )
      {
        this.matrix[nativeRowIndex][ii] = elements[ii];
      }
      
      retval = rowIndex;
    }
    
    return retval;
  }
  
  public void removeRow( int rowIndex )
  {
    //
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    final int rowMaxSize = this.nativeRowIndices.length;
    if ( nativeRowIndex >= 0 )
    {
      boolean activeRow = this.activeRowBitSet.get( nativeRowIndex );
      if ( activeRow )
      {
        Arrays.fill( this.matrix[nativeRowIndex], null );
      }
      
      this.activeRowBitSet.clear( nativeRowIndex );
      for ( int iRowIndex = rowIndex; iRowIndex < rowMaxSize - 1; iRowIndex++ )
      {
        this.nativeRowIndices[iRowIndex] = this.nativeRowIndices[iRowIndex + 1];
      }
      this.nativeRowIndices[rowMaxSize - 1] = 0;
      this.rowSize--;
    }
    
    //
    if ( this.rowSize < rowMaxSize / 4 )
    {
      //compact
      if ( this.rowSize > 0 )
      {
        int lowerNativeRowIndex = -1;
        int upperNativeRowIndex = this.nativeRowIndices.length - 1;
        while ( ( lowerNativeRowIndex = this.activeRowBitSet.nextClearBit( lowerNativeRowIndex + 1 ) ) < upperNativeRowIndex )
        {
          while ( upperNativeRowIndex > lowerNativeRowIndex && !this.activeRowBitSet.get( upperNativeRowIndex ) )
          {
            upperNativeRowIndex--;
          }
          
          if ( lowerNativeRowIndex < upperNativeRowIndex )
          {
            //
            for ( int ii = 0; ii < this.nativeRowIndices.length; ii++ )
            {
              if ( this.nativeRowIndices[ii] == upperNativeRowIndex )
              {
                this.nativeRowIndices[ii] = lowerNativeRowIndex;
                break;
              }
            }
            
            //
            this.activeRowBitSet.clear( upperNativeRowIndex );
            this.activeRowBitSet.set( lowerNativeRowIndex );
            
            //
            E[] upperRow = this.matrix[upperNativeRowIndex];
            this.matrix[upperNativeRowIndex] = this.matrix[lowerNativeRowIndex];
            this.matrix[lowerNativeRowIndex] = upperRow;
          }
        }
      }
      
      //
      final int newRowMaxSize = rowMaxSize / 2;
      this.nativeRowIndices = Arrays.copyOf( this.nativeRowIndices, newRowMaxSize );
      this.activeRowBitSet.clear( newRowMaxSize, rowMaxSize - 1 );
      this.matrix = Arrays.copyOf( this.matrix, newRowMaxSize );
    }
  }
  
  public int addColumn( E... elements )
  {
    int retval = -1;
    
    final int columnIndex = this.columnSize;
    this.ensureColumnSize( columnIndex + 1 );
    this.ensureRowSize( elements.length );
    
    final int nativeColumnIndex = this.determineNativeColumnIndex( columnIndex );
    if ( nativeColumnIndex >= 0 )
    {
      for ( int ii = 0; ii < elements.length; ii++ )
      {
        this.matrix[ii][nativeColumnIndex] = elements[ii];
      }
      
      retval = columnIndex;
    }
    
    return retval;
  }
  
  public E[] getRow( int rowIndex )
  {
    //
    final E[] retval = newArray( this.columnSize );
    for ( int iColumnIndex = 0; iColumnIndex < this.columnSize; iColumnIndex++ )
    {
      retval[iColumnIndex] = this.getData( rowIndex, iColumnIndex );
    }
    return retval;
  }
  
  public E[] getColumn( int columnIndex )
  {
    //
    final E[] retval = newArray( this.rowSize );
    for ( int iRowIndex = 0; iRowIndex < this.rowSize; iRowIndex++ )
    {
      retval[iRowIndex] = this.getData( iRowIndex, columnIndex );
    }
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  private E[] newArray( int size )
  {
    return (E[]) Array.newInstance( this.type, size );
  }
  
  public E getData( int rowIndex, int columnIndex )
  {
    E retval = null;
    
    final int nativeRowIndex = determineNativeRowIndex( rowIndex );
    final int nativeColumnIndex = determineNativeColumnIndex( columnIndex );
    if ( nativeRowIndex >= 0 && nativeColumnIndex >= 0 )
    {
      boolean activeRow = this.activeRowBitSet.get( nativeRowIndex );
      boolean activeColumn = this.activeColumnBitSet.get( nativeColumnIndex );
      
      if ( activeRow && activeColumn )
      {
        retval = this.matrix[nativeRowIndex][nativeColumnIndex];
      }
    }
    
    return retval;
  }
  
  private int determineNativeColumnIndex( int columnIndex )
  {
    return columnIndex < 0 || columnIndex >= this.columnSize ? -1 : this.nativeColumnIndices[columnIndex];
  }
  
  private int determineNativeRowIndex( int rowIndex )
  {
    return rowIndex < 0 || rowIndex >= this.rowSize ? -1 : this.nativeRowIndices[rowIndex];
  }
  
  private void ensureRowSize( int rowSize )
  {
    //
    int rowMaxSize;
    while ( rowSize > ( rowMaxSize = this.nativeRowIndices.length ) )
    {
      final int newRowMaxSize = rowMaxSize * 2;
      this.nativeRowIndices = Arrays.copyOf( this.nativeRowIndices, newRowMaxSize );
      this.matrix = Arrays.copyOf( this.matrix, newRowMaxSize );
      for ( int iRowIndex = rowMaxSize; iRowIndex < newRowMaxSize; iRowIndex++ )
      {
        this.matrix[iRowIndex] = this.newArray( this.nativeColumnIndices.length );
      }
    }
    
    //
    for ( int iRowIndex = this.rowSize; iRowIndex < rowSize; iRowIndex++ )
    {
      //
      final int nativeRowIndex = this.activeRowBitSet.nextClearBit( 0 );
      this.nativeRowIndices[iRowIndex] = nativeRowIndex;
      this.activeRowBitSet.set( nativeRowIndex );
      this.rowSize++;
    }
  }
  
  private void ensureColumnSize( int columnSize )
  {
    //
    int columnMaxSize;
    while ( columnSize > ( columnMaxSize = this.nativeColumnIndices.length ) )
    {
      final int newColumnMaxSize = columnMaxSize * 2;
      this.nativeColumnIndices = Arrays.copyOf( this.nativeColumnIndices, newColumnMaxSize );
      for ( int iRowIndex = 0; iRowIndex < this.matrix.length; iRowIndex++ )
      {
        this.matrix[iRowIndex] = Arrays.copyOf( this.matrix[iRowIndex], newColumnMaxSize );
      }
    }
    
    //
    for ( int iColumnIndex = this.columnSize; iColumnIndex < columnSize; iColumnIndex++ )
    {
      //
      final int nativeColumnIndex = this.activeColumnBitSet.nextClearBit( 0 );
      this.nativeColumnIndices[iColumnIndex] = nativeColumnIndex;
      this.activeColumnBitSet.set( nativeColumnIndex );
      this.columnSize++;
    }
  }
  
  public int rowSize()
  {
    return this.rowSize;
  }
  
  public int columnSize()
  {
    return this.columnSize;
  }
  
  public int size()
  {
    return this.columnSize * this.rowSize;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "TableDataCore [matrix=\n" );
    for ( E[] row : this.matrix )
    {
      builder.append( Arrays.deepToString( row ) + "\n" );
    }
    builder.append( ", \nnativeColumnIndices=" );
    builder.append( Arrays.toString( this.nativeColumnIndices ) );
    builder.append( ", \nnativeRowIndices=" );
    builder.append( Arrays.toString( this.nativeRowIndices ) );
    builder.append( ", \nactiveRowBitSet=" );
    builder.append( this.activeRowBitSet );
    builder.append( ", \nactiveColumnBitSet=" );
    builder.append( this.activeColumnBitSet );
    builder.append( ", \nrowSize=" );
    builder.append( this.rowSize );
    builder.append( ", columnSize=" );
    builder.append( this.columnSize );
    builder.append( ", type=" );
    builder.append( this.type );
    builder.append( "]" );
    return builder.toString();
  }
  
}
