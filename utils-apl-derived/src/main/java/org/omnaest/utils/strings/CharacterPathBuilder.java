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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionTransformerToString;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * Static builder for a {@link CharacterPath}
 * 
 * @author Omnaest
 */
public class CharacterPathBuilder
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * A {@link CharacterPath} represents a path of characters. A path can have multiple junctions so a {@link CharacterPath} can
   * represent a {@link Set} of multiple {@link String}s
   * 
   * @author Omnaest
   */
  public static class CharacterPath
  {
    /* ********************************************** Variables ********************************************** */
    private Character                                character                 = null;
    private List<CharacterPathBuilder.CharacterPath> characterPathChildrenList = null;
    private boolean                                  leaf                      = false;
    
    /* ********************************************** Methods ********************************************** */
    
    public CharacterPath( Character character, List<CharacterPath> characterPathChildrenList, boolean leaf )
    {
      super();
      this.character = character;
      this.characterPathChildrenList = characterPathChildrenList;
      this.leaf = leaf;
    }
    
    /**
     * Returns a the first characters of the children of the current {@link CharacterPath} as a concatenated {@link String}
     * 
     * @return
     */
    public String determineFirstCharactersOfChildrenAsString()
    {
      //
      CollectionConverter<Character, String> collectionTransformer = new CollectionTransformerToString<Character>()
      {
        @Override
        public void process( Character character, StringBuilder resultStringBuilder )
        {
          resultStringBuilder.append( character != null ? character : "" );
        }
      };
      return CollectionUtils.convert( ListUtils.filterExcludingNullElements( ListUtils.convert( this.characterPathChildrenList,
                                                                                                new ElementConverter<CharacterPath, Character>()
                                                                                                {
                                                                                                  @Override
                                                                                                  public Character convert( CharacterPath characterPath )
                                                                                                  {
                                                                                                    //
                                                                                                    boolean characterPathIsEmptyOrNull = ( characterPath == null || characterPath.getCharacter() == null );
                                                                                                    return !characterPathIsEmptyOrNull ? characterPath.getCharacter()
                                                                                                                                      : null;
                                                                                                  }
                                                                                                } ) ), collectionTransformer );
    }
    
    public Character getCharacter()
    {
      return this.character;
    }
    
    public List<CharacterPathBuilder.CharacterPath> getCharacterPathChildrenList()
    {
      return this.characterPathChildrenList;
    }
    
    /**
     * Returns the {@link CharacterPath} which matches the last character of the given relative path to the current
     * {@link CharacterPath}. The beginning of the relative path has to match the currents {@link CharacterPath#getCharacter()}.
     * 
     * @param relativePath
     * @return
     */
    public CharacterPath matchingPath( String relativePath )
    {
      //
      CharacterPath retval = null;
      
      //
      if ( relativePath != null && !relativePath.isEmpty() )
      {
        //
        boolean isRootCharacterPath = this.character == null;
        
        //
        String currentRelativePath = relativePath.substring( isRootCharacterPath ? 0 : 1 );
        char firstCharacter = relativePath.charAt( 0 );
        if ( isRootCharacterPath || this.character.equals( firstCharacter ) )
        {
          //          
          CharacterPath currentCharacterPath = this;
          while ( !currentRelativePath.isEmpty() && currentCharacterPath != null )
          {
            //
            firstCharacter = currentRelativePath.charAt( 0 );
            List<CharacterPath> characterPathChildrenList = currentCharacterPath.getCharacterPathChildrenList();
            
            //
            currentCharacterPath = null;
            for ( CharacterPath characterPathChild : characterPathChildrenList )
            {
              //
              Character characterChild = characterPathChild.getCharacter();
              if ( characterChild != null && characterChild.equals( firstCharacter ) )
              {
                currentCharacterPath = characterPathChild;
                break;
              }
            }
            
            //
            currentRelativePath = currentRelativePath.substring( 1 );
          }
          
          //
          if ( currentCharacterPath != null )
          {
            retval = currentCharacterPath;
          }
        }
      }
      
      //
      return retval;
    }
    
    /**
     * Builds all {@link String} combinations of the current {@link CharacterPath} and all descending {@link CharacterPath}
     * instances related to the first one.
     * 
     * @return
     */
    public List<String> buildStringList()
    {
      //
      List<String> retlist = new ArrayList<String>();
      
      //
      final Character character = this.character;
      final List<CharacterPath> characterPathChildrenList = this.characterPathChildrenList;
      
      //
      if ( !characterPathChildrenList.isEmpty() )
      {
        //
        for ( CharacterPath characterPathChild : characterPathChildrenList )
        {
          //
          List<String> childStringList = characterPathChild.buildStringList();
          if ( !childStringList.isEmpty() )
          {
            //
            ElementConverter<String, String> elementTransformer = new ElementConverter<String, String>()
            {
              @Override
              public String convert( String element )
              {
                // 
                return character + element;
              }
            };
            
            List<String> transformedChildStringList = ListUtils.convert( childStringList, elementTransformer );
            
            //
            retlist.addAll( transformedChildStringList );
          }
        }
      }
      
      //
      if ( characterPathChildrenList.isEmpty() || this.leaf )
      {
        retlist.add( String.valueOf( character ) );
      }
      
      //
      return retlist;
    }
    
    /**
     * Returns true if the current {@link CharacterPath} has no children
     * 
     * @return
     */
    public boolean hasNoChildren()
    {
      return this.characterPathChildrenList.isEmpty();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "\nCharacterPath [character=" );
      builder.append( this.character );
      builder.append( ", characterPathChildrenList=" );
      builder.append( this.characterPathChildrenList );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * Returns true if the current {@link CharacterPath} represents a leaf node. This does not mean that there are no further
     * children.
     * 
     * @return
     */
    public boolean isLeaf()
    {
      return this.leaf;
    }
    
    /**
     * Returns true if the current {@link CharacterPath} is the root node and returns null for the {@link #getCharacter()}
     * 
     * @return
     */
    public boolean isRoot()
    {
      return this.getCharacter() == null;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Builds the {@link CharacterPath} from a given {@link List} of {@link String}s. If the given {@link List} of {@link String}s
   * has different multiple starting characters, the first returned {@link CharacterPath} is a virtual root element and will have
   * null as {@link CharacterPath#getCharacter()}.
   * 
   * @param stringList
   * @return
   */
  public static CharacterPath buildPath( List<String> stringList )
  {
    //
    CharacterPath retval = null;
    List<CharacterPath> characterPathRetlist = new ArrayList<CharacterPath>();
    
    //
    if ( stringList != null )
    {
      //
      List<Character> firstCharacterList = ListUtils.filterExcludingNullElements( ListUtils.convert( stringList,
                                                                                                     new ElementConverter<String, Character>()
                                                                                                     {
                                                                                                       @Override
                                                                                                       public Character convert( String element )
                                                                                                       {
                                                                                                         //
                                                                                                         boolean elementIsEmptyOrNull = ( element == null || element.length() == 0 );
                                                                                                         return !elementIsEmptyOrNull ? element.charAt( 0 )
                                                                                                                                     : null;
                                                                                                       }
                                                                                                     } ) );
      
      //
      Set<Character> firstCharacterSet = new LinkedHashSet<Character>( firstCharacterList );
      for ( Character firstCharacter : firstCharacterSet )
      {
        //
        final ElementHolder<Boolean> hasEmptyElementHolder = new ElementHolder<Boolean>( false );
        
        //          
        List<String> stringListForCurrentCharacterWithRemovedFirstCharacter = ListUtils.filterExcludingNullElements( ListUtils.convert( ListUtils.filterIncludingIndexPositions( stringList,
                                                                                                                                                                                 ListUtils.indexListOf( firstCharacterList,
                                                                                                                                                                                                        firstCharacter ) ),
                                                                                                                                        new ElementConverter<String, String>()
                                                                                                                                        {
                                                                                                                                          @Override
                                                                                                                                          public String convert( String element )
                                                                                                                                          {
                                                                                                                                            //
                                                                                                                                            String retval = null;
                                                                                                                                            
                                                                                                                                            //
                                                                                                                                            boolean elementIsEmptyOrNull = ( element == null || element.length() == 0 );
                                                                                                                                            
                                                                                                                                            //
                                                                                                                                            retval = elementIsEmptyOrNull ? null
                                                                                                                                                                         : element.substring( 1 );
                                                                                                                                            
                                                                                                                                            //
                                                                                                                                            if ( StringUtils.isEmpty( retval ) )
                                                                                                                                            {
                                                                                                                                              hasEmptyElementHolder.setElement( true );
                                                                                                                                            }
                                                                                                                                            
                                                                                                                                            //
                                                                                                                                            return retval;
                                                                                                                                          }
                                                                                                                                        } ) );
        //
        CharacterPath characterPath = CharacterPathBuilder.buildPath( stringListForCurrentCharacterWithRemovedFirstCharacter );
        
        //
        Character character = firstCharacter;
        List<CharacterPath> characterPathChildrenList = new ArrayList<CharacterPath>();
        if ( characterPath != null )
        {
          if ( characterPath.isRoot() )
          {
            characterPathChildrenList.addAll( characterPath.getCharacterPathChildrenList() );
          }
          else
          {
            characterPathChildrenList.add( characterPath );
          }
        }
        boolean leaf = hasEmptyElementHolder.getElement();
        characterPathRetlist.add( new CharacterPath( character, characterPathChildrenList, leaf ) );
      }
    }
    
    //
    if ( characterPathRetlist.size() > 1 )
    {
      //
      Character character = null;
      List<CharacterPath> characterPathChildrenList = characterPathRetlist;
      boolean leaf = false;
      retval = new CharacterPath( character, characterPathChildrenList, leaf );
    }
    else if ( characterPathRetlist.size() == 1 )
    {
      //
      retval = characterPathRetlist.get( 0 );
    }
    
    //
    return retval;
  }
}
