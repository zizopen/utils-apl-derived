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

/**
 * Utility class for supporting the work with {@link String}s
 * 
 * @author Omnaest
 */
public class StringUtils
{
  /* ********************************************** Constants ********************************************** */
  public static final String DEFAULT_LINESEPARATOR = System.getProperty( "line.separator" );
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns true if the given text ends with the start of the other given text. E.g. "bcd" will start with the and of "abc"
   * 
   * @param text
   * @param textOther
   * @return
   */
  public static boolean endsWithStartOfOther( String text, String textOther )
  {
    //
    boolean retval = false;
    
    //
    return retval;
  }
  
  /**
   * @param strings
   * @param delimiter
   * @return
   */
  public static String stringJoin( String[] strings, String delimiter )
  {
    String retval = null;
    StringBuffer sb = new StringBuffer( "" );
    boolean first = true;
    for ( String iString : strings )
    {
      //
      if ( !first )
      {
        sb.append( delimiter );
      }
      else
      {
        first = false;
      }
      
      //
      sb.append( iString );
    }
    if ( sb.length() > 0 )
    {
      retval = sb.toString();
    }
    return retval;
  }
  
  public static String insertString( String baseString, String insertString, int insertPosition, boolean overwrite )
  {
    //
    String retval = null;
    
    //
    if ( insertPosition < baseString.length() )
    {
      StringBuffer sb = new StringBuffer( baseString );
      for ( int iInsertPosition = insertPosition; iInsertPosition < insertPosition + insertString.length()
                                                  && iInsertPosition < baseString.length(); iInsertPosition++ )
      {
        int insertStringSourcePosition = iInsertPosition - insertPosition;
        if ( overwrite )
        {
          sb.deleteCharAt( iInsertPosition );
        }
        sb.insert( iInsertPosition, insertString.substring( insertStringSourcePosition, insertStringSourcePosition + 1 ) );
      }
      retval = sb.toString();
    }
    else
    {
      throw new StringIndexOutOfBoundsException();
    }
    
    //
    return retval;
  }
  
  /**
   * @param value
   * @param width
   * @return
   */
  public static String setFixedWitdth( String value, int width )
  {
    if ( width > 0 )
    {
      if ( org.apache.commons.lang3.StringUtils.isNotBlank( value ) )
      {
        //
        return String.format( "%" + width + "s", value );
      }
      
      //
      return org.apache.commons.lang3.StringUtils.repeat( " ", width );
    }
    
    //
    return "";
  }
  
  /**
   * Determines the maximum width of the given {@link Iterable} {@link String} elements
   * 
   * @param iterable
   * @return
   */
  public static int maximumWidth( Iterable<String> iterable )
  {
    //
    int retval = 0;
    
    //
    if ( iterable != null )
    {
      for ( String value : iterable )
      {
        if ( value != null )
        {
          retval = Math.max( retval, value.length() );
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Repeats the given {@link CharSequence}
   * 
   * @param token
   * @param repeats
   * @return
   */
  public static String repeat( CharSequence token, int repeats )
  {
    //
    StringBuilder stringBuilder = new StringBuilder();
    
    //
    if ( token != null && repeats > 0 )
    {
      for ( int ii = 1; ii <= repeats; ii++ )
      {
        stringBuilder.append( token );
      }
    }
    
    //
    return stringBuilder.toString();
  }
  
  /**
   * Returns a percentage bar looking like:<br>
   * [====o ]
   * 
   * @param value
   * @param width
   * @return
   */
  public static String percentageBar( double value, int width )
  {
    //
    final StringBuilder stringBuilder = new StringBuilder();
    
    //
    value = Math.min( 1.0, Math.max( value, 0.0 ) );
    int widthOfArrow = (int) Math.round( value * ( width - 2 ) );
    stringBuilder.append( "[" );
    if ( widthOfArrow > 0 )
    {
      stringBuilder.append( repeat( "=", widthOfArrow - 1 ) );
      stringBuilder.append( ">" );
    }
    stringBuilder.append( repeat( " ", width - 2 - widthOfArrow ) );
    stringBuilder.append( "]" );
    
    //
    return stringBuilder.toString();
  }
}
