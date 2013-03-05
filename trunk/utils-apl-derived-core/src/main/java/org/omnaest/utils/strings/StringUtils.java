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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.StrTokenizer;
import org.omnaest.utils.strings.StringTokenEncoder.Configuration;
import org.omnaest.utils.strings.tokenizer.ConvertingCharacterSequenceTokenizerDecoratorToString;
import org.omnaest.utils.strings.tokenizer.PatternBasedCharacterSequenceTokenizer;
import org.omnaest.utils.structure.array.ArrayUtils;

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
   * @param delimiter
   * @param tokens
   * @return
   */
  public static String join( String delimiter, String... tokens )
  {
    final StringBuilder sb = new StringBuilder();
    boolean first = true;
    if ( tokens != null )
    {
      for ( String token : tokens )
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
        sb.append( token );
      }
    }
    return sb.toString();
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
  
  /**
   * Splits a given {@link String} text by an interval. <br>
   * <br>
   * E.g. "This is a text" is split into "Thi","s i","s a", " te", "xt" <br>
   * <br>
   * A given interval will be reduced to 1 if it is lower than that and reduced to the length of the text if it is larger than
   * that.
   * 
   * @param text
   * @param interval
   * @return
   */
  public static String[] splitByInterval( String text, int interval )
  {
    //
    String[] retvals = null;
    
    //
    if ( text != null )
    {
      //
      final int length = text.length();
      interval = Math.max( 1, interval );
      interval = Math.min( length, interval );
      
      //
      final int tokenNumber = (int) Math.ceil( length * 1.0 / interval );
      retvals = new String[tokenNumber];
      
      //
      for ( int ii = 0; ii < tokenNumber; ii++ )
      {
        final int beginIndex = ii * interval;
        final int endIndex = Math.min( ( ii + 1 ) * interval, length );
        final String token = text.substring( beginIndex, endIndex );
        
        //
        retvals[ii] = token;
      }
    }
    
    //
    return retvals;
  }
  
  /**
   * Simple form of the {@link StrTokenizer}
   * 
   * @param text
   * @param delimiter
   * @param quote
   * @return
   */
  public static String[] split( String text, char delimiter, char quote )
  {
    return new StrTokenizer( text, delimiter, quote ).getTokenArray();
  }
  
  /**
   * Counts the number of matching substrings within the given text. As substring a regular expression is expected, please use
   * {@link Pattern#quote(String)} if a substring contains special characters. <br>
   * <br>
   * 
   * <pre>
   * count("ab ab","ab") = 2
   * count("abab","ab") = 2
   * count("ababab","abab") = 1
   * count("ababab ababab","abab") = 2
   * count("aba bab","abab") = 0
   * </pre>
   * 
   * @param text
   * @param regExSubstring
   * @return
   */
  public static int count( String text, String regExSubstring )
  {
    //
    int retval = 0;
    
    //
    if ( regExSubstring != null && text != null )
    {
      try
      {
        //
        final Pattern pattern = Pattern.compile( regExSubstring );
        final Matcher matcher = pattern.matcher( text );
        while ( matcher.find() )
        {
          retval++;
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a new {@link Iterable} instance for the given {@link CharSequence} which uses the given regular expression delimiter
   * to produce tokens. <br>
   * <br>
   * If the given {@link CharSequence} or the given delimiter is null this returns null. <br>
   * <br>
   * Example:
   * 
   * <pre>
   * StringUtils.tokenizerPatternBased( "a;b", ";" )  => "a","b"
   * StringUtils.tokenizerPatternBased( null, ";" )   => null
   * StringUtils.tokenizerPatternBased( "a;b", null ) => null
   * </pre>
   * 
   * @param charSequence
   * @param regexDelimiter
   * @return
   */
  public static Iterable<String> tokenizerPatternBased( CharSequence charSequence, String regexDelimiter )
  {
    //
    return charSequence != null && regexDelimiter != null ? new ConvertingCharacterSequenceTokenizerDecoratorToString(
                                                                                                                       new PatternBasedCharacterSequenceTokenizer(
                                                                                                                                                                   charSequence,
                                                                                                                                                                   regexDelimiter ) )
                                                         : null;
  }
  
  /**
   * Formats an given array into a {@link String} array using {@link String#format(String, Object...)}
   * 
   * @param format
   * @param elements
   *          has to be a non primitive array
   * @return
   */
  public static <E> String[] formatPerArrayElement( String format, E... elements )
  {
    final List<String> retlist = new ArrayList<String>();
    if ( format != null && elements != null )
    {
      for ( E element : elements )
      {
        retlist.add( String.format( format, element ) );
      }
    }
    return retlist.toArray( new String[] {} );
  }
  
  /**
   * Similar to {@link #formatPerArrayElement(String, Object...)} using {@link String#format(Locale, String, Object...)} instead.
   * 
   * @param locale
   *          {@link Locale}
   * @param format
   * @param elements
   * @return
   */
  public static <E> String[] formatPerArrayElement( Locale locale, String format, E... elements )
  {
    final List<String> retlist = new ArrayList<String>();
    if ( format != null && elements != null )
    {
      for ( E element : elements )
      {
        retlist.add( String.format( locale, format, element ) );
      }
    }
    return retlist.toArray( new String[] {} );
  }
  
  /**
   * Similar to {@link #findAll(Pattern, String)}
   * 
   * @see #findAll(Pattern, String)
   * @param regex
   *          {@link Pattern#compile(String)}
   * @param text
   * @return
   */
  public static String[] findAll( String regex, String text )
  {
    return findAll( Pattern.compile( regex ), text );
  }
  
  /**
   * Finds all matching strings within the given text using the given {@link Pattern}
   * 
   * @see #findAll(String, String)
   * @param pattern
   *          {@link Pattern}
   * @param text
   * @return
   */
  public static String[] findAll( Pattern pattern, String text )
  {
    List<String> retlist = new ArrayList<String>();
    if ( pattern != null && text != null )
    {
      Matcher matcher = pattern.matcher( text );
      while ( matcher.find() )
      {
        retlist.add( matcher.group() );
      }
    }
    return retlist.toArray( new String[0] );
  }
  
  /**
   * Returns a new {@link StringTokenEncoder} for the given {@link Configuration}
   * 
   * @param configuration
   * @return
   */
  public static StringTokenEncoder tokenEncoder( Configuration configuration )
  {
    return new StringTokenEncoder( configuration );
  }
  
  /**
   * Splits the given array into multiple groups of subarrays. The given group start indexes will determine the end of the
   * previous group also. The resulting array will contain the subarrays sorted like the given group start indexes.
   * 
   * @param array
   * @param groupStarts
   * @return
   */
  public static String[][] splitAsArrayGroups( String[] array, int... groupStarts )
  {
    return ArrayUtils.splitAsArrayGroups( String.class, array, groupStarts );
  }
  
}
