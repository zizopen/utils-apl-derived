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
package org.omnaest.utils.structure.table.internal;

import java.io.Serializable;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.Table.Stripe.StripeType;
import org.omnaest.utils.structure.table.Table.Stripe.Title;

/**
 * @see Table
 * @author Omnaest
 * @param <E>
 */
public interface TableInternal<E> extends Table<E>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public interface CellAndStripeResolver<E> extends Serializable
  {
    /**
     * Resolves the {@link Cell} for the given index positions.
     * 
     * @param rowIndexPosition
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveCell( int rowIndexPosition, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} for the given {@link Row} and column index position.
     * 
     * @param row
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveCell( Row<E> row, int columnIndexPosition );
    
    /**
     * Resolves a {@link Cell} by a row index position and a {@link Column}.
     * 
     * @param rowIndexPosition
     * @param column
     * @return
     */
    public Cell<E> resolveCell( int rowIndexPosition, Column<E> column );
    
    /**
     * Resolves a {@link Cell} by a given {@link Row} and {@link Column}
     * 
     * @param row
     * @param column
     * @return
     */
    public Cell<E> resolveCell( Row<E> row, Column<E> column );
    
    /**
     * Resolves a {@link Cell} by a given {@link Stripe} and the complementary index position
     * 
     * @param stripe
     * @param indexPosition
     * @return
     */
    public Cell<E> resolveCell( Stripe<E> stripe, int indexPosition );
    
    /**
     * Resolves a {@link Cell} by a given {@link Stripe} and the title value of the orthogonal stripe
     * 
     * @param stripe
     * @param titleValue
     * @return
     */
    public Cell<E> resolveCell( Stripe<E> stripe, Object titleValue );
    
    /**
     * Resolves a {@link ColumnInternal} for the given {@link Column} index position.
     * 
     * @param columnIndexPosition
     * @return
     */
    public ColumnInternal<E> resolveColumn( int columnIndexPosition );
    
    /**
     * Tries to resolve the {@link Column} for the given {@link Column} index position. If the {@link Column} does not exists but
     * would have a valid {@link Column} index position it will be created.
     * 
     * @param columnIndexPosition
     * @return
     */
    public ColumnInternal<E> resolveOrCreateColumn( int columnIndexPosition );
    
    /**
     * Resolves a {@link RowInternal} for the given {@link Row} index position.
     * 
     * @param rowIndexPosition
     * @return
     */
    public RowInternal<E> resolveRow( int rowIndexPosition );
    
    /**
     * Tries to resolve the {@link Row} for the given {@link Row} index position. If the {@link Row} does not exists but would
     * have a valid {@link Row} index position it will be created.
     * 
     * @param rowIndexPosition
     * @return
     */
    public RowInternal<E> resolveOrCreateRow( int rowIndexPosition );
    
    /**
     * Tries to resolve a {@link StripeInternal} instance for the given index position and {@link StripeType}. If no
     * {@link Stripe} instance could be resolved for the given valid index position as many are created until there is an instance
     * available for the given index position.
     * 
     * @param stripeType
     * @param indexPosition
     * @return
     */
    public StripeInternal<E> resolveOrCreateStripe( StripeType stripeType, int indexPosition );
    
    /**
     * Resolves the {@link Stripe} for the given {@link StripeType} and index position from the internal {@link Table} reference.
     * 
     * @param stripeType
     * @param indexPosition
     * @return
     */
    public StripeInternal<E> resolveStripe( StripeType stripeType, int indexPosition );
    
    /**
     * Resolves the {@link Row} by the given {@link Title#getValue()}
     * 
     * @param titleValue
     * @return
     */
    public RowInternal<E> resolveRow( Object titleValue );
    
    /**
     * Resolves the {@link Column} by the given {@link Title#getValue()}
     * 
     * @param titleValue
     * @return
     */
    public ColumnInternal<E> resolveColumn( Object titleValue );
    
    /**
     * Resolves a {@link Cell} by the {@link Title#getValue()} of the {@link Row} and the {@link Column}
     * 
     * @param rowTitleValue
     * @param column
     * @return
     */
    public Cell<E> resolveCell( Object rowTitleValue, Column<E> column );
    
    /**
     * Resolves a {@link Cell} by the {@link Row} and the {@link Title#getValue()} of the {@link Column}
     * 
     * @param row
     * @param columnTitleValue
     * @return
     */
    public Cell<E> resolveCell( Row<E> row, Object columnTitleValue );
    
    /**
     * Tries to resolve a {@link Cell} by a given {@link RowInternal} and {@link ColumnInternal}. If there is no {@link Cell}
     * available a new one is created.
     * 
     * @param row
     * @param column
     * @return
     */
    public Cell<E> resolveOrCreateCell( RowInternal<E> row, ColumnInternal<E> column );
    
    /**
     * Tries to resolve a {@link Cell} by the given {@link Row} and {@link Column} index position. If no {@link Cell} is available
     * a new one will be created.
     * 
     * @param row
     * @param columnIndexPosition
     * @return
     */
    public Cell<E> resolveOrCreateCell( RowInternal<E> row, int columnIndexPosition );
    
    /**
     * Tries to resolve a {@link Cell} by the given {@link Row} index position and a given {@link Column}. If no {@link Cell} is
     * available a new one will be created.
     * 
     * @param rowIndexPosition
     * @param column
     * @return
     */
    public Cell<E> resolveOrCreateCell( int rowIndexPosition, ColumnInternal<E> column );
  }
  
  /**
   * Resolver for {@link Stripe} instances
   * 
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeResolver<E>
  {
    
    /**
     * Resolves a {@link Stripe} by its {@link StripeType} and {@link Title}
     * 
     * @param stripeType
     * @param title
     * @return
     */
    public Stripe<E> resolveStripe( StripeType stripeType, Title title );
  }
  
  /**
   * Container for {@link StripeList} instances
   * 
   * @author Omnaest
   */
  public static interface StripeListContainer<E>
  {
    
    /**
     * Returns the {@link StripeList} for the given {@link StripeType}
     * 
     * @param stripeType
     * @return
     */
    public StripeList<E> getStripeList( StripeType stripeType );
    
    /**
     * Returns the {@link Row} list
     * 
     * @return
     */
    public StripeList<E> getRowList();
    
    /**
     * Returns the {@link Column} list
     * 
     * @return
     */
    public StripeList<E> getColumnList();
    
    /**
     * Switches the {@link StripeList} for the {@link Row} and {@link Column}.
     */
    public void switchRowAndColumnStripeList();
    
  }
  
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeList<E> extends Iterable<StripeInternal<E>>
  {
    /* ********************************************** Methods ********************************************** */
    /**
     * Returns the {@link StripeType} information of the {@link StripeList}
     * 
     * @return
     */
    public StripeType getStripeType();
    
    /**
     * Sets the {@link StripeType} information of the {@link StripeList}
     * 
     * @param stripeType
     */
    public void setStripeType( StripeType stripeType );
    
    /**
     * Returns the index of a given {@link Stripe}
     * 
     * @param stripe
     * @return
     */
    public int indexOf( Stripe<E> stripe );
    
    /**
     * Returns the stripe at the given index position. If the index position is out of bounds null is returned.
     * 
     * @param indexPosition
     * @return
     */
    public StripeInternal<E> getStripe( int indexPosition );
    
    /**
     * Returns the {@link Stripe} which contains the given {@link Cell}
     * 
     * @param cell
     * @return
     */
    public StripeInternal<E> getStripe( Cell<E> cell );
    
    /**
     * Returns true if the {@link StripeList} does not contain any {@link Stripe} instance.
     * 
     * @return
     */
    public boolean isEmpty();
    
    /**
     * Returns the size of the {@link StripeList}
     * 
     * @return
     */
    public int size();
    
    /**
     * Adds a new created {@link Stripe} instance to the end of the {@link StripeList}
     * 
     * @return
     */
    public StripeInternal<E> addNewStripe();
    
    /**
     * Returns true if the {@link StripeList} contains the given {@link Stripe}
     * 
     * @param stripe
     * @return
     */
    public boolean contains( Stripe<E> stripe );
    
    /**
     * Returns true if the {@link StripeList} contains a {@link Stripe} with the given {@link Title#getValue()}
     * 
     * @param stripe
     * @return
     */
    public boolean contains( Object titleValue );
    
    /**
     * Returns the {@link Stripe} with the given {@link Title#getValue()}. If no {@link Stripe} has the given {@link Title} null
     * is returned.
     * 
     * @param titleValue
     * @return
     */
    public StripeInternal<E> getStripe( Object titleValue );
  }
  
  /**
   * @see Stripe
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeInternal<E> extends Stripe<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    /**
     * @see StripeInternal
     * @see Title
     * @author Omnaest
     */
    public static interface TitleInternal extends Title
    {
      /**
       * Returns true if this and the given {@link Title} instance have an equal value. This uses the
       * {@link Object#equals(Object)} method to compare two values.
       * 
       * @param title
       * @return
       */
      public boolean hasEqualValueTo( Title title );
    }
    
    /* ********************************************** Methods ********************************************** */

    /**
     * Adds a {@link Cell} to the {@link Stripe}
     */
    public void addCell( Cell<E> cell );
  }
  
  /**
   * @see Row
   * @see StripeInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface RowInternal<E> extends Row<E>, StripeInternal<E>
  {
  }
  
  /**
   * @see Column
   * @see StripeInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface ColumnInternal<E> extends Column<E>, StripeInternal<E>
  {
  }
  
  /**
   * @see Cell
   * @author Omnaest
   * @param <E>
   */
  public static interface CellInternal<E> extends Cell<E>
  {
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see StripeListContainer
   * @return
   */
  public StripeListContainer<E> getStripeListContainer();
  
  /**
   * Returns the {@link CellAndStripeResolver} of the {@link Table}
   * 
   * @return
   */
  public CellAndStripeResolver<E> getCellResolver();
  
}
