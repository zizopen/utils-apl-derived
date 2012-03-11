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

import org.junit.Ignore;
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
    assertEquals( "(?:[^A]|A[^B]|(?:A)$|AB[^C]|(?:AB)$)", negatedPatternString );
    
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
  public void testNot2()
  {
    //
    final String patternString1 = "OR";
    final String patternString2 = "AND";
    
    //
    String negatedPatternString = PatternUtils.not( patternString1, patternString2 );
    assertNotNull( negatedPatternString );
    
    //
    System.out.println( negatedPatternString );
    //assertEquals( "(?:[^A]|(?:)$|A[^B]|(?:A)$|AB[^C]|(?:AB)$)", negatedPatternString );
    
    //
    assertFalse( Pattern.matches( negatedPatternString + "+", "OR" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "AND" ) );
    
    assertTrue( Pattern.matches( negatedPatternString + "+", "OO" ) );
    
  }
  
  @Test
  @Ignore("Not yet working")
  //FIXME
  public void testNotCollection()
  {
    //
    final String patternString1 = "ABCD";
    final String patternString2 = "BCD";
    final String patternString3 = "BCF";
    final String patternString4 = "CFG";
    
    //
    String negatedPatternString = PatternUtils.not( patternString1, patternString2, patternString3, patternString4 );
    assertNotNull( negatedPatternString );
    
    //
    //System.out.println( negatedPatternString );
    //    assertEquals( "(?:[^ABE]|A[^BAE]|(?:A)$|AB[^CABE]|(?:AB)$|ABC[^DFABE]|(?:ABC)$|B[^CABE]|(?:B)$|BC[^DFABE]|(?:BC)$|E[^FABE]|(?:E)$|EF[^GABE]|(?:EF)$)",
    //                  negatedPatternString );
    
    //
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString1 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString2 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString3 ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", patternString4 ) );
    
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxnv,mnv/&($%§mxvnv,mnxcmvn,vmcxn" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxAx" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "Axx" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCB" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCxx" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "ABCxx" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BCxxxA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "BxxxAB" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "xxABC" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "CxxxA" ) );
    assertTrue( Pattern.matches( negatedPatternString + "+", "CDxxxAB" ) );
    
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxBCDxx" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxCFGxx" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "ABCDxx" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxABCFxx" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxBCABCBCFxx" ) );
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxBCBCFxx" ) );    
    assertFalse( Pattern.matches( negatedPatternString + "+", "xxBCABCBCF" ) );
  }
  
}
