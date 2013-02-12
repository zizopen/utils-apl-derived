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
import java.util.regex.Pattern;

import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * {@link Grammar} definition for the {@link StringParser}
 * 
 * @see StringParser#use(Grammar)
 * @author Omnaest
 */
public interface Grammar
{
  /**
   * Simple text {@link Token}
   * 
   * @author Omnaest
   */
  public static class Token
  {
    private final Pattern pattern;
    
    /**
     * @see Token
     * @param regex
     */
    public Token( String regex )
    {
      super();
      this.pattern = Pattern.compile( regex );
    }
    
    /**
     * @see Token
     * @param pattern
     *          {@link Pattern}
     */
    public Token( Pattern pattern )
    {
      super();
      this.pattern = pattern;
    }
    
    private Pattern getPattern()
    {
      return this.pattern;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Token [pattern=" );
      builder.append( this.pattern );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  public static interface TokenAction
  {
    public void execute( String tokenMatch );
  }
  
  /**
   * A path declaration state, which allows to form token pathes.<br>
   * 
   * @see #add(Token)
   * @see #add(String)
   * @see #add(TokenPath)
   * @see #execute(TokenAction)
   * @see #withLookup(int)
   * @author Omnaest
   */
  public static class TokenPath
  {
    private List<TokenPath.StackElement> stackList = new ArrayList<TokenPath.StackElement>();
    
    static abstract class StackElement
    {
      protected Grammar.TokenAction tokenAction;
      protected int                 lookup = -1;
      
      protected Grammar.TokenAction getTokenAction()
      {
        return this.tokenAction;
      }
      
      protected int getLookup()
      {
        return this.lookup;
      }
      
      protected StackElement()
      {
        super();
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "StackElement [tokenAction=" );
        builder.append( this.tokenAction );
        builder.append( ", lookup=" );
        builder.append( this.lookup );
        builder.append( "]" );
        return builder.toString();
      }
      
      private void setTokenAction( Grammar.TokenAction tokenAction )
      {
        this.tokenAction = tokenAction;
      }
      
      private void setLookup( int lookup )
      {
        this.lookup = lookup;
      }
      
    }
    
    static class StackElementTokenPath extends TokenPath.StackElement
    {
      private final Grammar.TokenPath[] tokenPathes;
      
      private StackElementTokenPath( Grammar.TokenPath... tokenPathes )
      {
        super();
        this.tokenPathes = tokenPathes;
      }
      
      Grammar.TokenPath[] getTokenPathes()
      {
        return this.tokenPathes;
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "StackElementTokenPath [tokenPathes=" );
        builder.append( Arrays.toString( this.tokenPathes ) );
        builder.append( "]" );
        return builder.toString();
      }
      
    }
    
    static class StackElementPattern extends TokenPath.StackElement
    {
      private final Pattern pattern;
      
      private StackElementPattern( Pattern pattern )
      {
        super();
        this.pattern = pattern;
      }
      
      Pattern getPattern()
      {
        return this.pattern;
      }
      
    }
    
    public Grammar.TokenPath add( Grammar.Token abc )
    {
      this.stackList.add( new StackElementPattern( abc.getPattern() ) );
      return this;
    }
    
    public Grammar.TokenPath add( String text )
    {
      this.stackList.add( new StackElementPattern( Pattern.compile( text ) ) );
      return this;
    }
    
    /**
     * Executes the given {@link TokenAction} at the time the related token is being matched
     * 
     * @param tokenAction
     * @return
     */
    public Grammar.TokenPath execute( Grammar.TokenAction tokenAction )
    {
      TokenPath.StackElement lastElement = determineLastStackElement();
      if ( lastElement != null )
      {
        lastElement.setTokenAction( tokenAction );
      }
      return this;
    }
    
    private TokenPath.StackElement determineLastStackElement()
    {
      return ListUtils.lastElement( this.stackList );
    }
    
    /**
     * Allows to give alternative path ways
     * 
     * @param tokenPathes
     * @return
     */
    public Grammar.TokenPath or( Grammar.TokenPath... tokenPathes )
    {
      if ( tokenPathes != null && tokenPathes.length > 0 )
      {
        this.stackList.add( new StackElementTokenPath( tokenPathes ) );
      }
      return this;
    }
    
    public Grammar.TokenPath add( Grammar.TokenPath tokenPath )
    {
      this.stackList.add( new StackElementTokenPath( tokenPath ) );
      return this;
    }
    
    /**
     * Defines a lookup maximum. E.g. a value of 5 will limit the matches being done to further five.
     * 
     * @param lookup
     * @return
     */
    public Grammar.TokenPath withLookup( int lookup )
    {
      TokenPath.StackElement lastElement = determineLastStackElement();
      if ( lastElement != null )
      {
        lastElement.setLookup( lookup );
      }
      return this;
    }
    
    protected Iterator<TokenPath.StackElement> stackIterator()
    {
      return this.stackList.iterator();
    }
  }
  
  /**
   * Declares the {@link Grammar} definition.<br>
   * <br>
   * Use new {@link TokenPath} instances to form the pathes.
   * 
   * @return
   */
  public Grammar.TokenPath declare();
}
