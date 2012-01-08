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
package org.omnaest.utils.structure.collection.set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.collection.CollectionUtils;

/**
 * @see IdentityArrayListBasedSet
 * @author Omnaest
 */
public class IdentityArrayListBasedSetTest
{
  /* ********************************************** Variables ********************************************** */
  protected Set<String> set = new IdentityArrayListBasedSet<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.set.add( "value1" );
    this.set.add( "value2" );
    this.set.add( "value3" );
    this.set.add( "value4" );
    this.set.add( "value5" );
  }
  
  @Test
  public void testContains()
  {
    assertTrue( this.set.contains( "value1" ) ); //on modern JVMs the constant strings with same values have the same reference
    assertFalse( this.set.contains( "VALUE1".toLowerCase() ) );
  }
  
  @Test
  public void testRemove()
  {
    boolean remove = this.set.remove( "value1" );
    assertTrue( remove );
    assertFalse( this.set.contains( "value1" ) );
    assertEquals( 4, this.set.size() );
  }
  
  @Test
  public void testSize()
  {
    assertEquals( 5, this.set.size() );
  }
  
  @Test
  public void testAdd()
  {
    boolean add = this.set.add( "value6" );
    assertTrue( add );
    assertEquals( 6, this.set.size() );
  }
  
  @Test
  public void testClear()
  {
    this.set.clear();
    assertEquals( 0, this.set.size() );
  }
  
  @Test
  public void testAddAll()
  {
    this.set.addAll( Arrays.asList( "value1", "value6", "value7" ) );
    assertEquals( 7, this.set.size() ); //value1 should be omitted
  }
  
  @Test
  public void testContainsAll()
  {
    assertTrue( this.set.containsAll( Arrays.asList( "value1", "value5" ) ) );
    assertFalse( this.set.containsAll( Arrays.asList( "value1", "value6" ) ) );
  }
  
  @Test
  public void testIsEmpty()
  {
    //
    assertFalse( this.set.isEmpty() );
    
    //
    this.set.clear();
    assertTrue( this.set.isEmpty() );
  }
  
  @Test
  public void testRemoveAll()
  {
    this.set.removeAll( Arrays.asList( "value1", "value5", "value1" ) );
    assertEquals( 3, this.set.size() );
  }
  
  @Test
  public void testRetainAll()
  {
    this.set.retainAll( Arrays.asList( "value2", "value4" ) );
    assertEquals( 2, this.set.size() );
    assertEquals( true, this.set.containsAll( Arrays.asList( "value2", "value4" ) ) );
  }
  
  @Test
  public void testToArray()
  {
    Object[] array = this.set.toArray();
    assertArrayEquals( array, CollectionUtils.toArray( this.set, String.class ) );
  }
  
  @Test
  public void testToArrayTArray()
  {
    String[] array = this.set.toArray( new String[0] );
    assertArrayEquals( array, CollectionUtils.toArray( this.set, String.class ) );
  }
  
}
