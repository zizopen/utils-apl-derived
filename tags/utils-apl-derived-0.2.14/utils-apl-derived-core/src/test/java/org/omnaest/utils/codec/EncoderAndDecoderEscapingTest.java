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
package org.omnaest.utils.codec;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;
import org.omnaest.utils.codec.Codec.EncoderAndDecoder;
import org.omnaest.utils.structure.array.ArrayUtils;

import com.google.common.base.Splitter;

/**
 * @see EncoderAndDecoderEscaping
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
public class EncoderAndDecoderEscapingTest
{
  
  @Test
  public void testEncodeAndDecode()
  {
    final String escapeCharacter = "$";
    final String[] encodedCharacters = new String[] { "a", "b" };
    
    EncoderAndDecoder<String, String> encoderAndDecoder = new EncoderAndDecoderEscaping( escapeCharacter, encodedCharacters );
    
    final String text = "this is before any other valuable way of work";
    String encodedText = encoderAndDecoder.encode( text );
    
    //System.out.println( encodedText );
    
    String decodedText = encoderAndDecoder.decode( encodedText );
    assertEquals( text, decodedText );
  }
  
  @Test
  public void testEncodeAndDecodeManyCharacters()
  {
    for ( int ii = 0; ii < 100; ii++ )
    {
      final String escapeCharacter = "\\";
      final String[] encodedCharacters = ArrayUtils.valueOf( Splitter.fixedLength( 1 )
                                                                     .split( new BigInteger( 8 * ii + 8, new SecureRandom() ).toString( 26 )
                                                                                                                             .replaceAll( "[0-9]",
                                                                                                                                          "" ) ),
                                                             String.class );
      
      EncoderAndDecoder<String, String> encoderAndDecoder = new EncoderAndDecoderEscaping( escapeCharacter, encodedCharacters );
      
      final String text = "This is a very weired and absolutely unnecessary message text.";
      String encodedText = encoderAndDecoder.encode( text );
      
      //      System.out.println();
      //      System.out.println( Arrays.deepToString( encodedCharacters ) );
      //      System.out.println( encodedText );
      
      String decodedText = encoderAndDecoder.decode( encodedText );
      assertEquals( text, decodedText );
    }
  }
  
  @Test
  public void testEncodeAndDecode2()
  {
    final String escapeCharacter = "#";
    final String[] encodedCharacters = new String[] { "a", "b" };
    
    EncoderAndDecoder<String, String> encoderAndDecoder = new EncoderAndDecoderEscaping( escapeCharacter, encodedCharacters );
    
    final String text = "test#123abc";
    String encodedText = encoderAndDecoder.encode( text );
    
    //System.out.println( encodedText );
    
    String decodedText = encoderAndDecoder.decode( encodedText );
    assertEquals( text, decodedText );
  }
}
