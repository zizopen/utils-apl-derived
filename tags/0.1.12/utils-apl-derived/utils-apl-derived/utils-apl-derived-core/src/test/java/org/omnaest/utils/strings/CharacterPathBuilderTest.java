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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.strings.CharacterPathBuilder.CharacterPath;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see CharacterPathBuilder
 * @author Omnaest
 */
public class CharacterPathBuilderTest
{
  
  @SuppressWarnings("unchecked")
  @Test
  public void testBuildPath()
  {
    //
    List<String> stringList = Arrays.asList( "abcd", "abc", "abde", "bcdefghi" );
    
    //
    CharacterPath characterPathRoot = CharacterPathBuilder.buildPath( stringList );
    assertNotNull( characterPathRoot );
    
    //
    List<CharacterPath> characterPathList = characterPathRoot.getCharacterPathChildrenList();
    assertNotNull( characterPathList );
    
    //
    assertEquals( 2, characterPathList.size() );
    assertEquals( stringList,
                  ListUtils.mergeAll( characterPathList.get( 0 ).buildStringList(), characterPathList.get( 1 ).buildStringList() ) );
    
    //following first path
    {
      //
      CharacterPath characterPath = characterPathList.get( 0 );
      assertEquals( false, characterPath.hasNoChildren() );
      assertEquals( Character.valueOf( 'a' ), characterPath.getCharacter() );
      
      //
      characterPath = characterPath.getCharacterPathChildrenList().get( 0 );
      assertEquals( false, characterPath.hasNoChildren() );
      assertEquals( Character.valueOf( 'b' ), characterPath.getCharacter() );
      
      //
      characterPath = characterPath.getCharacterPathChildrenList().get( 0 );
      assertEquals( false, characterPath.hasNoChildren() );
      assertEquals( Character.valueOf( 'c' ), characterPath.getCharacter() );
      
      //
      characterPath = characterPath.getCharacterPathChildrenList().get( 0 );
      assertEquals( true, characterPath.hasNoChildren() );
      assertEquals( Character.valueOf( 'd' ), characterPath.getCharacter() );
    }
    
  }
  
  @Test
  public void testMatchingPath()
  {
    //
    List<String> stringList = Arrays.asList( "abcd", "abc", "abde", "bcdefghi" );
    
    //
    CharacterPath characterPathRoot = CharacterPathBuilder.buildPath( stringList );
    assertNotNull( characterPathRoot );
    
    //
    CharacterPath matchingPath = characterPathRoot.matchingPath( "abc" );
    assertNotNull( matchingPath );
    assertEquals( Character.valueOf( 'c' ), matchingPath.getCharacter() );
    
    //
    assertEquals( "d", matchingPath.determineFirstCharactersOfChildrenAsString() );
  }
  
  @Test
  public void testDetermineFirstCharactersOfChildrenAsString()
  {
    //
    List<String> stringList = Arrays.asList( "abcd", "abc", "abde", "bcdefghi" );
    
    //
    CharacterPath characterPathRoot = CharacterPathBuilder.buildPath( stringList );
    assertNotNull( characterPathRoot );
    
    //
    String firstCharactersOfChildrenAsString = characterPathRoot.determineFirstCharactersOfChildrenAsString();
    assertEquals( "ab", firstCharactersOfChildrenAsString );
  }
}
