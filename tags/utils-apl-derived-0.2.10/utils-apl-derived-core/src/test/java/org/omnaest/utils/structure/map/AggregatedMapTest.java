/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * @see AggregatedMap
 * @author Omnaest
 */
public class AggregatedMapTest
{
  
  @Test
  public void test()
  {
    final Map<String, String> map1 = MapUtils.builder()
                                             .put( "key1", "value1" )
                                             .put( "key2", "value2" )
                                             .put( "key3", "value3" )
                                             .buildAs()
                                             .linkedHashMap();
    final Map<String, String> map2 = MapUtils.builder()
                                             .put( "key2", "value2_1" )
                                             .put( "key3", "value3_1" )
                                             .put( "key4", "value4" )
                                             .buildAs()
                                             .linkedHashMap();
    @SuppressWarnings("unchecked")
    Map<String, String>[] maps = new Map[] { map1, map2 };
    Map<String, String> aggregatedMap = new AggregatedMap<String, String>( maps );
    
    assertEquals( 4, aggregatedMap.size() );
    assertEquals( 4, aggregatedMap.keySet().size() );
    assertEquals( SetUtils.valueOf( "key1", "key2", "key3", "key4" ), aggregatedMap.keySet() );
    assertEquals( SetUtils.valueOf( "value1", "value2", "value3", "value4" ), SetUtils.valueOf( aggregatedMap.values() ) );
    assertFalse( aggregatedMap.isEmpty() );
    assertEquals( "value1", aggregatedMap.get( "key1" ) );
    assertEquals( "value2", aggregatedMap.get( "key2" ) );
    assertEquals( "value3", aggregatedMap.get( "key3" ) );
    assertEquals( "value4", aggregatedMap.get( "key4" ) );
    
    aggregatedMap.put( "key2", "value2_2" );
    assertEquals( "value2_2", map1.get( "key2" ) );
    assertEquals( "value2_2", map2.get( "key2" ) );
    
    aggregatedMap.remove( "key2" );
    assertFalse( map1.containsKey( "key2" ) );
    assertFalse( map2.containsKey( "key2" ) );
    
    aggregatedMap.put( "key2", "value2_2" );
    assertEquals( "value2_2", map1.get( "key2" ) );
    assertFalse( map2.containsKey( "key2" ) );
    
    maps[0] = null;
    assertEquals( 2, aggregatedMap.size() );
    assertFalse( aggregatedMap.containsKey( "key1" ) );
    assertFalse( aggregatedMap.containsKey( "key2" ) );
    assertEquals( "value3_1", aggregatedMap.get( "key3" ) );
    assertEquals( "value4", aggregatedMap.get( "key4" ) );
    assertEquals( SetUtils.valueOf( "key3", "key4" ), aggregatedMap.keySet() );
    assertEquals( SetUtils.valueOf( "value3_1", "value4" ), SetUtils.valueOf( aggregatedMap.values() ) );
    
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testEquals() throws Exception
  {
    final Map<String, String> map1 = MapUtils.builder()
                                             .put( "key1", "value1" )
                                             .put( "key2", "value2" )
                                             .put( "key3", "value3" )
                                             .buildAs()
                                             .linkedHashMap();
    final Map<String, String> map2 = MapUtils.builder()
                                             .put( "key2", "value2_1" )
                                             .put( "key3", "value3_1" )
                                             .put( "key4", "value4" )
                                             .buildAs()
                                             .linkedHashMap();
    
    Map<String, String>[] maps = new Map[] { map1, map2 };
    Map<String, String> aggregatedMap = new AggregatedMap<String, String>( maps );
    assertEquals( aggregatedMap, new AggregatedMap<String, String>( aggregatedMap ) );
  }
}
