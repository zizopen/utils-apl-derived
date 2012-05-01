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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

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
}
