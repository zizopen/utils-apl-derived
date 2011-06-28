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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Defines a set on a given list object. The changed on the set will be populated to the list and vice versa.
 * 
 * @author Omnaest
 */
public class ListSet<E> implements Set<E>
{
  private List<E> list = null;
  
  /**
   * Constructor
   * 
   * @param underlyingList
   */
  public ListSet( List<E> underlyingList )
  {
    this.list = underlyingList;
  }
  
  @Override
  public boolean add( E e )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.add( e );
    }
    
    return retval;
  }
  
  /**
   * Returns true, if a list object is set.
   * 
   * @return
   */
  public boolean listIsNotNull()
  {
    return this.list != null;
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.addAll( c );
    }
    
    return retval;
  }
  
  @Override
  public void clear()
  {
    if ( this.listIsNotNull() )
    {
      this.list.clear();
    }
  }
  
  @Override
  public boolean contains( Object o )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.contains( o );
    }
    
    return retval;
  }
  
  @Override
  public boolean containsAll( Collection<?> c )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.containsAll( c );
    }
    
    return retval;
  }
  
  @Override
  public boolean isEmpty()
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.isEmpty();
    }
    
    return retval;
  }
  
  @Override
  public Iterator<E> iterator()
  {
    Iterator<E> retval = null;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.iterator();
    }
    
    return retval;
  }
  
  @Override
  public boolean remove( Object o )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.remove( o );
    }
    
    return retval;
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.removeAll( c );
    }
    
    return retval;
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    boolean retval = false;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.retainAll( c );
    }
    
    return retval;
  }
  
  @Override
  public int size()
  {
    int retval = 0;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.size();
    }
    
    return retval;
  }
  
  @Override
  public Object[] toArray()
  {
    Object[] retval = null;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.toArray();
    }
    
    return retval;
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    T[] retval = null;
    
    if ( this.listIsNotNull() )
    {
      retval = this.list.toArray( a );
    }
    
    return retval;
  }
  
  public List<E> getList()
  {
    return this.list;
  }
  
  public void setList( List<E> list )
  {
    this.list = list;
  }
  
  @Override
  public boolean equals( Object obj )
  {
    boolean retval = false;
    
    if ( obj != null && obj instanceof Set<?> )
    {
      Set<?> otherSet = (Set<?>) obj;
      if ( this.size() == otherSet.size() )
      {
        retval = true;
        for ( E iElement : this )
        {
          retval &= otherSet.contains( iElement );
          
          if ( !retval )
          {
            break;
          }
        }
      }
    }
    
    return retval;
  }
}
