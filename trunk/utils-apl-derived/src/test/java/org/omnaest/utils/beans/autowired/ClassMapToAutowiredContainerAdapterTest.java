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
package org.omnaest.utils.beans.autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @see ClassMapToAutowiredContainerAdapter
 * @author Omnaest
 */
public class ClassMapToAutowiredContainerAdapterTest
{
  
  @Rule
  public ContiPerfRule                               contiPerfRule      = new ContiPerfRule();
  
  /* ********************************************** Variables ********************************************** */
  private Map<Class<? extends TestClass>, TestClass> map                = new HashMap<Class<? extends TestClass>, TestClass>();
  private AutowiredContainer<TestClass>              autowiredContainer = ClassMapToAutowiredContainerAdapter.newInstance( this.map );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static class TestClass
  {
  }
  
  public static class TestClass2 extends TestClass
  {
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testGetValueSet()
  {
    //
    {
      //
      TestClass testClass = new TestClass();
      this.autowiredContainer.put( testClass );
      assertEquals( 1, this.autowiredContainer.getValueSet( TestClass.class ).size() );
      assertEquals( testClass, this.autowiredContainer.getValue( TestClass.class ) );
    }
    
    //
    {
      //
      TestClass testClass = new TestClass2();
      this.autowiredContainer.put( testClass );
      assertEquals( 2, this.autowiredContainer.getValueSet( TestClass.class ).size() );
      assertEquals( 1, this.autowiredContainer.getValueSet( TestClass2.class ).size() );
      assertNull( this.autowiredContainer.getValue( TestClass.class ) );
      assertEquals( testClass, this.autowiredContainer.getValue( TestClass2.class ) );
    }
    
    //
    {
      //
      this.autowiredContainer.put( new TestClass2() );
      assertEquals( 2, this.autowiredContainer.getValueSet( TestClass.class ).size() );
    }
  }
  
  @Test
  @PerfTest(invocations = 200)
  @Required(average = 10)
  public void testPerformancePerThousandGetValueInvocations()
  {
    //    
    Map<Class<? extends TestClass>, TestClass> classToObjectMap = new LinkedHashMap<Class<? extends TestClass>, TestClass>();
    AutowiredContainer<TestClass> autowiredContainer = new ClassMapToAutowiredContainerAdapter<TestClass>( classToObjectMap );
    
    //
    for ( int ii = 0; ii < 1000; ii++ )
    {
      //
      TestClass2 testClass2 = new TestClass2();
      autowiredContainer.put( testClass2 );
      assertEquals( testClass2, autowiredContainer.getValue( TestClass.class ) );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testPutWithTypes()
  {
    //
    {
      //
      TestClass testClass = new TestClass2();
      this.autowiredContainer.put( testClass, TestClass.class );
      assertEquals( 1, this.autowiredContainer.getValueSet( TestClass.class ).size() );
      assertEquals( 0, this.autowiredContainer.getValueSet( TestClass2.class ).size() );
    }
  }
  
  @Test
  public void testRemove()
  {
    //
    {
      //
      TestClass testClass2 = new TestClass2();
      TestClass testClass = new TestClass();
      this.autowiredContainer.put( testClass2 );
      this.autowiredContainer.put( testClass );
      assertEquals( 2, this.autowiredContainer.getValueSet( TestClass.class ).size() );
      assertEquals( 1, this.autowiredContainer.getValueSet( TestClass2.class ).size() );
      assertNull( this.autowiredContainer.getValue( TestClass.class ) );
      assertEquals( testClass2, this.autowiredContainer.getValue( TestClass2.class ) );
      
      //
      this.autowiredContainer.remove( testClass );
      assertTrue( this.autowiredContainer.isEmpty() );
    }
    {
      //
      TestClass testClass2 = new TestClass2();
      TestClass testClass = new TestClass();
      this.autowiredContainer.put( testClass2 );
      this.autowiredContainer.put( testClass );
      assertEquals( 2, this.autowiredContainer.getValueSet( TestClass.class ).size() );
      assertEquals( 1, this.autowiredContainer.getValueSet( TestClass2.class ).size() );
      assertNull( this.autowiredContainer.getValue( TestClass.class ) );
      assertEquals( testClass2, this.autowiredContainer.getValue( TestClass2.class ) );
      
      //
      this.autowiredContainer.remove( TestClass2.class );
      assertEquals( 1, this.autowiredContainer.size() );
    }
    
  }
  
}
