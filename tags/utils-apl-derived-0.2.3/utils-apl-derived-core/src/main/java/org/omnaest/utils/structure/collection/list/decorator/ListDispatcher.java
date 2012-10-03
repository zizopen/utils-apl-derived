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
package org.omnaest.utils.structure.collection.list.decorator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Dispatches any access to it to one or multiple other {@link List} instances.<br>
 * <br>
 * To use override any {@link List} method and use the protected {@link #switchToNextInternalList()},
 * {@link #switchToPreviousInternalList()}, {@link #getCurrentListIndex()} and {@link #getList()} methods to change the
 * {@link List} instance to which the method invocations are forwared.<br>
 * By default the first {@link List} implementation is active.
 * 
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
public abstract class ListDispatcher<E> implements List<E>
{
  /* ********************************************** Beans / Services / References ********************************************** */
  protected final ListDispatchControl<E> listDispatchControl;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * The {@link ListDispatchControl} allows to control to which {@link List} the method invocations to the {@link ListDispatcher}
   * will be forwarded to.
   * 
   * @author Omnaest
   * @param <E>
   */
  protected static class ListDispatchControl<E>
  {
    /* ********************************************** Variables ********************************************** */
    private List<List<E>> lists            = new ArrayList<List<E>>();
    private int           currentListIndex = 0;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ListDispatchControl
     * @param lists
     */
    public ListDispatchControl( List<E>... lists )
    {
      super();
      this.lists.addAll( Arrays.asList( lists ) );
    }
    
    /**
     * Switches to the next internally available {@link List}
     * 
     * @return this
     */
    public ListDispatchControl<E> switchToNextInternalList()
    {
      //
      this.currentListIndex = Math.min( this.lists.size() - 1, this.currentListIndex + 1 );
      
      //
      return this;
    }
    
    /**
     * Switches to the next internally available {@link List}
     * 
     * @return this
     */
    public ListDispatchControl<E> switchToPreviousInternalList()
    {
      //
      this.currentListIndex = Math.max( 0, this.currentListIndex - 1 );
      
      //
      return this;
    }
    
    /**
     * Moves all elements of the current active dispatch {@link List} to the next and switches to this {@link List}.
     * 
     * @return this
     */
    public ListDispatchControl<E> rolloverToNextList()
    {
      //
      final List<E> listOld = this.getList();
      this.switchToNextInternalList();
      final List<E> listNew = this.getList();
      if ( listOld != listNew )
      {
        listNew.addAll( listOld );
        listOld.clear();
      }
      
      //
      return this;
    }
    
    /**
     * Moves all elements of the current active dispatch {@link List} to the previous and switches to this {@link List}.
     * 
     * @return this
     */
    public ListDispatchControl<E> rolloverToPreviousList()
    {
      //
      final List<E> listOld = this.getList();
      this.switchToPreviousInternalList();
      final List<E> listNew = this.getList();
      if ( listOld != listNew )
      {
        listNew.addAll( listOld );
        listOld.clear();
      }
      
      //
      return this;
    }
    
    /**
     * Returns the currently active {@link List} to which all method invocations should be forwarded.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public <L extends List<E>> L getList()
    {
      return (L) this.lists.get( this.currentListIndex );
    }
    
    /**
     * @return the currentListIndex
     */
    public int getCurrentListIndex()
    {
      return this.currentListIndex;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ListDispatchControl [lists=" );
      builder.append( this.lists );
      builder.append( ", currentListIndex=" );
      builder.append( this.currentListIndex );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ListDispatcher
   * @param lists
   */
  public ListDispatcher( List<E>... lists )
  {
    super();
    this.listDispatchControl = new ListDispatchControl<E>( lists );
  }
  
  /**
   * @return
   * @see java.util.List#size()
   */
  public int size()
  {
    return this.getList().size();
  }
  
  /**
   * @return
   * @see java.util.List#isEmpty()
   */
  public boolean isEmpty()
  {
    return this.getList().isEmpty();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#contains(java.lang.Object)
   */
  public boolean contains( Object o )
  {
    return this.getList().contains( o );
  }
  
  /**
   * @return
   * @see java.util.List#iterator()
   */
  public Iterator<E> iterator()
  {
    return this.getList().iterator();
  }
  
  /**
   * @return
   * @see java.util.List#toArray()
   */
  public Object[] toArray()
  {
    return this.getList().toArray();
  }
  
  /**
   * @param a
   * @return
   * @see java.util.List#toArray(T[])
   */
  public <T> T[] toArray( T[] a )
  {
    return this.getList().toArray( a );
  }
  
  /**
   * @param e
   * @return
   * @see java.util.List#add(java.lang.Object)
   */
  public boolean add( E e )
  {
    return this.getList().add( e );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#remove(java.lang.Object)
   */
  public boolean remove( Object o )
  {
    return this.getList().remove( o );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#containsAll(java.util.Collection)
   */
  public boolean containsAll( Collection<?> c )
  {
    return this.getList().containsAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#addAll(java.util.Collection)
   */
  public boolean addAll( Collection<? extends E> c )
  {
    return this.getList().addAll( c );
  }
  
  /**
   * @param index
   * @param c
   * @return
   * @see java.util.List#addAll(int, java.util.Collection)
   */
  public boolean addAll( int index, Collection<? extends E> c )
  {
    return this.getList().addAll( index, c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#removeAll(java.util.Collection)
   */
  public boolean removeAll( Collection<?> c )
  {
    return this.getList().removeAll( c );
  }
  
  /**
   * @param c
   * @return
   * @see java.util.List#retainAll(java.util.Collection)
   */
  public boolean retainAll( Collection<?> c )
  {
    return this.getList().retainAll( c );
  }
  
  /**
   * @see java.util.List#clear()
   */
  public void clear()
  {
    this.getList().clear();
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#equals(java.lang.Object)
   */
  public boolean equals( Object o )
  {
    return this.getList().equals( o );
  }
  
  /**
   * @return
   * @see java.util.List#hashCode()
   */
  public int hashCode()
  {
    return this.getList().hashCode();
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#get(int)
   */
  public E get( int index )
  {
    return this.getList().get( index );
  }
  
  /**
   * @param index
   * @param element
   * @return
   * @see java.util.List#set(int, java.lang.Object)
   */
  public E set( int index, E element )
  {
    return this.getList().set( index, element );
  }
  
  /**
   * @param index
   * @param element
   * @see java.util.List#add(int, java.lang.Object)
   */
  public void add( int index, E element )
  {
    this.getList().add( index, element );
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#remove(int)
   */
  public E remove( int index )
  {
    return this.getList().remove( index );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#indexOf(java.lang.Object)
   */
  public int indexOf( Object o )
  {
    return this.getList().indexOf( o );
  }
  
  /**
   * @param o
   * @return
   * @see java.util.List#lastIndexOf(java.lang.Object)
   */
  public int lastIndexOf( Object o )
  {
    return this.getList().lastIndexOf( o );
  }
  
  /**
   * @return
   * @see java.util.List#listIterator()
   */
  public ListIterator<E> listIterator()
  {
    return this.getList().listIterator();
  }
  
  /**
   * @param index
   * @return
   * @see java.util.List#listIterator(int)
   */
  public ListIterator<E> listIterator( int index )
  {
    return this.getList().listIterator( index );
  }
  
  /**
   * @param fromIndex
   * @param toIndex
   * @return
   * @see java.util.List#subList(int, int)
   */
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.getList().subList( fromIndex, toIndex );
  }
  
  /**
   * @return
   */
  protected List<E> getList()
  {
    return this.listDispatchControl.getList();
  }
  
  @Override
  public String toString()
  {
    return this.getList().toString();
  }
  
}
