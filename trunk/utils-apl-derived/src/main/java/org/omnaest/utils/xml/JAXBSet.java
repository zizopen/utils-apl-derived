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
package org.omnaest.utils.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link Set} wrapper which acts as an {@link XmlRootElement} for any {@link Set}. Since the exact type of the internal
 * {@link Set} instance is determined at runtime, each of the objects have its own schema definition. This will cause some
 * overhead in comparison to a {@link Set} which is wrapped not by its interface.
 * 
 * @see #newInstance(Set)
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement
public class JAXBSet<E> implements Set<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElement
  protected Set<E> set = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Creates a new instance of a {@link JAXBSet} for a given {@link Set}.
   * 
   * @param <E>
   * @param set
   * @return new instance or null if set param is null
   */
  public static <E> JAXBSet<E> newInstance( Set<E> set )
  {
    //
    JAXBSet<E> result = null;
    
    //
    if ( set != null )
    {
      result = new JAXBSet<E>( set );
    }
    
    //
    return result;
  }
  
  /**
   * @see #newInstance(Collection)
   * @param set
   */
  protected JAXBSet( Set<E> set )
  {
    super();
    this.set = set;
  }
  
  /**
   * Used internally when JAXB does create a new default instance.
   * 
   * @param list
   */
  protected JAXBSet()
  {
    super();
  }
  
  @Override
  public boolean add( E arg0 )
  {
    return this.set.add( arg0 );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> arg0 )
  {
    return this.set.addAll( arg0 );
  }
  
  @Override
  public void clear()
  {
    this.set.clear();
  }
  
  @Override
  public boolean contains( Object arg0 )
  {
    return this.set.contains( arg0 );
  }
  
  @Override
  public boolean containsAll( Collection<?> arg0 )
  {
    return this.set.containsAll( arg0 );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.set.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.set.iterator();
  }
  
  @Override
  public boolean remove( Object arg0 )
  {
    return this.set.remove( arg0 );
  }
  
  @Override
  public boolean removeAll( Collection<?> arg0 )
  {
    return this.set.removeAll( arg0 );
  }
  
  @Override
  public boolean retainAll( Collection<?> arg0 )
  {
    return this.set.retainAll( arg0 );
  }
  
  @Override
  public int size()
  {
    return this.set.size();
  }
  
  @Override
  public Object[] toArray()
  {
    return this.set.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] arg0 )
  {
    return this.set.toArray( arg0 );
  }
  
}
