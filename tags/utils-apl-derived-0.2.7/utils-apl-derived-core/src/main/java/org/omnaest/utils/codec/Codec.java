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
package org.omnaest.utils.codec;

import org.omnaest.utils.structure.element.converter.ElementConverterAlphaNumericEncodedStringToDecodedString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToAlphaNumericEncodedString;

/**
 * Class containing {@link Codec}
 * 
 * @see #AlphaNumeric
 * @author Omnaest
 */
public abstract class Codec
{
  /* ********************************************** Constants ********************************************** */
  /**
   * The {@link EncoderAndDecoderAlphanumericTokens} instance (thread safe) allows to process texts as pure alphanumeric token
   * streams with a encoding for non alphanumeric characters.
   * 
   * @see ElementConverterStringToAlphaNumericEncodedString
   * @see ElementConverterAlphaNumericEncodedStringToDecodedString
   * @deprecated use {@link #alphaNumeric()} instead
   */
  @SuppressWarnings("javadoc")
  @Deprecated
  public final EncoderAndDecoder<String, String> AlphaNumeric = new EncoderAndDecoderAlphanumericTokens();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * An {@link Encoder} provides an {@link #encode(Object)} method to encode give source {@link Object}s
   * 
   * @see Decoder
   * @see Codec
   * @author Omnaest
   * @param <ENCODED>
   * @param <DECODED>
   */
  public static interface Encoder<ENCODED, DECODED>
  {
    /**
     * Encodes the given source
     * 
     * @param source
     * @return
     */
    public ENCODED encode( DECODED source );
  }
  
  /**
   * A {@link Decoder} offer the {@link #decode(Object)} method to decode encoded {@link Object}s
   * 
   * @see Encoder
   * @see Codec
   * @author Omnaest
   * @param <DECODED>
   * @param <ENCODED>
   */
  public static interface Decoder<DECODED, ENCODED>
  {
    /**
     * Decodes the given encoded source
     * 
     * @param source
     * @return
     */
    public DECODED decode( ENCODED source );
  }
  
  /**
   * @see Encoder
   * @see Decoder
   * @param <ENCODED>
   * @param <DECODED>
   * @author Omnaest
   */
  public static interface EncoderAndDecoder<ENCODED, DECODED> extends Encoder<ENCODED, DECODED>, Decoder<DECODED, ENCODED>
  {
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Returns a new {@link EncoderAndDecoderAlphanumericTokens} instance
   * 
   * @see ElementConverterStringToAlphaNumericEncodedString
   * @see ElementConverterAlphaNumericEncodedStringToDecodedString
   * @return
   */
  @SuppressWarnings("javadoc")
  public static EncoderAndDecoder<String, String> alphaNumeric()
  {
    return new EncoderAndDecoderAlphanumericTokens();
  }
  
  /**
   * Returns a new {@link EncoderAndDecoderEscaping} instance
   * 
   * @param escapeCharacter
   * @param encodedCharacters
   * @return
   */
  @SuppressWarnings("javadoc")
  public static EncoderAndDecoder<String, String> escaping( String escapeCharacter, String... encodedCharacters )
  {
    return new EncoderAndDecoderEscaping( escapeCharacter, encodedCharacters );
  }
}
