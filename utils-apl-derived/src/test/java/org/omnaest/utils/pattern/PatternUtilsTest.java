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
package org.omnaest.utils.pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @see PatternUtils
 * @author Omnaest
 */
public class PatternUtilsTest
{
  
  @Test
  public void testNot()
  {
    //
    final String patternString = "ABC";
    
    //
    String negatedPatternString = PatternUtils.not( patternString );
    assertNotNull( negatedPatternString );
    
    //
    //System.out.println( negatedPatternString );
    assertEquals( "(?:[^A]|(?:)$|A[^B]|(?:A)$|AB[^C]|(?:AB)$)", negatedPatternString );
    
    //
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "Axxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxvnv,mnxcmvn,vmcxnA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxvnv,mnxcmvn,vmcxnAB" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxABCv,mnxcmvn,vmcxn" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxv,mnxcmvn,vmABC" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "ABCxxnv,mnvmxv,mnxcmvn,vm" ) );
  }
  
  @Test
  public void testNotCollection()
  {
    //
    final String patternString1 = "ABCD";
    final String patternString2 = "BCD";
    final String patternString3 = "BCF";
    final String patternString4 = "EFG";
    
    //
    String negatedPatternString = PatternUtils.not( patternString1, patternString2, patternString3, patternString4 );
    assertNotNull( negatedPatternString );
    
    //
    //System.out.println( negatedPatternString );
    assertEquals( "(?:[^ABE]|A[^BAE]|(?:A)$|AB[^CABE]|(?:AB)$|ABC[^DFABE]|(?:ABC)$|B[^CABE]|(?:B)$|BC[^DFABE]|(?:BC)$|E[^FABE]|(?:E)$|EF[^GABE]|(?:EF)$)",
                  negatedPatternString );
    
    //
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString1 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString2 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString3 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString4 ) );
    
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "Axxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "ABCxnv,mnvAmxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCxnv,mnvmxvnv,mnxcmvn,vmcxnA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "Bxnv,mnvmxvnv,mnxcmvn,vmcxnAB" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxvnv,mnxcmvn,vmcxnABC" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "Cxxnv,mnvmCDxvnv,mnxcmvn,vmcxnA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "CDxxnv,mnvCmxvnv,mnxcmvn,vmcxnAB" ) );
    
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxBCDv,mnxcmvn,vmcxn" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxv,mnxcmvn,vmEFG" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "ABCDxxnv,mnvmxv,mnxcmvn,vm" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxABCFv,mnxcmvn,vmcxn" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxBCBCFv,mnxcmvn,vmcxn" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxBCABCBCFv,mnxcmvn,vmcxn" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxnv,mnvmxBCABCBCF" ) );
  }
  
}
