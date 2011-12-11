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

import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;

public class BeanReplicatorTest
{
  
  /**
   * @author Omnaest
   */
  public static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    private final String  fieldString;
    private final Integer fieldInteger;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see TestClass
     * @param fieldString
     * @param fieldInteger
     */
    public TestClass( String fieldString, Integer fieldInteger )
    {
      super();
      this.fieldString = fieldString;
      this.fieldInteger = fieldInteger;
    }
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    /**
     * @return the fieldInteger
     */
    public Integer getFieldInteger()
    {
      return this.fieldInteger;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  public static class TestClassDTO
  {
    /* ********************************************** Variables ********************************************** */
    private final String  fieldString;
    private final Integer fieldInteger;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see TestClassDTO
     * @param fieldString
     * @param fieldInteger
     */
    public TestClassDTO( String fieldString, Integer fieldInteger )
    {
      super();
      this.fieldString = fieldString;
      this.fieldInteger = fieldInteger;
    }
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    /**
     * @return the fieldInteger
     */
    public Integer getFieldInteger()
    {
      return this.fieldInteger;
    }
  }
  
  @Test
  @Ignore
  public void testCopy()
  {
    Adapter<TestClass, TestClassDTO> adapter = new Adapter<TestClass, TestClassDTO>( TestClass.class, TestClassDTO.class )
    {
      @Override
      public void declare( TestClass source, TestClassDTO target )
      {
        this.map( source.getFieldString() ).to( target.getFieldInteger() ).using( new ElementConverterStringToInteger() );
        this.map( source.getFieldInteger() ).to( target.getFieldString() ).using( new ElementConverterNumberToString() );
      }
    };
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final String fieldString = "10";
    final Integer fieldInteger = 5;
    TestClass testClass = new TestClass( fieldString, fieldInteger );
    
    TestClassDTO copy = beanReplicator.copy( testClass );
    assertNotNull( copy );
  }
  
}
