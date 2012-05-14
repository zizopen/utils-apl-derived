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
package org.omnaest.utils.structure.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.structure.container.Name;

/**
 * @see Name
 * @author Omnaest
 */
public class EnumerationTest
{
  /**
   * @author Omnaest
   */
  protected static enum TestEnum implements Name
  {
    optionA,
    optionB
  }
  
  /**
   * @author Omnaest
   */
  protected static enum TestEnum2 implements Name
  {
    optionC,
    optionD
  }
  
  @Test
  public void testEnumeration()
  {
    //
    Name enumeration = TestEnum.optionA;
    assertEquals( enumeration.name(), TestEnum.optionA.name() );
    
    //
    enumeration = TestEnum2.optionC;
    assertEquals( enumeration.name(), TestEnum2.optionC.name() );
    
  }
}
