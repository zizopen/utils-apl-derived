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

/**
 * Simple {@link StringParser} which allows to use a given {@link Grammar} to parse a given text.<br>
 * <br>
 * Example:
 * 
 * <pre>
 * final StringBuilder stringBuilder = new StringBuilder();
 * Grammar grammar = new Grammar()
 * {
 *   &#064;Override
 *   public TokenPath declare()
 *   {
 *     final Token ABC = new Token( &quot;(ABC)+&quot; );
 *     final Token DEF = new Token( &quot;DEF&quot; );
 *     
 *     final TokenPath root = new TokenPath();
 *     final TokenAction tokenActionABC = new TokenAction()
 *     {
 *       &#064;Override
 *       public void execute( String tokenMatch )
 *       {
 *         stringBuilder.append( tokenMatch );
 *       }
 *     };
 *     final TokenAction tokenActionEnd = new TokenAction()
 *     {
 *       &#064;Override
 *       public void execute( String tokenMatch )
 *       {
 *         stringBuilder.append( tokenMatch );
 *       }
 *     };
 *     final TokenAction tokenActionDEF = new TokenAction()
 *     {
 *       &#064;Override
 *       public void execute( String tokenMatch )
 *       {
 *         stringBuilder.append( &quot;def&quot; );
 *       }
 *     };
 *     
 *     return root.add( ABC )
 *                .execute( tokenActionABC )
 *                .or( new TokenPath().add( &quot;end&quot; ).execute( tokenActionEnd ),
 *                     new TokenPath().add( DEF ).execute( tokenActionDEF ).add( root ) )
 *                .withLookup( 3 );
 *     
 *   }
 *   
 * };
 * String text = &quot;ABCABCDEFABCend&quot;;
 * StringParser.use( grammar ).parse( text );
 * </pre>
 * 
 * @see #use(Grammar)
 * @see ParserState#parse(String)
 * @author Omnaest
 */
public class StringParser
{
  /**
   * Prepares the {@link StringParser} with a given {@link Grammar} definition.
   * 
   * @param grammar
   * @return
   */
  public static ParserState use( Grammar grammar )
  {
    return new ParserState( grammar );
  }
}
