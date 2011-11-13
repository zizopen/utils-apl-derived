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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see Range
 * @author Omnaest
 */
public class RangeTest
{
  
  @Test
  public void testRange()
  {
    int counter = 0;
    for ( @SuppressWarnings("unused")
    Long ii : new Range( 5l, 10l ) )
    {
      counter++;
    }
    assertEquals( 6, counter );
    
  }
  
  @Test
  public void testRangeWithString()
  {
    int counter = 0;
    for ( @SuppressWarnings("unused")
    Long ii : new Range( "1-5" ) )
    {
      counter++;
    }
    assertEquals( 5, counter );
    
  }
  
}
