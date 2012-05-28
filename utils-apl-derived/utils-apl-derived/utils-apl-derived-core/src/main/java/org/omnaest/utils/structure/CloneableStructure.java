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
package org.omnaest.utils.structure;

import java.util.Map;

/**
 * Interface for types which have to implement a {@link #cloneStructure()} method.
 * 
 * @author Omnaest
 * @see CloneableStructureAndContent
 * @see Cloneable
 * @param <T>
 */
public interface CloneableStructure<T>
{
  /**
   * Returns a copy of this object, instead of a shallow copy like {@link Object#clone()}. For high level structures like e.g. a
   * {@link Map} only the {@link Map} structure is cloned not the content.
   * 
   * @param <T>
   * @return
   */
  public T cloneStructure();
  
}
