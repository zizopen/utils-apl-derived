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
package org.omnaest.utils.beans.replicator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterDeclarableBindings;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorTest
{
  @Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();
  
  @PerfTest(invocations = 10)
  @Test
  public void testCopy()
  {
    //
    final AdapterDeclarableBindings<TestClass, TestClassDTO> adapter = new AdapterDeclarableBindings<TestClass, TestClassDTO>(
                                                                                                                               TestClass.class,
                                                                                                                               TestClassDTO.class )
    {
      @Override
      public void declareBindings( TestClass source, TestClassDTO target )
      {
        this.bind( source.getFieldString() ).to( target.getFieldInteger() ).using( new ElementConverterStringToInteger() );
        this.bind( source.getFieldInteger() ).to( target.getFieldString() ).usingAutodetectedElementConverter();
      }
    };
    
    //
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final String fieldString = "10";
    final Integer fieldInteger = 5;
    TestClass testClass = new TestClass( fieldString, fieldInteger );
    
    TestClassDTO copy = beanReplicator.copy( testClass );
    assertNotNull( copy );
    assertEquals( 10, copy.getFieldInteger().intValue() );
    assertEquals( "5", copy.getFieldString() );
  }
  
  @Test
  public void testReplicate()
  {
    
  }
  
}
