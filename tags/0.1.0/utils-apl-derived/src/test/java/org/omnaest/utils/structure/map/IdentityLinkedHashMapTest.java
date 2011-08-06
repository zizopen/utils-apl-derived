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
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * @see IdentityLinkedHashMap
 * @author Omnaest
 */
public class IdentityLinkedHashMapTest
{
  /* ********************************************** Variables ********************************************** */
  protected Map<String, String> map = new IdentityLinkedHashMap<String, String>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.map.put( "key1", "value1" );
    this.map.put( "key2", "value2" );
    this.map.put( "key3", "value3" );
    this.map.put( "key4", "value4" );
    this.map.put( "key5", "value5" );
  }
  
  @Test
  public void testGet()
  {
    assertEquals( "value2", this.map.get( "key2" ) );
    assertEquals( null, this.map.get( "value19" ) );
  }
  
  @Test
  public void testPut()
  {
    //
    this.map.put( "key1", "valueXX" );
    assertEquals( "valueXX", this.map.get( "key1" ) );
    
    //
    assertEquals( 5, this.map.size() );
    assertEquals( 5, this.map.values().size() );
    assertEquals( 5, this.map.keySet().size() );
    
    //
    this.map.put( "key6", "valueXX" );
    assertEquals( "valueXX", this.map.get( "key6" ) );
    
    //
    assertEquals( 6, this.map.size() );
    assertEquals( 6, this.map.values().size() );
    assertEquals( 6, this.map.keySet().size() );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.map.remove( "key6" );
    assertEquals( 5, this.map.size() );
    
    //
    this.map.remove( "key2" );
    assertFalse( this.map.containsKey( "key2" ) );
    assertEquals( 4, this.map.size() );
  }
  
  @Test
  public void testContainsKey()
  {
    assertTrue( this.map.containsKey( "key1" ) );
    assertTrue( this.map.containsKey( "key5" ) );
    assertFalse( this.map.containsKey( "key6" ) );
  }
  
  @Test
  public void testContainsValue()
  {
    assertTrue( this.map.containsValue( "value1" ) );
    assertTrue( this.map.containsValue( "value5" ) );
    assertFalse( this.map.containsValue( "value6" ) );
  }
  
}
