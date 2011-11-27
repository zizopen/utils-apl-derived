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
package org.omnaest.utils.structure.hierarchy.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.strings.StringUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * A {@link TreeNavigator} allows to navigate on a given {@link Tree}. It will store a path to the current {@link TreeNode}, so
 * that it can navigate in a upward direction, too.
 * 
 * @see Tree
 * @see TreeNode
 * @author Omnaest
 * @param <T>
 *          {@link Tree}
 * @param <TN>
 *          {@link TreeNode}
 */
@SuppressWarnings("rawtypes")
public class TreeNavigator<T extends Tree<?, TN>, TN extends TreeNode>
{
  /* ********************************************** Variables ********************************************** */
  protected final T      tree;
  /** Stores the current path through the {@link TreeNode}s. The last element is the current. */
  
  protected TreeNodePath treeNodePath         = new TreeNodePath();
  protected boolean      navigationSuccessful = true;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TreeNavigator
   * @author Omnaest
   * @param <TN>
   */
  public static interface TreeNodeVisitor<TN extends TreeNode>
  {
    
    /**
     * Visit method for the traversed {@link TreeNode}s
     * 
     * @param treeNode
     * @param treeNavigatorTraversalControl
     *          {@link TreeNavigatorTraversalControl}
     */
    public void visit( TN treeNode, TreeNavigatorTraversalControl treeNavigatorTraversalControl );
  }
  
  /**
   * Control instance for the traversal mechanism of the {@link TreeNavigator}
   * 
   * @see TreeNavigator
   * @author Omnaest
   */
  public static interface TreeNavigatorTraversalControl
  {
    /**
     * Cancels the current traversal of the {@link Tree}
     */
    public void cancelTraversal();
  }
  
  /**
   * Internal representation of the path of {@link TreeNode}s.<br>
   * <br>
   * The children of all path {@link TreeNode}s are stored as well to allow sibling determination.
   * 
   * @author Omnaest
   */
  protected class TreeNodePath
  {
    /* ********************************************** Variables ********************************************** */
    private final List<TN>            treeNodePathList          = new ArrayList<TN>();
    protected final Map<TN, List<TN>> treeNodeToChildrenListMap = new HashMap<TN, List<TN>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Creates a fork of the current {@link TreeNodePath}
     * 
     * @return
     */
    public TreeNodePath fork()
    {
      //
      final TreeNodePath retval = new TreeNodePath();
      retval.treeNodePathList.addAll( this.treeNodePathList );
      retval.treeNodeToChildrenListMap.putAll( this.treeNodeToChildrenListMap );
      
      //
      return retval;
    }
    
    /**
     * Adds a new {@link TreeNode} to the current tree
     * 
     * @param treeNode
     */
    public void addTreeNodeToTreeNodePath( TN treeNode )
    {
      //
      if ( treeNode != null )
      {
        //
        this.treeNodePathList.add( treeNode );
      }
    }
    
    private List<TN> determineAndCacheChildrenListOfCurrentTreeNode()
    {
      //
      List<TN> retlist = null;
      
      //
      TN currentTreeNode = this.getCurrentTreeNode();
      if ( currentTreeNode != null )
      {
        //
        retlist = this.determineAndCacheChildrenListOf( currentTreeNode );
      }
      
      //
      return retlist;
    }
    
    @SuppressWarnings("unchecked")
    private List<TN> determineAndCacheChildrenListOf( TN treeNode )
    {
      //
      List<TN> retlist = null;
      
      //
      if ( treeNode != null )
      {
        //
        retlist = this.treeNodeToChildrenListMap.get( treeNode );
        
        //
        if ( retlist == null )
        {
          //
          retlist = treeNode.getChildrenList();
          this.treeNodeToChildrenListMap.put( treeNode, retlist );
        }
      }
      
      //
      return retlist;
    }
    
    /**
     * @return
     * @see java.util.List#size()
     */
    public int size()
    {
      return this.treeNodePathList.size();
    }
    
    /**
     * @return
     * @see java.util.List#isEmpty()
     */
    public boolean isEmpty()
    {
      return this.treeNodePathList.isEmpty();
    }
    
    /**
     * Returns the current {@link TreeNode}
     * 
     * @return
     */
    public TN getCurrentTreeNode()
    {
      return ListUtils.lastElement( this.treeNodePathList );
    }
    
