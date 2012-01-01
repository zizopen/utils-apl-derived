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
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterDeclarableBindings;
import org.omnaest.utils.beans.replicator.BeanReplicator.AdapterSourceToTargetTypeMapBased;
import org.omnaest.utils.beans.replicator.BeanReplicatorTest.TestClass.TestClassSub;
import org.omnaest.utils.beans.replicator.BeanReplicatorTest.TestClassDTO.TestClassDTOSub;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   */
  protected static class TestClassDTO
  {
    /* ********************************************** Variables ********************************************** */
    private String          fieldString;
    private Integer         fieldInteger;
    private TestClassDTOSub testClassDTOSub;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    public static class TestClassDTOSub
    {
      private String fieldString = null;
      
      public TestClassDTOSub( String fieldString )
      {
        super();
        this.fieldString = fieldString;
      }
      
      public TestClassDTOSub()
      {
        super();
      }
      
      /**
       * @return the fieldString
       */
      public String getFieldString()
      {
        return this.fieldString;
      }
      
      /**
       * @param fieldString
       *          the fieldString to set
       */
      public void setFieldString( String fieldString )
      {
        this.fieldString = fieldString;
      }
      
    }
    
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
     * @see TestClassDTO
     */
    protected TestClassDTO()
    {
      super();
      this.fieldInteger = null;
      this.fieldString = null;
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
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TestClassDTO [fieldString=" );
      builder.append( this.fieldString );
      builder.append( ", fieldInteger=" );
      builder.append( this.fieldInteger );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @param fieldString
     *          the fieldString to set
     */
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    /**
     * @param fieldInteger
     *          the fieldInteger to set
     */
    public void setFieldInteger( Integer fieldInteger )
    {
      this.fieldInteger = fieldInteger;
    }
    
    /**
     * @return the testClassDTOSub
     */
    public TestClassDTOSub getTestClassDTOSub()
    {
      return this.testClassDTOSub;
    }
    
    /**
     * @param testClassDTOSub
     *          the testClassDTOSub to set
     */
    public void setTestClassDTOSub( TestClassDTOSub testClassDTOSub )
    {
      this.testClassDTOSub = testClassDTOSub;
    }
  }
  
  /**
   * @author Omnaest
   */
  protected static class TestClass
  {
    /* ********************************************** Variables ********************************************** */
    private String       fieldString;
    private Integer      fieldInteger;
    private TestClassSub testClassSub;
    
    /* ********************************************** Classes/Interfaces ********************************************** */
    
    public static class TestClassSub
    {
      private String fieldString = null;
      
      public TestClassSub( String fieldString )
      {
        super();
        this.fieldString = fieldString;
      }
      
      public TestClassSub()
      {
        super();
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
    
    /**
     * @see TestClass
     * @param fieldString
     * @param fieldInteger
     * @param testClassSub
     */
    public TestClass( String fieldString, Integer fieldInteger, TestClassSub testClassSub )
    {
      super();
      this.fieldString = fieldString;
      this.fieldInteger = fieldInteger;
      this.testClassSub = testClassSub;
    }
    
    protected TestClass()
    {
      super();
      this.fieldInteger = null;
      this.fieldString = null;
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
    
    /**
     * @return the testClassSub
     */
    public TestClassSub getTestClassSub()
    {
      return this.testClassSub;
    }
    
  }
  
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
        this.bind( source.getTestClassSub() ).to( target.getTestClassDTOSub() ).usingOngoingBeanReplication();
      }
    };
    
    Adapter adapterSourceToTargetTypeMapBased = new AdapterSourceToTargetTypeMapBased(
                                                                                       new MapBuilder<Class<?>, Class<?>>().linkedHashMap()
                                                                                                                           .put( TestClassSub.class,
                                                                                                                                 TestClassDTOSub.class )
                                                                                                                           .build() );
    
    //
    BeanReplicator beanReplicator = new BeanReplicator( adapter, adapterSourceToTargetTypeMapBased );
    
    //    
    final String fieldString = "10";
    final Integer fieldInteger = 5;
    TestClassSub testClassSub = new TestClassSub( "subfield" );
    TestClass testClass = new TestClass( fieldString, fieldInteger, testClassSub );
    
    TestClassDTO copy = beanReplicator.copy( testClass );
    assertNotNull( copy );
    assertEquals( 10, copy.getFieldInteger().intValue() );
    assertEquals( "5", copy.getFieldString() );
    
    //
    final TestClassDTOSub testClassDTOSub = copy.getTestClassDTOSub();
    assertNotNull( testClassDTOSub );
    assertEquals( testClassSub.getFieldString(), testClassDTOSub.getFieldString() );
  }
  
}
