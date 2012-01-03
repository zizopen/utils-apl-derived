package org.omnaest.utils.strings;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.omnaest.utils.assertion.Assert;

/**
 * The {@link StringReplacementBuilder} allows to {@link #add(String, String)} pairs of regEx patterns and their replacement
 * {@link String}s and to {@link #process(String)} values using these pairs.
 * 
 * @see #add(String, String)
 * @see #process(String)
 * @author Omnaest
 */
public class StringReplacementBuilder
{
  /* ********************************************** Variables ********************************************** */
  protected final Map<Pattern, String> regExPatternToReplacementMap = new LinkedHashMap<Pattern, String>();
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Adds a regEx pattern {@link String} and the corresponding replacement to the {@link StringReplacementBuilder}
   * 
   * @param regEx
   * @param replacement
   * @return this
   */
  public StringReplacementBuilder add( String regEx, String replacement )
  {
    //
    if ( regEx != null )
    {
      try
      {
        Pattern pattern = Pattern.compile( regEx );
        if ( pattern != null )
        {
          this.regExPatternToReplacementMap.put( pattern, replacement );
        }
      }
      catch ( Exception e )
      {
        Assert.fails( "Failed to create Pattern for regEx=" + regEx + " and replacement=" + replacement, e );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Similar to {@link #add(String, String)} for a given {@link Map}
   * 
   * @param regExPatternStringToReplacementMap
   * @return this
   */
  public StringReplacementBuilder addAll( Map<String, String> regExPatternStringToReplacementMap )
  {
    //
    if ( regExPatternStringToReplacementMap != null )
    {
      for ( String regEx : regExPatternStringToReplacementMap.keySet() )
      {
        final String replacement = regExPatternStringToReplacementMap.get( regEx );
        this.add( regEx, replacement );
      }
    }
    
    //
    return this;
  }
  
  /**
   * Replaces all matching regEx patterns added to the {@link StringReplacementBuilder} within the given value with the
   * corresponding replacement {@link String}s
   * 
   * @param value
   * @return processed {@link String}
   */
  public String process( String value )
  {
    //
    String retval = value;
    
    //
    if ( retval != null )
    {
      for ( Entry<Pattern, String> regExPatternToReplacementEntry : this.regExPatternToReplacementMap.entrySet() )
      {
        //
        final Pattern pattern = regExPatternToReplacementEntry.getKey();
        final String replacement = regExPatternToReplacementEntry.getValue();
        retval = pattern.matcher( retval ).replaceAll( replacement );
      }
    }
    
    //
    return retval;
  }
}
