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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.structure.map.MapUtils.MapEntryToElementTransformer;

/**
 * @see MapUtils
 * @author Omnaest
 */
public class MapUtilsTest
{
  
  @Test
  public void testAsList()
  {
    //
    Map<String, String> map = new LinkedHashMap<String, String>();
    map.put( "key1", "value1" );
    map.put( "key2", "value2" );
    
    //
    MapEntryToElementTransformer<String, String, String> mapEntryToElementTransformer = new MapEntryToElementTransformer<String, String, String>()
    {
      @Override
      public String transform( Entry<String, String> entry )
      {
        return entry.getKey() + "=" + entry.getValue();
      }
    };
    
    //
    List<String> list = MapUtils.asList( map, mapEntryToElementTransformer );
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
  
}
