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

import java.util.Set;

import org.omnaest.utils.structure.table.Table.Column;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.concrete.internal.selection.join.Join;
import org.omnaest.utils.structure.table.concrete.predicates.PredicateFactory;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeData;
import org.omnaest.utils.structure.table.subspecification.TableSelectable.Predicate;

/**
 * Every {@link PredicateJoiner} should be represented by a static factory method within the {@link PredicateFactory}. <br>
 * <br>
 * A {@link PredicateJoiner} will provide the respecting {@link StripeData} instances from another {@link TableBlock} when a
 * {@link StripeData} is given from a previous {@link TableBlock}. This allows to implement {@link Join}s which uses indexes.
 * 
 * @see PredicateFactory
 * @see Predicate
 * @see TableBlock
 * @author Omnaest
 * @param <E>
 */
public interface PredicateJoiner<E> extends Predicate<E>
{
  /**
   * Provides the {@link StripeData} instances from the right {@link TableBlock} which should be joined with the given
   * {@link StripeData} of the left {@link TableBlock}. The {@link Set} will be ordered.
   * 
   * @param stripeData
   * @param tableBlockLeft
   * @param tableBlockRight
   * @return
   */
  public Set<StripeData<E>> determineJoinableStripeDataSet( StripeData<E> stripeData,
                                                            TableBlock<E> tableBlockLeft,
                                                            TableBlock<E> tableBlockRight );
  
  /**
   * Returns true if the given {@link TableBlock}s are affected by this {@link PredicateJoiner}
   * 
   * @param tableBlockLeft
   * @param tableBlockRight
   * @return
   */
  public boolean affectsBothTableBlocks( TableBlock<E> tableBlockLeft, TableBlock<E> tableBlockRight );
  
  /**
   * Returns the {@link Column}s which are required for this {@link PredicateJoiner}
   * 
   * @return
   */
  public Column<E>[] getRequiredColumns();
}
