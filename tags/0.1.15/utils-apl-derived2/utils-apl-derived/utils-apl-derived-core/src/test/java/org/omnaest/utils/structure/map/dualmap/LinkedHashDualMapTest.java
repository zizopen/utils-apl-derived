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
package org.omnaest.utils.structure.map.dualmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.map.AssertContract;

public class LinkedHashDualMapTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testMapContract()
  {
    //
    DualMap<String, Double> dualMap = new LinkedHashDualMap<String, Double>();
    
    //
    Map<String, Double> testDataMap = new LinkedHashMap<String, Double>();
    testDataMap.put( "abc", 1.234 );
    testDataMap.put( "def", 3.456 );
    testDataMap.put( "ghi", 3.456 );
    
    AssertContract.assertMapContract( dualMap, testDataMap );
  }
  
  @Test
  public void testDualMap()
  {
    //
    DualMap<String, Integer> dualMap = new LinkedHashDualMap<String, Integer>();
    dualMap.put( "1", 1 );
    dualMap.put( "2", 2 );
    dualMap.put( "3", 3 );
    
    //
    assertEquals( Integer.valueOf( 1 ), dualMap.get( "1" ) );
    assertEquals( Integer.valueOf( 2 ), dualMap.get( "2" ) );
    assertEquals( Integer.valueOf( 3 ), dualMap.get( "3" ) );
    
    assertEquals( "1", dualMap.invert().get( 1 ) );
    assertEquals( "2", dualMap.invert().get( 2 ) );
    assertEquals( "3", dualMap.invert().get( 3 ) );
    
    //
    {
      Map<String, Integer> additionalMap = new LinkedHashMap<String, Integer>();
      additionalMap.put( "4", 4 );
      additionalMap.put( "5", 5 );
      dualMap.putAll( additionalMap );
      
      assertEquals( Integer.valueOf( 4 ), dualMap.get( "4" ) );
      assertEquals( Integer.valueOf( 5 ), dualMap.get( "5" ) );
      
      assertEquals( "4", dualMap.invert().get( 4 ) );
      assertEquals( "5", dualMap.invert().get( 5 ) );
    }
    
    //
    {
      Map<Integer, String> additionalMap = new LinkedHashMap<Integer, String>();
      additionalMap.put( 6, "6" );
      additionalMap.put( 7, "7" );
      
      dualMap.invert().putAll( additionalMap );
      
      assertEquals( Integer.valueOf( 6 ), dualMap.get( "6" ) );
      assertEquals( Integer.valueOf( 7 ), dualMap.get( "7" ) );
      
      assertEquals( "6", dualMap.invert().get( 6 ) );
      assertEquals( "7", dualMap.invert().get( 7 ) );
    }
    
    //
    {
      assertEquals( 7, dualMap.size() );
      
      dualMap.remove( "4" );
      dualMap.invert().remove( 5 );
      
      assertEquals( 5, dualMap.size() );
      
      assertNull( dualMap.get( "4" ) );
      assertNull( dualMap.invert().get( "5" ) );
      
      assertNull( dualMap.invert().get( 4 ) );
      assertNull( dualMap.invert().get( 5 ) );
    }
    
    //
    {
      //
      Set<String> keyKeySet = dualMap.keySet();
      Set<Integer> valueKeySet = dualMap.invert().keySet();
      
      //
      assertEquals( 5, keyKeySet.size() );
      assertEquals( 5, valueKeySet.size() );
      
      //
      assertEquals( Arrays.asList( 1, 2, 3, 6, 7 ), new ArrayList<Integer>( valueKeySet ) );
      assertEquals( Arrays.asList( "1", "2", "3", "6", "7" ), new ArrayList<String>( keyKeySet ) );
    }
    
    //
    {
      //
      Collection<Integer> keyValues = dualMap.values();
      Collection<String> valueValues = dualMap.invert().values();
      
      //
      assertEquals( 5, keyValues.size() );
      assertEquals( 5, valueValues.size() );
      
      //
      assertEquals( Arrays.asList( 1, 2, 3, 6, 7 ), new ArrayList<Integer>( keyValues ) );
      assertEquals( Arrays.asList( "1", "2", "3", "6", "7" ), new ArrayList<String>( valueValues ) );
    }
    
    //
    {
      //
      assertTrue( !dualMap.isEmpty() );
      assertTrue( dualMap.size() > 1 );
      
      //
      dualMap.clear();
      
      //
      assertTrue( dualMap.size() == 0 );
      assertTrue( dualMap.isEmpty() );
    }
    
  }
  
  @Test
  public void testInvert()
  {
    //
    DualMap<String, Integer> dualMap = new LinkedHashDualMap<String, Integer>();
    dualMap.put( "1", 1 );
    dualMap.put( "2", 2 );
    dualMap.put( "3", 3 );
    
    //
    DualMap<Integer, String> invertedDualMap = dualMap.invert();
    
    //
    assertNotNull( invertedDualMap );
    assertEquals( dualMap.size(), invertedDualMap.size() );
    assertEquals( dualMap.get( 1 ), invertedDualMap.invert().get( 1 ) );
  }
}
