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
package org.omnaest.utils.structure.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.sorted.SortedList;

/**
 * Abstract test for any {@link SortedList} implementation
 * 
 * @author Omnaest
 */
public abstract class SortedListTestAbstract
{
  
  /**
   * Returns a new {@link SortedList} instance
   */
  protected abstract SortedList<String> newSortedList();
  
  /**
   * Similar to {@link #newSortedList()} but used {@link SortedList#addAll(Collection)} to add all given {@link Collection}s
   * 
   * @param collections
   * @return
   */
  protected SortedList<String> newSortedList( Collection<String>... collections )
  {
    //    
    final SortedList<String> retlist = this.newSortedList();
    
    //
    for ( Collection<String> collection : collections )
    {
      retlist.addAll( collection );
    }
    
    //
    return retlist;
  }
  
  /**
   * Prepares a new {@link SortedList} instance with following values:<br>
   * <code>"b", "a", "b", "d", "f", "c", "e", "e"</code><br>
   * which will result in a sorted form as:<br>
   * <code>"a", "b", "b", "c", "d", "e", "e", "f"</code>
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  protected SortedList<String> newPreparedSortedList()
  {
    return this.newSortedList( Arrays.asList( "b", "a", "b", "d", "f", "c", "e", "e" ) );
  }
  
  @Test
  public void testIndexOfAndLastIndexOf()
  {
    //
    final SortedList<String> sortedList = this.newPreparedSortedList();
    
    //
    assertEquals( -1, sortedList.indexOf( "x" ) );
    assertEquals( -1, sortedList.lastIndexOf( "x" ) );
    
    //
    assertEquals( 0, sortedList.indexOf( "a" ) );
    assertEquals( 0, sortedList.lastIndexOf( "a" ) );
    assertEquals( 1, sortedList.indexOf( "b" ) );
    assertEquals( 2, sortedList.lastIndexOf( "b" ) );
    assertEquals( 3, sortedList.indexOf( "c" ) );
    assertEquals( 3, sortedList.lastIndexOf( "c" ) );
    assertEquals( 5, sortedList.indexOf( "e" ) );
    assertEquals( 6, sortedList.lastIndexOf( "e" ) );
    assertEquals( 7, sortedList.indexOf( "f" ) );
    assertEquals( 7, sortedList.lastIndexOf( "f" ) );
  }
  
  @Test
  public void testClearAndIsEmptyAndSize()
  {
    //
    final SortedList<String> sortedList = this.newSortedList();
    assertTrue( sortedList.isEmpty() );
    assertEquals( 0, sortedList.size() );
    
    //
    sortedList.addAll( Arrays.asList( "a", "a", "b" ) );
    assertFalse( sortedList.isEmpty() );
    assertEquals( 3, sortedList.size() );
    
    //
    sortedList.clear();
    assertTrue( sortedList.isEmpty() );
    assertEquals( 0, sortedList.size() );
  }
  
  @Test
  public void testHeadAndSubAndTailList()
  {
    //
    final SortedList<String> sortedList = this.newPreparedSortedList();
    
    //
    assertEquals( Arrays.asList(), sortedList.headList( "a" ) );
    assertEquals( Arrays.asList( "a", "b", "b" ), sortedList.headList( "c" ) );
    assertEquals( Arrays.asList( "a", "b", "b", "c", "d" ), sortedList.headList( "e" ) );
    
    //
    assertEquals( Arrays.asList( "f" ), sortedList.tailList( "f" ) );
    assertEquals( sortedList, sortedList.tailList( "a" ) );
    
    //
    assertEquals( Arrays.asList( "b", "b", "c", "d" ), sortedList.subList( "b", "e" ) );
  }
  
  @Test
  public void testAddE()
  {
    //
    final SortedList<String> sortedList = this.newSortedList();
    
    //
    sortedList.add( "b" );
    assertEquals( 1, sortedList.size() );
    assertEquals( 0, sortedList.indexOf( "b" ) );
    
    //
    sortedList.add( "b" );
    assertEquals( 2, sortedList.size() );
    assertEquals( 0, sortedList.indexOf( "b" ) );
    
    //
    sortedList.add( "a" );
    assertEquals( 3, sortedList.size() );
    assertEquals( 1, sortedList.indexOf( "b" ) );
    assertEquals( 0, sortedList.indexOf( "a" ) );
  }
  
  @Test
  public void testAddAll()
  {
    //
    final SortedList<String> sortedList = this.newSortedList();
    
    //
    sortedList.addAll( Arrays.asList( "b", "a", "b" ) );
    assertEquals( 3, sortedList.size() );
    assertEquals( 1, sortedList.indexOf( "b" ) );
    assertEquals( 0, sortedList.indexOf( "a" ) );
  }
  
  @Test
  public void testSortedOrder()
  {
    //
    final SortedList<String> sortedList = this.newSortedList();
    
    //
    final List<String> list = newListWithRandomElements( 256 );
    sortedList.addAll( list );
    
    //
    Collections.sort( list );
    assertEquals( list, sortedList );
  }
  
  @Test
  public void testGet()
  {
    //
    final SortedList<String> sortedList = this.newPreparedSortedList();
    
    //    
    int index = 0;
    assertEquals( "a", sortedList.get( index++ ) );
    assertEquals( "b", sortedList.get( index++ ) );
    assertEquals( "b", sortedList.get( index++ ) );
    assertEquals( "c", sortedList.get( index++ ) );
    assertEquals( "d", sortedList.get( index++ ) );
    assertEquals( "e", sortedList.get( index++ ) );
    assertEquals( "e", sortedList.get( index++ ) );
    assertEquals( "f", sortedList.get( index++ ) );
  }
  
  protected static List<String> newListWithRandomElements( int listSize )
  {
    //
    final List<String> list = new ArrayList<String>();
    for ( int ii = 1; ii <= listSize; ii++ )
    {
      list.add( "" + Math.round( Math.random() * 10 ) );
    }
    return list;
  }
}
