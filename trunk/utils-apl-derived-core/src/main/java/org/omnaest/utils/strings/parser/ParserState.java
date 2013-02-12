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
package org.omnaest.utils.strings.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.strings.parser.Grammar.TokenPath;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see #parse(String)
 * @author Omnaest
 */
public class ParserState
{
  private Grammar grammar;
  
  private static class Match
  {
    private final String          token;
    private Grammar.TokenAction[] tokenActions = null;
    
    private Match( String token )
    {
      super();
      this.token = token;
    }
    
    private String getToken()
    {
      return this.token;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Match [token=" );
      builder.append( this.token );
      builder.append( ", tokenActions=" );
      builder.append( Arrays.toString( this.tokenActions ) );
      builder.append( "]" );
      return builder.toString();
    }
    
    private Grammar.TokenAction[] getTokenActions()
    {
      return this.tokenActions;
    }
    
    public void addTokenAction( Grammar.TokenAction tokenAction )
    {
      if ( tokenAction != null )
      {
        this.tokenActions = ArrayUtils.add( this.tokenActions, tokenAction );
      }
    }
    
  }
  
  ParserState( Grammar grammar )
  {
    this.grammar = grammar;
    
  }
  
  /**
   * Parses a given text
   * 
   * @param text
   * @return true if the given text has been matched otherwise false
   */
  public boolean parse( String text )
  {
    boolean retval = false;
    
    final Grammar.TokenPath tokenPath = this.grammar.declare();
    final int lookup = -1;
    List<ParserState.Match> matchList = this.parseInternal( text, tokenPath, lookup );
    if ( matchList != null )
    {
      retval = true;
      
      for ( ParserState.Match match : matchList )
      {
        final String token = match.getToken();
        
        Grammar.TokenAction[] tokenActions = match.getTokenActions();
        if ( tokenActions != null )
        {
          for ( Grammar.TokenAction tokenAction : tokenActions )
          {
            tokenAction.execute( token );
          }
        }
      }
    }
    return retval;
  }
  
  private List<ParserState.Match> parseInternal( String remainingText, Iterator<TokenPath.StackElement> stackIterator, int lookup )
  {
    final boolean stackHasNext = stackIterator.hasNext();
    final boolean remainingTextIsEmpty = StringUtils.isEmpty( remainingText );
    
    List<ParserState.Match> retlist = !stackHasNext && remainingTextIsEmpty ? new ArrayList<ParserState.Match>() : null;
    
    if ( stackHasNext && ( lookup <= -1 || lookup > 0 ) )
    {
      TokenPath.StackElement stackElement = stackIterator.next();
      
      final int localLookup = stackElement.getLookup();
      final int reducedLookup = Math.max( Math.max( lookup - 1, localLookup ), -1 );
      final int normalizedLookup = Math.max( Math.max( lookup, localLookup ), -1 );
      
      if ( stackElement instanceof TokenPath.StackElementPattern )
      {
        TokenPath.StackElementPattern stackElementPattern = (TokenPath.StackElementPattern) stackElement;
        Pattern pattern = stackElementPattern.getPattern();
        
        Matcher matcher = pattern.matcher( remainingText );
        boolean lookingAt = matcher.lookingAt();
        if ( lookingAt )
        {
          if ( remainingTextIsEmpty )
          {
            retlist = ListUtils.emptyList();
          }
          else
          {
            final int end = matcher.end();
            final String remainingTextNew = StringUtils.substring( remainingText, end );
            retlist = this.parseInternal( remainingTextNew, stackIterator, reducedLookup );
            
            if ( retlist != null )
            {
              final String token = matcher.group();
              Grammar.TokenAction tokenAction = stackElement.getTokenAction();
              ParserState.Match match = new Match( token );
              match.addTokenAction( tokenAction );
              
              retlist = ListUtils.add( retlist, 0, match );
            }
          }
        }
      }
      else if ( stackElement instanceof TokenPath.StackElementTokenPath )
      {
        TokenPath.StackElementTokenPath stackElementTokenPath = (TokenPath.StackElementTokenPath) stackElement;
        final Grammar.TokenPath[] tokenPathes = stackElementTokenPath.getTokenPathes();
        final Grammar.TokenAction tokenAction = stackElement.getTokenAction();
        if ( tokenPathes != null )
        {
          for ( Grammar.TokenPath tokenPathCurrent : tokenPathes )
          {
            retlist = this.parseInternal( remainingText, tokenPathCurrent, normalizedLookup );
            if ( retlist != null )
            {
              ParserState.Match lastElement = ListUtils.lastElement( retlist );
              if ( lastElement != null )
              {
                lastElement.addTokenAction( tokenAction );
              }
              break;
            }
          }
        }
      }
    }
    
    return retlist;
  }
  
  private List<ParserState.Match> parseInternal( String remainingText, Grammar.TokenPath tokenPath, int lookup )
  {
    return this.parseInternal( remainingText, tokenPath.stackIterator(), lookup );
  }
  
}
