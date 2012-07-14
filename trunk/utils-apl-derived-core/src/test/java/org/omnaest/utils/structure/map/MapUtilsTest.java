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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.FactoryParameterized;
import org.omnaest.utils.structure.map.MapUtils.MapEntryToElementConverter;

/**
 * @see MapUtils
 * @author Omnaest
 */
public class MapUtilsTest
{
  
  @Test
  public void testToList()
  {
    //
    Map<String, String> map = new LinkedHashMap<String, String>();
    map.put( "key1", "value1" );
    map.put( "key2", "value2" );
    
    //
    MapEntryToElementConverter<String, String, String> mapEntryToElementTransformer = new MapEntryToElementConverter<String, String, String>()
    {
      @Override
      public String convert( Entry<String, String> entry )
      {
        return entry.getKey() + "=" + entry.getValue();
      }
    };
    
    //
    List<String> list = MapUtils.toList( map, mapEntryToElementTransformer );
    assertNotNull( list );
    assertEquals( "key1=value1,key2=value2", StringUtils.join( list, "," ) );
    
  }
  
  @Test
  public void testFilteredMap()
  {
    //
    Map<String, String> map = new LinkedHashMap<String, String>();
    map.put( "key1", "value1" );
    map.put( "key2", "value2" );
    map.put( "key3", "value3" );
    
    //
    Set<String> filterKeySet = new LinkedHashSet<String>( Arrays.asList( "key1", "key3" ) );
    Map<String, String> filteredMap = MapUtils.filteredMap( map, filterKeySet );
    
    //
    assertNotNull( filteredMap );
    assertEquals( 2, filteredMap.size() );
    
    Iterator<String> iterator = filteredMap.keySet().iterator();
    assertEquals( "key1", iterator.next() );
    assertEquals( "key3", iterator.next() );
  }
  
  @Test
  public void testInvert()
  {
    final Map<String, Integer> sourceMap = new LinkedHashMap<String, Integer>();
    sourceMap.put( "key1", 1 );
    sourceMap.put( "key2", 1 );
    sourceMap.put( "key3", 2 );
    sourceMap.put( "key4", 3 );
    
    //
    final Map<Integer, Set<String>> invertedMap = MapUtils.invert( sourceMap );
    assertNotNull( invertedMap );
    assertEquals( 3, invertedMap.size() );
    
    //
    final Iterator<Entry<Integer, Set<String>>> iteratorEntrySet = invertedMap.entrySet().iterator();
    {
      assertTrue( iteratorEntrySet.hasNext() );
      final Entry<Integer, Set<String>> entry = iteratorEntrySet.next();
      {
        final int key = entry.getKey();
        final Set<String> value = entry.getValue();
        
        assertEquals( 1, key );
        assertEquals( SetUtils.valueOf( "key1", "key2" ), value );
      }
    }
    {
      assertTrue( iteratorEntrySet.hasNext() );
      final Entry<Integer, Set<String>> entry = iteratorEntrySet.next();
      {
        final int key = entry.getKey();
        final Set<String> value = entry.getValue();
        
        assertEquals( 2, key );
        assertEquals( SetUtils.valueOf( "key3" ), value );
      }
    }
    {
      assertTrue( iteratorEntrySet.hasNext() );
      final Entry<Integer, Set<String>> entry = iteratorEntrySet.next();
      {
        final int key = entry.getKey();
        final Set<String> value = entry.getValue();
        
        assertEquals( 3, key );
        assertEquals( SetUtils.valueOf( "key4" ), value );
      }
    }
  }
  
  /**
   * @see MapUtilsTest#testEnumMapWithFilledDefaultValues()
   * @author Omnaest
   */
  private enum TestEnum
  {
    key1,
    key2
  }
  
  /**
   * @see TestEnum
   */
  @SuppressWarnings("javadoc")
  @Test
  public void testInitializedEnumMap()
  {
    //
    final Class<TestEnum> enumType = TestEnum.class;
    final Factory<Set<String>> factory = new Factory<Set<String>>()
    {
      @Override
      public Set<String> newInstance()
      {
        return SetUtils.valueOf( "a", "b" );
      }
    };
    
    final EnumMap<TestEnum, Set<String>> enumMapWithFilledDefaultValues = MapUtils.initializedEnumMap( enumType, factory );
    assertNotNull( enumMapWithFilledDefaultValues );
    assertEquals( 2, enumMapWithFilledDefaultValues.size() );
    assertEquals( SetUtils.valueOf( "a", "b" ), enumMapWithFilledDefaultValues.get( TestEnum.key1 ) );
    assertEquals( SetUtils.valueOf( "a", "b" ), enumMapWithFilledDefaultValues.get( TestEnum.key2 ) );
  }
  
  @Test
  public void testInitializedMap()
  {
    FactoryParameterized<Boolean, String> valueFactory = new FactoryParameterized<Boolean, String>()
    {
      @Override
      public Boolean newInstance( String parameterMap )
      {
        return true;
      }
      
    };
    final Map<String, Boolean> initializedMap = MapUtils.initializedMap( valueFactory );
    assertTrue( initializedMap.get( "test" ) );
  }
  
  @Test
  public void testInvertedBidirectionalMap()
  {
    //
    final Map<String, Integer> sourceMap = new LinkedHashMap<String, Integer>();
    sourceMap.put( "key1", 1 );
    sourceMap.put( "key2", 2 );
    sourceMap.put( "key3", 3 );
    sourceMap.put( "key4", 4 );
    
    //
    final Map<Integer, String> invertedMap = MapUtils.invertedBidirectionalMap( sourceMap );
    assertNotNull( invertedMap );
    assertEquals( 4, invertedMap.size() );
    
    //
    assertEquals( new MapBuilderOld<Integer, String>().linkedHashMap()
                                                   .put( 1, "key1" )
                                                   .put( 2, "key2" )
                                                   .put( 3, "key3" )
                                                   .put( 4, "key4" )
                                                   .build(), invertedMap );
  }
  
  @Test
  public void testGetValueByRegex()
  {
    //
    {
      //
      final Map<String, String> map = new LinkedHashMap<String, String>();
      map.put( "key1", "value1" );
      map.put( "key2", "value2" );
      map.put( "thisKey", "value3" );
      
      //
      assertEquals( "value3", MapUtils.getValueByRegex( map, "this.*" ) );
      assertEquals( "value1", MapUtils.getValueByRegex( map, ".*" ) );
    }
    
    //
    assertEquals( null, MapUtils.getValueByRegex( null, "" ) );
    assertEquals( null, MapUtils.getValueByRegex( new HashMap<String, String>(), null ) );
  }
  
  @Test
  public void testParseString() throws Exception
  {
    final Map<String, String> map = MapUtils.parseString( "key1=value1;key2=value2" );
    assertEquals( 2, map.size() );
    assertEquals( "value1", map.get( "key1" ) );
    assertEquals( "value2", map.get( "key2" ) );
  }
  
  @Test
  public void testBuilder() throws Exception
  {
    String key = "key1";
    Object value = "value1";
    Map<String, Object> map = MapUtils.builder().put( key, value ).buildAs().linkedHashMap();
    assertNotNull( map );
    assertEquals( "key1", map.keySet().iterator().next() );
    assertEquals( "value1", map.values().iterator().next() );
  }
  
}
