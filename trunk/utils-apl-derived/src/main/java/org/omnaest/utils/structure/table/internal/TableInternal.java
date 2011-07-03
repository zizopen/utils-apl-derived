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
     * @param type
     * @return
     */
    public StripeList<E> getStripeList( Class<StripeType> type );
    
  }
  
  /**
   * @see TableInternal
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeList<E>
  {
    
    /**
     * Returns the {@link StripeType} information of the {@link StripeList}
     * 
     * @return
     */
    public Class<StripeType> getStripeType();
    
    /**
     * Sets the {@link StripeType} information of the {@link StripeList}
     * 
     * @param stripeType
     */
    public void setStripeType( Class<StripeType> stripeType );
  }
  
  /**
   * @see Stripe
   * @author Omnaest
   * @param <E>
   */
  public static interface StripeInternal<E> extends Stripe<E>
  {
    
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
  
}
