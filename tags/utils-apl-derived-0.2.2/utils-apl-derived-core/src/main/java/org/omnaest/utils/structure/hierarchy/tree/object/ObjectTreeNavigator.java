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
package org.omnaest.utils.structure.hierarchy.tree.object;

import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator;

/**
 * {@link TreeNavigator} for {@link ObjectTree} and {@link ObjectTreeNode}
 * 
 * @see ObjectUtils#treeNavigator(Object)
 * @author Omnaest
 */
public class ObjectTreeNavigator extends TreeNavigator<ObjectTree, ObjectTreeNode>
{
  
  /**
   * @see ObjectTreeNavigator
   * @param tree
   * @param cachingChildrenOfPathNodes
   */
  public ObjectTreeNavigator( ObjectTree tree, boolean cachingChildrenOfPathNodes )
  {
    super( tree, cachingChildrenOfPathNodes );
  }
  
  /**
   * @see ObjectTreeNavigator
   * @param tree
   */
  public ObjectTreeNavigator( ObjectTree tree )
  {
    super( tree );
  }
  
  /**
   * @see ObjectTreeNavigator
   * @param object
   * @param cachingChildrenOfPathNodes
   */
  public ObjectTreeNavigator( Object object, boolean cachingChildrenOfPathNodes )
  {
    super( new ObjectToTreeNodeAdapter( object ), cachingChildrenOfPathNodes );
  }
  
  /**
   * @see ObjectTreeNavigator
   * @param object
   */
  public ObjectTreeNavigator( Object object )
  {
    super( new ObjectToTreeNodeAdapter( object ) );
  }
  
}
