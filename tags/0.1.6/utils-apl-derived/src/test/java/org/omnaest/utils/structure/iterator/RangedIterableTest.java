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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.element.Range;

/**
 * @see RangedIterable
 * @author Omnaest
 */
public class RangedIterableTest
{
  
  /* ********************************************** Variables ********************************************** */
  private List<String>           valueList      = Arrays.asList( "value0", "value1", "value2", "value3", "value4" );
  private RangedIterable<String> rangedIterable = new RangedIterable<String>( new Range( 2, 3 ), this.valueList );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testRangedIterable()
  {
    //
    List<String> valueList = new ArrayList<String>();
    for ( String value : this.rangedIterable )
    {
      valueList.add( value );
    }
    
    //
    assertEquals( this.valueList.subList( 2, 3 + 1 ), valueList );
  }
}
