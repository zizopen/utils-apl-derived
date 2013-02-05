package org.omnaest.utils.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for easier {@link String} replacements
 * 
 * @author Omnaest
 */
public class StringReplacer
{
  private final Pattern pattern;
  private int           group = 0;
  
  /**
   * Find and replacement result
   * 
   * @see #getMatchingTokens()
   * @see #getOutput()
   * @author Omnaest
   */
  public static class ReplacementResult
  {
    private final String[] matchingTokens;
    private final String   output;
    
    /**
     * @see ReplacementResult
     * @param matchingTokens
     * @param output
     */
    ReplacementResult( String[] matchingTokens, String output )
    {
      super();
      this.matchingTokens = matchingTokens;
      this.output = output;
    }
    
    /**
     * Get all collected matching groups
     * 
     * @return
     */
    public String[] getMatchingTokens()
    {
      return this.matchingTokens;
    }
    
    /**
     * The modified input {@link String}
     * 
     * @return
     */
    public String getOutput()
    {
      return this.output;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ReplacementResult [matchingTokens=" );
      builder.append( Arrays.toString( this.matchingTokens ) );
      builder.append( ", output=" );
      builder.append( this.output );
      builder.append( "]" );
      return builder.toString();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode( this.matchingTokens );
      result = prime * result + ( ( this.output == null ) ? 0 : this.output.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof ReplacementResult ) )
      {
        return false;
      }
      ReplacementResult other = (ReplacementResult) obj;
      if ( !Arrays.equals( this.matchingTokens, other.matchingTokens ) )
      {
        return false;
      }
      if ( this.output == null )
      {
        if ( other.output != null )
        {
          return false;
        }
      }
      else if ( !this.output.equals( other.output ) )
      {
        return false;
      }
      return true;
    }
    
    public boolean hasMatchingTokens()
    {
      return this.matchingTokens != null && this.matchingTokens.length > 0;
    }
    
  }
  
  /**
   * @see StringReplacer
   * @param regex
   *          {@link Pattern#compile(String)}
   */
  public StringReplacer( String regex )
  {
    super();
    this.pattern = Pattern.compile( regex );
  }
  
  /**
   * @see StringReplacer
   * @param pattern
   *          {@link Pattern}
   */
  public StringReplacer( Pattern pattern )
  {
    super();
    this.pattern = pattern;
  }
  
  /**
   * Matches the first {@link Pattern} and returns the original text with the removed matching group
   * 
   * @param input
   * @return {@link ReplacementResult}
   */
  public ReplacementResult findAndRemoveFirst( String input )
  {
    final int max = 1;
    return this.findAndRemove( input, max );
  }
  
  /**
   * Finds the maximum number of matching tokens and removes them from the input
   * 
   * @param input
   * @return {@link ReplacementResult}
   */
  public ReplacementResult findAndRemoveAll( String input )
  {
    final int max = Integer.MAX_VALUE;
    return this.findAndRemove( input, max );
  }
  
  /**
   * Finds the maximum number of matching tokens and removes them from the input
   * 
   * @param input
   * @param max
   * @return {@link ReplacementResult}
   */
  public ReplacementResult findAndRemove( String input, int max )
  {
    final String replacement = "";
    return this.findAndReplace( input, replacement, max );
  }
  
  /**
   * @see #findAndReplace(String, String, int) with no maximum matching number
   * @param input
   * @param replacement
   * @return {@link ReplacementResult}
   */
  public ReplacementResult findAndReplaceAll( String input, String replacement )
  {
    final int max = Integer.MAX_VALUE;
    return this.findAndReplace( input, replacement, max );
  }
  
  /**
   * Finds and replaces the first matching within the input
   * 
   * @see #findAndReplace(String, String, int)
   * @param input
   * @param replacement
   * @return
   */
  public ReplacementResult findAndReplaceFirst( String input, String replacement )
  {
    final int max = 1;
    return this.findAndReplace( input, replacement, max );
  }
  
  /**
   * Finds all matching tokens and replaces them with the given replacement
   * 
   * @param input
   * @param replacement
   * @param max
   *          maximum number of matches
   * @return {@link ReplacementResult}
   */
  public ReplacementResult findAndReplace( String input, String replacement, int max )
  {
    StringBuffer stringBuilder = new StringBuffer();
    List<String> groupList = new ArrayList<String>();
    
    Matcher matcher = this.pattern.matcher( input );
    for ( int ii = 0; ii < max && matcher.find(); ii++ )
    {
      groupList.add( matcher.group( this.group ) );
      matcher.appendReplacement( stringBuilder, replacement );
    }
    matcher.appendTail( stringBuilder );
    
    String[] matchingTokens = groupList.toArray( new String[0] );
    String output = stringBuilder.toString();
    return new ReplacementResult( matchingTokens, output );
  }
  
  /**
   * Sets the group index which is to be extracted, default is 0
   * 
   * @see Matcher#group(int)
   * @param group
   * @return this
   */
  public StringReplacer setGroup( int group )
  {
    this.group = group;
    return this;
  }
  
}
