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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.structure.collection.SortedListTestAbstract;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see FractalList
 * @author Omnaest
 */
public class FractalListTest extends SortedListTestAbstract
{
  //@Rule
  public ContiPerfRule          contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  protected FractalList<String> sortedList    = new FractalList<String>();
  
  private final List<String>    sourceList    = newSourceList();
  
  /* ********************************************** Methods ********************************************** */
  
  @PerfTest(invocations = 10)
  @Test
  public void testPerformanceTreeList()
  {
    //
    final SortedList<String> sortedList = new TreeList<String>();
    this.doTestPerformanceLoad( sortedList );
  }
  
  @PerfTest(invocations = 10)
  //@Test
  public void testPerformanceFractalList()
  {
    //
    final SortedList<String> sortedList = new FractalList<String>();
    this.doTestPerformanceLoad( sortedList );
  }
  
  private void doTestPerformanceLoad( SortedList<String> sortedList )
  {
    //
    sortedList.addAll( this.sourceList );
    
    //
    final List<String> listWithSortedElements = new ArrayList<String>( sortedList );
    sortedList.clear();
    sortedList.addAll( listWithSortedElements );
    
    //
    for ( int jj = 1; jj < 10; jj++ )
    {
      for ( String element : this.sourceList )
      {
        sortedList.indexOf( element );
      }
      
      //
      for ( int ii = 0; ii < this.sourceList.size(); ii++ )
      {
        sortedList.get( ii );
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAddE()
  {
    //
    this.sortedList.add( "b" );
    assertEquals( 1, this.sortedList.size() );
    
    this.sortedList.add( "a" );
    assertEquals( 2, this.sortedList.size() );
    
    //    
    this.sortedList.addAll( this.sourceList );
    
    // System.out.println( this.sortedList.rootNode );
    assertEquals( 2 + this.sourceList.size(), this.sortedList.size() );
    assertEquals( new TreeList<String>( ListUtils.mergeAll( this.sourceList, Arrays.asList( "a", "b" ) ) ), this.sortedList );
    
  }
  
  @Test
  public void testAddAllClear()
  {
    //
    assertTrue( this.sortedList.isEmpty() );
    this.sortedList.addAll( this.sourceList );
    assertFalse( this.sortedList.isEmpty() );
    
    //
    this.sortedList.clear();
    assertTrue( this.sortedList.isEmpty() );
  }
  
  private static List<String> newSourceList()
  {
    //
    final List<String> retlist = new ArrayList<String>();
    
    //
    final int numberOfElements = 1 * 256;
    for ( int ii = 1; ii <= numberOfElements; ii++ )
    {
      retlist.add( String.format( "%03d", Math.round( Math.random() * 100 ) ) );
    }
    
    //
    return retlist;
  }
  
  @Override
  protected SortedList<String> newSortedList()
  {
    return new FractalList<String>();
  }
  
}
