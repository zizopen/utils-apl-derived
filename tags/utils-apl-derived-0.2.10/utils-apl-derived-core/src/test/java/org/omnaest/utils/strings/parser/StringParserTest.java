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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

public class StringParserTest
{
  
  @Test
  public void testParse()
  {
    final StringBuilder stringBuilder = new StringBuilder();
    Grammar grammar = new Grammar()
    {
      @Override
      public TokenPath declare()
      {
        final Token ABC = new Token( "(ABC)+" );
        final Token DEF = new Token( "DEF" );
        
        final TokenPath root = new TokenPath();
        final TokenAction tokenActionABC = new TokenAction()
        {
          @Override
          public void execute( String tokenMatch )
          {
            stringBuilder.append( tokenMatch );
          }
        };
        final TokenAction tokenActionEnd = new TokenAction()
        {
          @Override
          public void execute( String tokenMatch )
          {
            stringBuilder.append( tokenMatch );
          }
        };
        final TokenAction tokenActionDEF = new TokenAction()
        {
          @Override
          public void execute( String tokenMatch )
          {
            stringBuilder.append( "def" );
          }
        };
        
        return root.add( ABC )
                   .execute( tokenActionABC )
                   .or( new TokenPath().add( "end" ).execute( tokenActionEnd ),
                        new TokenPath().add( DEF ).execute( tokenActionDEF ).add( root ) )
                   .withLookup( 3 );
        
      }
      
    };
    String text = "ABCABCDEFABCend";
    StringParser.use( grammar ).parse( text );
    
    assertEquals( "ABCABCdefABCend", stringBuilder.toString() );
  }
  
  @Test
  public void testParse2()
  {
    final List<String> tokenList = new ArrayList<String>();
    Grammar grammar = new Grammar()
    {
      @Override
      public TokenPath declare()
      {
        final Token ABC = new Token( "[^\\(\\,]*(\\(([^\\)]*(\\s*,\\s*)?)*\\))?" );
        final Token DEF = new Token( "\\s*\\,?\\s*" );
        
        final TokenPath root = new TokenPath();
        final TokenAction tokenActionABC = new TokenAction()
        {
          @Override
          public void execute( String tokenMatch )
          {
            tokenList.add( tokenMatch );
          }
        };
        return root.add( ABC ).execute( tokenActionABC ).or( new TokenPath().add( DEF ).add( root ), new TokenPath() );
      }
      
    };
    String text = "xyz(ABC)  ,def(STU,VWX) , abc";
    StringParser.use( grammar ).parse( text );
    
    assertEquals( ListUtils.valueOf( "xyz(ABC)", "def(STU,VWX)", "abc" ), tokenList );
  }
}
