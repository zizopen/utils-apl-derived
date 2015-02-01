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
import static org.junit.Assert.assertNotSame;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @see MapBuilderOld
 * @author Omnaest
 */
public class MapBuilderOldTest
{
  
  @Test
  public void testBuild()
  {
    //
    assertMapBuilder( new MapBuilderOld<String, String>().map( LinkedHashMap.class ) );
    assertMapBuilder( new MapBuilderOld<String, String>().concurrentHashMap() );
    assertMapBuilder( new MapBuilderOld<String, String>().linkedHashMap() );
    assertMapBuilder( new MapBuilderOld<String, String>().hashMap() );
    assertMapBuilder( new MapBuilderOld<String, String>().treeMap() );
  }
  
  @Test
  public void testBuildMultipleTimes()
  {
    //
    assertNotSame( new MapBuilderOld<String, String>().map( LinkedHashMap.class ),
                   new MapBuilderOld<String, String>().map( LinkedHashMap.class ) );
    
  }
  
  private static void assertMapBuilder( MapBuilderOld<String, String>.MapBuilderWithMap<? extends Map<String, String>> mapBuilderWithMap )
  {
    //
    Map<String, String> map = mapBuilderWithMap.put( "key1", "value1" ).put( "key2", "value2" ).build();
    assertNotNull( map );
    
    //
    assertEquals( 2, map.size() );
    assertEquals( new HashSet<String>( Arrays.asList( "key1", "key2" ) ), map.keySet() );
    assertEquals( new HashSet<String>( Arrays.asList( "value1", "value2" ) ), new HashSet<String>( map.values() ) );
    
  }
  
}
