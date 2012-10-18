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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetDelta;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * @see ThreadLocalCachedIterator
 * @author Omnaest
 */
public class ThreadLocalCachedIteratorTest
{
  /* ********************************************** Variables ********************************************** */
  private final List<String> sourceList = Arrays.asList( "a", "b", "c", "d", "e" );
  private Iterator<String>   iterator   = new ThreadLocalCachedIterator<String>( this.sourceList.iterator() );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testBasicFunctionality()
  {
    assertEquals( this.sourceList, ListUtils.valueOf( this.iterator ) );
  }
  
  @Test
  public void testThreadsafety() throws InterruptedException
  {
    final Iterator<String> iterator = generateIterator( 100 );
    
    Callable<List<String>> callable = new Callable<List<String>>()
    {
      @Override
      public List<String> call() throws Exception
      {
        List<String> retlist = new ArrayList<String>();
        while ( iterator.hasNext() )
        {
          try
          {
            String value = iterator.next();
            retlist.add( value );
            Thread.sleep( 1 );
          }
          catch ( Exception exception )
          {
            retlist.add( null );
          }
        }
        return retlist;
      }
    };
    
    ExecutorService executorService = Executors.newFixedThreadPool( 10 );
    
    Collection<Callable<List<String>>> taskList = new ArrayList<Callable<List<String>>>();
    taskList.add( callable );
    taskList.add( callable );
    
    List<Future<List<String>>> result = executorService.invokeAll( taskList );
    executorService.shutdown();
    
    List<Set<String>> setCollection = ListUtils.convert( result, new ElementConverter<Future<List<String>>, Set<String>>()
    {
      @Override
      public Set<String> convert( Future<List<String>> future )
      {
        try
        {
          final List<String> list = future.get();
          assertFalse( list.contains( null ) );
          return SetUtils.valueOf( list );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
        return null;
      }
    } );
    
    final Set<String> firstSet = setCollection.get( 0 );
    final Set<String> secondSet = setCollection.get( 1 );
    SetDelta<String> delta = SetUtils.delta( firstSet, secondSet );
    assertTrue( delta.getRetainedElementSet().isEmpty() );
    assertFalse( firstSet.isEmpty() );
    assertFalse( secondSet.isEmpty() );
    assertEquals( 100, firstSet.size() + secondSet.size() );
    
  }
  
  private static Iterator<String> generateIterator( int number )
  {
    List<String> retlist = new ArrayList<String>();
    for ( int ii = 0; ii < number; ii++ )
    {
      retlist.add( ii + "" );
    }
    return new ThreadLocalCachedIterator<String>( retlist.iterator() );
  }
}
