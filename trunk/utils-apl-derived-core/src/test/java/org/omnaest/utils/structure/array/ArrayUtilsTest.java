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
package org.omnaest.utils.structure.array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @see ArrayUtils
 * @author Omnaest
 */
public class ArrayUtilsTest
{
  
  @Test
  public void testValueOfEArray()
  {
    //
    final String[] strings = ArrayUtils.valueOf( "a", "b", "c" );
    assertArrayEquals( new String[] { "a", "b", "c" }, strings );
    
    //
    @SuppressWarnings("unchecked")
    final Number[] numbers = ArrayUtils.valueOf( Integer.valueOf( 10 ), Long.valueOf( 3 ), Double.valueOf( 3.5 ) );
    assertEquals( Number.class, ArrayUtils.componentType( numbers.getClass() ) );
  }
  
  @Test
  public void testMerge()
  {
    //
    assertArrayEquals( new String[] { "a", "b", "c", "d" },
                       ArrayUtils.merge( new String[] { "a", "b" }, new String[] { "c", "d" } ) );
  }
  
  @Test
  public void testValueOf() throws Exception
  {
    assertArrayEquals( new String[] { "a", "b", "c", "d" },
                       ArrayUtils.valueOf( (Iterable<String>) Arrays.asList( "a", "b", "c", "d" ), String.class ) );
  }
}
