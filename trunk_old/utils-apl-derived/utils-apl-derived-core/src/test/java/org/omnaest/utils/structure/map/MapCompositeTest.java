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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;

/**
 * @see MapComposite
 * @author Omnaest
 */
public class MapCompositeTest
{
  /* ********************************************** Variables ********************************************** */
  private final Map<String, String> map1         = new LinkedHashMap<String, String>();
  private final Map<String, String> map2         = new LinkedHashMap<String, String>();
  
  @SuppressWarnings("unchecked")
  private final Map<String, String> compositeMap = new MapComposite<String, String>( this.map1, this.map2 );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testGet()
  {
    //
    initializeMaps();
    
    //
    assertEquals( 4, this.compositeMap.size() );
    assertEquals( Arrays.asList( "key1", "key2", "key3", "key4" ), ListUtils.valueOf( this.compositeMap.keySet() ) );
    assertEquals( Arrays.asList( "value1", "value2", "value3", "value4" ), ListUtils.valueOf( this.compositeMap.values() ) );
    
    //
    assertTrue( this.compositeMap.containsKey( "key3" ) );
    this.compositeMap.remove( "key3" );
    assertFalse( this.compositeMap.containsKey( "key3" ) );
    
    //
    this.compositeMap.put( "key5", "value5" );
    assertTrue( this.compositeMap.containsKey( "key5" ) );
    assertTrue( this.map2.containsKey( "key5" ) );
    
  }
  
  private void initializeMaps()
  {
    this.map1.put( "key1", "value1" );
    this.map1.put( "key2", "value2" );
    this.map2.put( "key3", "value3" );
    this.map2.put( "key4", "value4" );
  }
  
  @Test
  public void testKeySetAndValues() throws Exception
  {
    //
    this.initializeMaps();
    assertEquals( SetUtils.valueOf( "key1", "key2", "key3", "key4" ), this.compositeMap.keySet() );
    assertEquals( Arrays.asList( "value1", "value2", "value3", "value4" ), new ArrayList<String>( this.compositeMap.values() ) );
    
    //
    assertNotNull( this.map2.get( "key3" ) );
    Set<String> keySet = this.compositeMap.keySet();
    keySet.remove( "key3" );
    assertNull( this.map2.get( "key3" ) );
    
    //
    assertEquals( SetUtils.valueOf( "key1", "key2", "key4" ), this.compositeMap.keySet() );
    assertEquals( Arrays.asList( "value1", "value2", "value4" ), new ArrayList<String>( this.compositeMap.values() ) );
  }
  
}