    /**
     * Removes the last current {@link TreeNode}
     */
    public void removeLastTreeNodeAndClearUnusedCachedChildrenLists()
    {
      TN treeNodeRemoved = ListUtils.removeLast( this.treeNodePathList );
      if ( treeNodeRemoved != null )
      {
        this.treeNodeToChildrenListMap.keySet().retainAll( this.treeNodePathList );
      }
    }
    
    /**
     * Returns the {@link List} of children which are determined when the {@link TreeNode} is added to the {@link TreeNodePath}
     * 
     * @param treeNode
     * @return
     */
    public List<TN> getChildrenListFor( TN treeNode )
    {
      return treeNode != null ? this.determineAndCacheChildrenListOf( treeNode ) : new ArrayList<TN>();
    }
    
    /**
     * Returns the parent {@link TreeNode} of the current {@link TreeNode} is one exists, otherwise null
     * 
     * @return
     */
    public TN getParent()
    {
      //
      int inverseIndex = 1;
      return ListUtils.elementAtInverseIndex( this.treeNodePathList, inverseIndex );
    }
    
    /**
     * Returns the children {@link TreeNode} {@link List} of the parental {@link TreeNode}
     * 
     * @return
     */
    public List<TN> getChildrenListOfParent()
    {
      return this.getChildrenListFor( this.getParent() );
    }
    
    /**
     * Returns the children {@link TreeNode} {@link List} of the parental {@link TreeNode}
     * 
     * @return
     */
    public List<TN> getChildrenListOfCurrentTreeNode()
    {
      return this.getChildrenListFor( this.getCurrentTreeNode() );
    }
    
