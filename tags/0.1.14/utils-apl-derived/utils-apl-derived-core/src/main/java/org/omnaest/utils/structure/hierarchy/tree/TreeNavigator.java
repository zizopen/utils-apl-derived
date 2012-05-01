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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.strings.StringUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor.TraversalConfiguration;
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
  protected final T                                   tree;
  
  /** Stores the current path through the {@link TreeNode}s. The last element is the current. */
  protected TreeNodePathAndCache                      treeNodePathAndCache       = new TreeNodePathAndCache();
  protected boolean                                   navigationSuccessful       = true;
  protected boolean                                   cachingChildrenOfPathNodes = true;
  
  protected TreeNodeTraversal<TreeNodeVisitor<T, TN>> treeNodeTraversal          = new TreeNodeTraversal<TreeNodeVisitor<T, TN>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TreeNavigator
   * @author Omnaest
   * @param <TN>
   */
  public static interface TreeNodeVisitor<T extends Tree<?, TN>, TN extends TreeNode>
  {
    /**
     * Controls the ongoing traversal of the {@link TreeNavigator}
     * 
     * @author Omnaest
     */
    public static enum TraversalControl
    {
      /** Goes on with an unchanged {@link TraversalConfiguration} */
      GO_ON,
      /** Changes the {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} permanently to true */
      GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES,
      /** Changes the {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} permanently to false */
      GO_ON_EXCLUDE_ALREADY_TRAVERSED_NODES,
      /** Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to true */
      GO_ON_INCLUDE_CHILDREN,
      /** Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to false */
      GO_ON_EXCLUDE_CHILDREN,
      /**
       * Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to true and the
       * {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} to true
       */
      GO_ON_INCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
      /**
       * Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to false and the
       * {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} to false
       */
      GO_ON_EXCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
      /**
       * Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to false and the
       * {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} to true
       */
      GO_ON_EXCLUDE_CHILDREN_AND_INCLUDE_ALREADY_TRAVERSED_NODES,
      /**
       * Changes the {@link TraversalConfiguration#isIncludingChildren()} permanently to true and the
       * {@link TraversalConfiguration#isIncludingAlreadyTraversedNodes()} to false
       */
      GO_ON_INCLUDE_CHILDREN_AND_EXCLUDE_ALREADY_TRAVERSED_NODES,
      /** Skips all children only for the current node */
      SKIP_CHILDREN,
      /** Skips further siblings of the current node */
      SKIP_FURTHER_SIBLINGS,
      /** Skips all children and further siblings of the current node */
      SKIP_CHILDREN_AND_FURTHER_SIBLINGS,
      /**
       * Cancels the traversal for the current {@link TreeNavigator.TreeNodeVisitor}. If multiple
       * {@link TreeNavigator.TreeNodeVisitor}s are specified the others are unaffected. Each
       * {@link TreeNavigator.TreeNodeVisitor} has to cancel the traversal for itself.
       */
      CANCEL_TRAVERSAL
    }
    
    /**
     * Configuration for the {@link TreeNavigator#traverse(TreeNodeVisitor)} methods. <br>
     * <br>
     * As default the current node is included and all children will be traversed, but no already traversed nodes will be visited.<br>
     * <br>
     * A {@link TraversalConfiguration} is immutable, to change single flags use the {@link #copy(Boolean, Boolean, Boolean)}
     * method to create a new {@link TraversalConfiguration} from the current one. <br>
     * <br>
     * The {@link TraversalConfiguration} implements {@link #equals(Object)} and {@link #hashCode()} using the internal flags to
     * compare. This results that two {@link TraversalConfiguration} instances are treated as equal, if their flag state is equal.
     * 
     * @see #isIncludingCurrentNode()
     * @see #isIncludingChildren()
     * @see #isIncludingAlreadyTraversedNodes()
     * @see #copy(Boolean, Boolean, Boolean)
     * @author Omnaest
     */
    public static class TraversalConfiguration
    {
      /* ********************************************** Variables ********************************************** */
      private boolean includingCurrentNode           = true;
      private boolean includingAlreadyTraversedNodes = false;
      private boolean includingChildren              = true;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * Copies the current {@link TraversalConfiguration} but uses all explicit given flags if they are not null. This means if a
       * flag should not be overridden it should be set to null.
       * 
       * @param includingCurrentNode
       * @param includingAlreadyTraversedNodes
       * @param includingChildren
       * @return new {@link TraversalConfiguration} instance
       */
      public TraversalConfiguration copy( Boolean includingCurrentNode,
                                          Boolean includingAlreadyTraversedNodes,
                                          Boolean includingChildren )
      {
        //
        boolean includingCurrentNodeMerged = ObjectUtils.defaultIfNull( includingCurrentNode, this.includingCurrentNode );
        boolean includingAlreadyTraversedNodesMerged = ObjectUtils.defaultIfNull( includingAlreadyTraversedNodes,
                                                                                  this.includingAlreadyTraversedNodes );
        boolean includingChildrenMerged = ObjectUtils.defaultIfNull( includingChildren, this.includingChildren );
        return new TraversalConfiguration( includingCurrentNodeMerged, includingAlreadyTraversedNodesMerged,
                                           includingChildrenMerged );
      }
      
      /**
       * @see TraversalConfiguration
       * @param includingCurrentNode
       * @param includingAlreadyTraversedNodes
       * @param includingChildren
       */
      public TraversalConfiguration( boolean includingCurrentNode, boolean includingAlreadyTraversedNodes,
                                     boolean includingChildren )
      {
        super();
        this.includingCurrentNode = includingCurrentNode;
        this.includingAlreadyTraversedNodes = includingAlreadyTraversedNodes;
        this.includingChildren = includingChildren;
      }
      
      /**
       * @see TraversalConfiguration
       */
      public TraversalConfiguration()
      {
        super();
      }
      
      /**
       * @return the includeCurrentNode
       */
      public boolean isIncludingCurrentNode()
      {
        return this.includingCurrentNode;
      }
      
      /**
       * @return the includeAlreadyTraversedNodes
       */
      public boolean isIncludingAlreadyTraversedNodes()
      {
        return this.includingAlreadyTraversedNodes;
      }
      
      /**
       * @return the includeChildren
       */
      public boolean isIncludingChildren()
      {
        return this.includingChildren;
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "TraversalConfiguration [includingCurrentNode=" );
        builder.append( this.includingCurrentNode );
        builder.append( ", includingAlreadyTraversedNodes=" );
        builder.append( this.includingAlreadyTraversedNodes );
        builder.append( ", includingChildren=" );
        builder.append( this.includingChildren );
        builder.append( "]" );
        return builder.toString();
      }
      
      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode()
      {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.includingAlreadyTraversedNodes ? 1231 : 1237 );
        result = prime * result + ( this.includingChildren ? 1231 : 1237 );
        result = prime * result + ( this.includingCurrentNode ? 1231 : 1237 );
        return result;
      }
      
      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals( Object obj )
      {
        if ( this == obj )
        {
          return true;
        }
        if ( obj == null )
        {
          return false;
        }
        if ( !( obj instanceof TraversalConfiguration ) )
        {
          return false;
        }
        TraversalConfiguration other = (TraversalConfiguration) obj;
        if ( this.includingAlreadyTraversedNodes != other.includingAlreadyTraversedNodes )
        {
          return false;
        }
        if ( this.includingChildren != other.includingChildren )
        {
          return false;
        }
        if ( this.includingCurrentNode != other.includingCurrentNode )
        {
          return false;
        }
        return true;
      }
      
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
    private final List<TN>            treeNodePathList                          = new ArrayList<TN>();
    private final List<Integer>       treeNodeIndexWithinParentChildrenListList = new ArrayList<Integer>();
    protected final Map<TN, List<TN>> treeNodeToChildrenListMap                 = new HashMap<TN, List<TN>>();
    
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
      retval.treeNodeIndexWithinParentChildrenListList.addAll( this.treeNodeIndexWithinParentChildrenListList );
      retval.treeNodeToChildrenListMap.putAll( this.treeNodeToChildrenListMap );
      
      //
      return retval;
    }
    
    /**
     * Adds a new {@link TreeNode} to the current tree
     * 
     * @param treeNode
     * @param indexWithinParentChildrenList
     */
    public void addTreeNodeToTreeNodePath( TN treeNode, Integer indexWithinParentChildrenList )
    {
      //
      if ( treeNode != null )
      {
        //
        this.treeNodePathList.add( treeNode );
        this.treeNodeIndexWithinParentChildrenListList.add( indexWithinParentChildrenList );
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
      ListUtils.removeLast( this.treeNodeIndexWithinParentChildrenListList );
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
      final int size = this.size();
      return size < 2 ? -1 : this.treeNodeIndexWithinParentChildrenListList.get( size - 1 );
    }
    
    /**
     * @return the treeNodePathList
     */
    public List<TN> getTreeNodePathList()
    {
      return this.treeNodePathList;
    }
    
  }
  
  /**
   * Reduced {@link TraversalControl} which affects the local behavior
   * 
   * @author Omnaest
   */
  protected static enum LocalAndReducedTraversalControl
  {
    /* ********************************************** Constants ********************************************** */
    SKIP_CHILDREN( TraversalControl.SKIP_CHILDREN, TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN, TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_INCLUDE_ALREADY_TRAVERSED_NODES, TraversalControl.CANCEL_TRAVERSAL ),
    SKIP_FURTHER_SIBLINGS( TraversalControl.SKIP_FURTHER_SIBLINGS, TraversalControl.SKIP_CHILDREN_AND_FURTHER_SIBLINGS,
        TraversalControl.CANCEL_TRAVERSAL ),
    EXCLUDE_ALREADY_TRAVERSED_NODES( TraversalControl.GO_ON_EXCLUDE_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_INCLUDE_CHILDREN_AND_EXCLUDE_ALREADY_TRAVERSED_NODES, TraversalControl.CANCEL_TRAVERSAL ),
    INCLUDE_ALREADY_TRAVERSED_NODES( TraversalControl.GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_INCLUDE_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_INCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES ),
    EXCLUDE_CHILDREN( TraversalControl.GO_ON_EXCLUDE_CHILDREN,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_EXCLUDE_CHILDREN_AND_INCLUDE_ALREADY_TRAVERSED_NODES, TraversalControl.CANCEL_TRAVERSAL ),
    INCLUDE_CHILDREN( TraversalControl.GO_ON_INCLUDE_CHILDREN,
        TraversalControl.GO_ON_INCLUDE_CHILDREN_AND_ALREADY_TRAVERSED_NODES,
        TraversalControl.GO_ON_INCLUDE_CHILDREN_AND_EXCLUDE_ALREADY_TRAVERSED_NODES );
    
    /* ********************************************** Variables ********************************************** */
    private final TraversalControl[] traversalControls;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see LocalAndReducedTraversalControl
     * @param traversalControls
     */
    private LocalAndReducedTraversalControl( TraversalControl... traversalControls )
    {
      this.traversalControls = traversalControls;
    }
    
    /**
     * @return the traversalControls
     */
    public TraversalControl[] getTraversalControls()
    {
      return this.traversalControls;
    }
    
    /**
     * @return {@link Set} of {@link TraversalControl} instances
     */
    public Set<TraversalControl> getTraversalControlSet()
    {
      return SetUtils.valueOf( this.traversalControls );
    }
    
  }
  
  /**
   * Includes the logic to traverse {@link TreeNode}s
   * 
   * @author Omnaest
   */
  protected class TreeNodeTraversal<TNV extends TreeNodeVisitor<T, TN>>
  {
    /**
     * @param defaultTraversalConfiguration
     * @param treeNodeVisitors
     */
    public void traverse( TraversalConfiguration defaultTraversalConfiguration, TNV... treeNodeVisitors )
    {
      //
      for ( TNV treeNodeVisitor : treeNodeVisitors )
      {
        //
        final Set<TN> visitedTreeNodeSet = new HashSet<TN>();
        final ElementHolder<TraversalConfiguration> traversalConfigurationHolder = new ElementHolder<TraversalConfiguration>(
                                                                                                                              defaultTraversalConfiguration );
        this.traverse( treeNodeVisitor, traversalConfigurationHolder, visitedTreeNodeSet );
      }
    }
    
    /**
     * @param treeNodeVisitor
     * @param traversalConfiguration
     * @returns false if further siblings should be skipped
     */
    protected boolean traverse( TNV treeNodeVisitor,
                                ElementHolder<TraversalConfiguration> traversalConfigurationHolder,
                                Set<TN> visitedTreeNodeSet )
    {
      //
      boolean retval = false;
      
      //    
      final TN treeNode = this.getCurrentTreeNode();
      final TraversalConfiguration traversalConfiguration = traversalConfigurationHolder.getElement();
      
      //
      final boolean passesIncludingAlreadyTraversedNodes;
      final boolean passesIncludingCurrentNode;
      {
        //
        final boolean includingAlreadyTraversedNodes = traversalConfiguration.isIncludingAlreadyTraversedNodes();
        final boolean includingCurrentNode = traversalConfiguration.isIncludingCurrentNode();
        
        //
        passesIncludingAlreadyTraversedNodes = includingAlreadyTraversedNodes || !visitedTreeNodeSet.contains( treeNode );
        passesIncludingCurrentNode = includingCurrentNode || !visitedTreeNodeSet.isEmpty();
      }
      
      //
      if ( passesIncludingAlreadyTraversedNodes )
      {
        //
        TraversalControl traversalControl = null;
        if ( passesIncludingCurrentNode )
        {
          //
          final TreeNavigator<T, TN> treeNavigator = this.getTreeNavigatorFork();
          
          try
          {
            //
            traversalControl = treeNodeVisitor.visit( treeNode, treeNavigator );
            
            //            
            visitedTreeNodeSet.add( treeNode );
          }
          catch ( Exception e )
          {
          }
          
          //
          if ( traversalControl != null && !TraversalControl.GO_ON.equals( traversalControl ) )
          {
            //
            final Boolean includingCurrentNode = null;
            
            Boolean includingAlreadyTraversedNodes = null;
            Boolean includingChildren = null;
            
            //
            includingAlreadyTraversedNodes = LocalAndReducedTraversalControl.EXCLUDE_ALREADY_TRAVERSED_NODES.getTraversalControlSet()
                                                                                                            .contains( traversalControl ) ? Boolean.FALSE
                                                                                                                                         : includingAlreadyTraversedNodes;
            includingAlreadyTraversedNodes = LocalAndReducedTraversalControl.INCLUDE_ALREADY_TRAVERSED_NODES.getTraversalControlSet()
                                                                                                            .contains( traversalControl ) ? Boolean.TRUE
                                                                                                                                         : includingAlreadyTraversedNodes;
            
            //
            includingChildren = LocalAndReducedTraversalControl.EXCLUDE_CHILDREN.getTraversalControlSet()
                                                                                .contains( traversalControl ) ? Boolean.FALSE
                                                                                                             : includingChildren;
            includingChildren = LocalAndReducedTraversalControl.INCLUDE_CHILDREN.getTraversalControlSet()
                                                                                .contains( traversalControl ) ? Boolean.TRUE
                                                                                                             : includingChildren;
            
            //
            traversalConfigurationHolder.setElement( traversalConfiguration.copy( includingCurrentNode,
                                                                                  includingAlreadyTraversedNodes,
                                                                                  includingChildren ) );
          }
        }
        
        //
        {
          //
          final boolean skipChildren = LocalAndReducedTraversalControl.SKIP_CHILDREN.getTraversalControlSet()
                                                                                    .contains( traversalControl );
          final boolean skipFurtherSiblings = LocalAndReducedTraversalControl.SKIP_FURTHER_SIBLINGS.getTraversalControlSet()
                                                                                                   .contains( traversalControl );
          
          //
          if ( !skipChildren )
          {
            this.traverseThroughChildren( treeNodeVisitor, traversalConfigurationHolder, visitedTreeNodeSet );
          }
          
          //
          retval = !skipFurtherSiblings;
        }
      }
      
      //
      return retval;
    }
    
    /**
     * @return
     */
    private TN getCurrentTreeNode()
    {
      return TreeNavigator.this.getCurrentTreeNode();
    }
    
    /**
     * @param treeNodeVisitor
     * @param traversalConfiguration
     * @param visitedTreeNodeSet
     */
    private void traverseThroughChildren( final TNV treeNodeVisitor,
                                          final ElementHolder<TraversalConfiguration> traversalConfigurationHolder,
                                          final Set<TN> visitedTreeNodeSet )
    {
      //
      if ( treeNodeVisitor != null && traversalConfigurationHolder != null && visitedTreeNodeSet != null )
      {
        //
        final TreeNavigator<T, TN> fork = this.getTreeNavigatorFork();
        boolean navigationSuccessful = fork.navigateToFirstChild().isNavigationSuccessful();
        boolean skipFurtherSiblings = false;
        while ( navigationSuccessful && !skipFurtherSiblings )
        {
          //
          skipFurtherSiblings = !fork.treeNodeTraversal.traverse( treeNodeVisitor, traversalConfigurationHolder,
                                                                  visitedTreeNodeSet );
          
          //          
          navigationSuccessful = fork.navigateToNextSibling().isNavigationSuccessful();
        }
      }
    }
    
    /**
     * @return
     */
    private TreeNavigator<T, TN> getTreeNavigatorFork()
    {
      return TreeNavigator.this.fork();
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
   * @param tree
   * @throws IllegalArgumentException
   *           if the {@link Tree} reference is null
   */
  public TreeNavigator( T tree )
  {
    super();
    this.tree = tree;
    
    this.treeNodePathAndCache.addTreeNodeToTreeNodePath( tree.getTreeRootNode(), -1 );
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
    this.treeNodePathAndCache = treeNodePath;
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
    return new TreeNavigator<T, TN>( this.tree, this.treeNodePathAndCache.fork(), this.cachingChildrenOfPathNodes );
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
    int index = this.treeNodePathAndCache.determineAndCacheChildrenListOfCurrentTreeNode().size() - 1;
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
    List<TN> childrenList = this.treeNodePathAndCache.getChildrenListOfCurrentTreeNode();
    TN element = ListUtils.elementAt( childrenList, index );
    if ( element != null )
    {
      this.treeNodePathAndCache.addTreeNodeToTreeNodePath( element, index );
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
    final int indexPosition = this.treeNodePathAndCache.determineIndexPositionOfCurrentTreeNodeWithinTheParentChildrenList();
    if ( indexPosition >= 0 )
    {
      //
      final List<TN> childrenListOfParent = this.treeNodePathAndCache.getChildrenListOfParent();
      
      //
      int newIndexPosition = indexPosition - relativeIndexPosition;
      if ( newIndexPosition >= 0 && newIndexPosition < childrenListOfParent.size() )
      {
        //
        TN treeNode = childrenListOfParent.get( newIndexPosition );
        this.treeNodePathAndCache.removeLastTreeNodeAndClearUnusedCachedChildrenLists();
        this.treeNodePathAndCache.addTreeNodeToTreeNodePath( treeNode, newIndexPosition );
        
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
      this.treeNodePathAndCache.removeLastTreeNodeAndClearUnusedCachedChildrenLists();
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
    return this.treeNodePathAndCache.size() > 1;
  }
  
  /**
   * Returns true if the current {@link TreeNode#getChildrenList()} is not empty
   * 
   * @return
   */
  public boolean hasChildren()
  {
    final List<TN> childrenListOfCurrentTreeNode = this.treeNodePathAndCache.getChildrenListOfCurrentTreeNode();
    return childrenListOfCurrentTreeNode != null && !childrenListOfCurrentTreeNode.isEmpty();
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
   * {@link #traverse(TraversalConfiguration, TreeNodeVisitor)}
   * 
   * @see #traverse(TraversalConfiguration, TreeNodeVisitor)
   * @param treeNodeVisitors
   *          {@link TreeNodeVisitor}
   * @return this
   */
  public TreeNavigator<T, TN> traverse( TreeNodeVisitor<T, TN>... treeNodeVisitors )
  {
    return this.traverse( new TraversalConfiguration(), treeNodeVisitors );
  }
  
  /**
   * @see #traverse(TreeNodeVisitor...)
   * @param treeNodeVisitor
   * @return
   */
  @SuppressWarnings("unchecked")
  public TreeNavigator<T, TN> traverse( TreeNodeVisitor<T, TN> treeNodeVisitor )
  {
    return this.traverse( new TreeNodeVisitor[] { treeNodeVisitor } );
  }
  
  /**
   * Similar to {@link #traverse(TraversalConfiguration, TreeNodeVisitor)}
   * 
   * @param defaultTraversalConfiguration
   * @param treeNodeVisitor
   * @return
   */
  @SuppressWarnings("unchecked")
  public TreeNavigator<T, TN> traverse( TraversalConfiguration defaultTraversalConfiguration,
                                        TreeNodeVisitor<T, TN> treeNodeVisitor )
  {
    return this.traverse( defaultTraversalConfiguration, new TreeNodeVisitor[] { treeNodeVisitor } );
  }
  
  /**
   * Similar to {@link #traverse(TreeNodeVisitor...)} but allows to specify a default {@link TraversalConfiguration}. <br>
   * <br>
   * The default {@link TraversalConfiguration} is used initially for all {@link TreeNodeVisitor} instances. If any
   * {@link TreeNodeVisitor} instance returns a {@link TraversalControl} flag, the {@link TraversalConfiguration} of the
   * particular node is adapted while the other {@link TreeNodeVisitor} will keep their {@link TraversalConfiguration}. <br>
   * <br>
   * The {@link TraversalConfiguration} defines which nodes are traversed.
   * 
   * @see #traverse(TreeNodeVisitor...)
   * @param defaultTraversalConfiguration
   *          {@link TraversalConfiguration}
   * @param treeNodeVisitors
   *          {@link TreeNodeVisitor}
   * @return this
   */
  public TreeNavigator<T, TN> traverse( TraversalConfiguration defaultTraversalConfiguration,
                                        TreeNodeVisitor<T, TN>... treeNodeVisitors )
  {
    //
    this.treeNodeTraversal.traverse( defaultTraversalConfiguration, treeNodeVisitors );
    
    //
    return this;
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
    return this.treeNodePathAndCache.getCurrentTreeNode();
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
        
        //
        builder.append( indentionString );
        builder.append( "|--" );
        builder.append( String.valueOf( model ) );
        builder.append( StringUtils.DEFAULT_LINESEPARATOR );
        
        //
        alreadyTraversedNode = alreadyTraversedModelSet.contains( model );
        if ( !alreadyTraversedNode )
        {
          alreadyTraversedModelSet.add( model );
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
    return new ArrayList<TN>( this.treeNodePathAndCache.getTreeNodePathList() );
  }
  
}
