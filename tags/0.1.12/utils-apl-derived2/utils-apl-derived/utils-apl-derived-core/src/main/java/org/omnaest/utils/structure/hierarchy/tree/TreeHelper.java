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
package org.omnaest.utils.structure.hierarchy.tree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * Helper for {@link Tree} instances
 * 
 * @author Omnaest
 */
public class TreeHelper
{
  
  /**
   * Similar to {@link #treeNodeToChildrenTreeNodeListMap(Tree)} but for the model of the {@link TreeNode}s
   * 
   * @param tree
   * @return
   */
  public static <M, TN extends TreeNode<M>, T extends Tree<M, TN>> Map<M, List<M>> treeNodeModelToChildrenTreeNodeModelListMap( T tree )
  {
    //
    final Map<TN, List<TN>> map = treeNodeToChildrenTreeNodeListMap( tree );
    
    //
    final ElementConverter<TN, M> treeNodeToModelConverter = new ElementConverter<TN, M>()
    {
      @Override
      public M convert( TN element )
      {
        return element != null ? element.getModel() : null;
      }
    };
    
    //
    ElementConverter<TN, M> keyElementConverter = treeNodeToModelConverter;
    ElementConverter<List<TN>, List<M>> valueElementConverter = new ElementConverter<List<TN>, List<M>>()
    {
      @Override
      public List<M> convert( List<TN> elementList )
      {
        return ListUtils.convert( elementList, treeNodeToModelConverter );
      }
    };
    return map == null ? null : MapUtils.convertMap( map, keyElementConverter, valueElementConverter );
  }
  
  /**
   * Returns a {@link Map} containing all {@link TreeNode}s of the hierarchy of a given {@link Tree} mapped to a {@link List} of
   * all children {@link TreeNode}s for each key node.
   * 
   * @param tree
   * @return
   */
  public static <M, TN extends TreeNode<M>, T extends Tree<M, TN>> Map<TN, List<TN>> treeNodeToChildrenTreeNodeListMap( T tree )
  {
    //    
    final Map<TN, List<TN>> retmap = new LinkedHashMap<TN, List<TN>>();
    
    //
    final TreeNavigator<T, TN> treeNavigator = new TreeNavigator<T, TN>( tree );
    TreeNodeVisitor<T, TN> treeNodeVisitor = new TreeNodeVisitor<T, TN>()
    {
      @SuppressWarnings("unchecked")
      @Override
      public TraversalControl visit( TN treeNode, TreeNavigator<T, TN> treeNavigator )
      {
        //
        TN currentTreeNode = treeNavigator.getCurrentTreeNode();
        retmap.put( currentTreeNode, (List<TN>) currentTreeNode.getChildrenList() );
        
        // 
        return null;
      }
    };
    treeNavigator.traverse( treeNodeVisitor );
    
    //
    return retmap;
  }
}
