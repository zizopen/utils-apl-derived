/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.utils.strings;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.omnaest.utils.strings.StringReplacer.ReplacementResult;
import org.omnaest.utils.structure.array.ArrayUtils;

/**
 * @see StringReplacer
 * @author Omnaest
 */
public class StringReplacerTest
{
  
  @Test
  public void testFindAndRemoveAll() throws Exception
  {
    ReplacementResult replacementResult = new StringReplacer( ":([0-9]+)$" ).setGroup( 1 ).findAndRemoveFirst( "123-456:2000" );
    assertEquals( "123-456", replacementResult.getOutput() );
    assertArrayEquals( ArrayUtils.valueOf( "2000" ), replacementResult.getMatchingTokens() );
  }
  
}
