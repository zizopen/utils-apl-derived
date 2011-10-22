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
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;
import org.omnaest.utils.structure.iterator.ListIteratorIndexBased;

/**
 * @see ListIteratorIndexBased
 * @author Omnaest
 */
public class ListIteratorIndexBasedTest
{
  /* ********************************************** Variables ********************************************** */
  protected List<String>         list         = new ArrayList<String>( Arrays.asList( "1", "2", "3" ) );
  protected ListIterator<String> listIterator = new ListIteratorIndexBased<String>( this.list );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testIteration()
  {
    //-1
    assertEquals( 0, this.listIterator.nextIndex() );
    assertEquals( -1, this.listIterator.previousIndex() );
    assertTrue( this.listIterator.hasNext() );
    assertFalse( this.listIterator.hasPrevious() );
    
    //0
    assertNotNull( this.listIterator.next() );
    assertEquals( 1, this.listIterator.nextIndex() );
    assertEquals( 0, this.listIterator.previousIndex() );
    assertTrue( this.listIterator.hasNext() );
    assertTrue( this.listIterator.hasPrevious() );
    
    //1
    assertNotNull( this.listIterator.next() );
    assertEquals( 2, this.listIterator.nextIndex() );
    assertEquals( 1, this.listIterator.previousIndex() );
    assertTrue( this.listIterator.hasNext() );
    assertTrue( this.listIterator.hasPrevious() );
    
    //1
    assertNotNull( this.listIterator.previous() );
    assertEquals( 1, this.listIterator.nextIndex() );
    assertEquals( 0, this.listIterator.previousIndex() );
    assertTrue( this.listIterator.hasNext() );
    assertTrue( this.listIterator.hasPrevious() );
    
    //1
    assertNotNull( this.listIterator.next() );
    assertEquals( 2, this.listIterator.nextIndex() );
    assertEquals( 1, this.listIterator.previousIndex() );
    assertTrue( this.listIterator.hasNext() );
    assertTrue( this.listIterator.hasPrevious() );
    
    //2
    assertNotNull( this.listIterator.next() );
    assertEquals( 3, this.listIterator.nextIndex() );
    assertEquals( 2, this.listIterator.previousIndex() );
    assertFalse( this.listIterator.hasNext() );
    assertTrue( this.listIterator.hasPrevious() );
    
    //3
    assertNull( this.listIterator.next() );
    assertEquals( 4, this.listIterator.nextIndex() );
    assertEquals( 3, this.listIterator.previousIndex() );
    assertFalse( this.listIterator.hasNext() );
    assertFalse( this.listIterator.hasPrevious() );
    
    //4
    assertNull( this.listIterator.next() );
    assertEquals( 5, this.listIterator.nextIndex() );
    assertEquals( 4, this.listIterator.previousIndex() );
    assertFalse( this.listIterator.hasNext() );
    assertFalse( this.listIterator.hasPrevious() );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.listIterator.next();//1
    this.listIterator.next();//2
    
    //
    this.listIterator.remove();//2 is gone
    
    //
    assertEquals( "3", this.listIterator.next() );
    assertEquals( "3", this.listIterator.previous() );
    assertEquals( "1", this.listIterator.previous() );
  }
  
  @Test
  public void testNext()
  {
    //
    ListIterator<String> iterator = this.list.listIterator();
    while ( iterator.hasNext() )
    {
      assertEquals( iterator.next(), this.listIterator.next() );
      assertEquals( iterator.previousIndex(), this.listIterator.previousIndex() );
      assertEquals( iterator.nextIndex(), this.listIterator.nextIndex() );
    }
  }
  
  @Test
  public void testPrevious()
  {
    //
    ListIterator<String> iterator = this.list.listIterator();
    while ( iterator.hasNext() )
    {
      this.listIterator.next();
      iterator.next();
    }
    
    //
    while ( iterator.hasPrevious() )
    {
      assertEquals( iterator.previous(), this.listIterator.previous() );
      assertEquals( iterator.previousIndex(), this.listIterator.previousIndex() );
      assertEquals( iterator.nextIndex(), this.listIterator.nextIndex() );
    }
    assertEquals( null, this.listIterator.previous() );
  }
  
  @Test
  public void testSet()
  {
    //
    this.listIterator.next();//0
    this.listIterator.next();//1
    this.listIterator.set( "x" );
    
    //
    assertEquals( "x", this.list.get( 1 ) );
  }
  
  @Test
  public void testAdd()
  {
    //
    this.listIterator.next();//0
    this.listIterator.next();//1
    this.listIterator.add( "x" );
    
    //
    assertEquals( "x", this.list.get( 1 ) );
    assertEquals( "2", this.list.get( 2 ) );
  }
  
}
