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
package org.omnaest.utils.structure.hierarchy.tree;

import java.util.List;

/**
 * Representation of a single node of a {@link Tree}. A {@link TreeNode} will hold references on its children and a data model.
 * 
 * @author Omnaest
 * @param <M>
 *          model of the {@link TreeNode} (Does not have to match the model of the surrounding {@link Tree})
 */
public interface TreeNode<M>
{
  
  /**
   * Returns the data model which belongs to the current {@link TreeNode}
   * 
   * @return
   */
  public M getModel();
  
  /**
   * Returns the {@link List} of references to the children of the current {@link TreeNode}
   * 
   * @return
   */
  public List<TreeNode<M>> getChildrenList();
}
