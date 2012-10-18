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
package org.omnaest.utils.beans.autowired;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

/**
 * @see AutowiredContainerUtils
 * @author Omnaest
 */
public class AutowiredContainerUtilsTest
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  final AutowiredContainer<TestEntityInterface> autowiredContainer = AutowiredContainerUtils.newInstance();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected static interface TestEntityInterface
  {
    
  }
  
  protected static class TestEntity implements TestEntityInterface
  {
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Before
  public void setUp()
  {
    for ( int ii = 0; ii < 1000; ii++ )
    {
      this.autowiredContainer.put( new TestEntity() );
    }
    for ( int ii = 0; ii < 10000; ii++ )
    {
      assertNotNull( this.autowiredContainer.getValue( TestEntity.class ) );
      assertNotNull( this.autowiredContainer.getValue( TestEntityInterface.class ) );
    }
  }
  
  @Test
  public void testNewInstancePerformanceGetExactType()
  {
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      TestEntityInterface value = this.autowiredContainer.getValue( TestEntity.class );
      assertNotNull( value );
    }
  }
  
  @Test
  public void testNewInstancePerformanceGetInterfaceType()
  {
    //
    for ( int ii = 0; ii < 100000; ii++ )
    {
      TestEntityInterface value = this.autowiredContainer.getValue( TestEntityInterface.class );
      assertNotNull( value );
    }
  }
  
}
