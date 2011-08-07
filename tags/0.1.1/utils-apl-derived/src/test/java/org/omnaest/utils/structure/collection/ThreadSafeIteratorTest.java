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
package org.omnaest.utils.structure.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * @see ThreadSafeIterator
 * @author Omnaest
 */
public class ThreadSafeIteratorTest
{
  /* ********************************************** Variables ********************************************** */
  protected List<String>               list               = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
  protected Iterator<String>           iterator           = this.list.iterator();
  protected ThreadSafeIterator<String> threadSafeIterator = new ThreadSafeIterator<String>( this.iterator );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testHasNext()
  {
    //
    while ( this.iterator.hasNext() )
    {
      //
      assertTrue( this.threadSafeIterator.hasNext() );
      
      //
      this.iterator.next();
    }
    
    //
    assertFalse( this.threadSafeIterator.hasNext() );
  }
  
  @Test
  public void testNext()
  {
    //
    Iterator<String> iterator = this.list.iterator();
    while ( iterator.hasNext() )
    {
      assertEquals( iterator.next(), this.threadSafeIterator.next() );
    }
  }
  
  @Test
  public void testRemove()
  {
    //
    this.threadSafeIterator.next();
    this.threadSafeIterator.next();
    
    //
    this.threadSafeIterator.remove();
    
    //
    assertEquals( 2, this.list.size() );
  }
  
}
