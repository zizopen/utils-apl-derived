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
package org.omnaest.utils.structure.collection.set.adapter;

import java.util.Collection;
import java.util.Set;

import org.omnaest.utils.structure.collection.adapter.CollectionToCollectionAdapter;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverter;

/**
 * Adapter from one {@link Set} with a specific type to another {@link Set} with another specific type using a
 * {@link ElementBidirectionalConverter}
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class SetToSetAdapter<FROM, TO> extends CollectionToCollectionAdapter<FROM, TO> implements Set<TO>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -4801808423662504974L;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @param collection
   * @param elementBidirectionalConverter
   * @see SetToSetAdapter
   */
  public SetToSetAdapter( Collection<FROM> collection, ElementBidirectionalConverter<FROM, TO> elementBidirectionalConverter )
  {
    super( collection, elementBidirectionalConverter );
  }
}
