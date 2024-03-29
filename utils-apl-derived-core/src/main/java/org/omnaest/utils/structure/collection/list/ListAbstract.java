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
package org.omnaest.utils.structure.collection.list;

import java.io.Serializable;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.collections.IteratorUtils;
import org.omnaest.utils.propertyfile.content.MapAbstract;
import org.omnaest.utils.structure.collection.CollectionAbstract;
import org.omnaest.utils.structure.collection.list.adapter.ListToListIteratorAdapter;

/**
 * This abstract list implementation offers the basic methods like addAll, removeAll, retainAll, etc. which rely only on other
 * list methods and not on any underlying structure. This list offers automatic {@link ListIterator}, {@link Iterator} and sublist
 * which are backed to the given list. <br>
 * <br>
 * Additionally the {@link Deque} interface is completely implemented as well.
 * 
 * @see List
 * @see Deque
 * @see CollectionAbstract
 * @see MapAbstract
 * @author Omnaest
 */
public abstract class ListAbstract<E> extends CollectionAbstract<E> implements List<E>, Deque<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 3410678520148023549L;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Sublist implementation for the {@link ListAbstract} including the {@link #fromIndex} and excluding the {@link #toIndex}
   * 
   * @author Omnaest
   */
  protected static class ListAbstractSublist<E> extends ListAbstract<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 81960052895916590L;
    /* ********************************************** Variables ********************************************** */
    protected int             fromIndex        = -1;
    protected int             toIndex          = -1;
    protected List<E>         parentList       = null;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ListAbstractSublist
     * @param parentList
     * @param fromIndex
     * @param toIndex
     */
    public ListAbstractSublist( List<E> parentList, int fromIndex, int toIndex )
    {
      this.fromIndex = fromIndex;
      this.toIndex = toIndex;
      this.parentList = parentList;
    }
    
    /**
     * @return
     */
    private boolean isParentListNotNull()
    {
      if ( this.parentList == null )
      {
        throw new NullPointerException( "Parental list of sublist is null." );
      }
      return true;
    }
    
    @Override
    public boolean add( E e )
    {
      throw new UnsupportedOperationException( "Sublist cannot add new elements." );
    }
    
    @Override
    public void add( int index, E element )
    {
      throw new UnsupportedOperationException( "Sublist cannot add new elements." );
    }
    
    @Override
    public E get( int index )
    {
      //
      E retval = null;
      
      //
      if ( this.isParentListNotNull() && this.isValidIndex( index ) )
      {
        retval = this.parentList.get( this.determineParentListIndexFromCurrentListIndex( index ) );
      }
      
      //
      return retval;
    }
    
    private int determineParentListIndexFromCurrentListIndex( int index )
    {
      return this.fromIndex + index;
    }
    
    private int determineCurrentListIndexFromParentListIndex( int index )
    {
      return index - this.fromIndex;
    }
    
    @Override
    public int indexOf( Object o )
    {
      //
      int retval = -1;
      
      //
      int indexOfWithinParent = this.parentList.indexOf( o );
      if ( indexOfWithinParent >= this.fromIndex && indexOfWithinParent < this.toIndex )
      {
        retval = this.determineCurrentListIndexFromParentListIndex( indexOfWithinParent );
      }
      
      //
      return retval;
    }
    
    @Override
    public int lastIndexOf( Object o )
    {
      //
      int retval = -1;
      
      //
      int lastIndexOfWithinParent = this.parentList.lastIndexOf( o );
      if ( lastIndexOfWithinParent >= this.fromIndex && lastIndexOfWithinParent < this.toIndex )
      {
        retval = this.determineCurrentListIndexFromParentListIndex( lastIndexOfWithinParent );
      }
      
      //
      return retval;
    }
    
    @Override
    public E remove( int index )
    {
      //
      E retval = null;
      
      //
      if ( this.isParentListNotNull() && this.isValidIndex( index ) )
      {
        this.parentList.remove( this.determineParentListIndexFromCurrentListIndex( index ) );
        this.toIndex--;
      }
      
      //
      return retval;
    }
    
    @Override
    public E set( int index, E element )
    {
      //
      E retval = null;
      
      //
      if ( this.isParentListNotNull() && this.isValidIndex( index ) )
      {
        this.parentList.set( this.determineParentListIndexFromCurrentListIndex( index ), element );
      }
      
      //
      return retval;
    }
    
    @Override
    public int size()
    {
      return Math.max( 0, this.toIndex - this.fromIndex );
    }
    
