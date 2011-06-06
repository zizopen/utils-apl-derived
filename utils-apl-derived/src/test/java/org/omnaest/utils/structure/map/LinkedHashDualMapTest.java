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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class LinkedHashDualMapTest
{
  
  @Before
  public void setUp() throws Exception
  {
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
    assertEquals( Integer.valueOf( 1 ), dualMap.getSecondElementBy( "1" ) );
    assertEquals( Integer.valueOf( 2 ), dualMap.getSecondElementBy( "2" ) );
    assertEquals( Integer.valueOf( 3 ), dualMap.getSecondElementBy( "3" ) );
    
    assertEquals( "1", dualMap.getFirstElementBy( 1 ) );
    assertEquals( "2", dualMap.getFirstElementBy( 2 ) );
    assertEquals( "3", dualMap.getFirstElementBy( 3 ) );
    
    //
    {
      Map<String, Integer> additionalMap = new LinkedHashMap<String, Integer>();
      additionalMap.put( "4", 4 );
      additionalMap.put( "5", 5 );
      dualMap.putAllFirstElementToSecondElement( additionalMap );
      
      assertEquals( Integer.valueOf( 4 ), dualMap.getSecondElementBy( "4" ) );
      assertEquals( Integer.valueOf( 5 ), dualMap.getSecondElementBy( "5" ) );
      
      assertEquals( "4", dualMap.getFirstElementBy( 4 ) );
      assertEquals( "5", dualMap.getFirstElementBy( 5 ) );
    }
    
    //
    {
      Map<Integer, String> additionalMap = new LinkedHashMap<Integer, String>();
      additionalMap.put( 6, "6" );
      additionalMap.put( 7, "7" );
      
      dualMap.putAllSecondElementToFirstElement( additionalMap );
      
      assertEquals( Integer.valueOf( 6 ), dualMap.getSecondElementBy( "6" ) );
      assertEquals( Integer.valueOf( 7 ), dualMap.getSecondElementBy( "7" ) );
      
      assertEquals( "6", dualMap.getFirstElementBy( 6 ) );
      assertEquals( "7", dualMap.getFirstElementBy( 7 ) );
    }
    
    //
    {
      assertEquals( 7, dualMap.size() );
      
      dualMap.removeFirstElement( "4" );
      dualMap.removeSecondElement( 5 );
      
      assertEquals( 5, dualMap.size() );
      
      assertNull( dualMap.getSecondElementBy( "4" ) );
      assertNull( dualMap.getSecondElementBy( "5" ) );
      
      assertNull( dualMap.getFirstElementBy( 4 ) );
      assertNull( dualMap.getFirstElementBy( 5 ) );
    }
    
    //
    {
      //
      List<String> firstElementList = dualMap.getFirstElementList();
      List<Integer> secondElementList = dualMap.getSecondElementList();
      
      //
      assertEquals( 5, firstElementList.size() );
      assertEquals( 5, secondElementList.size() );
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
}
