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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

/**
 * @see QueueToCircularIteratorAdapter
 * @author Omnaest
 */
public class QueueToCircularIteratorAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private Queue<String>    queue    = new ConcurrentLinkedQueue<String>( Arrays.asList( "a", "b", "c" ) );
  private Iterator<String> iterator = new QueueToCircularIteratorAdapter<String>( this.queue );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNext()
  {
    //
    assertTrue( this.iterator.hasNext() );
    assertEquals( "a", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "b", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "c", this.iterator.next() );
    
    assertTrue( this.iterator.hasNext() );
    assertEquals( "a", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "b", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "c", this.iterator.next() );
    
    assertTrue( this.iterator.hasNext() );
    assertEquals( "a", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "b", this.iterator.next() );
    assertTrue( this.iterator.hasNext() );
    assertEquals( "c", this.iterator.next() );
    
    //
    this.queue.clear();
    assertFalse( this.iterator.hasNext() );
  }
  
}
