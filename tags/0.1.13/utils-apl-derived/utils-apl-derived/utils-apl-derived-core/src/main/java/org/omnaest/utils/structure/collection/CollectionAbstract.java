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
package org.omnaest.utils.structure.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract implementation for the {@link Collection} interface. Only a few very needed methods are left to be implemented by
 * derived classes.
 * 
 * @author Omnaest
 * @param <E>
 */
public abstract class CollectionAbstract<E> implements Collection<E>, Serializable
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 3810482055181299089L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public CollectionAbstract()
  {
    super();
  }
  
  /**
   * @param collection
   */
  public CollectionAbstract( Collection<E> collection )
  {
    super();
    this.addAll( collection );
  }
  
  @Override
  public void clear()
  {
    this.removeAll( new ArrayList<E>( this ) );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> collection )
  {
    //
    boolean retval = false;
    
    //
    if ( collection != null )
    {
      for ( E element : collection )
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
  
  @Override
  public Object[] toArray()
  {
    //
    int size = this.size();
    Object[] retval = new Object[size];
    
    //
    Iterator<E> iterator = this.iterator();
    for ( int ii = 0; ii < size; ii++ )
    {
      retval[ii] = iterator.next();
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
      Iterator<E> iterator = this.iterator();
      for ( int ii = 0; ii < size; ii++ )
      {
        a[ii] = (T) iterator.next();
      }
      
      //
      retval = a;
    }
    
    //
    return retval;
  }
  
  @Override
  public int hashCode()
  {
    return CollectionUtils.hashCode( this );
  }
  
  @Override
  public boolean equals( Object object )
  {
    if ( object instanceof Iterable )
    {
      return CollectionUtils.equals( this, (Iterable<?>) object );
    }
    return false;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( Arrays.toString( this.toArray() ) );
    return builder.toString();
  }
}
