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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Data core of an {@link ArrayTable} managing the underlying array structure
 * 
 * @author Omnaest
 * @param <E>
 */
class TableDataCore<E> implements Serializable
{
  public static final int   INITIAL_DEFAULT_COLUMN_SIZE = 4;
  
  public static final int   INITIAL_DEFAULT_ROW_SIZE    = 16;
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID            = 5131972948341159104L;
  
  /* *************************************************** Methods **************************************************** */
  
  private BitSet            activeColumnBitSet;
  
  private BitSet            activeRowBitSet;
  private int               columnSize;
  
  private final int         initialColumnSize;
  private final int         initialRowSize;
  
  private E[][]             matrix;
  private int[]             nativeColumnIndices;
  
  private int[]             nativeRowIndices;
  private int               rowSize;
  private final Class<E>    type;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Creates a new {@link TableDataCore} with an initial row size of {@value #INITIAL_DEFAULT_ROW_SIZE} and an initial column size
   * of {@value #INITIAL_DEFAULT_COLUMN_SIZE}
   * 
   * @see TableDataCore
   * @param type
   */
  @SuppressWarnings("unchecked")
  TableDataCore( Class<? extends E> type )
  {
    super();
    this.type = (Class<E>) type;
    this.initialRowSize = INITIAL_DEFAULT_ROW_SIZE;
    this.initialColumnSize = INITIAL_DEFAULT_COLUMN_SIZE;
    
    this.initialize( type, this.initialRowSize, this.initialColumnSize );
  }
  
  /**
   * @see TableDataCore
   * @param type
   * @param initialRowSize
   * @param initialColumnSize
   */
  @SuppressWarnings("unchecked")
  TableDataCore( Class<? extends E> type, int initialRowSize, int initialColumnSize )
  {
    super();
    this.initialRowSize = initialRowSize;
    this.initialColumnSize = initialColumnSize;
    this.type = (Class<E>) type;
    
    this.initialize( type, initialRowSize, initialColumnSize );
  }
  
  public int addColumn( E... elements )
  {
    final int columnIndex = this.columnSize;
    return this.addColumn( columnIndex, elements );
  }
  
  public int addColumn( int columnIndex, E... elements )
  {
    int retval = -1;
    
    final int lastColumnIndex = this.columnSize;
    final boolean isLastRow = ( columnIndex == lastColumnIndex );
    this.ensureColumnSize( lastColumnIndex + 1 );
    this.ensureRowSize( elements.length );
    
    if ( !isLastRow )
    {
      int nativeLastColumnIndex = this.determineNativeColumnIndex( lastColumnIndex );
      for ( int ii = this.nativeColumnIndices.length - 1; ii > columnIndex && ii > 0; ii-- )
      {
        this.nativeColumnIndices[ii] = this.nativeColumnIndices[ii - 1];
      }
      this.nativeColumnIndices[columnIndex] = nativeLastColumnIndex;
    }
    
    final int nativeColumnIndex = this.determineNativeColumnIndex( lastColumnIndex );
    if ( nativeColumnIndex >= 0 )
    {
      for ( int ii = 0; ii < elements.length; ii++ )
      {
        final int nativeRowIndex = this.determineNativeRowIndex( ii );
        this.matrix[nativeRowIndex][nativeColumnIndex] = elements[ii];
      }
      
      retval = lastColumnIndex;
    }
    
    return retval;
  }
  
  /**
   * Adds the given elements as new row
   * 
   * @param elements
   * @return row index position of the new added row
   */
  public int addRow( E... elements )
  {
    final int rowIndex = this.rowSize;
    return this.addRow( rowIndex, elements );
  }
  
  /**
   * Adds the given elements as new row at the specific index position
   * 
   * @param elements
   * @return row index position of the new added row
   */
  public int addRow( int rowIndex, E... elements )
  {
    int retval = -1;
    
    final int lastRowIndex = this.rowSize;
    final boolean isLastRow = ( rowIndex == lastRowIndex );
    
    if ( elements == null )
    {
      elements = this.newArray( 0 );
    }
    
    this.ensureColumnSize( elements.length );
    this.ensureRowSize( this.rowSize + 1 );
    
    if ( !isLastRow )
    {
      int nativeLastRowIndex = this.determineNativeRowIndex( lastRowIndex );
      for ( int ii = this.nativeRowIndices.length - 1; ii > rowIndex && ii > 0; ii-- )
      {
        this.nativeRowIndices[ii] = this.nativeRowIndices[ii - 1];
      }
      this.nativeRowIndices[rowIndex] = nativeLastRowIndex;
    }
    
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    if ( nativeRowIndex >= 0 )
    {
      for ( int ii = 0; ii < elements.length; ii++ )
      {
        final int nativeColumnIndex = this.determineNativeColumnIndex( ii );
        this.matrix[nativeRowIndex][nativeColumnIndex] = elements[ii];
      }
      
      retval = rowIndex;
    }
    
    return retval;
  }
  
  public void clear()
  {
    this.initialize( this.type, this.initialRowSize, this.initialColumnSize );
  }
  
  public int columnSize()
  {
    return this.columnSize;
  }
  
  private void compactColumnsIfNecessary( int columnMaxSize )
  {
    if ( this.columnSize < columnMaxSize / 4 )
    {
      //
      if ( this.columnSize > 0 )
      {
        int lowerNativeColumnIndex = -1;
        int upperNativeColumnIndex = this.nativeColumnIndices.length - 1;
        while ( ( lowerNativeColumnIndex = this.activeColumnBitSet.nextClearBit( lowerNativeColumnIndex + 1 ) ) < upperNativeColumnIndex )
        {
          while ( upperNativeColumnIndex > lowerNativeColumnIndex && !this.activeColumnBitSet.get( upperNativeColumnIndex ) )
          {
            upperNativeColumnIndex--;
          }
          
          if ( lowerNativeColumnIndex < upperNativeColumnIndex )
          {
            //
            for ( int ii = 0; ii < this.nativeColumnIndices.length; ii++ )
            {
              if ( this.nativeColumnIndices[ii] == upperNativeColumnIndex )
              {
                this.nativeColumnIndices[ii] = lowerNativeColumnIndex;
                break;
              }
            }
            
            //
            this.activeColumnBitSet.clear( upperNativeColumnIndex );
            this.activeColumnBitSet.set( lowerNativeColumnIndex );
            
            //
            for ( int ii = 0; ii < this.nativeRowIndices.length; ii++ )
            {
              E element = this.matrix[ii][upperNativeColumnIndex];
              this.matrix[ii][upperNativeColumnIndex] = this.matrix[ii][lowerNativeColumnIndex];
              this.matrix[ii][lowerNativeColumnIndex] = element;
            }
          }
        }
      }
      
      //
      final int newColumnMaxSize = columnMaxSize / 2;
      this.nativeColumnIndices = Arrays.copyOf( this.nativeColumnIndices, newColumnMaxSize );
      this.activeColumnBitSet.clear( newColumnMaxSize, columnMaxSize - 1 );
      for ( int ii = 0; ii < this.nativeRowIndices.length; ii++ )
      {
        this.matrix[ii] = Arrays.copyOf( this.matrix[ii], newColumnMaxSize );
      }
    }
  }
  
  private void compactRowsIfNecessary( final int rowMaxSize )
  {
    if ( this.rowSize < rowMaxSize / 4 )
    {
      //
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
  
  private int determineNativeColumnIndex( int columnIndex )
  {
    return columnIndex < 0 || columnIndex >= this.columnSize ? -1 : this.nativeColumnIndices[columnIndex];
  }
  
  private int determineNativeRowIndex( int rowIndex )
  {
    return rowIndex < 0 || rowIndex >= this.rowSize ? -1 : this.nativeRowIndices[rowIndex];
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
  
  public E[] getColumn( int columnIndex )
  {
    //
    final E[] retval = newArray( this.rowSize );
    for ( int iRowIndex = 0; iRowIndex < this.rowSize; iRowIndex++ )
    {
      retval[iRowIndex] = this.getElement( iRowIndex, columnIndex );
    }
    return retval;
  }
  
  public E getElement( int rowIndex, int columnIndex )
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
  
  public E[] getRow( int rowIndex )
  {
    //
    final E[] retval = newArray( this.columnSize );
    for ( int iColumnIndex = 0; iColumnIndex < this.columnSize; iColumnIndex++ )
    {
      retval[iColumnIndex] = this.getElement( rowIndex, iColumnIndex );
    }
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  private void initialize( Class<? extends E> type, int initialRowSize, int initialColumnSize )
  {
    this.matrix = (E[][]) Array.newInstance( type, initialRowSize, initialColumnSize );
    
    this.nativeColumnIndices = new int[initialColumnSize];
    this.nativeRowIndices = new int[initialRowSize];
    
    this.activeColumnBitSet = new BitSet( initialColumnSize );
    this.activeRowBitSet = new BitSet( initialRowSize );
    
    this.rowSize = 0;
    this.columnSize = 0;
  }
  
  @SuppressWarnings("unchecked")
  private E[] newArray( int size )
  {
    return (E[]) Array.newInstance( this.type, size );
  }
  
  public E[] removeColumn( int columnIndex )
  {
    //
    E[] retvals = null;
    
    //
    final int nativeColumnIndex = this.determineNativeColumnIndex( columnIndex );
    final int columnMaxSize = this.nativeColumnIndices.length;
    if ( nativeColumnIndex >= 0 )
    {
      boolean isActiveColumn = this.activeColumnBitSet.get( nativeColumnIndex );
      if ( isActiveColumn )
      {
        retvals = this.newArray( this.rowSize );
        for ( int ii = 0; ii < retvals.length; ii++ )
        {
          final int nativeRowIndex = this.determineNativeRowIndex( ii );
          retvals[ii] = this.matrix[nativeRowIndex][nativeColumnIndex];
        }
        
        for ( int ii = 0; ii < this.nativeRowIndices.length; ii++ )
        {
          this.matrix[ii][nativeColumnIndex] = null;
        }
      }
      
      this.activeColumnBitSet.clear( nativeColumnIndex );
      for ( int iColumnIndex = columnIndex; iColumnIndex < columnMaxSize - 1; iColumnIndex++ )
      {
        this.nativeColumnIndices[iColumnIndex] = this.nativeColumnIndices[iColumnIndex + 1];
      }
      this.nativeColumnIndices[columnMaxSize - 1] = 0;
      this.columnSize--;
    }
    
    //
    this.compactColumnsIfNecessary( columnMaxSize );
    
    return retvals;
  }
  
  public E[] removeRow( int rowIndex )
  {
    //
    E[] retvals = null;
    
    //
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    final int rowMaxSize = this.nativeRowIndices.length;
    if ( nativeRowIndex >= 0 )
    {
      boolean activeRow = this.activeRowBitSet.get( nativeRowIndex );
      if ( activeRow )
      {
        retvals = this.newArray( this.columnSize );
        for ( int ii = 0; ii < retvals.length; ii++ )
        {
          final int nativeColumnIndex = this.determineNativeColumnIndex( ii );
          retvals[ii] = this.matrix[nativeRowIndex][nativeColumnIndex];
        }
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
    this.compactRowsIfNecessary( rowMaxSize );
    
    return retvals;
  }
  
  public int rowSize()
  {
    return this.rowSize;
  }
  
  /**
   * Sets the given element to the given row and column index position
   * 
   * @param element
   * @param rowIndex
   * @param columnIndex
   * @return previous element at the same location
   */
  public E set( E element, int rowIndex, int columnIndex )
  {
    E retval = null;
    
    this.ensureColumnSize( columnIndex + 1 );
    this.ensureRowSize( rowIndex + 1 );
    
    final int nativeColumnIndex = this.determineNativeColumnIndex( columnIndex );
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    if ( nativeColumnIndex >= 0 && nativeRowIndex >= 0 )
    {
      retval = this.matrix[nativeRowIndex][nativeColumnIndex];
      this.matrix[nativeRowIndex][nativeColumnIndex] = element;
    }
    
    return retval;
  }
  
  /**
   * Sets the given elements as the row at the specific index position
   * 
   * @param elements
   * @return the previously set elements
   */
  @SuppressWarnings("unchecked")
  public E[] setRow( int rowIndex, E... elements )
  {
    E[] retval = null;
    
    while ( this.rowSize <= rowIndex )
    {
      this.addRow();
    }
    
    this.ensureColumnSize( elements != null ? elements.length : 0 );
    this.ensureRowSize( this.rowSize );
    
    final int nativeRowIndex = this.determineNativeRowIndex( rowIndex );
    if ( nativeRowIndex >= 0 )
    {
      retval = Arrays.copyOfRange( this.matrix[nativeRowIndex], 0, this.columnSize );
      
      this.matrix[nativeRowIndex] = this.newArray( this.nativeColumnIndices.length );
      if ( elements != null )
      {
        for ( int ii = 0; ii < elements.length; ii++ )
        {
          this.matrix[nativeRowIndex][ii] = elements[ii];
        }
      }
    }
    
    return retval;
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
