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
package org.omnaest.utils.structure.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CollectionUtilsTest
{
  
  @Test
  public void testContainsObjectIdentity()
  {
    //
    final String value = "test";
    final String valueOtherIdentity = "test".toUpperCase().toLowerCase();
    List<String> valueList = new ArrayList<String>();
    valueList.add( "value0" );
    valueList.add( value );
    valueList.add( "value2" );
    
    //
    assertTrue( valueList.contains( valueOtherIdentity ) );
    assertTrue( valueList.contains( value ) );
    
    assertFalse( CollectionUtils.containsObjectIdentity( valueList, valueOtherIdentity ) );
    assertTrue( CollectionUtils.containsObjectIdentity( valueList, value ) );
  }
  
  @Test
  public void testLastIndexOfObjectIdentity()
  {
    //
    final String value = "test";
    final String valueOtherIdentity = "test".toUpperCase().toLowerCase();
    List<String> valueList = new ArrayList<String>();
    valueList.add( valueOtherIdentity );
    valueList.add( value );
    valueList.add( value );
    valueList.add( value );
    valueList.add( value );
    valueList.add( valueOtherIdentity );
    
    //
    assertEquals( 1, CollectionUtils.indexOfObjectIdentity( valueList, value ) );
  }
  
  @Test
  public void testIndexOfObjectIdentity()
  {
    //
    final String value = "test";
    final String valueOtherIdentity = "test".toUpperCase().toLowerCase();
    List<String> valueList = new ArrayList<String>();
    valueList.add( valueOtherIdentity );
    valueList.add( value );
    valueList.add( value );
    valueList.add( value );
    valueList.add( value );
    valueList.add( value );
    valueList.add( valueOtherIdentity );
    
    //
    assertEquals( 5, CollectionUtils.lastIndexOfObjectIdentity( valueList, value ) );
  }
  
  @Test
  public void testToString()
  {
    //
    String string = CollectionUtils.toString( Arrays.asList( "element1", "element2", "element3" ) );
    //System.out.println( string );
    assertEquals( "[\n element1,\n element2,\n element3\n]\n", string );
  }
  
}
