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
package org.omnaest.utils.structure.collection.set;

import java.util.Collection;
import java.util.Set;

import org.omnaest.utils.structure.collection.CollectionAbstract;
import org.omnaest.utils.structure.collection.CollectionUtils;

/**
 * Abstract implementation of a {@link Set}. Reduces the {@link Set} interface to the very needed methods.
 * 
 * @see Set
 * @see CollectionAbstract
 * @author Omnaest
 * @param <E>
 */
public abstract class SetAbstract<E> extends CollectionAbstract<E> implements Set<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 8562343467153734124L;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public int hashCode()
  {
    return CollectionUtils.hashCodeUnordered( this );
  }
  
  @Override
  public boolean equals( Object object )
  {
    if ( object instanceof Set )
    {
      return CollectionUtils.equalsUnordered( this, (Collection<?>) object );
    }
    return false;
  }
  
}
