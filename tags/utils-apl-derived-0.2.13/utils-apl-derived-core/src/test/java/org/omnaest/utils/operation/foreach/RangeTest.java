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
package org.omnaest.utils.operation.foreach;

import static org.junit.Assert.assertArrayEquals;

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
    assertArrayEquals( new int[] { 1, 2, 3, 4, 5 }, new Range( 1, 5 ).toIntArray() );
    assertArrayEquals( new int[] { 3, 2, 1 }, new Range( 3, 1 ).toIntArray() );
  }
  
  @Test
  public void testRangeWithString()
  {
    assertArrayEquals( new int[] { 1, 2, 3, 4, 5 }, new Range( "1-5" ).toIntArray() );
    assertArrayEquals( new int[] { 1, 3, 5 }, new Range( "1-5:2" ).toIntArray() );
    assertArrayEquals( new int[] { 1 }, new Range( "1:2" ).toIntArray() );
    assertArrayEquals( new int[] { 1 }, new Range( "1" ).toIntArray() );
    assertArrayEquals( new int[] { 3, 2, 1 }, new Range( "3-1" ).toIntArray() );
  }
}
