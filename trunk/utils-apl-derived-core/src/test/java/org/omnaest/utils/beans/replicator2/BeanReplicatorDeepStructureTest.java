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
package org.omnaest.utils.beans.replicator2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.omnaest.utils.beans.replicator2.BeanReplicator.Declaration;
import org.omnaest.utils.beans.replicator2.BeanReplicator.DeclarationSupport;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorDeepStructureTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class TestLevel0
  {
    private TestLevel1 testLevel1;
    
    public TestLevel1 getTestLevel1()
    {
      return this.testLevel1;
    }
    
    public void setTestLevel1( TestLevel1 testLevel1 )
    {
      this.testLevel1 = testLevel1;
    }
    
  }
  
  private static class TestLevel1
  {
    private TestLevel2 testLevel2;
    
    public TestLevel2 getTestLevel2()
    {
      return this.testLevel2;
    }
    
    public void setTestLevel2( TestLevel2 testLevel2 )
    {
      this.testLevel2 = testLevel2;
    }
    
  }
  
  private static class TestLevel2
  {
    private TestLevel3 testLevel3;
    
    public TestLevel3 getTestLevel3()
    {
      return this.testLevel3;
    }
    
    public void setTestLevel3( TestLevel3 testLevel3 )
    {
      this.testLevel3 = testLevel3;
    }
  }
  
  private static class TestLevel3
  {
    private String fieldString;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testCloningOfDeepStructure()
  {
    TestLevel0 testLevel0 = generateDeepStructuredInstance();
    
    BeanCopier<TestLevel0> beanCopier = new BeanCopier<TestLevel0>( TestLevel0.class );
    TestLevel0 clone = beanCopier.clone( testLevel0 );
    
    assertNotNull( clone );
    assertNotNull( clone.getTestLevel1() );
    assertNotNull( clone.getTestLevel1().getTestLevel2() );
    assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3() );
    assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
    assertEquals( "test", clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
    
    assertNotSame( testLevel0, clone );
    assertNotSame( testLevel0.getTestLevel1(), clone.getTestLevel1() );
    assertNotSame( testLevel0.getTestLevel1().getTestLevel2(), clone.getTestLevel1().getTestLevel2() );
    assertNotSame( testLevel0.getTestLevel1().getTestLevel2().getTestLevel3(), clone.getTestLevel1()
                                                                                    .getTestLevel2()
                                                                                    .getTestLevel3() );
  }
  
  @Test
  public void testCloningOfDeepStructureWithIgnoringDeepnessLevel()
  {
    {
      TestLevel0 testLevel0 = generateDeepStructuredInstance();
      
      BeanCopier<TestLevel0> beanCopier = new BeanCopier<TestLevel0>( TestLevel0.class ).declare( new Declaration()
      {
        @Override
        public void declare( DeclarationSupport support )
        {
          support.setIgnoredDeepnessLevel( 4 );
        }
      } );
      TestLevel0 clone = beanCopier.clone( testLevel0 );
      
      assertNotNull( clone );
      assertNotNull( clone.getTestLevel1() );
      assertNotNull( clone.getTestLevel1().getTestLevel2() );
      assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3() );
      assertNull( clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
    }
    {
      TestLevel0 testLevel0 = generateDeepStructuredInstance();
      
      BeanCopier<TestLevel0> beanCopier = new BeanCopier<TestLevel0>( TestLevel0.class ).declare( new Declaration()
      {
        @Override
        public void declare( DeclarationSupport support )
        {
          support.setIgnoredDeepnessLevel( 1 );
        }
      } );
      TestLevel0 clone = beanCopier.clone( testLevel0 );
      
      assertNotNull( clone );
      assertNull( clone.getTestLevel1() );
    }
  }
  
  @Test
  public void testCloningOfDeepStructureWithPreservedDeepnessLevel()
  {
    {
      TestLevel0 testLevel0 = generateDeepStructuredInstance();
      
      BeanCopier<TestLevel0> beanCopier = new BeanCopier<TestLevel0>( TestLevel0.class ).declare( new Declaration()
      {
        @Override
        public void declare( DeclarationSupport support )
        {
          support.setPreservedDeepnessLevel( 3 );
        }
      } );
      TestLevel0 clone = beanCopier.clone( testLevel0 );
      
      assertNotNull( clone );
      assertNotNull( clone.getTestLevel1() );
      assertNotNull( clone.getTestLevel1().getTestLevel2() );
      assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3() );
      assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
      assertEquals( "test", clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
      
      assertNotSame( testLevel0, clone );
      assertNotSame( testLevel0.getTestLevel1(), clone.getTestLevel1() );
      assertNotSame( testLevel0.getTestLevel1().getTestLevel2(), clone.getTestLevel1().getTestLevel2() );
      assertSame( testLevel0.getTestLevel1().getTestLevel2().getTestLevel3(), clone.getTestLevel1()
                                                                                   .getTestLevel2()
                                                                                   .getTestLevel3() );
    }
    {
      TestLevel0 testLevel0 = generateDeepStructuredInstance();
      
      BeanCopier<TestLevel0> beanCopier = new BeanCopier<TestLevel0>( TestLevel0.class ).declare( new Declaration()
      {
        @Override
        public void declare( DeclarationSupport support )
        {
          support.setPreservedDeepnessLevel( 1 );
        }
      } );
      TestLevel0 clone = beanCopier.clone( testLevel0 );
      
      assertNotNull( clone );
      assertNotNull( clone.getTestLevel1() );
      assertNotNull( clone.getTestLevel1().getTestLevel2() );
      assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3() );
      assertNotNull( clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
      assertEquals( "test", clone.getTestLevel1().getTestLevel2().getTestLevel3().getFieldString() );
      
      assertNotSame( testLevel0, clone );
      assertSame( testLevel0.getTestLevel1(), clone.getTestLevel1() );
      
    }
  }
  
  private static TestLevel0 generateDeepStructuredInstance()
  {
    TestLevel0 testLevel0 = new TestLevel0();
    TestLevel1 testLevel1 = new TestLevel1();
    TestLevel2 testLevel2 = new TestLevel2();
    TestLevel3 testLevel3 = new TestLevel3();
    testLevel3.setFieldString( "test" );
    testLevel2.setTestLevel3( testLevel3 );
    testLevel1.setTestLevel2( testLevel2 );
    testLevel0.setTestLevel1( testLevel1 );
    return testLevel0;
  }
  
}
