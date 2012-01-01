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
package org.omnaest.utils.strings;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTest
{
  
  @Test
  public void testPercentageBar()
  {
    //
    {
      //
      double value = 0.0;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[          ]", percentageBar );
    }
    {
      //
      double value = 0.1;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[>         ]", percentageBar );
    }
    {
      //
      double value = 0.8;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[=======>  ]", percentageBar );
    }
    {
      //
      double value = 1.0;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[=========>]", percentageBar );
    }
    {
      //
      double value = -0.3;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[          ]", percentageBar );
    }
    {
      //
      double value = 1.6;
      int width = 12;
      String percentageBar = StringUtils.percentageBar( value, width );
      //System.out.println( percentageBar );
      
      //
      assertEquals( "[=========>]", percentageBar );
    }
  }
  
  @Test
  public void testSplitByInterval()
  {
    int interval = 3;
    String text = "This is an easy text";
    String[] splitByInterval = StringUtils.splitByInterval( text, interval );
    assertArrayEquals( new String[] { "Thi", "s i", "s a", "n e", "asy", " te", "xt" }, splitByInterval );
  }
  
  @Test
  public void testCount()
  {
    final String text = "ab lalala ab lalala ab lalala ab lala a lalalalala";
    assertEquals( 4, StringUtils.count( text, "ab" ) );
    assertEquals( 16, StringUtils.count( text, "la" ) );
    assertEquals( 6, StringUtils.count( text, "lala" ) );
  }
  
}
