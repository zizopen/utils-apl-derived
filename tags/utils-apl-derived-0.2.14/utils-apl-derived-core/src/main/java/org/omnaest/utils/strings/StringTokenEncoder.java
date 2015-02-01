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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.omnaest.utils.codec.Codec;
import org.omnaest.utils.codec.Codec.EncoderAndDecoder;

/**
 * Encoder of {@link String} tokens into a single {@link String} and back to tokens
 * 
 * @see #encode(String...)
 * @see #decode(String)
 * @see Configuration
 * @author Omnaest
 */
public class StringTokenEncoder implements Serializable
{
  private static final long   serialVersionUID = -3236587652332815725L;
  private final Configuration configuration;
  
  /**
   * {@link Configuration} of the {@link StringTokenEncoder}
   * 
   * @author Omnaest
   */
  public static class Configuration implements Serializable
  {
    private static final long                 serialVersionUID  = 2757368412043768541L;
    public static final String                DEFAULT_DELIMITER = "_";
    private String                            delimiter         = DEFAULT_DELIMITER;
    private EncoderAndDecoder<String, String> encoderAndDecoder = Codec.escaping( "#", DEFAULT_DELIMITER );
    
    public String getDelimiter()
    {
      return this.delimiter;
    }
    
    /**
     * Sets a delimiter, default is {@value #DEFAULT_DELIMITER}
     * 
     * @param delimiter
     * @return
     */
    public Configuration setDelimiter( String delimiter )
    {
      this.delimiter = delimiter;
      return this;
    }
    
    public EncoderAndDecoder<String, String> getEncoderAndDecoder()
    {
      return this.encoderAndDecoder;
    }
    
    /**
     * @see Codec
     * @see Codec#alphaNumeric()
     * @param encoderAndDecoder
     *          {@link EncoderAndDecoder}
     * @return
     */
    public Configuration setEncoderAndDecoder( EncoderAndDecoder<String, String> encoderAndDecoder )
    {
      this.encoderAndDecoder = encoderAndDecoder;
      return this;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Configuration [delimiter=" );
      builder.append( this.delimiter );
      builder.append( ", encoderAndDecoder=" );
      builder.append( this.encoderAndDecoder );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  public StringTokenEncoder()
  {
    super();
    this.configuration = new Configuration();
  }
  
  public StringTokenEncoder( Configuration configuration )
  {
    super();
    this.configuration = configuration;
  }
  
  /**
   * Encodes a given set of tokens into a single {@link String}
   * 
   * @param elements
   * @return
   */
  public String encode( String... elements )
  {
    final EncoderAndDecoder<String, String> encoderAndDecoder = this.configuration.getEncoderAndDecoder();
    final String[] elementsEncoded;
    if ( encoderAndDecoder != null )
    {
      elementsEncoded = new String[elements != null ? elements.length : 0];
      for ( int ii = 0; ii < elements.length; ii++ )
      {
        elementsEncoded[ii] = encoderAndDecoder.encode( elements[ii] );
      }
    }
    else
    {
      elementsEncoded = elements;
    }
    return StringUtils.join( this.configuration.getDelimiter(), elementsEncoded );
  }
  
  /**
   * Decodes a given input {@link String} into its tokens
   * 
   * @param input
   * @return
   */
  public String[] decode( String input )
  {
    List<String> retlist = new ArrayList<String>();
    final EncoderAndDecoder<String, String> encoderAndDecoder = this.configuration.getEncoderAndDecoder();
    String[] tokens = org.apache.commons.lang3.StringUtils.splitPreserveAllTokens( input, this.configuration.getDelimiter() );
    if ( encoderAndDecoder != null )
    {
      for ( String token : tokens )
      {
        retlist.add( encoderAndDecoder.decode( token ) );
      }
    }
    else
    {
      for ( String token : tokens )
      {
        retlist.add( token );
      }
    }
    return retlist.toArray( new String[retlist.size()] );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "StringTokenEncoder [configuration=" );
    builder.append( this.configuration );
    builder.append( "]" );
    return builder.toString();
  }
  
}
