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
import java.util.List;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.list.decorator.ListDecorator;
import org.omnaest.utils.structure.collection.list.sorted.TreeList.ElementVisitor.TraversalHint;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.ElementHolderUnmodifiable;
import org.omnaest.utils.structure.element.accessor.Accessor;
import org.omnaest.utils.structure.element.accessor.AccessorReadable;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.iterator.ChainedListIterator;

import com.google.common.collect.TreeMultiset;

/**
 * This sorted {@link List} implementation does use an internal {@link TreeMap} to provide all features a {@link SortedSet} has,
 * except the fact that duplicate elements are allowed. <br>
 * <br>
 * But be aware, that the order of elements, which are considered equal due to the {@link Comparator#compare(Object, Object)}
 * method, is not determined. <br>
 * <br>
 * The {@link TreeList} has a quite fast {@link #add(Object)} method which uses the advantages of the underlying {@link TreeMap}.
 * All index based operations like {@link #get(int)} or {@link #remove(int)} are slow for large {@link List}s with many different
 * elements, since all elements of the {@link TreeMap} have to be traversed to identify the right index position. <br>
 * <br>
 * The {@link TreeList} does break the contract of the {@link List#add(Object)}, as well as the {@link List#add(int, Object)} in
 * the way, that it does insert new elements at the appropriate sort position. <br>
 * <br>
 * <br>
 * The {@link #listIterator()} does currently not support {@link ListIterator#nextIndex()} and
 * {@link ListIterator#previousIndex()} and throws {@link UnsupportedOperationException} in the case calling those methods. <br>
 * <br>
 * Performance is about 2x-3x slower compared to a {@link TreeSet} and quite similar to the performance of the
 * {@link TreeMultiset}.<br>
 * <br>
 * The following example is based on
 * <ul>
 * <li>10000 iterations adding a value from 100 random generated {@link String}s</li>
 * <li>checking further 10000 times for {@link #contains(Object)}</li>
 * <li>iterating over all elements</li>
 * </ul>
 * for each sample. <br>
 * <br>
 * ( Of course a {@link TreeList} and a {@link TreeMultiset} will contain about 100 times more elements than the {@link TreeSet}
 * since they allow duplicates )
 * 
 * <pre>
 * --- TreeList ---
 * samples: 50
 * max:     640
 * average: 187.08
 * median:  167
 * 
 * --- TreeSet ---
 * samples: 50
 * max:     280
 * average: 80.08
 * median:  75
 * 
 * --- TreeMultiSet ---
 * samples: 50
 * max:     862
 * average: 182.2
 * median:  163
 * 
 * (durations in milliseconds)
 * </pre>
 * 
 * @see SortedList
 * @author Omnaest
 * @param <E>
 */
