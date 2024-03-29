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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see IterableUtils
 * @author Omnaest
 */
public class IterableUtilsTest
{
  
  @Test
  public void testEqualsIterableOfQIterableOfQ()
  {
    assertTrue( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "b", "c" ) ) );
    assertTrue( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), new LinkedHashSet<String>( Arrays.asList( "a", "b", "c" ) ) ) );
    assertTrue( IterableUtils.equals( null, null ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "b", "c" ), Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b" ), Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "b" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "b", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "b", "c" ), Arrays.asList( "a", "d", "c" ) ) );
    assertFalse( IterableUtils.equals( Arrays.asList( "a", "d", "c" ), Arrays.asList( "a", "b", "c" ) ) );
  }
  
  @Test
  public void testHashCode()
  {
    assertEquals( IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ),
                  IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "b", "c", "d" ) ) == IterableUtils.hashCode( Arrays.asList( "a",
                                                                                                                         "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "c", "c" ) ) == IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
    assertFalse( IterableUtils.hashCode( Arrays.asList( "a", "c", "b" ) ) == IterableUtils.hashCode( Arrays.asList( "a", "b", "c" ) ) );
  }
  
  @Test
  public void testCircular()
  {
    //
    List<String> list = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
    
    //
    {
      //
      Iterable<String> circularIterable = IterableUtils.circular( list );
      assertNotNull( circularIterable );
      
      //
      Iterator<String> iterator = circularIterable.iterator();
      for ( int ii = 0; ii < 10; ii++ )
      {
        //
        assertTrue( iterator.hasNext() );
        assertEquals( "a", iterator.next() );
        assertTrue( iterator.hasNext() );
        assertEquals( "b", iterator.next() );
        assertTrue( iterator.hasNext() );
        assertEquals( "c", iterator.next() );
      }
    }
    
    //
    {
      //
      Iterable<String> circularIterable = IterableUtils.circular( list, 10 );
      assertNotNull( circularIterable );
      
      //
      Iterator<String> iterator = circularIterable.iterator();
      for ( int ii = 0; ii < 10; ii++ )
      {
        //
        assertTrue( iterator.hasNext() );
        assertEquals( "a", iterator.next() );
        assertTrue( iterator.hasNext() );
        assertEquals( "b", iterator.next() );
        assertTrue( iterator.hasNext() );
        assertEquals( "c", iterator.next() );
      }
      
      //
      assertFalse( iterator.hasNext() );
    }
    
  }
  
  @Test
  public void testCountEquals()
  {
    assertEquals( 2, IterableUtils.countEquals( Arrays.asList( "a", "a", "b" ), "a" ) );
  }
  
  @Test
  public void testFirstAndLastElement()
  {
    assertEquals( "a", IterableUtils.firstElement( Arrays.asList( "a", "b", "c" ) ) );
    assertEquals( "c", IterableUtils.lastElement( Arrays.asList( "a", "b", "c" ) ) );
  }
  
  @Test
  public void testLimitingIteratorDecorator()
  {
    //
    final int limit = 2;
    assertEquals( Arrays.asList( "a", "b" ),
                  ListUtils.valueOf( IteratorUtils.limitingIteratorDecorator( Arrays.asList( "a", "b", "c" ).iterator(), limit ) ) );
  }
  
  @Test
  public void testElementAt() throws Exception
  {
    //
    final List<String> list = Arrays.asList( "a", "b", "c" );
    assertEquals( "a", IterableUtils.elementAt( list, 0 ) );
    assertEquals( "b", IterableUtils.elementAt( list, 1 ) );
    assertEquals( "c", IterableUtils.elementAt( list, 2 ) );
    assertNull( IterableUtils.elementAt( list, 3 ) );
  }
  
  @Test
  public void testLastElement() throws Exception
  {
    final List<String> list = Arrays.asList( "a", "b", "c" );
    assertEquals( "c", IterableUtils.lastElement( list ) );
  }
  
  @Test
  public void testFiltered() throws Exception
  {
    BitSet filter = new BitSet();
    filter.set( 1 );
    filter.set( 3 );
    
    assertEquals( Arrays.asList( "b", "d" ),
                  ListUtils.valueOf( IterableUtils.filtered( Arrays.asList( "a", "b", "c", "d" ), filter ) ) );
    
  }
  
  @Test
  public void testToCountedElementsMap() throws Exception
  {
    Map<String, Integer> countedElementsMap = IterableUtils.toCountedElementsMap( Arrays.asList( "a", "b", "c", "a", "a", "c" ) );
    assertEquals( 3, countedElementsMap.size() );
    assertEquals( ListUtils.valueOf( "a", "c", "b" ), ListUtils.valueOf( countedElementsMap.keySet() ) );
    assertEquals( ListUtils.valueOf( 3, 2, 1 ), ListUtils.valueOf( countedElementsMap.values() ) );
  }
  
  @Test
  public void testReplicate() throws Exception
  {
    final List<String> list1 = new ArrayList<String>();
    final List<String> list2 = new ArrayList<String>();
    
    final List<String> sourceList = Arrays.asList( "a", "b", "c" );
    IterableUtils.replicate( sourceList ).to( list1 ).to( list2 );
    
    assertEquals( sourceList, list1 );
    assertEquals( sourceList, list2 );
  }
  
  @Test
  public void testReplicatingIteratorFactory() throws Exception
  {
    final List<String> sourceList = Arrays.asList( "a", "b", "c" );
    Iterable<String> iterable = IterableUtils.valueOf( sourceList.iterator(), true );
    assertEquals( sourceList, ListUtils.valueOf( iterable.iterator() ) );
    assertEquals( sourceList, ListUtils.valueOf( iterable.iterator() ) );
    assertEquals( sourceList, ListUtils.valueOf( iterable.iterator() ) );
  }
  
  @Test
  public void testRoundRobin() throws Exception
  {
    @SuppressWarnings("unchecked")
    Iterable<String>[] iterables = new Iterable[] { Arrays.asList( "a", "b", "c" ), Arrays.asList( "d", "e", "f" ),
        Arrays.asList( "g" ), null, new Iterable<String>()
        {
          @Override
          public Iterator<String> iterator()
          {
            return null;
          }
        } };
    Iterable<String> roundRobinIterable = IterableUtils.roundRobin( iterables );
    
    assertEquals( Arrays.asList( "a", "d", "g", "b", "e", "c", "f" ), ListUtils.valueOf( roundRobinIterable ) );
    assertEquals( Arrays.asList( "a", "d", "g", "b", "e", "c", "f" ), ListUtils.valueOf( roundRobinIterable ) );
  }
}
