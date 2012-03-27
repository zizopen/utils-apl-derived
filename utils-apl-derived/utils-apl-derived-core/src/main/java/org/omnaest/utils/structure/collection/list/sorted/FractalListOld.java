/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.structure.collection.list.sorted;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.omnaest.utils.structure.collection.list.sorted.FractalListOld.NodeVisitor.TraversalHint;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.hierarchy.tree.Tree;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator;
import org.omnaest.utils.structure.hierarchy.tree.TreeNode;

/**
 * !!!! PLEASE USE {@link TreeList} instead !!!! <br>
 * <br>
 * {@link SortedList} implementation using linked elements which are linked with multiple partners which are in an exponential
 * increasing distance. The ranges between those exponential distances are filled with another {@link FractalListOld}. This allows
 * the {@link List} to grow in a fractal manner. <br>
 * <br>
 * Unfortunately this implementation is much worse than its sister implementation {@link TreeList}.
 * 
 * @see List
 * @see SortedList
 * @author Omnaest
 * @param <E>
 */
public class FractalListOld<E> extends SortedListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 1677750838884402008L;
  
  /* ********************************************** Variables ********************************************** */
  protected Node<E>         rootNode         = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   * @param <E>
   */
  @SuppressWarnings("hiding")
  protected class Node<E> implements Iterable<Node<E>>
  {
    /* ********************************************** Variables ********************************************** */
    protected Node<E>                nextNode            = null;
    protected SortedSplitableList<E> sortedSplitableList = newSortedList();
    protected int                    size                = 0;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Node
     * @param element
     */
    public Node( E element )
    {
      super();
      this.add( element );
    }
    
    /**
     * @see Node
     * @param nextNode
     * @param sortedSplitableList
     */
    public Node( Node<E> nextNode, SortedSplitableList<E> sortedSplitableList )
    {
      super();
      this.nextNode = nextNode;
      this.sortedSplitableList.addAll( sortedSplitableList );
      this.size = sortedSplitableList.size();
    }
    
    /**
     * @return
     */
    private SortedSplitableList<E> newSortedList()
    {
      return this.newSortedList( null );
    }
    
    /**
     * @return
     */
    private SortedSplitableList<E> newSortedList( SortedList<E> sortedList )
    {
      //
      final SortedList<E> firstList = new TreeList<E>();
      final SortedList<E> secondList = new FractalListOld<E>( sortedList );
      final int exceedThreshold = 16;
      final int dropUnderThreshold = 1;
      return new SortedListDispatcherSizeBased<E>( firstList, secondList, exceedThreshold, dropUnderThreshold );
    }
    
    /**
     * @param element
     */
    public void add( E element )
    {
      //
      this.sortedSplitableList.add( element );
      this.size++;
      
      //
      final double factor = 0.5;
      final int threshold = (int) Math.round( factor * FractalListOld.this.size() );
      while ( this.size >= 6 && this.size >= threshold )
      {
        //
        final int splitIndexPosition = this.sortedSplitableList.size() / 2;
        final SortedList<E> sortedListTail = this.sortedSplitableList.splitAt( splitIndexPosition );
        
        //
        final Node<E> node = new Node<E>( this.nextNode, (SortedSplitableList<E>) sortedListTail );
        this.nextNode = node;
        
        //
        this.size -= node.getSize();
        
        //
        {
          final Node<E> nextNode = this.nextNode.nextNode;
          if ( nextNode != null && nextNode.getSize() + sortedListTail.size() < threshold / 2 )
          {
            node.assimilateNextNodeAndPutElementsInto();
          }
        }
        
      }
    }
    
    protected void assimilateNextNodeAndPutElementsInto()
    {
      //
      final Node<E> nextNode = this.nextNode;
      final Node<E> overnextNode = nextNode != null ? nextNode.getNextNode() : null;
      
      //
      this.sortedSplitableList.addAll( nextNode.sortedSplitableList );
      nextNode.sortedSplitableList.clear();
      
      //
      this.nextNode = overnextNode;
    }
    
    /**
     * Returns the first element this node is based on. If this node does not contain any element it will return null
     * 
     * @return
     */
    public E getFirstElement()
    {
      return this.sortedSplitableList.first();
    }
    
    /**
     * @return the nextNode
     */
    public Node<E> getNextNode()
    {
      return this.nextNode;
    }
    
    /**
     * @return the size
     */
    public int getSize()
    {
      return this.size;
    }
    
    /**
     * @param nextNode
     *          the nextNode to set
     */
    public void setNextNode( Node<E> nextNode )
    {
      this.nextNode = nextNode;
    }
    
    @Override
    public Iterator<Node<E>> iterator()
    {
      return new Iterator<Node<E>>()
      {
        /* ********************************************** Variables ********************************************** */
        protected Node<E> node = Node.this;
        
        /* ********************************************** Methods ********************************************** */
        
        @Override
        public boolean hasNext()
        {
          return this.node != null;
        }
        
        @Override
        public Node<E> next()
        {
          //         
          Node<E> retval = this.node;
          
          //
          this.node = this.node.getNextNode();
          
          //
          return retval;
        }
        
        @Override
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
    /**
     * @param index
     * @return
     * @see java.util.List#get(int)
     */
    public E getElementAt( int index )
    {
      return this.sortedSplitableList.get( index );
    }
    
    /**
     * @param index
     * @return
     * @see java.util.List#remove(int)
     */
    public E removeElementAt( int index )
    {
      //
      final E retval = this.sortedSplitableList.remove( index );
      this.size = Math.max( 0, this.size - 1 );
      
      //
      return retval;
    }
    
    @Override
    public String toString()
    {
      //
      final class Model
      {
        /* ********************************************** Variables ********************************************** */
        protected final Node<E> node;
        
        /* ********************************************** Methods ********************************************** */
        /**
         * @see Model
         * @param node
         */
        public Model( Node<E> node )
        {
          super();
          this.node = node;
        }
        
        private int size()
        {
          return this.node.getSize();
        }
        
        private List<E> getSortedList()
        {
          return this.node.sortedSplitableList;
        }
        
        private String getType()
        {
          //
          String retval = "";
          
          //
          List<E> sortedList = this.getSortedList();
          while ( sortedList != null )
          {
            //
            retval += ">" + sortedList.getClass().getSimpleName();
            
            //
            if ( sortedList instanceof SortedListDispatcherSizeBased )
            {
              SortedListDispatcherSizeBased<E> sortedListDispatcherSizeBased = (SortedListDispatcherSizeBased<E>) sortedList;
              sortedList = sortedListDispatcherSizeBased.getList();
            }
            else
            {
              sortedList = null;
            }
          }
          
          //
          return retval;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
          StringBuilder builder = new StringBuilder();
          builder.append( "Model [size()=" );
          builder.append( this.size() );
          builder.append( ", getSortedList()=" );
          builder.append( this.getSortedList() );
          builder.append( ", getType()=" );
          builder.append( this.getType() );
          builder.append( "]" );
          return builder.toString();
        }
        
      }
      
      Tree<Void, TreeNode<Model>> tree = new Tree<Void, TreeNode<Model>>()
      {
        final class TreeNodeAdapterInternalList implements TreeNode<Model>
        {
          /* ********************************************** Variables ********************************************** */
          protected final Node<E> node;
          
          /* ********************************************** Methods ********************************************** */
          
          /**
           * @see TreeNodeAdapterInternalList
           * @param node
           */
          public TreeNodeAdapterInternalList( Node<E> node )
          {
            super();
            this.node = node;
          }
          
          @Override
          public Model getModel()
          {
            // 
            return new Model( this.node );
          }
          
          @SuppressWarnings("unchecked")
          @Override
          public List<TreeNode<Model>> getChildrenList()
          {
            //
            final List<TreeNode<Model>> retlist = new ArrayList<TreeNode<Model>>();
            
            //
            SortedSplitableList<E> sortedSplitableList = this.node.sortedSplitableList;
            if ( sortedSplitableList instanceof SortedListDispatcherSizeBased )
            {
              final SortedListDispatcherSizeBased<E> sortedListDispatcherSizeBased = (SortedListDispatcherSizeBased<E>) sortedSplitableList;
              SortedSplitableList<E> internalSortedSplitableList = sortedListDispatcherSizeBased.getList();
              if ( internalSortedSplitableList instanceof FractalListOld )
              {
                FractalListOld<E> FractalListOld = (FractalListOld<E>) internalSortedSplitableList;
                retlist.add( new TreeNodeAdapterSiblings( (Node<E>) FractalListOld.rootNode ) );
              }
            }
            
            //
            return retlist;
          }
        }
        
        final class TreeNodeAdapterSiblings implements TreeNode<Model>
        {
          /* ********************************************** Variables ********************************************** */
          protected final Node<E> node;
          
          /* ********************************************** Methods ********************************************** */
          
          /**
           * @see TreeNodeAdapterInternalList
           * @param node
           */
          public TreeNodeAdapterSiblings( Node<E> node )
          {
            super();
            this.node = node;
          }
          
          @Override
          public Model getModel()
          {
            return new Model( this.node );
          }
          
          @Override
          public List<TreeNode<Model>> getChildrenList()
          {
            //
            final List<TreeNode<Model>> retlist = new ArrayList<TreeNode<Model>>();
            
            //
            Node<E> currentNode = this.node;
            while ( currentNode != null )
            {
              retlist.add( new TreeNodeAdapterInternalList( currentNode ) );
              currentNode = currentNode.nextNode;
            }
            
            //
            return retlist;
          }
        }
        
        @Override
        public Void getModel()
        {
          return null;
        }
        
        @Override
        public TreeNode<Model> getTreeRootNode()
        {
          //
          return new TreeNodeAdapterSiblings( Node.this );
        }
      };
      TreeNavigator<Tree<Void, TreeNode<Model>>, TreeNode<Model>> treeNavigator = new TreeNavigator<Tree<Void, TreeNode<Model>>, TreeNode<Model>>(
                                                                                                                                                   tree );
      
      StringBuilder builder = new StringBuilder();
      builder.append( "Node [" );
      builder.append( "sortedSplitableList=" );
      builder.append( this.sortedSplitableList );
      builder.append( "model=\n" );
      builder.append( treeNavigator.toString() );
      builder.append( ", size=" );
      builder.append( this.size );
      //      builder.append( ", nextNode=\n" );
      //      builder.append( this.nextNode );
      builder.append( "\n]" );
      return builder.toString();
    }
    
    /**
     * @param o
     * @return
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public int indexOf( Object o )
    {
      return this.sortedSplitableList.indexOf( o );
    }
    
    /**
     * @param o
     * @return
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    public int lastIndexOf( Object o )
    {
      return this.sortedSplitableList.lastIndexOf( o );
    }
    
  }
  
  /**
   * @see FractalListOld#traverseNodes(NodeVisitor)
   * @author Omnaest
   * @param <E>
   */
  protected static interface NodeVisitor<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * @see NodeVisitor
     * @author Omnaest
     */
    public static enum TraversalHint
    {
      CANCEL,
      JUMP_TO_NEXT_NODE
    }
    
    /**
     * @see Node
     * @param node
     * @return {@link TraversalHint}
     */
    @SuppressWarnings("javadoc")
    public TraversalHint visit( FractalListOld<E>.Node<E> node );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FractalListOld
   */
  public FractalListOld()
  {
    super();
  }
  
  /**
   * @see FractalListOld
   * @param collection
   * @param comparator
   */
  public FractalListOld( Collection<E> collection, Comparator<E> comparator )
  {
    super( comparator );
    this.addAll( collection );
  }
  
  /**
   * @see FractalListOld
   * @param collection
   */
  public FractalListOld( Collection<E> collection )
  {
    super();
    this.addAll( collection );
  }
  
  /**
   * @see FractalListOld
   * @param comparator
   */
  public FractalListOld( Comparator<E> comparator )
  {
    super( comparator );
  }
  
  /**
   * Traverses the available {@link Node} using a {@link NodeVisitor}
   * 
   * @param nodeVisitor
   */
  protected void traverseNodes( NodeVisitor<E> nodeVisitor )
  {
    //    
    if ( this.rootNode != null )
    {
      for ( Node<E> node : this.rootNode )
      {
        //      
        final TraversalHint traversalHint = nodeVisitor.visit( node );
        if ( !TraversalHint.JUMP_TO_NEXT_NODE.equals( traversalHint ) )
        {
          break;
        }
      }
    }
  }
  
  @Override
  public int size()
  {
    //
    final AtomicInteger retval = new AtomicInteger();
    
    //    
    final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
    {
      @Override
      public TraversalHint visit( Node<E> node )
      {
        retval.addAndGet( node.getSize() );
        return TraversalHint.JUMP_TO_NEXT_NODE;
      }
    };
    this.traverseNodes( nodeVisitor );
    
    //
    return retval.get();
  }
  
  @Override
  public boolean add( final E element )
  {
    //
    boolean retval = false;
    
    //
    if ( element != null )
    {
      if ( this.rootNode == null )
      {
        this.rootNode = new Node<E>( element );
      }
      else
      {
        //
        final ElementHolder<Node<E>> lastSmallerNodeHolder = new ElementHolder<Node<E>>();
        final AtomicInteger sizeThreshold = new AtomicInteger( 1 );
        final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
        {
          @Override
          public TraversalHint visit( Node<E> node )
          {
            //
            TraversalHint traversalHint = TraversalHint.JUMP_TO_NEXT_NODE;
            
            //
            final E firstElement = node.getFirstElement();
            final int compare = FractalListOld.this.comparator.compare( element, firstElement );
            if ( compare < 0 )
            {
              traversalHint = TraversalHint.CANCEL;
            }
            else
            {
              lastSmallerNodeHolder.setElement( node );
              sizeThreshold.addAndGet( sizeThreshold.get() );
            }
            
            //
            return traversalHint;
          }
        };
        this.traverseNodes( nodeVisitor );
        
        //
        @SuppressWarnings("unchecked")
        final Node<E> lastSmallerNode = ObjectUtils.defaultIfNull( lastSmallerNodeHolder.getElement(), this.rootNode );
        lastSmallerNode.add( element );
      }
      
      //
      retval = true;
    }
    
    //
    return retval;
  }
  
  @Override
  public E get( final int index )
  {
    //
    final ElementHolder<E> retval = new ElementHolder<E>();
    
    //
    final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
    {
      private int currentIndexPosition = 0;
      
      @Override
      public TraversalHint visit( Node<E> node )
      {
        //
        TraversalHint traversalHint = TraversalHint.CANCEL;
        
        //
        final int size = node.getSize();
        final int requestedIndexPositionRelativeToCurrentNode = index - this.currentIndexPosition;
        
        if ( requestedIndexPositionRelativeToCurrentNode >= 0 )
        {
          //
          traversalHint = TraversalHint.JUMP_TO_NEXT_NODE;
          
          //
          if ( requestedIndexPositionRelativeToCurrentNode < size )
          {
            //
            retval.setElement( node.getElementAt( requestedIndexPositionRelativeToCurrentNode ) );
            traversalHint = TraversalHint.CANCEL;
          }
          
          //
          this.currentIndexPosition += size;
        }
        
        //
        return traversalHint;
      }
    };
    this.traverseNodes( nodeVisitor );
    
    // 
    return retval.getElement();
  }
  
  @Override
  public E remove( final int index )
  {
    //
    final ElementHolder<E> retval = new ElementHolder<E>();
    
    //
    final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
    {
      private int currentIndexPosition = 0;
      
      @Override
      public TraversalHint visit( Node<E> node )
      {
        //
        TraversalHint traversalHint = TraversalHint.CANCEL;
        
        //
        final int size = node.getSize();
        final int requestedIndexPositionRelativeToCurrentNode = index - this.currentIndexPosition;
        
        if ( requestedIndexPositionRelativeToCurrentNode >= 0 )
        {
          //
          traversalHint = TraversalHint.JUMP_TO_NEXT_NODE;
          
          //
          if ( requestedIndexPositionRelativeToCurrentNode < size )
          {
            //
            retval.setElement( node.removeElementAt( requestedIndexPositionRelativeToCurrentNode ) );
            traversalHint = TraversalHint.CANCEL;
          }
          
          //
          this.currentIndexPosition += size;
        }
        
        //
        return traversalHint;
      }
    };
    this.traverseNodes( nodeVisitor );
    
    //
    {
      Node<E> currentNode = this.rootNode;
      while ( currentNode != null )
      {
        //
        final Node<E> nextNode = currentNode.getNextNode();
        if ( nextNode != null && nextNode.getSize() == 0 )
        {
          currentNode.setNextNode( nextNode.getNextNode() );
        }
        
        //
        currentNode = currentNode.getNextNode();
      }
    }
    
    // 
    return retval.getElement();
  }
  
  @Override
  public int indexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retval = new ElementHolder<Integer>( -1 );
    
    //
    final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
    {
      private int currentIndexPosition = 0;
      
      @Override
      public TraversalHint visit( Node<E> node )
      {
        //
        TraversalHint traversalHint = TraversalHint.JUMP_TO_NEXT_NODE;
        
        //
        final int indexOf = node.indexOf( object );
        if ( indexOf >= 0 )
        {
          retval.setElement( this.currentIndexPosition + indexOf );
          traversalHint = TraversalHint.CANCEL;
        }
        else
        {
          //
          this.currentIndexPosition += node.getSize();
        }
        
        //
        return traversalHint;
      }
    };
    this.traverseNodes( nodeVisitor );
    
    // 
    return retval.getElement();
  }
  
  @Override
  public int lastIndexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retval = new ElementHolder<Integer>( -1 );
    
    //
    final NodeVisitor<E> nodeVisitor = new NodeVisitor<E>()
    {
      private int currentIndexPosition        = 0;
      private int lastDeterminedIndexPosition = -1;
      
      @Override
      public TraversalHint visit( Node<E> node )
      {
        //
        TraversalHint traversalHint = TraversalHint.JUMP_TO_NEXT_NODE;
        
        //
        final int indexOf = node.lastIndexOf( object );
        if ( indexOf >= 0 )
        {
          this.lastDeterminedIndexPosition = this.currentIndexPosition + indexOf;
          retval.setElement( this.lastDeterminedIndexPosition );
        }
        else if ( this.lastDeterminedIndexPosition >= 0 )
        {
          traversalHint = TraversalHint.CANCEL;
        }
        else
        {
          //
          this.currentIndexPosition += node.getSize();
        }
        
        //
        return traversalHint;
      }
    };
    this.traverseNodes( nodeVisitor );
    
    // 
    return retval.getElement();
  }
  
  @Override
  protected SortedList<E> newInstance( Collection<E> collection )
  {
    //
    final SortedList<E> retlist = new FractalListOld<E>();
    retlist.addAll( collection );
    return retlist;
  }
  
  @Override
  public void clear()
  {
    this.rootNode = null;
  }
  
}
