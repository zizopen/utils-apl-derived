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
package org.omnaest.utils.beans.replicator2;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Omnaest
 */
interface PreservedTypeInstanceManager extends Serializable
{
  
  /**
   * @param typeCollection
   */
  public void removeAll( Collection<? extends Class<?>> typeCollection );
  
  /**
   * @param typeCollection
   */
  public void retainAll( Collection<? extends Class<?>> typeCollection );
  
  /**
   * @param typeCollection
   */
  public void addAll( Collection<? extends Class<?>> typeCollection );
  
  /**
   * @param type
   */
  public void remove( Class<?> type );
  
  /**
   * @param type
   */
  public void add( Class<?> type );
  
  /**
   * @param type
   * @return
   */
  public boolean contains( Class<?> type );
}
