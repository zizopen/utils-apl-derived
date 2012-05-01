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
package org.omnaest.utils.math;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * @see Average
 * @author Omnaest
 */
public class AverageTest
{
  
  @Test
  public void testCalculate()
  {
    //
    Average<Double> average = new Average<Double>( Arrays.asList( 1.0, 2.0, 3.0 ) );
    assertEquals( 3, average.size() );
    assertEquals( 2.0, average.calculate().doubleValue(), 0.01 );
  }
  
}
