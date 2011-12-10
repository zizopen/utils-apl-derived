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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.strings.StringUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor.TraversalControl;

/**
 * A {@link TreeNavigator} allows to navigate on a given {@link Tree}. It will store a path to the current {@link TreeNode}, so
 * that it can navigate in a upward direction, too. <br>
 * <br>
 * The {@link TreeNavigator} will use an internal cache to cache the children of the selected {@link TreeNode} path. This can be
 * turned of using #se
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
  protected final T              tree;
  
  /** Stores the current path through the {@link TreeNode}s. The last element is the current. */
  protected TreeNodePathAndCache treeNodePath               = new TreeNodePathAndCache();
  protected boolean              navigationSuccessful       = true;
  protected boolean              cachingChildrenOfPathNodes = true;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TreeNavigator
   * @author Omnaest
   * @param <TN>
   */
  public static interface TreeNodeVisitor<T extends Tree<?, TN>, TN extends TreeNode>
  {
    /**
     * Controls the traversal of the {@link TreeNavigator}
     * 
     * @author Omnaest
     */
    public static enum TraversalControl
    {
      GO_ON,
      GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES,
      SKIP_CHILDREN,
      SKIP_FURTHER_SIBLINGS,
      SKIP_CHILDREN_AND_FURTHER_SIBLINGS,
      CANCEL_TRAVERSAL
    }
    
    /**
     * Visit method for the traversed {@link TreeNode}s
     * 
     * @param treeNode
     * @param treeNavigator
     *          {@link TreeNavigator#fork()}
     * @return {@link TraversalControl}, return of null is treated as {@link TraversalControl#GO_ON}
     */
    public TraversalControl visit( TN treeNode, TreeNavigator<T, TN> treeNavigator );
  }
  
  /**
   * Internal representation of the path of {@link TreeNode}s.<br>
   * <br>
   * The children of all path {@link TreeNode}s are cached to allow sibling determination.
   * 
   * @author Omnaest
   */
  protected class TreeNodePathAndCache
  {
    /* ********************************************** Variables ********************************************** */
    private final List<TN>            treeNodePathList          = new ArrayList<TN>();
    protected final Map<TN, List<TN>> treeNodeToChildrenListMap = new HashMap<TN, List<TN>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Creates a fork of the current {@link TreeNodePathAndCache}
     * 
     * @return
     */
    public TreeNodePathAndCache fork()
    {
      //
      final TreeNodePathAndCache retval = new TreeNodePathAndCache();
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
          
          //
          if ( TreeNavigator.this.cachingChildrenOfPathNodes )
          {
            this.treeNodeToChildrenListMap.put( treeNode, retlist );
          }
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
     * Returns the {@link List} of children which are determined when the {@link TreeNode} is added to the
     * {@link TreeNodePathAndCache}
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
    
    /**
     * @return the treeNodePathList
     */
    public List<TN> getTreeNodePathList()
    {
      return this.treeNodePathList;
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
    
    this.treeNodePath.addTreeNodeToTreeNodePath( tree.getTreeRootNode() );
  }
  
  /**
   * @see TreeNavigator
   * @param tree
   * @param treeNodePath
   * @param cachingChildrenOfPathNodes
   */
  protected TreeNavigator( T tree, TreeNodePathAndCache treeNodePath, boolean cachingChildrenOfPathNodes )
  {
    this.tree = tree;
    this.treeNodePath = treeNodePath;
    this.cachingChildrenOfPathNodes = cachingChildrenOfPathNodes;
  }
  
  /**
   * @see TreeNavigator
   * @param tree
   * @param cachingChildrenOfPathNodes
   */
  public TreeNavigator( T tree, boolean cachingChildrenOfPathNodes )
  {
    //
    this( tree );
    this.cachingChildrenOfPathNodes = cachingChildrenOfPathNodes;
  }
  
  /**
   * Creates a fork instance of the current {@link TreeNavigator}. Any navigation actions to the fork will not affect the current
   * {@link TreeNavigator} instance and vice versa.
   * 
   * @return
   */
  public TreeNavigator<T, TN> fork()
  {
    return new TreeNavigator<T, TN>( this.tree, this.treeNodePath.fork(), this.cachingChildrenOfPathNodes );
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
   * Traverses the current {@link TreeNode} and its children transitively invoking {@link TreeNodeVisitor}s for each
   * {@link TreeNode}.<br>
   * <br>
   * As default the {@link TraversalControl#GO_ON} is used for the case the {@link TreeNodeVisitor} returns null.<br>
   * If multiple {@link TreeNodeVisitor} instances are given and they return different {@link TraversalControl} results they are
   * grouped and a {@link TreeNavigator#fork()} is used for the different groups.<br>
   * <br>
   * Nodes which are already visited will be excluded by default. This prevents indefinite loops related to cyclic references. To
   * override this use {@link TraversalControl#GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES} in combination with
   * {@link #traverse(TraversalControl, TreeNodeVisitor...)}
   * 
   * @see #traverse(TraversalControl, TreeNodeVisitor...)
   * @param treeNodeVisitors
   *          {@link TreeNodeVisitor}
   * @return this
   */
  public TreeNavigator<T, TN> traverse( TreeNodeVisitor<T, TN>... treeNodeVisitors )
  {
    return this.traverse( TraversalControl.GO_ON, treeNodeVisitors );
  }
  
  /**
   * Similar to {@link #traverse(TreeNodeVisitor...)} but allows to specify a default {@link TraversalControl}. <br>
   * The default {@link TraversalControl} is used if the {@link TreeNodeVisitor} returns null.<br>
   * <br>
   * Nodes which are already visited will be excluded by default. This prevents indefinite loops related to cyclic references. To
   * override this use {@link TraversalControl#GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES} as default {@link TraversalControl}.
   * 
   * @see #traverse(TreeNodeVisitor...)
   * @param defaultTraversalControl
   *          {@link TraversalControl}
   * @param treeNodeVisitors
   *          {@link TreeNodeVisitor}
   * @return this
   */
  public TreeNavigator<T, TN> traverse( TraversalControl defaultTraversalControl, TreeNodeVisitor<T, TN>... treeNodeVisitors )
  {
    //
    final Set<TN> visitedTreeNodeSet = new LinkedHashSet<TN>();
    this.traverse( defaultTraversalControl, visitedTreeNodeSet, treeNodeVisitors );
    
    //
    return this;
  }
  
  /**
   * @see #traverse(TraversalControl, TreeNodeVisitor...)
   * @param defaultTraversalControl
   * @param visitedTreeNodeSet
   * @param treeNodeVisitors
   * @return {@link Map} of {@link TraversalControl} to a {@link Set} of {@link TreeNodeVisitor}s including only for the caller
   *         relevant types of {@link TraversalControl} like {@link TraversalControl#CANCEL_TRAVERSAL}
   */
  @SuppressWarnings("unchecked")
  protected Map<TraversalControl, Set<TreeNodeVisitor<T, TN>>> traverse( TraversalControl defaultTraversalControl,
                                                                         Set<TN> visitedTreeNodeSet,
                                                                         TreeNodeVisitor<T, TN>... treeNodeVisitors )
  {
    //
    final Map<TraversalControl, Set<TreeNodeVisitor<T, TN>>> retmap = new LinkedHashMap<TraversalControl, Set<TreeNodeVisitor<T, TN>>>();
    retmap.put( TraversalControl.CANCEL_TRAVERSAL, new LinkedHashSet<TreeNodeVisitor<T, TN>>() );
    retmap.put( TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS, new LinkedHashSet<TreeNodeVisitor<T, TN>>() );
    retmap.put( TraversalControl.SKIP_FURTHER_SIBLINGS, new LinkedHashSet<TreeNodeVisitor<T, TN>>() );
    
    //
    final Map<TraversalControl, Set<TreeNodeVisitor<T, TN>>> traversalControlToTreeNodeVisitorSetMap = new EnumMap<TraversalControl, Set<TreeNodeVisitor<T, TN>>>(
                                                                                                                                                                   TraversalControl.class );
    for ( TraversalControl traversalControl : TraversalControl.values() )
    {
      traversalControlToTreeNodeVisitorSetMap.put( traversalControl, new LinkedHashSet<TreeNodeVisitor<T, TN>>() );
    }
    
    //    
    final Set<TreeNodeVisitor<T, TN>> treeNodeVisitorTraversingSet = SetUtils.valueOf( treeNodeVisitors );
    for ( TreeNodeVisitor<T, TN> treeNodeVisitor : treeNodeVisitorTraversingSet )
    {
      //
      final TreeNavigator<T, TN> treeNavigator = this.fork();
      final TN treeNode = this.getCurrentTreeNode();
      TraversalControl traversalControl = null;
      try
      {
        traversalControl = treeNodeVisitor.visit( treeNode, treeNavigator );
      }
      catch ( Exception e )
      {
      }
      
      //
      traversalControl = ObjectUtils.defaultIfNull( traversalControl, defaultTraversalControl );
      if ( traversalControl != null )
      {
        traversalControlToTreeNodeVisitorSetMap.get( traversalControl ).add( treeNodeVisitor );
      }
    }
    
    //
    {
      //
      final Set<TreeNodeVisitor<T, TN>> treeNodeVisitorSet = traversalControlToTreeNodeVisitorSetMap.get( TraversalControl.GO_ON );
      
      //
      final TreeNavigator<T, TN> fork = this.fork();
      boolean navigationSuccessful = fork.navigateToFirstChild().isNavigationSuccessful();
      while ( navigationSuccessful )
      {
        //
        final Map<TraversalControl, Set<TreeNodeVisitor<T, TN>>> currentTraversalControlToTreeNodeVisitorSetMap = fork.traverse( defaultTraversalControl,
                                                                                                                                 visitedTreeNodeSet,
                                                                                                                                 treeNodeVisitorSet.toArray( new TreeNodeVisitor[0] ) );
        
        final Set<TreeNodeVisitor<T, TN>> cancelTreeNodeVisitorSet = currentTraversalControlToTreeNodeVisitorSetMap.get( TraversalControl.CANCEL_TRAVERSAL );
        final Set<TreeNodeVisitor<T, TN>> skipFurtherSiblingsTreeNodeVisitorSet = currentTraversalControlToTreeNodeVisitorSetMap.get( TraversalControl.SKIP_FURTHER_SIBLINGS );
        final Set<TreeNodeVisitor<T, TN>> skipChildrenAndFurtherSiblingsTreeNodeVisitorSet = currentTraversalControlToTreeNodeVisitorSetMap.get( TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS );
        
        //
        treeNodeVisitorSet.removeAll( cancelTreeNodeVisitorSet );
        treeNodeVisitorSet.removeAll( skipFurtherSiblingsTreeNodeVisitorSet );
        treeNodeVisitorSet.removeAll( skipChildrenAndFurtherSiblingsTreeNodeVisitorSet );
        
        //
        retmap.get( TraversalControl.CANCEL_TRAVERSAL ).addAll( cancelTreeNodeVisitorSet );
        
        //
        navigationSuccessful = fork.navigateToNextSibling().isNavigationSuccessful();
      }
    }
    {
      retmap.get( TraversalControl.CANCEL_TRAVERSAL )
            .addAll( traversalControlToTreeNodeVisitorSetMap.get( TraversalControl.CANCEL_TRAVERSAL ) );
      retmap.get( TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS )
            .addAll( traversalControlToTreeNodeVisitorSetMap.get( TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS ) );
      retmap.get( TraversalControl.SKIP_FURTHER_SIBLINGS )
            .addAll( traversalControlToTreeNodeVisitorSetMap.get( TraversalControl.SKIP_FURTHER_SIBLINGS ) );
    }
    
    //
    return retmap;
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
    final StringBuilder builder = new StringBuilder();
    
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
        final Object model = currentTreeNode.getModel();
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
  
  /**
   * @return the cachingChildrenOfPathNodes
   */
  public boolean isCachingChildrenOfPathNodes()
  {
    return this.cachingChildrenOfPathNodes;
  }
  
  /**
   * @param cachingChildrenOfPathNodes
   *          the cachingChildrenOfPathNodes to set
   */
  public void setCachingChildrenOfPathNodes( boolean cachingChildrenOfPathNodes )
  {
    this.cachingChildrenOfPathNodes = cachingChildrenOfPathNodes;
  }
  
  /**
   * @return the tree
   */
  public T getTree()
  {
    return this.tree;
  }
  
  /**
   * Returns a new {@link List} instance containing the current path of {@link TreeNode}s
   * 
   * @return
   */
  public List<TN> getTreeNodePathList()
  {
    return new ArrayList<TN>( this.treeNodePath.getTreeNodePathList() );
  }
}
