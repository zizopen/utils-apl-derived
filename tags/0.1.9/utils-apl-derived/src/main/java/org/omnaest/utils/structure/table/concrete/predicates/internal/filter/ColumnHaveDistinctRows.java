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
package org.omnaest.utils.structure.table.concrete.predicates.internal.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ElementHolderUnmodifiable;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.Table.Stripe;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeInternal;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Selection;

import com.sun.rowset.internal.Row;

/**
 * Filters all duplicate {@link Row}s / {@link Row}s with the same {@link Column} values will be collapsed to one.
 * 
 * @see Predicate
 * @see PredicateFilter
 * @author Omnaest
 */
public class ColumnHaveDistinctRows<E> implements PredicateFilter<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -8287655781277028388L;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * {@link ElementHolderUnmodifiable} for a {@link StripeData} instance which redeclares equals and hashcode to use the
   * {@link StripeData#getCellElementList()}.
   * 
   * @author Omnaest
   * @param <E>
   */
  protected static class StripeWrapperWithEqualsAndHashCode<E> extends ElementHolderUnmodifiable<Stripe<E>>
  {
    
    /**
     * @param stripeData
     */
    public StripeWrapperWithEqualsAndHashCode( Stripe<E> stripeData )
    {
      super( stripeData );
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      List<E> cellElementList = this.element.getCellElementList();
      result = prime * result + ( ( cellElementList == null ) ? 0 : cellElementList.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof StripeWrapperWithEqualsAndHashCode ) )
      {
        return false;
      }
      @SuppressWarnings("unchecked")
      StripeWrapperWithEqualsAndHashCode<E> other = (StripeWrapperWithEqualsAndHashCode<E>) obj;
      if ( this.element == null )
      {
        if ( other.element != null )
        {
          return false;
        }
      }
      else if ( !this.element.getCellElementList().equals( other.element.getCellElementList() ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * This is an internal used class. Use {@link Selection#distinct()} instead.
   */
  public ColumnHaveDistinctRows()
  {
    super();
  }
  
  @Override
  public void filterStripeDataSet( Collection<TableBlock<E>> tableBlockCollection )
  {
    //
    if ( tableBlockCollection != null && tableBlockCollection.size() == 1 )
    {
      //
      TableBlock<E> tableBlock = tableBlockCollection.iterator().next();
      
      //      
      Set<StripeWrapperWithEqualsAndHashCode<E>> distinctStripeDataWrapperSet = new HashSet<StripeWrapperWithEqualsAndHashCode<E>>();
      for ( StripeData<E> stripeData : tableBlock.getRowStripeDataSet() )
      {
        StripeInternal<E> stripeInternal = tableBlock.getTableInternal()
                                                     .getStripeFactory()
                                                     .newInstanceOfStripeInternal( stripeData );
        distinctStripeDataWrapperSet.add( new StripeWrapperWithEqualsAndHashCode<E>( stripeInternal ) );
      }
      
      //
      ElementConverter<StripeWrapperWithEqualsAndHashCode<E>, StripeData<E>> elementTransformer = new ElementConverter<StripeWrapperWithEqualsAndHashCode<E>, StripeData<E>>()
      {
        @Override
        public StripeData<E> convert( StripeWrapperWithEqualsAndHashCode<E> stripeDataWrapper )
        {
          Stripe<E> stripe = stripeDataWrapper.getElement();
          return ( (StripeInternal<E>) stripe ).getStripeData();
        }
      };
      List<StripeData<E>> distinctStripeDataList = ListUtils.convert( distinctStripeDataWrapperSet, elementTransformer );
      
      //
      tableBlock.getRowStripeDataSet().retainAll( distinctStripeDataList );
    }
    
  }
  
  @Override
  public Column<E>[] getRequiredColumns()
  {
    throw new UnsupportedOperationException();
  }
}
