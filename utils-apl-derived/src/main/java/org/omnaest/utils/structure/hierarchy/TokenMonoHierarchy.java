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
package org.omnaest.utils.structure.hierarchy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;

/**
 * A {@link TokenMonoHierarchy} allows to create hierarchical structures which are based on {@link List}s of elements.<br>
 * <br>
 * If you take e.g. a {@link String} based {@link TokenMonoHierarchy}, the following two {@link List} of {@link String}s will create a
 * {@link TokenMonoHierarchy}:<br>
 * <br>
 * <ul>
 * <li>"node1","node1","node1":value1</li>
 * <li>"node1","node1","node2":value2</li>
 * <li>"node1","node2":value3</li>
 * <li>"node1","node2":value4</li>
 * </ul>
 * <br>
 * that looks like:<br>
 * <br>
 * 
 * <pre>
 * node1+
 *      |-node1+
 *      |      |-node1:value1
 *      |      |-node2:value2
 *      |-node2:value3,value4
 * </pre>
 * 
 * @param <E>
 *          Element
 * @param <V>
 *          Value
 * @author Omnaest
 */
public class TokenMonoHierarchy<E, V>
{
  /* ********************************************** Variables ********************************************** */
  protected TokenElementNode tokenElementNodeRoot = new TokenElementNode( null, null );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * A {@link TokenElementPath} represents a path of token elements
   * 
   * @author Omnaest
   * @param <E>
   */
  public static class TokenElementPath<E>
  {
    /* ********************************************** Variables ********************************************** */
    private List<E> tokenElementList = new ArrayList<E>();
    
    /* ********************************************** Methods ********************************************** */
    /**
     * @see TokenElementPath
     * @param tokenPathElementList
     */
    public TokenElementPath( List<E> tokenPathElementList )
    {
      //
      super();
      
      //
      if ( tokenPathElementList != null )
      {
        this.tokenElementList.addAll( tokenPathElementList );
      }
    }
    
    /**
     * @see TokenElementPath
     * @param tokenPathElements
     */
    public TokenElementPath( E... tokenPathElements )
    {
      //
      this( Arrays.asList( tokenPathElements ) );
    }
    
    /**
     * Returns true if there are given token elements
     * 
     * @return
     */
    protected boolean isValid()
    {
      return !this.tokenElementList.isEmpty();
    }
    
    protected List<E> getTokenElementList()
    {
      return this.tokenElementList;
    }
    
  }
  
  /**
   * Node of a {@link TokenMonoHierarchy} which has children an one parent. Used only internally, so it does not have to protect the
   * internal {@link Collection}s
   * 
   * @author Omnaest
   */
  protected class TokenElementNode
  {
    /* ********************************************** Variables ********************************************** */
    private Map<E, TokenElementNode> tokenElementTotokenElementNodeChildrenMap = new LinkedHashMap<E, TokenElementNode>();
    private List<V>                  valueList                                 = new ArrayList<V>();
    private TokenElementNode         parentTokenElementNode                    = null;
    private E                        tokenElement                              = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see TokenElementNode
     * @param parentTokenElementNode
     * @param tokenElement
     */
    public TokenElementNode( TokenElementNode parentTokenElementNode, E tokenElement )
    {
      super();
      this.parentTokenElementNode = parentTokenElementNode;
      this.tokenElement = tokenElement;
    }
    
    /**
     * @return
     */
    public List<V> getValueList()
    {
      return this.valueList;
    }
    
    /**
     * @return
     */
    public TokenElementNode getParentTokenElementNode()
    {
      return this.parentTokenElementNode;
    }
    
    /**
     * @param parentTokenElementNode
     */
    public void setParentTokenElementNode( TokenElementNode parentTokenElementNode )
    {
      this.parentTokenElementNode = parentTokenElementNode;
    }
    
    /**
     * @return
     */
    public E getTokenElement()
    {
      return this.tokenElement;
    }
    
    /**
     * @param tokenElement
     */
    public void setTokenElement( E tokenElement )
    {
      this.tokenElement = tokenElement;
    }
    
