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
package org.omnaest.utils.codec.component;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omnaest.utils.codec.Codec;

/**
 * @see EncoderAndDecoderAlphanumericTokens
 * @author Omnaest
 */
@RunWith(Parameterized.class)
public class EncoderAndDecoderAlphanumericTokensTest
{
  
  @Parameters
  public static Collection<Object[]> data()
  {
    //
    List<Object[]> retlist = new ArrayList<Object[]>();
    retlist.add( new String[] { "Easy text" } );
    retlist.add( new String[] { "§" } );
    retlist.add( new String[] { "More complex text containing numbers: 0987654321, special chararcter: !\"§$%&/()=?`*+~-#':;" } );
    
    //
    return retlist;
  }
  
  /* ********************************************** Variables ********************************************** */
  private final String source;
  
  /* ********************************************** Methods ********************************************** */
  
  public EncoderAndDecoderAlphanumericTokensTest( String source )
  {
    super();
    this.source = source;
  }
  
  @Test
  public void testAlphaNumeric()
  {
    final String source = this.source;
    String encodedSource = Codec.AlphaNumeric.encode( source );
    
    System.out.println( encodedSource );
    
    String decodedSource = Codec.AlphaNumeric.decode( encodedSource );
    assertEquals( source, decodedSource );
  }
  
}
