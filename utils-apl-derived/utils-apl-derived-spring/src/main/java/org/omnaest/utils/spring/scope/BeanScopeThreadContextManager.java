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
package org.omnaest.utils.spring.scope;

import org.springframework.beans.factory.config.Scope;

/**
 * A {@link BeanScopeThreadContextManager} allows to add and remove {@link Thread}s to or from a spring bean {@link Scope}.
 * 
 * @see Scope
 * @author Omnaest
 */
public interface BeanScopeThreadContextManager
{
  /**
   * Adds the method calling {@link Thread} to the bean scope managed by this {@link BeanScopeThreadContextManager}
   */
  public void addCurrentThreadToBeanScope();
  
  /**
   * Removes the method calling {@link Thread} to the bean scope managed by this {@link BeanScopeThreadContextManager}
   */
  public void removeCurrentThreadFromBeanScope();
}
