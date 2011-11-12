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
 * @see MapBuilder
 * @author Omnaest
 */
public class MapBuilderTest
{
  
  @Test
  public void testBuild()
  {
    //
    assertMapBuilder( MapBuilder.<String, String> newMapBuilder( LinkedHashMap.class ) );
    assertMapBuilder( MapBuilder.<String, String> newConcurrentHashMapBuilder() );
    assertMapBuilder( MapBuilder.<String, String> newLinkedHashMapBuilder() );
    assertMapBuilder( MapBuilder.<String, String> newTreeMapBuilder() );
  }
  
  @Test
  public void testBuildMultipleTimes()
  {
    //
    assertNotSame( MapBuilder.<String, String> newMapBuilder( LinkedHashMap.class ),
                   MapBuilder.<String, String> newMapBuilder( LinkedHashMap.class ) );
    
  }
  
  private static void assertMapBuilder( MapBuilder<String, String> mapBuilder )
  {
    //
    Map<String, String> map = mapBuilder.put( "key1", "value1" ).put( "key2", "value2" ).build();
    assertNotNull( map );
    
    //
    assertEquals( 2, map.size() );
    assertEquals( new HashSet<String>( Arrays.asList( "key1", "key2" ) ), map.keySet() );
    assertEquals( new HashSet<String>( Arrays.asList( "value1", "value2" ) ), new HashSet<String>( map.values() ) );
    
  }
  
}
