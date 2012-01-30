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
package org.omnaest.utils.structure.map.decorator;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.map.AssertContract;

/**
 * @see LockingMapDecorator
 * @author Omnaest
 */
public class LockingMapDecoratorTest
{
  /* ********************************************** Variables ********************************************** */
  protected Map<String, String> map          = new LinkedHashMap<String, String>();
  protected Map<String, String> mapDecorator = new LockingMapDecorator<String, String>( this.map );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testMapContract()
  {
    //
    Map<String, String> testDataMap = new LinkedHashMap<String, String>();
    testDataMap.put( "abc", "value1" );
    testDataMap.put( "def", "value2" );
    testDataMap.put( "ghi", "value2" );
    
    AssertContract.assertMapContract( this.mapDecorator, testDataMap );
  }
  
}
