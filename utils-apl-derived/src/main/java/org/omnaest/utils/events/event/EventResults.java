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
package org.omnaest.utils.events.event;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.omnaest.utils.structure.collection.list.iterator.ListIterable;

/**
 * Container for event results offering several methods to get meta information about the results.
 * 
 * @see ListIterable
 * @author Omnaest
 * @param <RESULT>
 */
public class EventResults<RESULT> implements ListIterable<RESULT>
{
  /* ********************************************** Variables ********************************************** */
  protected List<RESULT> resultList = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param resultList
   */
  public EventResults( List<RESULT> resultList )
  {
    super();
    this.resultList = resultList;
  }
  
  /**
   * @return
   */
  public int size()
  {
    return this.resultList.size();
  }
  
  /**
   * @return
   */
  public boolean isEmpty()
  {
    return this.resultList.isEmpty();
  }
  
  /**
   * @param o
   * @return
   */
  public boolean contains( Object o )
  {
    return this.resultList.contains( o );
  }
  
  /**
   * @param c
   * @return
   */
  public boolean containsAll( Collection<?> c )
  {
    return this.resultList.containsAll( c );
  }
  
  /**
   * @param index
   * @return
   */
  public RESULT get( int index )
  {
    return this.resultList.get( index );
  }
  
  @Override
  public ListIterator<RESULT> iterator()
  {
    return this.resultList.listIterator();
  }
  
  /**
   * Returns an undmodifyable {@link List} of the event results
   * 
   * @return
   */
  public List<RESULT> getResultList()
  {
    return Collections.unmodifiableList( this.resultList );
  }
  
}
