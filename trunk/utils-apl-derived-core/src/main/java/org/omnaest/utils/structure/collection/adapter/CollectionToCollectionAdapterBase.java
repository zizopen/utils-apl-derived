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
package org.omnaest.utils.structure.collection.adapter;

import java.util.Collection;
import java.util.Iterator;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.CollectionAbstract;
import org.omnaest.utils.structure.collection.CollectionUtils;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;
import org.omnaest.utils.structure.iterator.IteratorToIteratorAdapter;

import com.google.common.base.Joiner;

/**
 * Base implementation of an {@link CollectionToCollectionAdapter}
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public abstract class CollectionToCollectionAdapterBase<FROM, TO> extends CollectionAbstract<TO>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                               serialVersionUID = -6369301794639842338L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  protected final ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see CollectionToCollectionAdapterBase
   * @param elementBidirectionalConverter
   */
  protected CollectionToCollectionAdapterBase( ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    this.elementBidirectionalConverter = elementBidirectionalConverter;
    Assert.isNotNull( elementBidirectionalConverter, "elementBidirectionalConverter must not be null" );
  }
  
  @Override
  public int size()
  {
    // 
    return this.getCollection().size();
  }
  
  @Override
  public boolean contains( Object o )
  {
    //     
    Object elementConverted = tryConvertBackwards( o );
    return this.getCollection().contains( elementConverted );
  }
  
  /**
   * @param o
   * @return
   */
  private FROM tryConvertBackwards( Object o )
  {
    FROM elementConverted = null;
    {
      try
      {
        @SuppressWarnings("unchecked")
        TO element = (TO) o;
        elementConverted = this.elementBidirectionalConverter.convertBackwards( element );
      }
      catch ( Exception e )
      {
      }
    }
    return elementConverted;
  }
  
  @Override
  public Iterator<TO> iterator()
  {
    final Iterator<FROM> iterator = this.getCollection().iterator();
    return new IteratorToIteratorAdapter<TO, FROM>( iterator, this.elementBidirectionalConverter );
  }
  
  @Override
  public boolean add( TO element )
  {
    // 
    return this.getCollection().add( this.elementBidirectionalConverter.convertBackwards( element ) );
  }
  
  @Override
  public boolean remove( Object o )
  {
    // 
    return this.getCollection().remove( this.tryConvertBackwards( o ) );
  }
  
  protected abstract Collection<FROM> getCollection();
  
  @Override
  public String toString()
  {
    return CollectionUtils.toString( this, Joiner.on( "," ) );
  }
  
}
