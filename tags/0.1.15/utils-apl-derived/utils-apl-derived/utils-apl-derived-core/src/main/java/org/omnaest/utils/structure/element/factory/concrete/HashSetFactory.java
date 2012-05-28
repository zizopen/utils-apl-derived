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
package org.omnaest.utils.structure.element.factory.concrete;

import java.util.HashSet;
import java.util.Set;

import org.omnaest.utils.structure.element.factory.FactoryTypeAware;

/**
 * {@link FactoryTypeAware} creating new instances of {@link HashSet}
 * 
 * @param <E>
 * @author Omnaest
 */
public class HashSetFactory<E> implements FactoryTypeAware<Set<E>>
{
  
  @Override
  public Set<E> newInstance()
  {
    return new HashSet<E>();
  }
  
  @Override
  public Class<?> getInstanceType()
  {
    return HashSet.class;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "HashSetFactory []" );
    return builder.toString();
  }
  
}
