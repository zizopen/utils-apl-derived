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

import org.apache.commons.lang.StringUtils;

public class StringUtil
{
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
   * Repeats the given string count times.
   * 
   * @param count
   * @param repeatString
   * @return
   */
  public static String repeatString( int count, String repeatString )
  {
    return StringUtils.repeat( repeatString, count );
  }
  
  public static String setFixedWitdth( String value, int width )
  {
    if ( width > 0 )
    {
      if ( StringUtils.isNotBlank( value ) )
      {
        return String.format( "%" + width + "s", value );
      }
      else
      {
        return StringUtils.repeat( " ", width );
      }
    }
    else
    {
      return "";
    }
  }
}
