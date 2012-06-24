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

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * Adapter from one {@link Collection} with its specific type to an underlying {@link Collection} with another specific type using
 * an {@link ElementBidirectionalConverter} instance to translate forth and back in types.
 * 
 * @author Omnaest
 */
public class CollectionToCollectionAdapter<FROM, TO> extends CollectionToCollectionAdapterBase<FROM, TO>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -1669630436990558468L;
  final Collection<FROM>    collection;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @param collection
   * @param elementBidirectionalConverter
   * @see CollectionToCollectionAdapter
   */
  public CollectionToCollectionAdapter( Collection<FROM> collection,
                                        ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    super( elementBidirectionalConverter );
    this.collection = collection;
    Assert.isNotNull( collection, "collection must not be null" );
    
  }
  
  @Override
  protected Collection<FROM> getCollection()
  {
    return this.collection;
  }
  
}
