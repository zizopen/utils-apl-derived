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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.structure.collection.set.SetAbstract;

/**
 * Defines a {@link Set} operating on a given {@link List}. The changes on the {@link Set} will be populated to the {@link List}
 * and vice versa.
 * 
 * @see Set
 * @see SetAbstract
 * @author Omnaest
 */
public class ListToSetAdapter<E> extends SetAbstract<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 837283691356459016L;
  /* ********************************************** Variables ********************************************** */
  private List<E>           list             = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Constructor
   * 
   * @param list
   */
  public ListToSetAdapter( List<E> list )
  {
    this.list = list;
  }
  
  /**
   * @return true if {@link #list} != null
   */
  private boolean listIsNotNull()
  {
    return this.list != null;
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
  public boolean add( E element )
  {
    //
    boolean retval = false;
    
    //
    if ( element != null && this.listIsNotNull() && !this.list.contains( element ) )
    {
      retval = this.list.add( element );
    }
    
    //
    return retval;
  }
  
  @Override
  public String toString()
  {
    return this.list.toString();
  }
  
}
