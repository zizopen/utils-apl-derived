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
package org.omnaest.utils.structure.element.factory;

/**
 * Parameterized alternative to the {@link Factory} which provides a {@link #newInstance(Object...)}
 * 
 * @see #newInstance(Object...)
 * @author Omnaest
 */
public interface FactoryWithArguments<E, A> extends Factory<E>
{
  /**
   * Returns a new element instance for the given parameters
   * 
   * @param arguments
   * @return new instance
   */
  public E newInstance( A... arguments );
  
}
