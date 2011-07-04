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
  public interface CellResolver<E> extends Serializable
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
     * Switches the {@link StripeList} for the {@link Row} and {@link Column}.
     */
    public void switchRowAndColumnStripeList();
    
  }
  
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeList<E> extends Iterable<Stripe<E>>
  {
    
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
    public Stripe<E> get( int indexPosition );
    
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
   * Returns the {@link CellResolver} of the {@link Table}
   * 
   * @return
   */
  public CellResolver<E> getCellResolver();
  
}
