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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see CompositeMap
 * @author Omnaest
 */
public class CompositeMapTest
{
  /* ********************************************** Variables ********************************************** */
  private final Map<String, String> map1         = new LinkedHashMap<String, String>();
  private final Map<String, String> map2         = new LinkedHashMap<String, String>();
  
  @SuppressWarnings("unchecked")
  private final Map<String, String> compositeMap = new CompositeMap<String, String>( this.map1, this.map2 );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testGet()
  {
    //
    this.map1.put( "key1", "value1" );
    this.map1.put( "key2", "value2" );
    this.map2.put( "key3", "value3" );
    this.map2.put( "key4", "value4" );
    
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
  
}
