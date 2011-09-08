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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @see MapToAutowiredContainerAdapter
 * @author Omnaest
 */
public class MapToAutowiredContainerAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private Map<Class<? extends TestClass>, TestClass> map                = new HashMap<Class<? extends TestClass>, TestClass>();
  private AutowiredContainer<TestClass>              autowiredContainer = MapToAutowiredContainerAdapter.newInstance( this.map );
  
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
  
}
