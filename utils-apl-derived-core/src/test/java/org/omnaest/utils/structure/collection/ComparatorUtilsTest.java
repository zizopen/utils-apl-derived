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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

/**
 * @see ComparatorUtils
 * @author Omnaest
 */
public class ComparatorUtilsTest
{
  
  @Test
  public void testComparatorUsingListIndexPosition()
  {
    //
    List<String> list = Arrays.asList( "g", "a", "b", "c", "d", "e", "f" );
    List<String> sortList = Arrays.asList( "c", "b", "a" );
    
    //
    Comparator<String> comparatorUsingListIndexPosition = ComparatorUtils.comparatorUsingListIndexPosition( sortList );
    
    //
    Collections.sort( list, comparatorUsingListIndexPosition );
    assertEquals( Arrays.asList( "c", "b", "a", "g", "d", "e", "f" ), list );
    
    //
    //System.out.println( list  );
  }
  
  @Test
  public void testComparatorDecoratorUsingWeakHashMapCache()
  {
    //
    final AtomicBoolean atomicBoolean = new AtomicBoolean( true );
    final Comparator<String> comparator = new Comparator<String>()
    {
      @Override
      public int compare( String o1, String o2 )
      {
        return atomicBoolean.get() ? o1.compareTo( o2 ) : -1;
      }
    };
    
    Comparator<String> comparatorDecoratorUsingWeakHashMapCache = ComparatorUtils.comparatorDecoratorUsingWeakHashMapCache( comparator );
    
    assertEquals( 0, comparatorDecoratorUsingWeakHashMapCache.compare( "test", "test" ) );
    atomicBoolean.set( false );
    assertEquals( 0, comparatorDecoratorUsingWeakHashMapCache.compare( "test", "test" ) );
  }
  
}
