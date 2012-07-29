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
package org.omnaest.utils.structure.collection.set;

import java.util.Set;

import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.factory.concrete.HashSetFactory;

/**
 * A {@link SetDelta} calculates the changes between two given {@link Set}s
 * 
 * @author Omnaest
 * @param <E>
 */
public class SetDelta<E>
{
  /* ******************************** Variables / State (internal/hiding) ******************************** */
  private final Set<E> addedElementSet;
  private final Set<E> removedElementSet;
  private final Set<E> intersection;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SetDelta
   * @param firstSet
   * @param secondSet
   */
  public SetDelta( Set<E> firstSet, Set<E> secondSet )
  {
    // 
    firstSet = ObjectUtils.defaultIfNull( firstSet, new HashSetFactory<E>() );
    secondSet = ObjectUtils.defaultIfNull( secondSet, new HashSetFactory<E>() );
    
    //
    this.intersection = SetUtils.intersection( firstSet, secondSet );
    this.addedElementSet = SetUtils.removeAllAsNewSet( secondSet, this.intersection );
    this.removedElementSet = SetUtils.removeAllAsNewSet( firstSet, this.intersection );
  }
  
  /**
   * Returns all elements which have been added to the first {@link Set} to became the second {@link Set}
   * 
   * @return
   */
  public Set<E> getAddedElementSet()
  {
    return this.addedElementSet;
  }
  
  /**
   * Returns all elements which have been removed from the first {@link Set} to become the second {@link Set}
   * 
   * @return
   */
  public Set<E> getRemovedElementSet()
  {
    return this.removedElementSet;
  }
  
  /**
   * Returns all elements which does not have changed between the first and the second {@link Set}
   * 
   * @return
   */
  public Set<E> getRetainedElementSet()
  {
    return this.intersection;
  }
}
