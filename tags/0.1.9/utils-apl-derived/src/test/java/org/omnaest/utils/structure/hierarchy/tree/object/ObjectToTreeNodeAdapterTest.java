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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor;
import org.omnaest.utils.structure.hierarchy.tree.TreeNavigator.TreeNodeVisitor.TraversalControl;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode.ObjectModel;

/**
 * @see ObjectToTreeNodeAdapter
 * @author Omnaest
 */
public class ObjectToTreeNodeAdapterTest
{
  @Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  private TestClass    testClass     = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    protected final TestClassSub testClassSub;
    protected final TestClass    testClass = this;
    protected final TestClassSub testClassSub2;
    protected final String       fieldString;
    protected final String       fieldString2;
    protected final Double       fieldDouble;
    
    /* ********************************************** Methods ********************************************** */
    public TestClass( TestClassSub testClassSub, String fieldString, String fieldString2, Double fieldDouble )
    {
      super();
      this.testClassSub = testClassSub;
      this.testClassSub2 = testClassSub;
      this.fieldString = fieldString;
      this.fieldString2 = fieldString2;
      this.fieldDouble = fieldDouble;
    }
    
    /**
     * @return the testClassSub
     */
    public TestClassSub getTestClassSub()
    {
      return this.testClassSub;
    }
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    /**
     * @return the fieldString2
     */
    public String getFieldString2()
    {
      return this.fieldString2;
    }
    
    /**
     * @return the fieldDouble
     */
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    /**
     * @return the testClass
     */
    public TestClass getTestClass()
    {
      return this.testClass;
    }
    
    /**
     * @return the testClassSub2
     */
    public TestClassSub getTestClassSub2()
    {
      return this.testClassSub2;
    }
    
  }
  
  protected static class TestClassSub
  {
    protected final String fieldString;
    
    public TestClassSub( String fieldString )
    {
      super();
      this.fieldString = fieldString;
    }
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    String fieldString3 = "value3";
    TestClassSub testClassSub = new TestClassSub( fieldString3 );
    String fieldString = "value1";
    String fieldString2 = "value2";
    Double fieldDouble = 1.234;
    this.testClass = new TestClass( testClassSub, fieldString, fieldString2, fieldDouble );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testNavigate()
  {
    //
    ObjectTree tree = new ObjectToTreeNodeAdapter( new ObjectModel( this.testClass ) );
    TreeNavigator<ObjectTree, ObjectTreeNode> treeNavigator = new TreeNavigator<ObjectTree, ObjectTreeNode>( tree );
    
    //
    {
      //
      ObjectTreeNode currentTreeNode = treeNavigator.getCurrentTreeNode();
      assertNotNull( currentTreeNode );
      assertEquals( this.testClass, currentTreeNode.getModel().getObject() );
    }
    
    //
    final Set<Object> traversedObjectSet = new HashSet<Object>();
    
    TreeNodeVisitor<ObjectTree, ObjectTreeNode> treeNodeVisitors = new TreeNodeVisitor<ObjectTree, ObjectTreeNode>()
    {
      @Override
      public TraversalControl visit( ObjectTreeNode treeNode, TreeNavigator<ObjectTree, ObjectTreeNode> treeNavigator )
      {
        //
        assertNotNull( treeNode );
        assertNotNull( treeNavigator );
        
        //
        final ObjectModel model = treeNode.getModel();
        if ( treeNavigator.hasParent() )
        {
          assertNotNull( model.getPropertyName() );
          assertNotNull( model.getField() );
          assertNotNull( model.getGetterMethod() );
          assertNull( model.getSetterMethod() );
        }
        assertNotNull( model.getObject() );
        
        //
        TraversalControl traversalControl = null;
        if ( traversedObjectSet.contains( model.getObject() ) )
        {
          traversalControl = TraversalControl.SKIP_CHILDREN;
        }
        
        //
        traversedObjectSet.add( model.getObject() );
        
        //
        return traversalControl;
      }
    };
    assertTrue( treeNavigator.hasChildren() );
    treeNavigator.traverse( TraversalControl.GO_ON_INCLUDE_ALREADY_TRAVERSED_NODES, treeNodeVisitors );
    
    //
    assertTrue( traversedObjectSet.contains( this.testClass.getFieldDouble() ) );
    assertTrue( traversedObjectSet.contains( this.testClass.getFieldString() ) );
    assertTrue( traversedObjectSet.contains( this.testClass.getFieldString2() ) );
    assertTrue( traversedObjectSet.contains( this.testClass.getTestClassSub() ) );
    assertTrue( traversedObjectSet.contains( this.testClass.getTestClassSub().getFieldString() ) );
    
    //
    assertTrue( treeNavigator.navigateToFirstChild().isNavigationSuccessful() );
    
    //
    assertTrue( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    assertFalse( treeNavigator.navigateToNextSibling().isNavigationSuccessful() );
    
    //
    assertTrue( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    assertTrue( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    assertFalse( treeNavigator.navigateToPreviousSibling().isNavigationSuccessful() );
    
    //
    assertTrue( treeNavigator.navigateToParent().isNavigationSuccessful() );
    {
      //
      ObjectTreeNode currentTreeNode = treeNavigator.getCurrentTreeNode();
      assertNotNull( currentTreeNode );
      assertEquals( this.testClass, currentTreeNode.getModel().getObject() );
    }
    
    //
    //System.out.print( treeNavigator );
  }
  
  @PerfTest(invocations = 200)
  @Required(average = 10)
  @Test
  public void testTraversalPerformance()
  {
    //
    ObjectTree tree = new ObjectToTreeNodeAdapter( new ObjectModel( this.testClass ) );
    TreeNavigator<ObjectTree, ObjectTreeNode> treeNavigator = new TreeNavigator<ObjectTree, ObjectTreeNode>( tree );
    
    //
    
    TreeNodeVisitor<ObjectTree, ObjectTreeNode> treeNodeVisitors = new TreeNodeVisitor<ObjectTree, ObjectTreeNode>()
    {
      @Override
      public TraversalControl visit( ObjectTreeNode treeNode, TreeNavigator<ObjectTree, ObjectTreeNode> treeNavigator )
      {
        //
        return null;
      }
    };
    treeNavigator.traverse( treeNodeVisitors );
    
  }
}