    @Override
    public List<E> subList( int fromIndex, int toIndex )
    {
      return new ListAbstractSublist<E>( this, fromIndex, toIndex );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public ListAbstract()
  {
    super();
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> elementCollection )
  {
    //
    boolean retval = false;
    
    //
    if ( elementCollection != null )
    {
      for ( E element : elementCollection )
      {
        if ( element != null )
        {
          this.add( index++, element );
          retval = true;
        }
      }
    }
    
    //
    return retval;
  }
  
  protected boolean isValidIndex( int index )
  {
    return index >= 0 && index < this.size();
  }
  
  @Override
  public ListIterator<E> listIterator()
  {
    return new ListToListIteratorAdapter<E>( this );
  }
  
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return new ListToListIteratorAdapter<E>( this, index );
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return new ListAbstractSublist<E>( this, fromIndex, toIndex );
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.listIterator();
  }
  
  @Override
  public boolean remove( Object o )
  {
    //
    boolean retval = false;
    
    //
    int index = this.indexOf( o );
    if ( index >= 0 )
    {
      //
      this.remove( index );
      
      //
      retval = true;
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.indexOf( o ) >= 0;
  }
  
  @Override
  public void clear()
  {
    //
    final Iterator<E> iterator = this.iterator();
    while ( iterator.hasNext() )
    {
      iterator.next();
      iterator.remove();
    }
  }
  
  @Override
  public void addFirst( E element )
  {
    //
    final int index = 0;
    this.add( index, element );
  }
  
  @Override
  public void addLast( E e )
  {
    //
    this.add( e );
  }
  
  @Override
  public boolean offerFirst( E element )
  {
    //
    this.addFirst( element );
    return true;
  }
  
  @Override
  public boolean offerLast( E e )
  {
    //
    this.addLast( e );
    return true;
  }
  
  @Override
  public E removeFirst()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = 0;
      retval = this.remove( index );
    }
    else
    {
      throw new NoSuchElementException();
    }
    
    // 
    return retval;
  }
  
  @Override
  public E removeLast()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.size() - 1;
      retval = this.remove( index );
    }
    else
    {
      throw new NoSuchElementException();
    }
    
    // 
    return retval;
  }
  
  @Override
  public E pollFirst()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = 0;
      retval = this.remove( index );
    }
    
    // 
    return retval;
  }
  
  @Override
  public E pollLast()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.size() - 1;
      retval = this.remove( index );
    }
    
    // 
    return retval;
  }
  
  @Override
  public E getFirst()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = 0;
      retval = this.get( index );
    }
    else
    {
      throw new NoSuchElementException();
    }
    
    // 
    return retval;
  }
  
  @Override
  public E getLast()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.size() - 1;
      retval = this.get( index );
    }
    else
    {
      throw new NoSuchElementException();
    }
    
    // 
    return retval;
  }
  
  @Override
  public E peekFirst()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = 0;
      retval = this.get( index );
    }
    
    // 
    return retval;
  }
  
  @Override
  public E peekLast()
  {
    //
    E retval = null;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.size() - 1;
      retval = this.get( index );
    }
    else
    {
      throw new NoSuchElementException();
    }
    
    // 
    return retval;
  }
  
  @Override
  public boolean removeFirstOccurrence( Object o )
  {
    //
    boolean retval = false;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.indexOf( o );
      if ( index >= 0 )
      {
        this.remove( index );
        retval = true;
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public boolean removeLastOccurrence( Object o )
  {
    //
    boolean retval = false;
    
    //
    if ( !this.isEmpty() )
    {
      final int index = this.lastIndexOf( o );
      if ( index >= 0 )
      {
        this.remove( index );
        retval = true;
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public boolean offer( E e )
  {
    // 
    return this.offerLast( e );
  }
  
  @Override
  public E remove()
  {
    // 
    return this.removeFirst();
  }
  
  @Override
  public E poll()
  {
    //
    return this.pollFirst();
  }
  
  @Override
  public E element()
  {
    // 
    return this.getFirst();
  }
  
  @Override
  public E peek()
  {
    // 
    return this.peekFirst();
  }
  
  @Override
  public void push( E element )
  {
    this.addFirst( element );
  }
  
  @Override
  public E pop()
  {
    // 
    return this.removeFirst();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Iterator<E> descendingIterator()
  {
    //    
    return IteratorUtils.unmodifiableIterator( ListUtils.reverse( this ).iterator() );
  }
}