public class TreeList<E> extends SortedListAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long                                 serialVersionUID = -3181777537000872260L;
  /* ********************************************** Variables ********************************************** */
  private final SortedMap<AccessorReadable<E>, ElementList> accessorToElementListMap;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see TreeList
   * @see TreeList#visitElements(ElementVisitor)
   * @see TraversalHint
   * @author Omnaest
   * @param <E>
   */
  protected static interface ElementVisitor<E>
  {
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    /**
     * A {@link TraversalHint} affects the traversal over the elements of the {@link TreeList}
     * 
     * @see ElementVisitor
     * @author Omnaest
     */
    public static enum TraversalHint
    {
      GO_ON,
      SKIP_SUBLIST,
      CANCEL_TRAVERSAL
    }
    
    /* ********************************************** Methods ********************************************** */
    /**
     * This method is called before an {@link ElementList} is traversed by the {@link #visitElement(Object, int, List, int)}
     * method.<br>
     * <br>
     * If this function returns {@link TraversalHint#SKIP_SUBLIST} the {@link ElementList} is skipped and this method will be
     * called for the next available {@link ElementList}
     * 
     * @param indexPosition
     * @param elementListSize
     * @param elementList
     * @return {@link TraversalHint}
     */
    @SuppressWarnings("javadoc")
    public TraversalHint beforeTraversalOfElementList( int indexPosition, int elementListSize, List<E> elementList );
    
    /**
     * This method is called for each visited element of a sub {@link List} of the internal {@link SortedMap#values()}<br>
     * <br>
     * 
     * @param element
     * @param indexPosition
     * @param elementList
     * @param elementListIndexPosition
     *          relative index position within the {@link ElementList} which is currently traversed
     * @return {@link TraversalHint}
     */
    @SuppressWarnings("javadoc")
    public TraversalHint visitElement( E element, int indexPosition, List<E> elementList, int elementListIndexPosition );
  }
  
  /**
   * An {@link ElementList} is based on an regular {@link ArrayList} but additionally removes itself from the underlying
   * {@link SortedMap} if its last element is removed and the {@link List} is going to get empty.<br>
   * <br>
   * And it represents an {@link AccessorReadable} for the first element of the {@link List} to allow sorting.
   * 
   * @author Omnaest
   */
  protected class ElementList extends ListDecorator<E> implements AccessorReadable<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long   serialVersionUID = 3764571149202052105L;
    private AccessorReadable<E> accessorReadable = new AccessorReadable<E>()
                                                 {
                                                   @Override
                                                   public E getElement()
                                                   {
                                                     return getFirstElement();
                                                   }
                                                 };
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ElementList
     */
    public ElementList()
    {
      super( new ArrayList<E>() );
    }
    
    @Override
    public boolean remove( Object o )
    {
      //
      final E element = this.getFirstElement();
      final boolean retval = super.remove( o );
      
      //
      this.removeFromAccessorToElementListMapIfThisListIsEmpty( element );
      
      // 
      return retval;
    }
    
    @Override
    public boolean removeAll( Collection<?> c )
    {
      //
      final E element = this.getFirstElement();
      final boolean retval = super.removeAll( c );
      
      //
      this.removeFromAccessorToElementListMapIfThisListIsEmpty( element );
      
      // 
      return retval;
    }
    
    @Override
    public boolean retainAll( Collection<?> c )
    {
      //
      final E element = this.getFirstElement();
      final boolean retval = super.retainAll( c );
      
      //
      this.removeFromAccessorToElementListMapIfThisListIsEmpty( element );
      
      // 
      return retval;
    }
    
    @Override
    public E remove( int index )
    {
      //
      final E retval = super.remove( index );
      
      //
      this.removeFromAccessorToElementListMapIfThisListIsEmpty( retval );
      
      // 
      return retval;
    }
    
    /**
     * @return
     */
    protected E getFirstElement()
    {
      return ListUtils.firstElement( this );
    }
    
    /**
     * @param element
     *          an element this list contained before it was emptied
     */
    private void removeFromAccessorToElementListMapIfThisListIsEmpty( E element )
    {
      //
      if ( this.isEmpty() )
      {
        this.accessorReadable = new ElementHolderUnmodifiable<E>( element );
        TreeList.this.accessorToElementListMap.remove( this );
      }
    }
    
    @Override
    public E getElement()
    {
      return this.accessorReadable.getElement();
    }
    
    @Override
    public void clear()
    {
      //
      E element = this.getFirstElement();
      super.clear();
      this.removeFromAccessorToElementListMapIfThisListIsEmpty( element );
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TreeList
   */
  public TreeList()
  {
    this( new Factory<SortedMap<AccessorReadable<E>, ElementList>>()
    {
      @Override
      public SortedMap<AccessorReadable<E>, ElementList> newInstance()
      {
        @SuppressWarnings("unchecked")
        final Comparator<E> comparator = ComparatorUtils.NATURAL_COMPARATOR;
        return new TreeMap<AccessorReadable<E>, ElementList>( newComparatorForAccessor( comparator ) );
      }
    } );
  }
  
  /**
   * @see TreeList
   * @param comparator
   */
  public TreeList( final Comparator<E> comparator )
  {
    this( new Factory<SortedMap<AccessorReadable<E>, ElementList>>()
    {
      @Override
      public SortedMap<AccessorReadable<E>, ElementList> newInstance()
      {
        return new TreeMap<AccessorReadable<E>, ElementList>( newComparatorForAccessor( comparator ) );
      }
    } );
    
  }
  
  /**
   * @see TreeList
   * @param collection
   */
  public TreeList( Collection<E> collection )
  {
    this();
    
    //
    this.addAll( collection );
  }
  
  /**
   * @see TreeList
   * @param comparator
   * @param collection
   */
  public TreeList( final Comparator<E> comparator, Collection<E> collection )
  {
    this( comparator );
    
    //
    this.addAll( collection );
  }
  
  /**
   * @param comparator
   * @return
   */
  private static <E> Comparator<AccessorReadable<E>> newComparatorForAccessor( final Comparator<E> comparator )
  {
    return new Comparator<AccessorReadable<E>>()
    {
      @Override
      public int compare( AccessorReadable<E> accessor1, AccessorReadable<E> accessor2 )
      {
        return comparator.compare( accessor1.getElement(), accessor2.getElement() );
      }
    };
  }
  
  /**
   * @see TreeList
   * @param elementToEqualElementListMapFactory
   */
  protected TreeList( Factory<SortedMap<AccessorReadable<E>, ElementList>> elementToEqualElementListMapFactory )
  {
    super();
    this.accessorToElementListMap = elementToEqualElementListMapFactory.newInstance();
  }
  
  /**
   * @return
   */
  private Factory<ElementList> newElementListFactory()
  {
    return new Factory<ElementList>()
    {
      @Override
      public ElementList newInstance()
      {
        return new ElementList();
      }
    };
  }
  
  @Override
  public int size()
  {
    //
    int retval = 0;
    
    //
    for ( List<E> elementList : this.accessorToElementListMap.values() )
    {
      retval += elementList.size();
    }
    
    // 
    return retval;
  }
  
  /**
   * This does add the given element at the right order position. This does break the contract of the {@link Collection}
   * interface, since the new element is not appended to the {@link Collection}.
   */
  @Override
  public boolean add( E element )
  {
    //
    boolean retval = false;
    
    //
    if ( element != null )
    {
      //
      final AccessorReadable<E> accessor = new ElementHolderUnmodifiable<E>( element );
      
      //
      ElementList elementList = this.accessorToElementListMap.get( accessor );
      if ( elementList == null )
      {
        //
        elementList = this.newElementListFactory().newInstance();
        elementList.add( element );
        this.accessorToElementListMap.put( elementList, elementList );
      }
      else
      {
        //
        elementList.add( element );
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
    final ElementHolder<E> retvalHolder = new ElementHolder<E>();
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public TraversalHint beforeTraversalOfElementList( int indexPosition, int elementListSize, List<E> elementList )
      {
        //
        TraversalHint retval = null;
        
        //        
        if ( indexPosition + elementListSize < index )
        {
          retval = TraversalHint.SKIP_SUBLIST;
        }
        
        //
        return retval;
      }
      
      @Override
      public TraversalHint visitElement( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        //
        TraversalHint retval = null;
        
        //
        if ( indexPosition == index )
        {
          //
          retvalHolder.setElement( element );
          
          //
          retval = TraversalHint.CANCEL_TRAVERSAL;
        }
        
        //
        return retval;
      }
      
    };
    this.visitElements( elementVisitor );
    
    //
    return retvalHolder.getElement();
  }
  
  /**
   * Similar to {@link #visitElements(ElementVisitor, Iterable)} which uses all available {@link ElementList} instances of the
   * internal {@link SortedMap} in their sort order.
   * 
   * @see #visitElements(ElementVisitor, int)
   * @param elementVisitor
   */
  protected void visitElements( ElementVisitor<E> elementVisitor )
  {
    //
    final Collection<ElementList> elementListCollection = this.accessorToElementListMap.values();
    this.visitElements( elementVisitor, elementListCollection );
  }
  
  /**
   * Visitor method which allows to traverse over all given {@link ElementList} in their current order and execute the
   * {@link ElementVisitor#beforeTraversalOfElementList(int, int, List)} on them.
   * 
   * @see #visitElements(ElementVisitor, int)
   * @param elementVisitor
   * @param elementListCollection
   */
  protected void visitElements( ElementVisitor<E> elementVisitor, Iterable<ElementList> elementListCollection )
  {
    //
    int indexPosition = -1;
    outerloop: for ( ElementList elementList : elementListCollection )
    {
      //
      final int elementListSize = elementList.size();
      TraversalHint traversalHint = elementVisitor.beforeTraversalOfElementList( indexPosition, elementListSize, elementList );
      if ( TraversalHint.CANCEL_TRAVERSAL.equals( traversalHint ) )
      {
        break;
      }
      
      //
      if ( TraversalHint.SKIP_SUBLIST.equals( traversalHint ) )
      {
        indexPosition += elementListSize;
        continue;
      }
      
      //      
      int elementListIndexPosition = -1;
      for ( E element : elementList )
      {
        //
        indexPosition++;
        elementListIndexPosition++;
        
        //
        traversalHint = elementVisitor.visitElement( element, indexPosition, elementList, elementListIndexPosition );
        if ( TraversalHint.CANCEL_TRAVERSAL.equals( traversalHint ) )
        {
          break outerloop;
        }
      }
    }
  }
  
  @Override
  public E remove( final int index )
  {
    //
    final ElementHolder<E> retvalHolder = new ElementHolder<E>();
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public TraversalHint beforeTraversalOfElementList( int indexPosition, int elementListSize, List<E> elementList )
      {
        //
        TraversalHint retval = null;
        
        //        
        if ( indexPosition + elementListSize >= index )
        {
          //
          final int subListIndexPosition = index - ( indexPosition + 1 );
          
          //
          retvalHolder.setElement( elementList.remove( subListIndexPosition ) );
          
          //
          retval = TraversalHint.CANCEL_TRAVERSAL;
        }
        
        //
        return retval;
      }
      
      @Override
      public TraversalHint visitElement( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        return null;
      }
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  /**
   * Returns the index position of the first occurrence of an element which is {@link Object#equals(Object)} to the given
   * {@link Object}
   */
  @Override
  public int indexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retvalHolder = new ElementHolder<Integer>( -1 );
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      @Override
      public TraversalHint beforeTraversalOfElementList( int indexPosition, int elementListSize, List<E> elementList )
      {
        return elementList.contains( object ) ? TraversalHint.GO_ON : TraversalHint.SKIP_SUBLIST;
      }
      
      @Override
      public TraversalHint visitElement( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        //
        TraversalHint retval = null;
        
        //
        if ( ObjectUtils.equals( object, element ) )
        {
          //
          retvalHolder.setElement( indexPosition );
          retval = TraversalHint.CANCEL_TRAVERSAL;
        }
        
        //
        return retval;
      }
      
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  @Override
  public int lastIndexOf( final Object object )
  {
    //
    final ElementHolder<Integer> retvalHolder = new ElementHolder<Integer>( -1 );
    
    //
    ElementVisitor<E> elementVisitor = new ElementVisitor<E>()
    {
      private boolean hasAlreadyFoundEqualElement = false;
      
      @Override
      public TraversalHint beforeTraversalOfElementList( int indexPosition, int elementListSize, List<E> elementList )
      {
        return elementList.contains( object ) || this.hasAlreadyFoundEqualElement ? TraversalHint.GO_ON
                                                                                 : TraversalHint.SKIP_SUBLIST;
      }
      
      @Override
      public TraversalHint visitElement( E element, int indexPosition, List<E> elementList, int subListIndexPosition )
      {
        //
        TraversalHint retval = null;
        
        //
        if ( ObjectUtils.equals( object, element ) )
        {
          //
          this.hasAlreadyFoundEqualElement = true;
        }
        else if ( this.hasAlreadyFoundEqualElement )
        {
          //
          retvalHolder.setElement( indexPosition - 1 );
          retval = TraversalHint.CANCEL_TRAVERSAL;
        }
        
        //
        return retval;
      }
      
    };
    this.visitElements( elementVisitor );
    
    // 
    return retvalHolder.getElement();
  }
  
  /**
   * Removes the first occurring element within the {@link TreeList}, which is {@link Object#equals(Object)} to the given
   * {@link Object}
   * 
   * @return true: if any equal {@link Object} is found
   */
  @Override
  public boolean remove( Object object )
  {
    //
    boolean retval = false;
    
    //
    final Accessor<Object> accessor = new ElementHolder<Object>( object );
    if ( this.accessorToElementListMap.containsKey( accessor ) )
    {
      //
      final ElementList elementList = this.accessorToElementListMap.get( accessor );
      elementList.remove( object );
    }
    
    // 
    return retval;
  }
  
  /**
   * Returns true if any element within the {@link TreeList} is {@link Object#equals(Object)} to the given {@link Object}
   */
  @Override
  public boolean contains( Object object )
  {
    //
    boolean retval = false;
    
    //
    final Accessor<Object> accessor = new ElementHolder<Object>( object );
    if ( this.accessorToElementListMap.containsKey( accessor ) )
    {
      retval = this.accessorToElementListMap.get( accessor ).contains( object );
    }
    
    // 
    return retval;
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.accessorToElementListMap.isEmpty();
  }
  
  @Override
  public void clear()
  {
    this.accessorToElementListMap.clear();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public ListIterator<E> listIterator()
  {
    return new ChainedListIterator<E>( this.accessorToElementListMap.values().toArray( new List[] {} ) );
  }
  
  @Override
  protected SortedList<E> newInstance( Collection<E> collection )
  {
    //
    return new TreeList<E>( collection );
  }
  
}
