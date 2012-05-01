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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

/**
 * @see IteratorDecoratorSwitchable
 * @author Omnaest
 */
public class IteratorDecoratorSwitchableTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @SuppressWarnings("unchecked")
  private final List<Iterator<String>>          iteratorList = Arrays.asList( Arrays.asList( "a", "b" ).iterator(),
                                                                              Arrays.asList( "b", "c" ).iterator() );
  protected IteratorDecoratorSwitchable<String> iterator     = new IteratorDecoratorSwitchable<String>( this.iteratorList );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testHasNextAndSwitch()
  {
    //
    assertTrue( this.iterator.hasActiveIterator() );
    
    // 
    assertTrue( this.iterator.hasNext() );
    this.iterator.next();
    assertTrue( this.iterator.hasNext() );
    this.iterator.next();
    assertFalse( this.iterator.hasNext() );
    
    //
    this.iterator.switchTo( 1 );
    assertTrue( this.iterator.hasNext() );
    
    //
    this.iterator.switchTo( this.iteratorList.get( 0 ) );
    assertFalse( this.iterator.hasNext() );
    
    this.iterator.switchTo( this.iteratorList.get( 1 ) );
    assertTrue( this.iterator.hasNext() );
    
    //
    this.iterator.switchToPrevious();
    assertFalse( this.iterator.hasNext() );
    assertTrue( this.iterator.hasActiveIterator() );
    this.iterator.switchToPrevious();
    assertFalse( this.iterator.hasNext() );
    assertFalse( this.iterator.hasActiveIterator() );
    this.iterator.switchToPrevious();
    assertFalse( this.iterator.hasNext() );
    assertFalse( this.iterator.hasActiveIterator() );
    
    //
    this.iterator.switchToNext();
    assertFalse( this.iterator.hasNext() );
    this.iterator.switchToNext();
    assertTrue( this.iterator.hasNext() );
    
    //
    this.iterator.switchToNext();
    assertFalse( this.iterator.hasNext() );
    assertFalse( this.iterator.hasActiveIterator() );
    this.iterator.switchToNext();
    assertFalse( this.iterator.hasNext() );
    assertFalse( this.iterator.hasActiveIterator() );
    this.iterator.switchToPrevious();
    assertTrue( this.iterator.hasNext() );
    assertTrue( this.iterator.hasActiveIterator() );
    
    //    
    assertTrue( this.iterator.hasNext() );
    this.iterator.next();
    assertTrue( this.iterator.hasNext() );
    this.iterator.next();
    assertFalse( this.iterator.hasNext() );
    
  }
  
  @Test(expected = UnsupportedOperationException.class)
  public void testRemove()
  {
    //
    this.iterator.next();
    this.iterator.remove();
  }
  
  @Test
  public void testSwitchToNextIteratorWhichHasNext()
  {
    assertTrue( this.iterator.hasActiveIterator() );
    assertTrue( this.iterator.hasNext() );
    this.iterator.switchToNextIteratorWhichHasNext();
    this.iterator.next();
    this.iterator.switchToNextIteratorWhichHasNext();
    this.iterator.next();
    assertFalse( this.iterator.hasNext() );
    assertTrue( this.iterator.hasActiveIterator() );
    this.iterator.switchToNextIteratorWhichHasNext();
    assertTrue( this.iterator.hasNext() );
    this.iterator.next();
    this.iterator.switchToNextIteratorWhichHasNext();
    this.iterator.next();
    assertFalse( this.iterator.hasNext() );
    assertTrue( this.iterator.hasActiveIterator() );
    this.iterator.switchToNextIteratorWhichHasNext();
    assertFalse( this.iterator.hasNext() );
    assertFalse( this.iterator.hasActiveIterator() );
  }
  
}
