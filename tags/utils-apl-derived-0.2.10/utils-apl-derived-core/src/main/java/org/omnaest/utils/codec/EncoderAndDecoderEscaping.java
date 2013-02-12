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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.codec.Codec.EncoderAndDecoder;
import org.omnaest.utils.structure.map.MapUtils;

import com.google.common.collect.ImmutableMap;

/**
 * Encodes any character which is given to be encoded into a number with the escape character as prefix.<br>
 * <br>
 * The decoding of an encoded text will only work for <b>exactly</b> the same encoding characters and the escape character. <br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * This is a very weired and absolutely unnecessary message text.
 * </pre>
 * 
 * will be encoded in following ways: <br>
 * 
 * <pre>
 * [k]
 * This is a very weired and absolutely unnecessary message text.
 * 
 * [e, l, c, p]
 * This is a v\1ry w\1ir\1d and abso\2ut\1\2y unn\1\3\1ssary m\1ssag\1 t\1xt.
 * 
 * [m, c, i]
 * Th\3s \3s a very we\3red and absolutely unne\2essary \1essage text.
 * 
 * [j, p, l]
 * This is a very weired and abso\3ute\3y unnecessary message text.
 * 
 * [c, b, p, o, g, c, n, d]
 * This is a very weire\8 a\7\8 a\2s\4lutely u\7\7e\6essary messa\5e text.
 * 
 * [g, c, d, d, l, m, i, a, b, e, j, n, g, o, d]
 * Th\07s \07s \08 v\10ry w\10\07r\10\15 \08\12\15 \08\09s\14\05ut\10\05y u\12\12\10\02\10ss\08ry \06\10ss\08\13\10 t\10xt.
 * 
 * [m, o, n, j, b, b, p, g, p, f, j, a, k, a, g, a, b, k, m, j, a, j, o, p, m, c, m, p, p, a, m, b, d, n, h, p, a, m, p, a, i, a, f, g, c, o, f, m, k, e, e, i, m, j, k, f, a, k, i, d, n, o, b, f, k, e, d, a, j, b, n, e, f, p, b, b, a, f, e, f, b, f, e, j, b, i, d, c, h, f, h, k, c, e, a, n, l, m, o, p, a, b, d, d, p, d, d, o, g, b, b, d]
 * T\091\086s \086s \101 v\094ry w\094\086r\094\112 \101\096\112 \101\111s\108\097ut\094\097y u\096\096\094\093\094ss\101ry \098\094ss\101\109\094 t\094xt.
 * </pre>
 * 
 * @author Omnaest
 */
class EncoderAndDecoderEscaping implements EncoderAndDecoder<String, String>
{
  private final Map<String, String> encodedCharacterToEscapeSequenceMap;
  private final Map<String, String> escapeSequenceToEncodedCharacterMap;
  
  /**
   * @see EncoderAndDecoderEscaping
   * @param escapeCharacter
   * @param encodedCharacters
   */
  EncoderAndDecoderEscaping( String escapeCharacter, String[] encodedCharacters )
  {
    super();
    
    Assert.isNotNull( escapeCharacter, "escapeCharacter must not be null" );
    Assert.isNotNull( encodedCharacters, "encodedCharacters must not be null" );
    Assert.isFalse( Pattern.matches( ".*[0-9].*", Arrays.deepToString( encodedCharacters ) ),
                    "encodedCharacters must not contain any number" );
    
    final Map<String, String> encodedCharacterToEscapeSequenceMap = new LinkedHashMap<String, String>();
    {
      final String format = "%0" + ( (int) Math.ceil( Math.log10( encodedCharacters.length + 1 ) ) ) + "d";
      int counter = 0;
      for ( String encodedCharacter : ArrayUtils.addAll( new String[] { escapeCharacter }, encodedCharacters ) )
      {
        final String escapeSequence = escapeCharacter + String.format( format, counter );
        encodedCharacterToEscapeSequenceMap.put( encodedCharacter, escapeSequence );
        counter++;
      }
    }
    this.encodedCharacterToEscapeSequenceMap = ImmutableMap.<String, String> copyOf( encodedCharacterToEscapeSequenceMap );
    this.escapeSequenceToEncodedCharacterMap = ImmutableMap.<String, String> copyOf( MapUtils.invertedBidirectionalMap( encodedCharacterToEscapeSequenceMap ) );
  }
  
  @Override
  public String encode( String source )
  {
    String retval = source;
    {
      if ( retval != null )
      {
        for ( String encodedCharacter : this.encodedCharacterToEscapeSequenceMap.keySet() )
        {
          final String escapeSequence = this.encodedCharacterToEscapeSequenceMap.get( encodedCharacter );
          retval = retval.replaceAll( Pattern.quote( encodedCharacter ), Matcher.quoteReplacement( escapeSequence ) );
        }
      }
    }
    return retval;
  }
  
  @Override
  public String decode( String source )
  {
    String retval = source;
    {
      if ( retval != null )
      {
        for ( String escapeSequence : this.escapeSequenceToEncodedCharacterMap.keySet() )
        {
          final String encodedCharacter = this.escapeSequenceToEncodedCharacterMap.get( escapeSequence );
          retval = retval.replaceAll( Pattern.quote( escapeSequence ), Matcher.quoteReplacement( encodedCharacter ) );
        }
      }
    }
    return retval;
  }
  
}
