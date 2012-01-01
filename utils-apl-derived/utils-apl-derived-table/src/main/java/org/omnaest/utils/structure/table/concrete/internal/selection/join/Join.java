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
package org.omnaest.utils.structure.table.concrete.internal.selection.join;

import org.omnaest.utils.structure.table.concrete.internal.selection.SelectionExecutor;
import org.omnaest.utils.structure.table.concrete.internal.selection.data.TableBlock;
import org.omnaest.utils.structure.table.concrete.predicates.internal.joiner.PredicateJoinerCollector;
import org.omnaest.utils.structure.table.internal.TableInternal.StripeDataList;

/**
 * @see TableBlock
 * @see SelectionExecutor
 * @see JoinInner
 * @author Omnaest
 * @param <E>
 */
public interface Join<E>
{
  /**
   * Joins two {@link TableBlock}s into one using a join dependent merging strategy
   * 
   * @see TableBlock
   * @see PredicateJoinerCollector
   * @param tableBlockLeft
   * @param tableBlockRight
   * @param stripeDataList
   * @param predicateJoinerCollector
   * @return
   */
  public TableBlock<E> joinTableBlocks( TableBlock<E> tableBlockLeft,
                                        TableBlock<E> tableBlockRight,
                                        StripeDataList<E> stripeDataList,
                                        PredicateJoinerCollector<E> predicateJoinerCollector );
}
