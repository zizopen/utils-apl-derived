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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.assertion.Assert.FailedOperationException;
import org.omnaest.utils.codec.Codec;
import org.omnaest.utils.codec.Codec.EncoderAndDecoder;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * An {@link EncoderAndDecoder} which will translate arbitrary {@link String} based values in a {@link String} based only on
 * alphanumeric tokens.<br>
 * The used character encoding therefore is utf-8 <br>
 * <br>
 * E.g.<br>
 * <b> "More complex text containing numbers: 0987654321, special chararcter: !\"§$%&/()=?`*+~-#':;"</b> <br>
 * will result in<br>
 * <b>
 * "More160complex160text160containing160numbers186160176185184183182181180179178177172160special160chararcter186160161162066039164165166175168169189191224170171254173163167186187"
 * </b> <br>
 * The {@link #encode(String)} and {@link #decode(String)} method can throw a {@link FailedOperationException} if an illegal
 * source text token is encountered.
 * 
 * @see Codec
 * @see EncoderAndDecoder
 * @author Omnaest
 */
public class EncoderAndDecoderAlphanumericTokens implements EncoderAndDecoder<String, String>
{
  /* ********************************************** Constants ********************************************** */
  private static final String CHARSET_UTF_8      = "utf-8";
  private static final int    INTERVAL           = 3;
  private static final String ALPHA_LETTER_REGEX = "[a-zA-Z]";
  
  /* ********************************************** Methods ********************************************** */
  @Override
  public String encode( String source )
  {
    //
    final StringBuffer stringBuffer = new StringBuffer();
    
    //
    if ( source != null )
    {
      //
      final Pattern pattern = Pattern.compile( ALPHA_LETTER_REGEX );
      final int length = source.length();
      for ( int ii = 0; ii < length; ii++ )
      {
        final String token = source.substring( ii, ii + 1 );
        final Matcher matcher = pattern.matcher( String.valueOf( token ) );
        if ( matcher.matches() )
        {
          stringBuffer.append( token );
        }
        else
        {
          try
          {
            //
            final byte[] bytes = token.getBytes( CHARSET_UTF_8 );
            for ( byte byteToken : bytes )
            {
              stringBuffer.append( String.format( "%0" + INTERVAL + "d", byteToken - Byte.MIN_VALUE ) );
            }
          }
          catch ( Exception e )
          {
            Assert.fails( e );
          }
        }
      }
    }
    
    // 
    return stringBuffer.toString();
  }
  
  @Override
  public String decode( String source )
  {
    //
    final StringBuilder stringBuilder = new StringBuilder();
    
    //
    if ( source != null )
    {
      //
      final ElementConverter<String, Byte> elementConverterTokenToByte = new ElementConverter<String, Byte>()
      {
        @Override
        public Byte convert( String element )
        {
          short convert = Short.valueOf( element );
          return (byte) ( convert + Byte.MIN_VALUE );
        }
      };
      
      //
      final Pattern pattern = Pattern.compile( "(" + ALPHA_LETTER_REGEX + "+)|([0-9]+)" );
      final Matcher matcher = pattern.matcher( source );
      
      while ( matcher.find() )
      {
        //
        final String groupAlphaLetters = matcher.group( 1 );
        final String groupNumbers = matcher.group( 2 );
        
        //
        if ( StringUtils.isNotBlank( groupAlphaLetters ) )
        {
          stringBuilder.append( groupAlphaLetters );
        }
        else if ( StringUtils.isNotBlank( groupNumbers ) )
        {
          //
          try
          {
            //
            Assert.isTrue( groupNumbers.length() % INTERVAL == 0,
                           "Decoding encountered an numeric group with illegal length. The length must be a multiple of "
                               + INTERVAL );
            
            //
            final String[] numberTokens = org.omnaest.utils.strings.StringUtils.splitByInterval( groupNumbers, INTERVAL );
            final byte[] byteTokens = org.apache.commons.lang3.ArrayUtils.toPrimitive( ArrayUtils.convertArray( numberTokens,
                                                                                                                new Byte[numberTokens.length],
                                                                                                                elementConverterTokenToByte ) );
            
            //
            final String text = new String( byteTokens, CHARSET_UTF_8 );
            stringBuilder.append( text );
          }
          catch ( Exception e )
          {
            Assert.fails( e );
          }
        }
        else
        {
          Assert.fails( "Decoding encountered an illegal character group: " + matcher.group() );
        }
      }
      
    }
    
    // 
    return stringBuilder.toString();
  }
}
