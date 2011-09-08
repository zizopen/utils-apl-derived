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
package org.omnaest.utils.beans.autowired;

import java.util.Iterator;
import java.util.Set;

/**
 * Abstract implementation for {@link AutowiredContainer} which reduces the number of methods to be implemented.
 * 
 * @param <E>
 * @author Omnaest
 */
public abstract class AutowiredContainerAbstract<E> implements AutowiredContainer<E>
{
  /* ********************************************** Constants ********************************************** */
  private static final long    serialVersionUID = -7792783078590040662L;
  /* ********************************************** Variables ********************************************** */
  protected Class<? extends E> clazz            = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @SuppressWarnings("unchecked")
  @Override
  public Iterator<E> iterator()
  {
    return this.getValueSet( (Class<E>) this.clazz ).iterator();
  }
  
  @Override
  public <O extends E> O getValue( Class<O> clazz )
  {
    //
    O retval = null;
    
    //
    Set<O> valueSet = this.getValueSet( clazz );
    if ( valueSet != null && valueSet.size() == 1 )
    {
      retval = valueSet.iterator().next();
    }
    
    //
    return retval;
  }
  
}
