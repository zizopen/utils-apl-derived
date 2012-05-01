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
package org.omnaest.utils.structure.collection.list.sorted;

import java.util.Collection;
import java.util.List;

/**
 * {@link SortedListDispatcher} which is based on the {@link #size()} of the represented virtual {@link SortedList}. If the
 * {@link List} exceeds a given size threshold it will switch to the second {@link List} if it falls down to a second size
 * threshold it will switch back to the first {@link List} instance.
 * 
 * @see SortedListDispatcher
 * @author Omnaest
 */
public class SortedListDispatcherSizeBased<E> extends SortedListDispatcher<E>
{
  /* ********************************************** Variables ********************************************** */
  private final int exceedThreshold;
  private final int dropUnderThreshold;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SortedListDispatcherSizeBased
   * @param firstList
   * @param secondList
   * @param exceedThreshold
   * @param dropUnderThreshold
   */
  @SuppressWarnings({ "unchecked" })
  public SortedListDispatcherSizeBased( SortedList<E> firstList, SortedList<E> secondList, int exceedThreshold,
                                        int dropUnderThreshold )
  {
    super( new SortedList[] { firstList, secondList } );
    this.exceedThreshold = exceedThreshold;
    this.dropUnderThreshold = dropUnderThreshold;
    
    //
    if ( firstList.size() + secondList.size() >= this.exceedThreshold )
    {
      this.listDispatchControl.rolloverToNextList();
    }
    else if ( !secondList.isEmpty() )
    {
      firstList.addAll( secondList );
      secondList.clear();
    }
  }
  
  /**
   * 
   */
  protected void checkDistributionAfterShrink()
  {
    if ( this.size() <= this.dropUnderThreshold )
    {
      this.listDispatchControl.rolloverToPreviousList();
    }
  }
  
  /**
   * 
   */
  protected void checkDistributionAfterExpansion()
  {
    if ( this.size() >= this.exceedThreshold )
    {
      this.listDispatchControl.rolloverToNextList();
    }
  }
  
  @Override
  public SortedList<E> splitAt( int index )
  {
    //
    final SortedList<E> retlist = super.splitAt( index );
    
    //
    this.checkDistributionAfterShrink();
    
    //
    return retlist;
  }
  
  @Override
  public boolean add( E e )
  {
    //
    final boolean retval = super.add( e );
    
    //
    this.checkDistributionAfterExpansion();
    
    // 
    return retval;
  }
  
  @Override
  public boolean remove( Object o )
  {
    //
    final boolean retval = super.remove( o );
    
    //
    this.checkDistributionAfterShrink();
    
    //
    return retval;
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    //
    final boolean retval = super.addAll( c );
    
    //
    this.checkDistributionAfterExpansion();
    
    // 
    return retval;
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    //
    final boolean retval = super.addAll( index, c );
    
    //
    this.checkDistributionAfterExpansion();
    
    // 
    return retval;
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    //
    final boolean retval = super.removeAll( c );
    
    //
    this.checkDistributionAfterShrink();
    
    // 
    return retval;
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    //
    final boolean retval = super.retainAll( c );
    
    //
    this.checkDistributionAfterShrink();
    
    // 
    return retval;
  }
  
  @Override
  public void clear()
  {
    // 
    super.clear();
    this.checkDistributionAfterShrink();
  }
  
  @Override
  public void add( int index, E element )
  {
    //
    super.add( index, element );
    this.checkDistributionAfterExpansion();
  }
  
  @Override
  public E remove( int index )
  {
    //
    final E retval = super.remove( index );
    
    //
    this.checkDistributionAfterShrink();
    
    //
    return retval;
  }
  
}
