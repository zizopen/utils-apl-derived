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
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * @see HashIntersectionMap
 * @author Omnaest
 */
public class HashIntersectionMapTest
{
  
  @Test
  public void testPutGetRemove()
  {
    //
    Map<String, String> map = new HashIntersectionMap<String, String>();
    map.put( "key0", "value0" );
    map.put( "key1", "value1" );
    
    //
    assertNull( map.get( "non existing key" ) );
    assertEquals( "value0", map.get( "key0" ) );
    assertEquals( "value1", map.get( "key1" ) );
    
    //
    map.remove( "key0" );
    assertNull( map.get( "key0" ) );
  }
  
  @Test
  public void testKeySetAndValues() throws Exception
  {
    //
    Map<String, String> map = new HashIntersectionMap<String, String>();
    map.put( "key0", "value0" );
    map.put( "key1", "value1" );
    map.put( "key2", "value2" );
    map.put( "key3", "value3" );
    
    //
    assertEquals( SetUtils.valueOf( "key0", "key1", "key2", "key3" ), map.keySet() );
    assertEquals( SetUtils.valueOf( "value0", "value1", "value2", "value3" ), SetUtils.valueOf( map.values() ) );
    
    //
    map.keySet().remove( "key2" );
    assertEquals( SetUtils.valueOf( "key0", "key1", "key3" ), map.keySet() );
    assertEquals( SetUtils.valueOf( "value0", "value1", "value3" ), SetUtils.valueOf( map.values() ) );
  }
  
}
