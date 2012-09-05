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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentitiyCast;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToLong;
import org.omnaest.utils.structure.element.factory.Factory;

/**
 * @see IteratorUtils
 * @author Omnaest
 */
public class IteratorUtilsTest
{
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void testValueOf()
  {
    //
    final List<String> list0 = Arrays.asList( "a", "b" );
    final List<String> list1 = Arrays.asList( "b", "c" );
    Iterable[] iterables = new Iterable[] { list0, list1 };
    Iterator[] iterators = IteratorUtils.valueOf( iterables );
    
    //
    assertEquals( 2, iterators.length );
    assertEquals( list0, ListUtils.valueOf( iterators[0] ) );
    assertEquals( list1, ListUtils.valueOf( iterators[1] ) );
  }
  
  @Test
  public void testFactoryBasedIterator()
  {
    //    
    final Factory<Iterator<String>> iteratorFactory = new Factory<Iterator<String>>()
    {
      /* ********************************************** Variables ********************************************** */
      private int counter = 1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public Iterator<String> newInstance()
      {
        // 
        return this.counter++ <= 3 ? Arrays.asList( "a", "b", "c" ).iterator() : null;
      }
    };
    Iterator<String> iterator = IteratorUtils.factoryBasedIterator( iteratorFactory );
    assertNotNull( iterator );
    assertEquals( Arrays.asList( "a", "b", "c", "a", "b", "c", "a", "b", "c" ), ListUtils.valueOf( iterator ) );
  }
  
  @Test
  public void testAdapter()
  {
    //
    final List<String> sourceList = Arrays.asList( "100", "101", "102" );
    final Iterator<String> iterator = sourceList.iterator();
    final ElementConverter<String, Long> elementConverterFirst = new ElementConverterStringToLong();
    final ElementConverter<Long, Long> elementConverterSecond = new ElementConverterIdentitiyCast<Long, Long>();
    final ElementConverter<Number, String> elementConverterThird = new ElementConverterNumberToString();
    assertEquals( sourceList, ListUtils.valueOf( IteratorUtils.adapter( iterator, elementConverterFirst, elementConverterSecond,
                                                                        elementConverterThird ) ) );
  }
  
  @Test
  public void testDrainTo()
  {
    //    
    final Collection<String> collection = new ArrayList<String>();
    final int maxNumberOfElements = 2;
    final ArrayList<String> sourceList = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
    IteratorUtils.drainTo( sourceList.iterator(), collection, maxNumberOfElements );
    
    //
    assertEquals( Arrays.asList( "a", "b" ), collection );
    assertEquals( Arrays.asList( "c" ), sourceList );
  }
  
  @Test
  public void testReplicatingIteratorFactory() throws Exception
  {
    final List<String> sourceList = Arrays.asList( "a", "b", "c" );
    Factory<Iterator<String>> replicatingIteratorFactory = IteratorUtils.replicatingIteratorFactory( sourceList.iterator() );
    assertEquals( sourceList, ListUtils.valueOf( replicatingIteratorFactory.newInstance() ) );
    assertEquals( sourceList, ListUtils.valueOf( replicatingIteratorFactory.newInstance() ) );
    assertEquals( sourceList, ListUtils.valueOf( replicatingIteratorFactory.newInstance() ) );
  }
}
