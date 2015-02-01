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
package org.omnaest.utils.structure.collection.list.adapter;

import java.util.List;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * Adapter which allows a given {@link List} with a specific type to be used as a {@link List} with another specific type using a
 * {@link ElementBidirectionalConverter}
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class ListToListAdapter<FROM, TO> extends ListAbstract<TO>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                             serialVersionUID = 8535695705824958430L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final List<FROM>                              list;
  private final ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see ListToListAdapter
   * @param list
   * @param elementBidirectionalConverter
   */
  public ListToListAdapter( List<FROM> list, ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    super();
    this.list = list;
    this.elementBidirectionalConverter = elementBidirectionalConverter;
    Assert.isNotNull( list, "list must not be null" );
    Assert.isNotNull( elementBidirectionalConverter, "elementBidirectionalConverter must not be null" );
  }
  
  @Override
  public int size()
  {
    return this.list.size();
  }
  
  @Override
  public boolean add( TO e )
  {
    FROM from = this.elementBidirectionalConverter.convertBackwards( e );
    return this.list.add( from );
  }
  
  @Override
  public TO get( int index )
  {
    TO to = this.elementBidirectionalConverter.convert( this.list.get( index ) );
    return to;
  }
  
  @Override
  public TO set( int index, TO element )
  {
    FROM convertedElement = this.elementBidirectionalConverter.convertBackwards( element );
    FROM replacedElement = this.list.set( index, convertedElement );
    TO retval = this.elementBidirectionalConverter.convert( replacedElement );
    return retval;
  }
  
  @Override
  public void add( int index, TO element )
  {
    FROM from = this.elementBidirectionalConverter.convertBackwards( element );
    this.list.add( index, from );
  }
  
  @Override
  public TO remove( int index )
  {
    FROM removedElement = this.list.remove( index );
    TO retval = this.elementBidirectionalConverter.convert( removedElement );
    return retval;
  }
  
  @Override
  public int indexOf( Object o )
  {
    try
    {
      @SuppressWarnings("unchecked")
      FROM from = this.elementBidirectionalConverter.convertBackwards( (TO) o );
      return this.list.indexOf( from );
    }
    catch ( Exception e )
    {
      return -1;
    }
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    try
    {
      @SuppressWarnings("unchecked")
      FROM from = this.elementBidirectionalConverter.convertBackwards( (TO) o );
      return this.list.lastIndexOf( from );
    }
    catch ( Exception e )
    {
      return -1;
    }
  }
  
  @Override
  public void clear()
  {
    this.list.clear();
  }
  
}
