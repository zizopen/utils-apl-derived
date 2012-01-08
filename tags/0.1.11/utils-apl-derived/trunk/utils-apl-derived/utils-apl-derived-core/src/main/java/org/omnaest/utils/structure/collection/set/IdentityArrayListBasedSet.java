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

import org.omnaest.utils.structure.collection.list.IdentityArrayList;
import org.omnaest.utils.structure.collection.list.ListToSetAdapter;

/**
 * Ordered {@link Set} implementation using the {@link IdentityArrayList} as backing structure. This results in the identity
 * comparison used for {@link #contains(Object)} and other methods resolving objects by references.
 * 
 * @author Omnaest
 * @param <E>
 */
public class IdentityArrayListBasedSet<E> extends ListToSetAdapter<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 4225775781265130081L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * 
   */
  public IdentityArrayListBasedSet()
  {
    super( new IdentityArrayList<E>() );
  }
  
  /**
   * @param collection
   */
  public IdentityArrayListBasedSet( Collection<E> collection )
  {
    super( new IdentityArrayList<E>( collection ) );
  }
  
  @Override
  public String toString()
  {
    return super.toString();
  }
  
}
