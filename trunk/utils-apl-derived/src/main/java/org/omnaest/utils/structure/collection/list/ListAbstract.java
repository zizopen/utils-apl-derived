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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This abstract list implementation offers the basic methods like addAll, removeAll, retainAll, etc. which rely only on other
 * list methods and not on any underlying structure. This list offers automatic listiterator, iterator and sublist which are
 * backed to the given list.
 * 
 * @author Omnaest
 */
public abstract class ListAbstract<E> implements List<E>
{
  /* ********************************************** Methods ********************************************** */

  @Override
  public boolean equals( Object object )
  {
    //
    boolean retval = false;
    
    //
    if ( object != null )
    {
      if ( object instanceof List<?> )
      {
        //
        List<?> list = (List<?>) object;
        
        //
        int size = this.size();
        if ( list.size() == size )
        {
          //
          retval = true;
          
          //
          for ( int ii = 0; ii < size; ii++ )
          {
            //
            E ownElement = this.get( ii );
            Object foreignObject = list.get( ii );
            retval &= ownElement == foreignObject || ( ownElement != null && ownElement.equals( foreignObject ) );
            
            //
            if ( !retval )
            {
              break;
            }
          }
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
  public boolean addAll( Collection<? extends E> elementCollection )
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
          retval |= this.add( element );
        }
      }
    }
    
    //
    return retval;
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
  
  @Override
  public boolean contains( Object o )
  {
    return this.indexOf( o ) >= 0;
  }
  
  @Override
  public boolean containsAll( Collection<?> collection )
  {
    //
    boolean retval = true;
    
    //
    if ( collection != null )
    {
      for ( Object object : collection )
      {
        retval &= this.contains( object );
        if ( !retval )
        {
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.size() == 0;
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.listIterator();
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
  public boolean removeAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    if ( c != null )
    {
      for ( Object o : c )
      {
        retval |= this.remove( o );
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    //
    boolean retval = false;
    
    //
    if ( c != null )
    {
      //
      List<E> removeList = new ArrayList<E>();
      for ( E element : this )
      {
        if ( !c.contains( element ) )
        {
          removeList.add( element );
          retval = true;
        }
      }
      
      //
      this.removeAll( removeList );
    }
    
    //
    return retval;
  }
  
  /**
   * Sublist implementation for the {@link ListAbstract}
   * 
   * @author Omnaest
   */
  @SuppressWarnings("hiding")
  private class ListAbstractSublist<E> extends ListAbstract<E>
  {
    /* ********************************************** Constants ********************************************** */
    private static final long serialVersionUID = 5585767897799299485L;
    /* ********************************************** Variables ********************************************** */
    private int               fromIndex        = -1;
    private int               toIndex          = -1;
    private List<E>           parentList       = null;
    
    /* ********************************************** Methods ********************************************** */
    public ListAbstractSublist( List<E> parentList, int fromIndex, int toIndex )
    {
      this.fromIndex = fromIndex;
      this.toIndex = toIndex;
      this.parentList = parentList;
    }
    
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
    public void clear()
    {
      this.removeAll( new ArrayList<E>( this ) );
    }
    
    @Override
    public E get( int index )
    {
      //
      E retval = null;
      
      //
      if ( this.isParentListNotNull() && this.isValidIndex( index ) )
      {
        this.parentList.get( this.determineParentListIndexFromCurrentListIndex( index ) );
      }
      
      //
      return retval;
    }
    
    private int determineParentListIndexFromCurrentListIndex( int index )
    {
      return this.fromIndex + index;
    }
    
    @Override
    public int indexOf( Object o )
    {
      //
      int index = -1;
      
      //
      for ( int ii = 0; ii < this.size() && index < 0; ii++ )
      {
        E element = this.get( ii );
        if ( element == o || ( element != null && element.equals( o ) ) )
        {
          index = ii;
        }
      }
      
      //
      return index;
    }
    
    @Override
    public int lastIndexOf( Object o )
    {
      //
      int lastIndex = -1;
      
      //
      for ( int ii = this.size() - 1; ii >= 0 && lastIndex < 0; ii++ )
      {
        E element = this.get( ii );
        if ( element == o || ( element != null && element.equals( o ) ) )
        {
          lastIndex = ii;
        }
      }
      
      //
      return lastIndex;
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
  
  @Override
  public Object[] toArray()
  {
    //
    int size = this.size();
    Object[] retval = new Object[size];
    
    //
    for ( int ii = 0; ii < size; ii++ )
    {
      retval[ii] = this.get( ii );
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray( T[] a )
  {
    //
    T[] retval = null;
    
    //
    if ( a != null )
    {
      //
      int size = this.size();
      
      //
      if ( a.length != size )
      {
        a = (T[]) java.lang.reflect.Array.newInstance( a.getClass().getComponentType(), size );
      }
      
      //
      for ( int ii = 0; ii < size; ii++ )
      {
        a[ii] = (T) this.get( ii );
      }
      
      //
      retval = a;
    }
    
    //
    return retval;
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return new ListAbstractSublist<E>( this, fromIndex, toIndex );
  }
  
}
