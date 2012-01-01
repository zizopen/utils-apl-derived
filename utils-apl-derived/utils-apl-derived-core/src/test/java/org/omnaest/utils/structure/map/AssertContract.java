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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Helper to assert if a given {@link Map} instance fulfills the {@link Map} contract
 * 
 * @author Omnaest
 */
public class AssertContract
{
  
  /**
   * Asserts if a given {@link Map} instance fulfills the {@link Map} contract. There has testdata to be provided, since the key
   * and value type is generic.
   * 
   * @param map
   * @param testDataMap
   */
  public static <K, V> void assertMapContract( Map<K, V> map, Map<K, V> testDataMap )
  {
    //
    assertNotNull( map );
    assertNotNull( testDataMap );
    assertFalse( testDataMap.isEmpty() );
    
    //
    map.clear();
    assertTrue( map.isEmpty() );
    assertEquals( 0, map.size() );
    
    //
    {
      //
      final Entry<K, V> testEntry = testDataMap.entrySet().iterator().next();
      map.put( testEntry.getKey(), testEntry.getValue() );
      assertFalse( map.isEmpty() );
      assertEquals( 1, map.size() );
      assertTrue( map.containsKey( testEntry.getKey() ) );
      assertTrue( map.containsValue( testEntry.getValue() ) );
      assertNotNull( map.get( testEntry.getKey() ) );
      assertEquals( testEntry.getValue(), map.get( testEntry.getKey() ) );
    }
    
    //
    map.clear();
    assertTrue( map.isEmpty() );
    assertEquals( 0, map.size() );
    
    //
    {
      //
      final Entry<K, V> testEntry = testDataMap.entrySet().iterator().next();
      map.put( testEntry.getKey(), testEntry.getValue() );
      
      //
      assertEquals( testEntry.getValue(), map.remove( testEntry.getKey() ) );
      assertTrue( map.isEmpty() );
      assertFalse( map.containsKey( testEntry.getKey() ) );
      assertFalse( map.containsValue( testEntry.getValue() ) );
    }
    
    //
    map.clear();
    
    //
    {
      //
      map.putAll( testDataMap );
      
      //
      assertEquals( testDataMap.size(), map.size() );
      assertEquals( new ArrayList<K>( testDataMap.keySet() ), new ArrayList<K>( map.keySet() ) );
      assertEquals( new ArrayList<V>( testDataMap.values() ), new ArrayList<V>( map.values() ) );
    }
    
    //
    map.clear();
  }
}
