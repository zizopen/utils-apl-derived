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

import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.list.ListAbstract;

/**
 * Abstract implementation of a {@link SortedList} which reduces the need to implement all methods
 * 
 * @author Omnaest
 * @param <E>
 */
public abstract class SortedListAbstract<E> extends ListAbstract<E> implements SortedSplitableList<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long     serialVersionUID = -5663102837607165996L;
  
  /* ********************************************** Variables ********************************************** */
  /** The constructor will ensure that the {@link Comparator} will not be null */
  protected final Comparator<E> comparator;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Sublist implementation for {@link SortedListAbstract}
   * 
   * @author Omnaest
   * @param <E>
   */
  protected static class SortedListAbstractSublist<E> extends ListAbstractSublist<E> implements SortedList<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 5214337271801597442L;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see SortedListAbstractSublist
     * @param parentList
     * @param fromIndex
     * @param toIndex
     */
    public SortedListAbstractSublist( SortedList<E> parentList, int fromIndex, int toIndex )
    {
      super( parentList, fromIndex, toIndex );
    }
    
    @Override
    public Comparator<? super E> comparator()
    {
      return ( (SortedList<E>) this.parentList ).comparator();
    }
    
    @Override
    public SortedList<E> subList( E fromElement, E toElement )
    {
      return SortedListAbstract.subList( this, fromElement, toElement );
    }
    
    @Override
    public SortedList<E> subList( int fromIndex, int toIndex )
    {
      return (SortedList<E>) super.subList( fromIndex, toIndex );
    }
    
    @Override
    public SortedList<E> headList( E toElement )
    {
      return SortedListAbstract.headList( this, toElement );
    }
    
    @Override
    public SortedList<E> tailList( E fromElement )
    {
      return SortedListAbstract.tailList( this, fromElement );
    }
    
    @Override
    public E first()
    {
      return SortedListAbstract.first( this );
    }
    
    @Override
    public E last()
    {
      return SortedListAbstract.last( this );
    }
    
    public boolean add( E element )
    {
      return false;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SortedListAbstract
   * @param comparator
   */
  public SortedListAbstract( Comparator<E> comparator )
  {
    super();
    this.comparator = comparator != null ? comparator : new Comparator<E>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public int compare( E element1, E element2 )
      {
        return ( (Comparable<E>) element1 ).compareTo( element2 );
      }
    };
  }
  
  /**
   * Using this constructor enforces that all element types implement the {@link Comparable} interface
   * 
   * @see SortedListAbstract
   */
  public SortedListAbstract()
  {
    this( (Comparator<E>) null );
  }
  
  @Override
  public Comparator<? super E> comparator()
  {
    return this.comparator;
  }
  
  @Override
  public E first()
  {
    return first( this );
  }
  
  /**
   * @param sortedList
   * @return
   */
  protected static <E> E first( SortedList<E> sortedList )
  {
    return sortedList != null && !sortedList.isEmpty() ? sortedList.get( 0 ) : null;
  }
  
  @Override
  public E last()
  {
    return last( this );
  }
  
  /**
   * @param sortedList
   * @return
   */
  protected static <E> E last( SortedList<E> sortedList )
  {
    return sortedList != null && !sortedList.isEmpty() ? sortedList.get( sortedList.size() - 1 ) : null;
  }
  
  @Override
  public SortedList<E> subList( E fromElement, E toElement )
  {
    return subList( this, fromElement, toElement );
  }
  
  @Override
  public SortedList<E> headList( E toElement )
  {
    return headList( this, toElement );
  }
  
  @Override
  public SortedList<E> tailList( E fromElement )
  {
    return tailList( this, fromElement );
  }
  
  /**
   * @param list
   * @param fromElement
   * @param toElement
   * @return
   */
  protected static <E> SortedList<E> subList( SortedList<E> list, E fromElement, E toElement )
  {
    //
    SortedList<E> retlist = null;
    
    //
    if ( list != null )
    {
      int fromIndex = list.indexOf( fromElement );
      int toIndex = list.indexOf( toElement );
      retlist = list.subList( fromIndex, toIndex );
    }
    
    return retlist;
  }
  
  /**
   * @param list
   * @param toElement
   * @return
   */
  protected static <E> SortedList<E> headList( SortedList<E> list, E toElement )
  {
    //
    SortedList<E> retlist = null;
    
    //
    if ( list != null )
    {
      int fromIndex = 0;
      int toIndex = list.indexOf( toElement );
      retlist = list.subList( fromIndex, toIndex );
    }
    
    return retlist;
  }
  
  /**
   * @param list
   * @param fromElement
   * @return
   */
  protected static <E> SortedList<E> tailList( SortedList<E> list, E fromElement )
  {
    //
    SortedList<E> retlist = null;
    
    //
    if ( list != null )
    {
      int fromIndex = list.indexOf( fromElement );
      int toIndex = list.size();
      retlist = list.subList( fromIndex, toIndex );
    }
    
    return retlist;
  }
  
  @Override
  public SortedList<E> subList( int fromIndex, int toIndex )
  {
    // 
    return new SortedListAbstractSublist<E>( this, fromIndex, toIndex );
  }
  
  @Override
  public void add( int index, E element )
  {
    this.add( element );
  }
  
  @Override
  public E set( int index, E element )
  {
    //
    E retval = this.remove( index );
    this.add( element );
    
    // 
    return retval;
  }
  
  @Override
  public SortedList<E> splitAt( int index )
  {
    //    
    SortedList<E> retlist = null;
    
    //
    final List<E> list = new ArrayList<E>();
    index = Math.max( 0, index );
    for ( int ii = this.size() - 1; ii >= index; ii-- )
    {
      list.add( 0, this.remove( ii ) );
    }
    
    //
    retlist = this.newInstance( list );
    
    //
    return retlist;
  }
  
  /**
   * Creates a new {@link SortedList} instance
   * 
   * @param collection
   * @return
   */
  protected SortedList<E> newInstance( Collection<E> collection )
  {
    //
    @SuppressWarnings("unchecked")
    Class<? extends SortedList<E>> type = (Class<? extends SortedList<E>>) this.getClass();
    
    //
    final SortedList<E> retlist = ReflectionUtils.createInstanceOf( type );
    retlist.addAll( collection );
    
    //
    return retlist;
  }
  
  @Override
  public E getFirst()
  {
    // 
    return this.first();
  }
  
  @Override
  public E getLast()
  {
    // 
    return this.last();
  }
  
}
