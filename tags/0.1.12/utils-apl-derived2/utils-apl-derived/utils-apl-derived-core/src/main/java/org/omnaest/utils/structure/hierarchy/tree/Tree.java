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

/**
 * A {@link Tree} acts as a container for a {@link TreeNode} object graph.
 * 
 * @see TreeNavigator
 * @see TreeHelper
 * @author Omnaest
 * @param <M>
 *          model of the tree (not necessarily the same as one of the {@link TreeNode}s models)
 * @param <TN>
 */
@SuppressWarnings("rawtypes")
public interface Tree<M, TN extends TreeNode>
{
  /**
   * Returns the model of the {@link Tree}
   * 
   * @return
   */
  public M getModel();
  
  /**
   * Returns the single root {@link TreeNode}
   * 
   * @return
   */
  public TN getTreeRootNode();
}
