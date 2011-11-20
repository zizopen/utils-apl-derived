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
import static org.junit.Assert.assertNotNull;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterElementToMapEntry;

public class ListUtilsTest
{
  
  @Test
  public void testMergeAll()
  {
    //
    @SuppressWarnings("unchecked")
    Collection<String>[] collections = new Collection[] { new ArrayList<String>(), new ArrayList<String>() };
    collections[0].add( "first value" );
    collections[0].add( "second value" );
    collections[1].add( "third value" );
    collections[1].add( "fourth value" );
    
    //
    List<String> mergedList = ListUtils.mergeAll( collections );
    assertEquals( 4, mergedList.size() );
    assertEquals( collections[0].toArray()[0], mergedList.get( 0 ) );
    assertEquals( collections[0].toArray()[1], mergedList.get( 1 ) );
    assertEquals( collections[1].toArray()[0], mergedList.get( 2 ) );
    assertEquals( collections[1].toArray()[1], mergedList.get( 3 ) );
  }
  
  @Test
  public void testIterableAsList()
  {
    //
    List<String> testList = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
    
    //
    List<String> listFromIterator = ListUtils.iteratorAsList( testList.iterator() );
    
    //
    assertEquals( testList, listFromIterator );
    
  }
  
  @Test
    public void testElementWithReverseIndexPosition()
    {
      //
      List<String> testList = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
      
      //
      assertEquals( "c", ListUtils.lastElementOf( testList ) );
      assertEquals( "c", ListUtils.elementWithReverseIndexPosition( testList, 0 ) );
      assertEquals( "b", ListUtils.elementWithReverseIndexPosition( testList, 1 ) );
      
      assertEquals( null, ListUtils.lastElementOf( Arrays.asList() ) );
      assertEquals( null, ListUtils.elementWithReverseIndexPosition( Arrays.asList(), 0 ) );
      assertEquals( null, ListUtils.elementWithReverseIndexPosition( Arrays.asList(), 1 ) );
      assertEquals( null, ListUtils.elementWithReverseIndexPosition( testList, testList.size() ) );
      
      assertEquals( null, ListUtils.lastElementOf( null ) );
      assertEquals( null, ListUtils.elementWithReverseIndexPosition( null, 0 ) );
    }
  
  @Test
  public void testIntersection()
  {
    //
    List<List<String>> testList = new ArrayList<List<String>>();
    testList.add( Arrays.asList( "a", "b", "c", "d" ) );
    testList.add( Arrays.asList( "a", "c", "d" ) );
    testList.add( Arrays.asList( "a", "b", "d" ) );
    
    //
    assertEquals( Arrays.asList( "a", "d" ), ListUtils.intersection( testList ) );
  }
  
  @Test
  public void testToMap()
  {
    //
    List<String> testList = new ArrayList<String>( Arrays.asList( "a", "b", "c" ) );
    
    //
    ElementConverterElementToMapEntry<String, String, String> elementToMapEntryTransformer = new ElementConverterElementToMapEntry<String, String, String>()
    {
      
      @Override
      public Entry<String, String> convert( String element )
      {
        //
        String key = "key" + element;
        String value = "value" + element;
        
        //
        return new AbstractMap.SimpleEntry<String, String>( key, value );
      }
    };
    
    //
    Map<String, String> map = ListUtils.toMap( testList, elementToMapEntryTransformer );
    
    //
    assertNotNull( map );
    assertEquals( testList.size(), map.size() );
    assertEquals( "key" + testList.get( 0 ), map.keySet().iterator().next() );
    assertEquals( "value" + testList.get( 0 ), map.values().iterator().next() );
  }
  
  @Test
  public void testIndexListOf()
  {
    //
    List<String> list = Arrays.asList( "a", "b", "a", "c" );
    
    //
    assertEquals( Arrays.asList( 0, 2 ), ListUtils.indexListOf( list, "a" ) );
    assertEquals( Arrays.asList( 1 ), ListUtils.indexListOf( list, "b" ) );
    assertEquals( Arrays.asList( 3 ), ListUtils.indexListOf( list, "c" ) );
    assertEquals( Arrays.asList(), ListUtils.indexListOf( list, "f" ) );
  }
  
  @Test
  public void testFilterExcludingElement()
  {
    //
    List<String> list = Arrays.asList( "abc", "def", "ghi" );
    assertEquals( Arrays.asList( "abc", "ghi" ), ListUtils.filterExcludingElement( list, "def" ) );
    
    //
    assertEquals( Arrays.asList( "abc", "ghi" ), ListUtils.filterExcludingIndexPositions( list, 1 ) );
    assertEquals( Arrays.asList( "def", "ghi" ), ListUtils.filterIncludingIndexPositions( list, Arrays.asList( 1, 2 ) ) );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testFilterAndOrderBy()
  {
    //
    List<String> sourceList = Arrays.asList( "a", "b", "c", "d", "e" );
    
    List<String> filterList1 = Arrays.asList( "b", "c", "d" );
    List<String> filterList2 = Arrays.asList( "a", "c", "d", "b", "e" );
    
    //
    List<String> filterAndOrderBy = ListUtils.filterAndOrderBy( sourceList, filterList1, filterList2 );
    assertNotNull( filterAndOrderBy );
    
    //
    assertEquals( Arrays.asList( "c", "d", "b" ), filterAndOrderBy );
    
  }
}