    /**
     * @return
     */
    public Map<E, TokenElementNode> getTokenElementTotokenElementNodeChildrenMap()
    {
      return this.tokenElementTotokenElementNodeChildrenMap;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TokenElementNode [tokenElementTotokenElementNodeChildrenMap=" );
      builder.append( this.tokenElementTotokenElementNodeChildrenMap );
      builder.append( ", valueList=" );
      builder.append( this.valueList );
      builder.append( ", tokenElement=" );
      builder.append( this.tokenElement );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * The {@link Navigator} allows to navigate on a given {@link TokenMonoHierarchy}
   * 
   * @author Omnaest
   */
  public class Navigator
  {
    /* ********************************************** Variables ********************************************** */
    protected TokenElementNode tokenElementNode     = null;
    protected boolean          navigationSuccessful = false;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Navigator
     * @param tokenElementNode
     */
    protected Navigator( TokenElementNode tokenElementNode )
    {
      super();
      this.tokenElementNode = tokenElementNode;
    }
    
    /**
     * Returns a new {@link Navigator} instance based on the current navigation position. This new {@link Navigator} will not
     * affect the current {@link Navigator} instance.
     * 
     * @return
     */
    public Navigator newNavigatorFork()
    {
      return new Navigator( this.tokenElementNode );
    }
    
    /**
     * Returns the current token element
     * 
     * @return
     */
    public E getTokenElement()
    {
      return this.tokenElementNode != null ? this.tokenElementNode.getTokenElement() : null;
    }
    
    /**
     * Returns a {@link List} with all token elements of the children.
     * 
     * @return
     */
    public List<E> getTokenElementOfChildrenList()
    {
      //      
      List<E> retlist = new ArrayList<E>();
      
      //
      if ( this.hasChildren() )
      {
        Navigator navigator = this.newNavigatorFork();
        navigator.navigateToFirstChild();
        while ( navigator.isNavigationSuccessful() )
        {
          retlist.add( navigator.getTokenElement() );
          navigator.navigateToNextSibling();
        }
      }
      
      //
      return retlist;
    }
    
    /**
     * Determines the {@link List} of token path elements which represents the current hierarchy path.
     * 
     * @return
     */
    public List<E> determineTokenPathElementList()
    {
      //     
      List<E> retlist = new ArrayList<E>();
      
      //
      TokenElementNode tokenElementNode = this.tokenElementNode;
      while ( tokenElementNode != null && tokenElementNode.getTokenElement() != null )
      {
        retlist.add( 0, tokenElementNode.getTokenElement() );
        tokenElementNode = tokenElementNode.getParentTokenElementNode();
      }
      
      //
      return retlist;
    }
    
    /**
     * Returns true if the current node has at least one value
     * 
     * @return
     */
    public boolean hasValues()
    {
      return !this.getValues().isEmpty();
    }
    
    /**
     * Navigates to the next node which {@link #hasValues()}. The navigation occurs at first through all children and their
     * children and then to the next siblings until the whole hierarchy is traversed.
     * 
     * @see #isNavigationSuccessful()
     * @return
     */
    public Navigator navigateToNextNodeWithValues()
    {
      //
      this.navigationSuccessful = false;
      
      //
      Navigator navigator = this.newNavigatorFork();
      
      boolean navigationSuccessful = true;
      boolean hasValues = false;
      while ( navigationSuccessful && !hasValues )
      {
        //
        if ( navigator.hasChildren() )
        {
          navigator.navigateToFirstChild();
          hasValues = navigator.hasValues();
        }
        else if ( navigator.hasNextSibling() )
        {
          navigator.navigateToNextSibling();
          hasValues = navigator.hasValues();
        }
        else if ( navigator.hasParent() )
        {
          navigator.navigateToParent();
          hasValues = false;
        }
        
        //
        navigationSuccessful = navigator.isNavigationSuccessful();
      }
      
      //
      if ( hasValues && navigationSuccessful )
      {
        this.tokenElementNode = navigator.tokenElementNode;
        this.navigationSuccessful = true;
      }
      
      //
      return this;
    }
    
    /**
     * Navigates to the next sibling
     * 
     * @return
     */
    public Navigator navigateToNextSibling()
    {
      return this.navigateToSibling( 1 );
    }
    
    /**
     * Navigates to the previous sibling
     * 
     * @return
     */
    public Navigator navigateToPreviousSibling()
    {
      return this.navigateToSibling( 1 );
    }
    
    /**
     * Navigates to a relative position within the siblings. A relative index position of 0 point at the current node.
     * 
     * @param relativeIndexPosition
     * @return
     */
    public Navigator navigateToSibling( int relativeIndexPosition )
    {
      //
      this.navigationSuccessful = false;
      
      //
      TokenElementNode tokenElementNodeSibling = this.determineSiblingTokenElementNode( relativeIndexPosition );
      if ( tokenElementNodeSibling != null )
      {
        this.tokenElementNode = tokenElementNodeSibling;
        this.navigationSuccessful = true;
      }
      
      //
      return this;
    }
    
    /**
     * Returns true if the current node has a next sibling
     * 
     * @return
     */
    public boolean hasNextSibling()
    {
      return this.determineSiblingTokenElementNode( 1 ) != null;
    }
    
    /**
     * Returns true if the current node has a previous sibling
     * 
     * @return
     */
    public boolean hasPreviousSibling()
    {
      return this.determineSiblingTokenElementNode( -1 ) != null;
    }
    
    /**
     * Returns true if the current node has a sibling a the given relative index position.
     * 
     * @param relativeIndexPosition
     * @return
     */
    public boolean hasSibling( int relativeIndexPosition )
    {
      return this.determineSiblingTokenElementNode( relativeIndexPosition ) != null;
    }
    
    /**
     * @param relativeIndexPosition
     * @return
     */
    protected TokenElementNode determineSiblingTokenElementNode( int relativeIndexPosition )
    {
      //
      TokenElementNode retval = null;
      
      //
      TokenElementNode tokenElementNode = this.tokenElementNode;
      if ( tokenElementNode != null )
      {
        //
        TokenElementNode parentTokenElementNode = tokenElementNode.getParentTokenElementNode();
        if ( parentTokenElementNode != null )
        {
          //
          Map<E, TokenElementNode> tokenElementTotokenElementNodeChildrenMap = parentTokenElementNode.getTokenElementTotokenElementNodeChildrenMap();
          List<TokenElementNode> siblingList = org.omnaest.utils.structure.collection.ListUtils.valueOf( tokenElementTotokenElementNodeChildrenMap.values() );
          int index = siblingList.indexOf( tokenElementNode );
          if ( index >= 0 )
          {
            //
            int newIndex = index + relativeIndexPosition;
            if ( newIndex >= 0 && newIndex < siblingList.size() )
            {
              retval = siblingList.get( newIndex );
            }
          }
        }
      }
      
      //
      return retval;
    }
    
    /**
     * Returns true if the current node has children
     * 
     * @return
     */
    public boolean hasChildren()
    {
      return !this.tokenElementNode.getTokenElementTotokenElementNodeChildrenMap().isEmpty();
    }
    
    /**
     * Returns true if the current node has a parent
     * 
     * @return
     */
    public boolean hasParent()
    {
      return this.tokenElementNode.getParentTokenElementNode() != null;
    }
    
    /**
     * Navigates the current {@link Navigator} to the given child token element. If no child token element exists it will be
     * created before.
     * 
     * @param tokenElement
     * @return this
     */
    protected Navigator navigateToChildAndCreateItIfNotExisting( E tokenElement )
    {
      //
      Map<E, TokenElementNode> tokenElementTotokenElementNodeChildrenMap = this.tokenElementNode.getTokenElementTotokenElementNodeChildrenMap();
      if ( !tokenElementTotokenElementNodeChildrenMap.containsKey( tokenElement ) )
      {
        TokenElementNode parentTokenElementNode = this.tokenElementNode;
        tokenElementTotokenElementNodeChildrenMap.put( tokenElement, new TokenElementNode( parentTokenElementNode, tokenElement ) );
      }
      
      //
      this.navigateToChild( tokenElement );
      
      //
      return this;
    }
    
    /**
     * Navigates the current {@link Navigator} to the given child token element.
     * 
     * @param tokenElement
     * @return this
     */
    public Navigator navigateToChild( E tokenElement )
    {
      //
      this.navigationSuccessful = false;
      
      //
      Map<E, TokenElementNode> tokenElementTotokenElementNodeChildrenMap = this.tokenElementNode.getTokenElementTotokenElementNodeChildrenMap();
      if ( tokenElementTotokenElementNodeChildrenMap.containsKey( tokenElement ) )
      {
        this.tokenElementNode = tokenElementTotokenElementNodeChildrenMap.get( tokenElement );
        this.navigationSuccessful = true;
      }
      
      //
      return this;
    }
    
    /**
     * Navigates the current {@link Navigator} to the first child token element.
     * 
     * @param tokenElement
     * @return this
     */
    public Navigator navigateToFirstChild()
    {
      //
      this.navigationSuccessful = false;
      
      //
      Map<E, TokenElementNode> tokenElementTotokenElementNodeChildrenMap = this.tokenElementNode.getTokenElementTotokenElementNodeChildrenMap();
      if ( !tokenElementTotokenElementNodeChildrenMap.isEmpty() )
      {
        E firstKey = tokenElementTotokenElementNodeChildrenMap.keySet().iterator().next();
        this.tokenElementNode = tokenElementTotokenElementNodeChildrenMap.get( firstKey );
        this.navigationSuccessful = true;
      }
      
      //
      return this;
    }
    
    /**
     * Adds values to the current node
     * 
     * @param values
     * @return this
     */
    protected Navigator addValuesToCurrentNode( V... values )
    {
      //
      List<V> valueList = this.tokenElementNode.getValueList();
      valueList.addAll( Arrays.asList( values ) );
      
      //
      return this;
    }
    
    /**
     * Returns the values of the current node
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<V> getValues()
    {
      return ListUtils.unmodifiableList( this.tokenElementNode.getValueList() );
    }
    
    /**
     * Navigates the current {@link Navigator} to its parent
     * 
     * @return this
     */
    public Navigator navigateToParent()
    {
      //
      TokenElementNode parentTokenElementNode = this.tokenElementNode.getParentTokenElementNode();
      if ( parentTokenElementNode != null )
      {
        this.tokenElementNode = parentTokenElementNode;
        this.navigationSuccessful = true;
      }
      else
      {
        this.navigationSuccessful = false;
      }
      
      //
      return this;
    }
    
    /**
     * Returns true if the last navigation was successful.
     * 
     * @see #navigateToChild(Object)
     * @see #navigateToParent()
     * @return
     */
    public boolean isNavigationSuccessful()
    {
      return this.navigationSuccessful;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Navigator [tokenElementNode=" );
      builder.append( this.tokenElementNode );
      builder.append( ", navigationSuccessful=" );
      builder.append( this.navigationSuccessful );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TokenElementPath
   * @param tokenElementPath
   * @param values
   * @return
   */
  public TokenMonoHierarchy<E, V> addTokenElementPathWithValues( TokenElementPath<E> tokenElementPath, V... values )
  {
    //
    if ( tokenElementPath != null && tokenElementPath.isValid() && values.length > 0 )
    {
      //
      List<E> tokenElementList = tokenElementPath.getTokenElementList();
      Navigator navigator = this.getNavigator();
      for ( E tokenElement : tokenElementList )
      {
        navigator.navigateToChildAndCreateItIfNotExisting( tokenElement );
      }
      navigator.addValuesToCurrentNode( values );
      
    }
    
    //
    return this;
  }
  
  /**
   * Returns a {@link Navigator} for the {@link TokenMonoHierarchy}
   * 
   * @return
   */
  public Navigator getNavigator()
  {
    return new Navigator( this.tokenElementNodeRoot );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "MonoHierarchy [tokenElementNodeRoot=" );
    builder.append( this.tokenElementNodeRoot );
    builder.append( "]" );
    return builder.toString();
  }
}
