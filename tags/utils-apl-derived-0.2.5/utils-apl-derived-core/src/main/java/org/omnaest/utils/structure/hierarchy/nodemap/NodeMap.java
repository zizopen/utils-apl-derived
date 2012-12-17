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
package org.omnaest.utils.structure.hierarchy.nodemap;

import java.util.Map;

/**
 * A {@link NodeMap} allows to specify a structure using a {@link Map} hierarchy where all {@link NodeMap} instance can hold a
 * special model which should be accessed by traversing a structure path.
 * 
 * @see #getModel()
 * @see #setModel(Object)
 * @author Omnaest
 * @param <K>
 *          key
 * @param <M>
 *          model
 */
public interface NodeMap<K, M> extends Map<K, NodeMap<K, M>>
{
  public M getModel();
  
  public void setModel( M model );
}
