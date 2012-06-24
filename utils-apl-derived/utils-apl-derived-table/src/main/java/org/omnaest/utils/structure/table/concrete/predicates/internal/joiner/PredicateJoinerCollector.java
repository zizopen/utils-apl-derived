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
package org.omnaest.utils.structure.table.concrete.predicates.internal.joiner;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * A {@link PredicateJoinerCollector} holds an internal {@link Set} of {@link PredicateJoiner} instances and collects and merges
 * their {@link PredicateJoiner#determineJoinableStripeDataSet(StripeData,TableBlock, TableBlock)} results by determining the
 * intersection of them.
 * 
 * @see PredicateJoiner
 * @see Predicate
 * @author Omnaest
 */
public class PredicateJoinerCollector<E> implements PredicateJoiner<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long         serialVersionUID   = 5878493436415716663L;
  /* ********************************************** Variables ********************************************** */
  protected Set<PredicateJoiner<E>> predicateJoinerSet = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see PredicateJoinerCollector
   * @param predicateJoinerSet
   *          : ordered set of {@link PredicateJoiner}
   */
  public PredicateJoinerCollector( Set<PredicateJoiner<E>> predicateJoinerSet )
  {
    super();
    this.predicateJoinerSet = predicateJoinerSet;
  }
  
  @Override
  public Set<StripeData<E>> determineJoinableStripeDataSet( StripeData<E> stripeData,
                                                            TableBlock<E> tableBlockLeft,
                                                            TableBlock<E> tableBlockRight )
  {
    //    
    Set<StripeData<E>> retlist = null;
    
    //
    if ( this.predicateJoinerSet != null )
    {
      for ( PredicateJoiner<E> predicateJoiner : this.predicateJoinerSet )
      {
        if ( predicateJoiner != null && predicateJoiner.affectsBothTableBlocks( tableBlockLeft, tableBlockRight ) )
        {
          //
          Set<StripeData<E>> joinableStripeDataSet = predicateJoiner.determineJoinableStripeDataSet( stripeData, tableBlockLeft,
                                                                                                     tableBlockRight );
          
          //
          if ( retlist == null )
          {
            retlist = new LinkedHashSet<StripeData<E>>( joinableStripeDataSet );
          }
          else
          {
            retlist.retainAll( joinableStripeDataSet );
          }
        }
      }
    }
    
    //
    return retlist;
  }
  
  @Override
  public boolean affectsBothTableBlocks( TableBlock<E> tableBlockLeft, TableBlock<E> tableBlockRight )
  {
    //
    boolean retval = false;
    
    //
    if ( this.predicateJoinerSet != null )
    {
      for ( PredicateJoiner<E> predicateJoiner : this.predicateJoinerSet )
      {
        if ( predicateJoiner != null )
        {
          //
          retval = predicateJoiner.affectsBothTableBlocks( tableBlockLeft, tableBlockRight );
          if ( retval )
          {
            break;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Column<E>[] getRequiredColumns()
  {
    //
    Set<Column<E>> retset = new LinkedHashSet<Column<E>>();
    
    //
    if ( this.predicateJoinerSet != null )
    {
      for ( PredicateJoiner<E> predicateJoiner : this.predicateJoinerSet )
      {
        if ( predicateJoiner != null )
        {
          //
          Column<E>[] requiredColumns = predicateJoiner.getRequiredColumns();
          retset.addAll( Arrays.asList( requiredColumns ) );
        }
      }
    }
    
    // 
    return retset.toArray( new Column[0] );
  }
  
}
