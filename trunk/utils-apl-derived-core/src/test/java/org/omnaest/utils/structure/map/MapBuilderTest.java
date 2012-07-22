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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * @see MapBuilder
 * @author Omnaest
 */
public class MapBuilderTest
{
  
  @Test
  public void testBuild()
  {
    Map<String, String> map = MapUtils.builder()
                                      .put( "key1", "value1" )
                                      .put( "key2", "value2" )
                                      .put( "key3", "value3" )
                                      .buildAs()
                                      .linkedHashMap();
    assertNotNull( map );
    assertEquals( 3, map.size() );
    assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
    assertEquals( ListUtils.valueOf( "value1", "value2", "value3" ), ListUtils.valueOf( map.values() ) );
  }
  
  @Test
  public void testPutAll()
  {
    Map<String, String> map = MapUtils.builder()
                                      .putAll( MapUtils.builder()
                                                       .put( "key1", "value1" )
                                                       .put( "key2", "value2" )
                                                       .put( "key3", "value3" )
                                                       .buildAs()
                                                       .linkedHashMap() )
                                      .buildAs()
                                      .linkedHashMap();
    assertNotNull( map );
    assertEquals( 3, map.size() );
    assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
    assertEquals( ListUtils.valueOf( "value1", "value2", "value3" ), ListUtils.valueOf( map.values() ) );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testBuildComplex()
  {
    {
      Map<String, Object> map = MapUtils.builder()
                                        .put( "key1", (Object) "value1" )
                                        .put( "key2", 2l )
                                        .put( "key3", 1.234 )
                                        .buildAs()
                                        .linkedHashMap();
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
      assertEquals( ListUtils.valueOf( "value1", 2l, 1.234 ), ListUtils.valueOf( map.values() ) );
    }
    {
      Map<String, Long> map = MapUtils.builder()
                                      .put( "key1", 1l )
                                      .put( "key2", 2l )
                                      .put( "key3", 3l )
                                      .put( "key4", 4l )
                                      .buildAs()
                                      .linkedHashMap();
      assertNotNull( map );
      assertEquals( 4, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3", "key4" ), map.keySet() );
      assertEquals( ListUtils.valueOf( 1l, 2l, 3l, 4l ), ListUtils.valueOf( map.values() ) );
    }
    {
      Map<String, Object> map = MapUtils.builder()
                                        .<String, Object> put( "key1", 1l )
                                        .put( "key2", 2l )
                                        .put( "key3", 3l )
                                        .put( "key4", 4l )
                                        .buildAs()
                                        .linkedHashMap();
      assertNotNull( map );
      assertEquals( 4, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3", "key4" ), map.keySet() );
      assertEquals( ListUtils.valueOf( 1l, 2l, 3l, 4l ), ListUtils.valueOf( map.values() ) );
    }
  }
  
  @Test
  public void testBuildVariousInstances()
  {
    {
      Map<String, String> map = MapUtils.builder()
                                        .put( "key1", "value1" )
                                        .put( "key2", "value2" )
                                        .put( "key3", "value3" )
                                        .buildAs()
                                        .hashMap();
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
      assertEquals( SetUtils.valueOf( "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    }
    {
      Map<String, String> map = MapUtils.builder()
                                        .put( "key1", "value1" )
                                        .put( "key2", "value2" )
                                        .put( "key3", "value3" )
                                        .buildAs()
                                        .concurrentHashMap();
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
      assertEquals( SetUtils.valueOf( "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    }
    {
      Map<String, String> map = MapUtils.builder()
                                        .put( "key2", "value2" )
                                        .put( "key3", "value3" )
                                        .put( "key1", "value1" )
                                        .buildAs()
                                        .concurrentSkipListMap();
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( ListUtils.valueOf( "key1", "key2", "key3" ), ListUtils.valueOf( map.keySet() ) );
      assertEquals( SetUtils.valueOf( "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    }
    {
      Map<String, String> map = MapUtils.builder()
                                        .put( "key3", "value3" )
                                        .put( "key2", "value2" )
                                        .put( "key1", "value1" )
                                        .buildAs()
                                        .treeMap();
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( ListUtils.valueOf( "key1", "key2", "key3" ), ListUtils.valueOf( map.keySet() ) );
      assertEquals( SetUtils.valueOf( "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    }
    {
      @SuppressWarnings("unchecked")
      Map<String, String> map = MapUtils.builder()
                                        .put( "key1", "value1" )
                                        .put( "key2", "value2" )
                                        .put( "key3", "value3" )
                                        .buildAs()
                                        .map( HashMap.class );
      assertNotNull( map );
      assertEquals( 3, map.size() );
      assertEquals( SetUtils.valueOf( "key1", "key2", "key3" ), map.keySet() );
      assertEquals( SetUtils.valueOf( "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    }
  }
  
}
