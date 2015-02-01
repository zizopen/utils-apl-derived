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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectToTreeNodeAdapter;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTree;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode.ObjectModel;

/**
 * @see TreeHelper
 * @author Omnaest
 */
public class TreeHelperTest
{
  
  /**
   * @author Omnaest
   */
  protected static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    protected final TestClassSub testClassSub = new TestClassSub();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @return the testClassSub
     */
    protected TestClassSub getTestClassSub()
    {
      return this.testClassSub;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  protected static class TestClassSub
  {
    /* ********************************************** Variables ********************************************** */
    protected final String fieldString  = "test";
    protected final String fieldString2 = "test2";
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @return the fieldString
     */
    protected String getFieldString()
    {
      return this.fieldString;
    }
    
  }
  
  @Test
  public void testTreeNodeToChildrenTreeNodeListMap()
  {
    //
    final TestClass testClass = new TestClass();
    final ObjectTree tree = new ObjectToTreeNodeAdapter( testClass );
    
    //
    Map<ObjectTreeNode, List<ObjectTreeNode>> treeNodeToChildrenTreeNodeListMap = TreeHelper.treeNodeToChildrenTreeNodeListMap( tree );
    
    //
    assertNotNull( treeNodeToChildrenTreeNodeListMap );
    assertEquals( 4, treeNodeToChildrenTreeNodeListMap.size() );
    
    //
    final Set<Integer> sizeSet = new TreeSet<Integer>();
    for ( ObjectTreeNode objectTreeNode : treeNodeToChildrenTreeNodeListMap.keySet() )
    {
      //
      final List<ObjectTreeNode> childrenList = treeNodeToChildrenTreeNodeListMap.get( objectTreeNode );
      sizeSet.add( childrenList.size() );
    }
    
    //
    assertEquals( Arrays.asList( 0, 1, 2 ), ListUtils.valueOf( sizeSet ) );
  }
  
  @Test
  public void testTreeNodeModelToChildrenTreeNodeModelListMap()
  {
    //
    final TestClass testClass = new TestClass();
    final ObjectTree tree = new ObjectToTreeNodeAdapter( testClass );
    
    //
    Map<ObjectModel, List<ObjectModel>> treeNodeModelToChildrenTreeNodeModelListMap = TreeHelper.treeNodeModelToChildrenTreeNodeModelListMap( tree );
    
    //
    assertNotNull( treeNodeModelToChildrenTreeNodeModelListMap );
    assertEquals( 4, treeNodeModelToChildrenTreeNodeModelListMap.size() );
    
    //
    final Set<Integer> sizeSet = new TreeSet<Integer>();
    for ( ObjectTreeNode.ObjectModel objectModel : treeNodeModelToChildrenTreeNodeModelListMap.keySet() )
    {
      //
      final List<ObjectTreeNode.ObjectModel> childrenList = treeNodeModelToChildrenTreeNodeModelListMap.get( objectModel );
      sizeSet.add( childrenList.size() );
    }
    
    //
    assertEquals( Arrays.asList( 0, 1, 2 ), ListUtils.valueOf( sizeSet ) );
  }
}
