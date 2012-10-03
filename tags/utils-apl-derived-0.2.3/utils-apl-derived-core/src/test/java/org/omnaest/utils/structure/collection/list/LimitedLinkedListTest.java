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
package org.omnaest.utils.structure.collection.list;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @see LimitedLinkedList
 * @author Omnaest
 */
public class LimitedLinkedListTest
{
  /* ********************************************** Variables ********************************************** */
  private LimitedLinkedList<String> limitedLinkedList = new LimitedLinkedList<String>( 3 );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testAddFirstE()
  {
    assertEquals( 0, this.limitedLinkedList.size() );
    this.limitedLinkedList.addFirst( "1" );
    this.limitedLinkedList.addFirst( "2" );
    this.limitedLinkedList.addFirst( "3" );
    assertEquals( 3, this.limitedLinkedList.size() );
    this.limitedLinkedList.addFirst( "4" );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "3", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testAddLastE()
  {
    this.limitedLinkedList.addLast( "1" );
    this.limitedLinkedList.addLast( "2" );
    this.limitedLinkedList.addLast( "3" );
    assertEquals( 3, this.limitedLinkedList.size() );
    this.limitedLinkedList.addLast( "4" );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "2", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testAddE()
  {
    this.limitedLinkedList.add( "1" );
    this.limitedLinkedList.add( "2" );
    this.limitedLinkedList.add( "3" );
    assertEquals( 3, this.limitedLinkedList.size() );
    this.limitedLinkedList.add( "4" );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "2", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testAddAllCollectionOfQextendsE()
  {
    this.limitedLinkedList.addAll( Arrays.asList( "1", "2", "3", "4" ) );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "2", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testAddIntE()
  {
    this.limitedLinkedList.add( "1" );
    this.limitedLinkedList.add( "2" );
    this.limitedLinkedList.add( "3" );
    assertEquals( 3, this.limitedLinkedList.size() );
    this.limitedLinkedList.add( 0, "4" );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "1", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testSetSizeMax()
  {
    this.limitedLinkedList.add( "1" );
    this.limitedLinkedList.add( "2" );
    this.limitedLinkedList.add( "3" );
    assertEquals( 3, this.limitedLinkedList.size() );
    this.limitedLinkedList.setSizeMax( 2 );
    assertEquals( 2, this.limitedLinkedList.size() );
    assertEquals( "2", this.limitedLinkedList.getFirst() );
  }
  
  @Test
  public void testSetRemoveFirstElementByExceedingSize()
  {
    //
    this.limitedLinkedList.setRemoveFirstElementByExceedingSize( false );
    
    //
    this.limitedLinkedList.addAll( Arrays.asList( "1", "2", "3", "4" ) );
    assertEquals( 3, this.limitedLinkedList.size() );
    assertEquals( "1", this.limitedLinkedList.getFirst() );
  }
  
}
