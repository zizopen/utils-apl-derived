/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.utils.structure.collection.set;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link ReducedSet} acts like the underlying {@link Set} but with removed elements of a second {@link Set}.<br>
 * <br>
 * This {@link Set} does perform the delta analysis for each method call once again, which makes it very costly to use.
 * 
 * @author Omnaest
 * @param <E>
 */
public class ReducedSet<E> implements Set<E>, Serializable
{
  private static final long serialVersionUID = -5990986312499631525L;
  private Iterable<Set<E>>  reductionSets;
  private Set<E>            set;
  
  /**
   * @see ReducedSet
   * @param set
   * @param reductionSets
   */
  public ReducedSet( Set<E> set, Iterable<Set<E>> reductionSets )
  {
    this.set = set;
    this.reductionSets = reductionSets;
  }
  
  private Set<E> getReducedSet()
  {
    return SetUtils.delta( this.set, SetUtils.mergeAll( this.reductionSets ) ).getRemovedElementSet();
  }
  
  @Override
  public int size()
  {
    return this.getReducedSet().size();
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.getReducedSet().isEmpty();
  }
  
  @Override
  public boolean contains( Object o )
  {
    return this.getReducedSet().contains( o );
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.getReducedSet().iterator();
  }
  
  @Override
  public Object[] toArray()
  {
    return this.getReducedSet().toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] a )
  {
    return this.getReducedSet().toArray( a );
  }
  
  @Override
  public boolean add( E e )
  {
    return this.getReducedSet().add( e );
  }
  
  @Override
  public boolean remove( Object o )
  {
    return this.getReducedSet().remove( o );
  }
  
  @Override
  public boolean containsAll( Collection<?> c )
  {
    return this.getReducedSet().containsAll( c );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> c )
  {
    return this.getReducedSet().addAll( c );
  }
  
  @Override
  public boolean retainAll( Collection<?> c )
  {
    return this.getReducedSet().retainAll( c );
  }
  
  @Override
  public boolean removeAll( Collection<?> c )
  {
    return this.getReducedSet().removeAll( c );
  }
  
  @Override
  public void clear()
  {
    this.getReducedSet().clear();
  }
  
  @Override
  public boolean equals( Object o )
  {
    return this.getReducedSet().equals( o );
  }
  
  @Override
  public int hashCode()
  {
    return this.getReducedSet().hashCode();
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ReducedSet [getReducedSet()=" );
    builder.append( this.getReducedSet() );
    builder.append( "]" );
    return builder.toString();
  }
  
}
