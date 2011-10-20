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
package org.omnaest.utils.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.strings.CharacterPathBuilder;
import org.omnaest.utils.strings.CharacterPathBuilder.CharacterPath;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.CollectionTransformer;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

/**
 * Static helper class related to {@link Pattern} concerns
 * 
 * @author Omnaest
 */
public class PatternUtils
{
  
  /**
   * Returns a negated pattern that matches everything that does not equal any of the given strings
   * 
   * @see #not(Collection)
   * @see #not(String)
   * @param patternStrings
   * @return
   */
  public static String not( String... patternStrings )
  {
    return not( Arrays.asList( patternStrings ) );
  }
  
  /**
   * Returns a negated pattern that matches everything that does not equal any of the given strings within the {@link Collection}
   * 
   * @see #not(String...)
   * @see #not(String)
   * @param patternStringCollection
   * @return
   */
  public static String not( Collection<String> patternStringCollection )
  {
    //
    StringBuilder retval = new StringBuilder();
    
    //
    if ( patternStringCollection != null && !patternStringCollection.isEmpty() )
    {
      //
      List<String> patternStringListWithClearedContainingOtherOnes = new ArrayList<String>( patternStringCollection );
      {
        //
        List<String> containingOtherPatternPatternStringList = new ArrayList<String>();
        for ( String patternString : patternStringCollection )
        {
          for ( String patternStringOther : patternStringCollection )
          {
            if ( patternString != patternStringOther && patternString.contains( patternStringOther ) )
            {
              containingOtherPatternPatternStringList.add( patternString );
            }
          }
        }
        
        //
        patternStringListWithClearedContainingOtherOnes.removeAll( containingOtherPatternPatternStringList );
      }
      
      //
      retval.append( "(?:" );
      
      //
      final CharacterPath characterPathRoot = CharacterPathBuilder.buildPath( patternStringListWithClearedContainingOtherOnes );
      
      //
      List<CharacterPath> characterPathList = new ArrayList<CharacterPath>();
      if ( characterPathRoot.isRoot() )
      {
        characterPathList.addAll( characterPathRoot.getCharacterPathChildrenList() );
      }
      else
      {
        characterPathList.add( characterPathRoot );
      }
      
      //
      //      final String firstCharactersOfRoot = characterPathRoot.isRoot() ? characterPathRoot.determineFirstCharactersOfChildrenAsString()
      //                                                                     : String.valueOf( characterPathRoot.getCharacter() );
      
      //
      class Parameter
      {
        public String              previousString    = null;
        public Character           characterPrevious = null;
        @SuppressWarnings("hiding")
        public List<CharacterPath> characterPathList = null;
        public boolean             isLeaf            = false;
      }
      
      Operation<String, Parameter> operation = new Operation<String, Parameter>()
      {
        @Override
        public String execute( Parameter parameter )
        {
          //
          StringBuilder stringBuilder = new StringBuilder();
          
          //
          final Character characterPrevious = parameter.characterPrevious;
          final List<CharacterPath> characterPathList = parameter.characterPathList;
          final String previousString = parameter.previousString;
          final boolean isLeaf = parameter.isLeaf;
          
          //
          boolean first = characterPrevious == null;
          String previousStringNew = previousString + ( characterPrevious != null ? characterPrevious : "" );
          
          //          
          String mergedFirstCharacters = ListUtils.transform( characterPathList,
                                                              new CollectionTransformer<CharacterPath, String>()
                                                              {
                                                                @SuppressWarnings("hiding")
                                                                private StringBuilder stringBuilder = new StringBuilder();
                                                                
                                                                @Override
                                                                public void process( CharacterPath characterPath )
                                                                {
                                                                  this.stringBuilder.append( characterPath.getCharacter() );
                                                                }
                                                                
                                                                @Override
                                                                public String result()
                                                                {
                                                                  return this.stringBuilder.toString();
                                                                }
                                                                
                                                              } );
          
          //          //
          //          String firstCharactersOfChildrenAsString = "";
          //          for ( int beginIndex = 1; beginIndex < previousStringNew.length(); beginIndex++ )
          //          {
          //            //
          //            String previousStringNewSubString = previousStringNew.substring( beginIndex );
          //            
          //            //
          //            CharacterPath matchingCharacterPath = characterPathRoot.matchingPath( previousStringNewSubString );
          //            if ( matchingCharacterPath != null )
          //            {
          //              firstCharactersOfChildrenAsString += matchingCharacterPath.determineFirstCharactersOfChildrenAsString();
          //            }
          //          }
          //          
          //          //
          //          for ( char iCharacter : ( firstCharactersOfChildrenAsString + firstCharactersOfRoot ).toCharArray() )
          //          {
          //            if ( !mergedFirstCharacters.contains( String.valueOf( iCharacter ) ) )
          //            {
          //              mergedFirstCharacters += iCharacter;
          //            }
          //          }
          
          //
          stringBuilder.append( !first ? "|" : "" );
          stringBuilder.append( previousStringNew );
          stringBuilder.append( "[^" + mergedFirstCharacters + "]" );
          
          //
          String subString = "";
          for ( CharacterPath characterPathChild : characterPathList )
          {
            //
            boolean hasChildren = !characterPathChild.hasNoChildren();
            if ( hasChildren )
            {
              //
              Parameter parameterNew = new Parameter();
              parameterNew.previousString = previousStringNew;
              parameterNew.characterPathList = characterPathChild.getCharacterPathChildrenList();
              parameterNew.characterPrevious = characterPathChild.getCharacter();
              parameterNew.isLeaf = characterPathChild.isLeaf();
              subString += this.execute( parameterNew );
            }
          }
          
          //
          if ( !first && !isLeaf )
          {
            stringBuilder.append( "|(?:" + previousStringNew + ")$" );
          }
          
          //
          stringBuilder.append( subString );
          
          //
          return stringBuilder.toString();
        }
      };
      
      //      
      Parameter parameter = new Parameter();
      parameter.previousString = "";
      parameter.characterPathList = characterPathList;
      parameter.characterPrevious = null;
      parameter.isLeaf = false;
      retval.append( operation.execute( parameter ) );
      
      //
      retval.append( ")" );
    }
    
    //
    return retval.toString();
  }
  
  /**
   * Returns the negated pattern. <br>
   * <br>
   * E.g. the negation of ABC will result in the non capturing group:<br>
   * <code>(?:[^A]|(?:)$|A[^B]|(?:A)$|AB[^C]|(?:AB)$)</code><br>
   * <br>
   * This ensures the negotiation will not affect capturing of groups and that the negotiation will work on the end of the given
   * input string, too.
   * 
   * @param patternString
   * @return
   */
  public static String not( String patternString )
  {
    //
    StringBuilder stringBuilder = new StringBuilder();
    
    //
    if ( patternString != null )
    {
      //
      stringBuilder.append( "(?:" );
      
      //
      StringBuilder previousStringBuilder = new StringBuilder();
      
      //
      char[] charArray = patternString.toCharArray();
      boolean first = true;
      for ( char iChar : charArray )
      {
        //
        stringBuilder.append( !first ? "|" : "" );
        stringBuilder.append( previousStringBuilder );
        stringBuilder.append( "[^" + iChar + "]" );
        
        //
        if ( !first )
        {
          stringBuilder.append( "|(?:" + previousStringBuilder + ")$" );
        }
        
        //
        previousStringBuilder.append( iChar );
        first = false;
      }
      
      //
      stringBuilder.append( ")" );
    }
    
    //
    return stringBuilder.toString();
  }
}