    /**
     * @return -1 if no parent is present
     */
    public int determineIndexPositionOfCurrentTreeNodeWithinTheParentChildrenList()
    {
      return this.getChildrenListOfParent().indexOf( this.getCurrentTreeNode() );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a {@link TreeNavigator} on top of the given {@link Tree} which starts with the {@link Tree#getTreeRootNode()} as
   * navigation origin.<br>
   * 
   * @see TreeNavigator
   * @see Tree
   * @see TreeNode
   * @throws IllegalArgumentException
   *           if the {@link Tree} reference is null
   * @param tree
   */
  public TreeNavigator( T tree )
  {
    super();
    this.tree = tree;
    Assert.isNotNull( tree );
    this.treeNodePath.addTreeNodeToTreeNodePath( tree.getTreeRootNode() );
  }
  
  /**
   * @see TreeNavigator
   * @param tree
   * @param treeNodePath
   *          {@link TreeNodePath}
   */
  protected TreeNavigator( T tree, TreeNodePath treeNodePath )
  {
    this.tree = tree;
    this.treeNodePath = treeNodePath;
  }
  
  /**
   * Creates a fork instance of the current {@link TreeNavigator}. Any navigation actions to the fork will not affect the current
   * {@link TreeNavigator} instance and vice versa.
   * 
   * @return
   */
  public TreeNavigator<T, TN> fork()
  {
    return new TreeNavigator<T, TN>( this.tree, this.treeNodePath.fork() );
  }
  
  /**
   * Navigates to the first child of the current {@link TreeNode}
   * 
   * @return this
   */
  public TreeNavigator<T, TN> navigateToFirstChild()
  {
    //
    int index = 0;
    return this.navigateToChildAt( index );
  }
  
  /**
   * Navigates to the last child of the current {@link TreeNode}
   * 
   * @return this
   */
  public TreeNavigator<T, TN> navigateToLastChild()
  {
    //
    int index = this.treeNodePath.determineAndCacheChildrenListOfCurrentTreeNode().size() - 1;
    return this.navigateToChildAt( index );
  }
  
  /**
   * Tries to navigate to the child of the current {@link TreeNode} at the given index position
   * 
   * @param index
   * @return this
   */
  public TreeNavigator<T, TN> navigateToChildAt( int index )
  {
    //
    this.navigationSuccessful = false;
    
    //
    List<TN> childrenList = this.treeNodePath.getChildrenListOfCurrentTreeNode();
    TN element = ListUtils.elementAt( childrenList, index );
    if ( element != null )
    {
      this.treeNodePath.addTreeNodeToTreeNodePath( element );
      this.navigationSuccessful = true;
    }
    
    //
    return this;
  }
  
  /**
   * Tries to navigate to the next {@link TreeNode} sibling with the given relative index position. A relative index position of 0
   * means the current {@link TreeNode}, a relative index position of 1 means the direct next one,...
   * 
   * @see #isNavigationSuccessful()
   * @see #navigateToNextSibling()
   * @see #navigateToPreviousSibling(int)
   * @see #navigateToPreviousSibling()
   * @param relativeIndexPosition
   * @return this
   */
  public TreeNavigator<T, TN> navigateToNextSibling( int relativeIndexPosition )
  {
    return this.navigateToPreviousSibling( -relativeIndexPosition );
  }
  
  /**
   * Tries to navigate to the next {@link TreeNode} sibling.
   * 
   * @see #isNavigationSuccessful()
   * @see #navigateToPreviousSibling()
   * @see #navigateToNextSibling(int)
   * @return this
   */
  public TreeNavigator<T, TN> navigateToNextSibling()
  {
    return this.navigateToNextSibling( 1 );
  }
  
  /**
   * Tries to navigate to the direct previous sibling.
   * 
   * @see #isNavigationSuccessful()
   * @see #navigateToNextSibling(int)
   * @see #navigateToPreviousSibling(int)
   * @param relativeIndexPosition
   * @return this
   */
  public TreeNavigator<T, TN> navigateToPreviousSibling()
  {
    return this.navigateToPreviousSibling( 1 );
  }
  
  /**
   * Navigates to the previous sibling using the given relative index position decrement. A relative index position of 0 means the
   * current {@link TreeNode}, a relative index position of 1 means the direct previous {@link TreeNode}
   * 
   * @see #isNavigationSuccessful()
   * @see #navigateToPreviousSibling()
   * @see #navigateToNextSibling()
   * @see #navigateToNextSibling(int)
   * @param relativeIndexPosition
   * @return this
   */
  public TreeNavigator<T, TN> navigateToPreviousSibling( int relativeIndexPosition )
  {
    //
    this.navigationSuccessful = false;
    
    //
    final int indexPosition = this.treeNodePath.determineIndexPositionOfCurrentTreeNodeWithinTheParentChildrenList();
    if ( indexPosition >= 0 )
    {
      //
      final List<TN> childrenListOfParent = this.treeNodePath.getChildrenListOfParent();
      
      //
      int newIndexPosition = indexPosition - relativeIndexPosition;
      if ( newIndexPosition >= 0 && newIndexPosition < childrenListOfParent.size() )
      {
        //
        TN treeNode = childrenListOfParent.get( newIndexPosition );
        this.treeNodePath.removeLastTreeNodeAndClearUnusedCachedChildrenLists();
        this.treeNodePath.addTreeNodeToTreeNodePath( treeNode );
        
        //
        this.navigationSuccessful = true;
      }
    }
    
    //
    return this;
  }
  
  /**
   * Returns true, if the current {@link TreeNode} has a next sibling
   * 
   * @return
   */
  public boolean hasNextSibling()
  {
    return this.fork().navigateToNextSibling().isNavigationSuccessful();
  }
  
  /**
   * Returns true, if the current {@link TreeNode} has a previous sibling
   * 
   * @return
   */
  public boolean hasPreviousSibling()
  {
    return this.fork().navigateToPreviousSibling().isNavigationSuccessful();
  }
  
  /**
   * Navigates to the parent {@link TreeNode}
   * 
   * @return this
   */
  public TreeNavigator<T, TN> navigateToParent()
  {
    //
    this.navigationSuccessful = false;
    
    //
    if ( this.hasParent() )
    {
      this.treeNodePath.removeLastTreeNodeAndClearUnusedCachedChildrenLists();
      this.navigationSuccessful = true;
    }
    
    //
    return this;
  }
  
  /**
   * Returns true, if the current {@link TreeNode} has a parent
   * 
   * @return
   */
  public boolean hasParent()
  {
    return this.treeNodePath.size() > 1;
  }
  
  /**
   * Returns true if the current {@link TreeNode#getChildrenList()} is not empty
   * 
   * @return
   */
  public boolean hasChildren()
  {
    return !this.treeNodePath.getChildrenListOfCurrentTreeNode().isEmpty();
  }
  
  /**
   * Visits all children of the current {@link TreeNode}. If transitive is set to true, the children of the children, and so on...
   * are included.
   * 
   * @param transitive
   * @param treeNodeVisitors
   * @return this
   */
  public TreeNavigator<T, TN> visitChildren( boolean transitive, TreeNodeVisitor<TN>... treeNodeVisitors )
  {
    //
    if ( treeNodeVisitors.length > 0 )
    {
      //
      final Set<TreeNodeVisitor> treeNodeVisitorTraversingSet = new HashSet<TreeNodeVisitor>( Arrays.asList( treeNodeVisitors ) );
      
      //
      final List<TN> childrenListOfCurrentTreeNode = this.treeNodePath.getChildrenListOfCurrentTreeNode();
      this.visitTreeNodeList( transitive, treeNodeVisitorTraversingSet, childrenListOfCurrentTreeNode, treeNodeVisitors );
    }
    
    //
    return this;
  }
  
  /**
   * @see #visitChildren(boolean, TreeNodeVisitor...)
   * @param transitive
   * @param treeNodeVisitorTraversingSet
   * @param treeNodeList
   * @param treeNodeVisitors
   */
  private void visitTreeNodeList( boolean transitive,
                                  final Set<TreeNodeVisitor> treeNodeVisitorTraversingSet,
                                  final List<TN> treeNodeList,
                                  final TreeNodeVisitor<TN>... treeNodeVisitors )
  {
    //
    if ( treeNodeList != null )
    {
      for ( TN treeNode : treeNodeList )
      {
        for ( final TreeNodeVisitor<TN> treeNodeVisitor : treeNodeVisitors )
        {
          //
          if ( treeNodeVisitorTraversingSet.contains( treeNodeVisitor ) )
          {
            //          
            final TreeNavigatorTraversalControl treeNavigatorTraversalControl = new TreeNavigatorTraversalControl()
            {
              @Override
              public void cancelTraversal()
              {
                treeNodeVisitorTraversingSet.remove( treeNodeVisitor );
              }
            };
            treeNodeVisitor.visit( treeNode, treeNavigatorTraversalControl );
          }
          
          //
          if ( transitive && treeNodeVisitorTraversingSet.contains( treeNodeVisitor ) )
          {
            @SuppressWarnings("unchecked")
            final List<TN> childrenListOfCurrentTreeNode = treeNode.getChildrenList();
            this.visitTreeNodeList( transitive, treeNodeVisitorTraversingSet, childrenListOfCurrentTreeNode, treeNodeVisitors );
          }
        }
      }
    }
  }
  
  /**
   * @return the navigationSuccessful
   */
  public boolean isNavigationSuccessful()
  {
    return this.navigationSuccessful;
  }
  
  /**
   * Returns the current {@link TreeNode}
   * 
   * @return
   */
  public TN getCurrentTreeNode()
  {
    return this.treeNodePath.getCurrentTreeNode();
  }
  
  @Override
  public String toString()
  {
    //
    StringBuilder builder = new StringBuilder();
    
    //
    final TreeNavigator<T, TN> treeNavigator = this.fork();
    final Set<Object> alreadyTraversedModelSet = new HashSet<Object>();
    
    //
    String indentionString = "";
    boolean isTraversing = true;
    while ( isTraversing )
    {
      //
      boolean alreadyTraversedNode = false;
      TN currentTreeNode = treeNavigator.getCurrentTreeNode();
      if ( currentTreeNode != null )
      {
        //
        Object model = currentTreeNode.getModel();
        alreadyTraversedNode = alreadyTraversedModelSet.contains( model );
        if ( !alreadyTraversedNode )
        {
          //
          alreadyTraversedModelSet.add( model );
          
          //
          builder.append( indentionString );
          builder.append( "|--" );
          builder.append( String.valueOf( model ) );
          builder.append( StringUtils.DEFAULT_LINESEPARATOR );
        }
      }
      
      //
      boolean hasNextSibling = treeNavigator.hasNextSibling();
      
      //
      if ( !alreadyTraversedNode && treeNavigator.hasChildren() )
      {
        indentionString += hasNextSibling ? "|  " : "   ";
        treeNavigator.navigateToFirstChild();
      }
      else if ( hasNextSibling )
      {
        treeNavigator.navigateToNextSibling();
      }
      else
      {
        //
        while ( !treeNavigator.hasNextSibling() && treeNavigator.hasParent() )
        {
          treeNavigator.navigateToParent();
          indentionString = indentionString.substring( 0, Math.max( 0, indentionString.length() - 3 ) );
        }
        
        //
        isTraversing = treeNavigator.navigateToNextSibling().isNavigationSuccessful();
      }
    }
    
    //
    return builder.toString();
  }
}
