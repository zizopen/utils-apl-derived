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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.beans.replicator.BeanReplicator.Adapter;
import org.omnaest.utils.beans.replicator.BeanReplicatorTest.TestClass.TestClassSub;
import org.omnaest.utils.beans.replicator.BeanReplicatorTest.TestClassDTO.TestClassDTOSub;
import org.omnaest.utils.beans.replicator.adapter.AdapterComposite;
import org.omnaest.utils.beans.replicator.adapter.AdapterDeclarableBindings;
import org.omnaest.utils.beans.replicator.adapter.AdapterSourceToTargetTypeMapBased;
import org.omnaest.utils.beans.replicator.adapter.AdapterTypePatternBased;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToInteger;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorTest
{
  /* ********************************************** Variables ********************************************** */
  private BeanReplicator beanReplicatorDefault = new BeanReplicator();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   */
  protected static class TestClassDTO implements Comparable<TestClassDTO>
  {
    /* ********************************************** Variables ********************************************** */
    private String          fieldString;
    private Integer         fieldInteger;
    private TestClassDTOSub testClassSub;
    
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
    public TestClassDTOSub getTestClassSub()
    {
      return this.testClassSub;
    }
    
    /**
     * @param testClassDTOSub
     *          the testClassDTOSub to set
     */
    public void setTestClassSub( TestClassDTOSub testClassDTOSub )
    {
      this.testClassSub = testClassDTOSub;
    }
    
    @Override
    public int compareTo( TestClassDTO o )
    {
      return 0;
    }
  }
  
  /**
   * @author Omnaest
   */
  protected static class TestClass implements Comparable<TestClass>
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
    
    @Override
    public int compareTo( TestClass o )
    {
      return 0;
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
        this.bind( source.getTestClassSub() ).to( target.getTestClassSub() ).usingOngoingBeanReplication();
      }
    };
    
    Adapter adapterSourceToTargetTypeMapBased = new AdapterSourceToTargetTypeMapBased(
                                                                                       new MapBuilder<Class<?>, Class<?>>().linkedHashMap()
                                                                                                                           .put( TestClassSub.class,
                                                                                                                                 TestClassDTOSub.class )
                                                                                                                           .build() );
    
    //
    BeanReplicator beanReplicator = new BeanReplicator( adapter, adapterSourceToTargetTypeMapBased );
    
    TestClass testClass = prepareTestClassInstance();
    
    TestClassDTO copy = beanReplicator.copy( testClass );
    assertNotNull( copy );
    assertEquals( 10, copy.getFieldInteger().intValue() );
    assertEquals( "5", copy.getFieldString() );
    
    //
    final TestClassDTOSub testClassDTOSub = copy.getTestClassSub();
    assertNotNull( testClassDTOSub );
    assertEquals( "subfield", testClassDTOSub.getFieldString() );
  }
  
  @Test
  public void testCopyReplacementAdapter()
  {
    //    
    //final String replacement = "{package}.{type}DTO";
    Adapter adapter1 = new AdapterTypePatternBased( "org.omnaest.utils.beans.replicator.BeanReplicatorTest${type}DTO" );
    Adapter adapter2 = new AdapterTypePatternBased(
                                                    "org.omnaest.utils.beans.replicator.BeanReplicatorTest$TestClassDTO$TestClassDTOSub" );
    
    //
    BeanReplicator beanReplicator = new BeanReplicator( adapter1, adapter2 );
    
    TestClass testClass = prepareTestClassInstance();
    
    TestClassDTO copy = beanReplicator.copy( testClass );
    assertTestClassCopy( copy );
  }
  
  @PerfTest(invocations = 10)
  @Test
  public void testCopyMap()
  {
    //
    final Adapter adapter = prepareAdapter();
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final TestClass testClass = prepareTestClassInstance();
    
    //
    Map<Object, Object> map = new HashMap<Object, Object>();
    map.put( "key", testClass );
    
    Map<Object, Object> mapCopy = beanReplicator.copy( map );
    TestClassDTO copy = (TestClassDTO) mapCopy.get( "key" );
    assertTestClassCopy( copy );
  }
  
  @Test
  public void testCopyArray()
  {
    //
    final Adapter adapter = prepareAdapter();
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final TestClass testClass = prepareTestClassInstance();
    
    //
    Object[] objects = new Object[] { testClass };
    
    Object[] objectsCopy = beanReplicator.copy( objects );
    TestClassDTO copy = (TestClassDTO) objectsCopy[0];
    assertTestClassCopy( copy );
  }
  
  @Test
  public void testCopySet()
  {
    //    
    final Adapter adapter = prepareAdapter();
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final TestClass testClass = prepareTestClassInstance();
    final Set<TestClass> objects = SetUtils.valueOf( testClass );
    
    //
    Set<TestClassDTO> setCopy = beanReplicator.copy( objects );
    
    //
    TestClassDTO copy = setCopy.iterator().next();
    assertTestClassCopy( copy );
  }
  
  @Test
  public void testCopyList()
  {
    //    
    final Adapter adapter = prepareAdapter();
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final TestClass testClass = prepareTestClassInstance();
    final List<TestClass> objects = ListUtils.valueOf( testClass );
    
    //
    List<TestClassDTO> wrapperCopy = beanReplicator.copy( objects );
    
    //
    TestClassDTO copy = wrapperCopy.iterator().next();
    assertTestClassCopy( copy );
  }
  
  @Test
  public void testCopySortedSet()
  {
    //    
    final Adapter adapter = prepareAdapter();
    BeanReplicator beanReplicator = new BeanReplicator( adapter );
    
    //    
    final TestClass testClass = prepareTestClassInstance();
    final Set<TestClass> objects = new TreeSet<TestClass>( SetUtils.valueOf( testClass ) );
    
    //
    SortedSet<TestClassDTO> setCopy = beanReplicator.copy( objects );
    
    //
    TestClassDTO copy = setCopy.iterator().next();
    assertTestClassCopy( copy );
  }
  
  @PerfTest(invocations = 1000)
  @Required(average = 1)
  @Test
  public void testCopyStringAndPrimitivesAndBigIntegerAndBigDecimal()
  {
    //    
    BeanReplicator beanReplicator = this.beanReplicatorDefault;
    
    //    
    assertEquals( "1", beanReplicator.copy( "1" ) );
    assertEquals( 1, beanReplicator.copy( 1 ) );
    assertEquals( 1l, beanReplicator.copy( 1l ) );
    assertEquals( 1.2344, beanReplicator.copy( 1.2344 ) );
    assertEquals( Long.valueOf( 1l ), beanReplicator.copy( Long.valueOf( 1l ) ) );
    assertEquals( BigInteger.valueOf( 101l ), beanReplicator.copy( BigInteger.valueOf( 101l ) ) );
    assertEquals( BigDecimal.valueOf( 101.12 ), beanReplicator.copy( BigDecimal.valueOf( 101.12 ) ) );
  }
  
  /**
   * @return
   */
  private static Adapter prepareAdapter()
  {
    final Adapter adapter3 = new AdapterTypePatternBased( "org.omnaest.utils.beans.replicator.BeanReplicatorTest${type}DTO" );
    final Adapter adapter4 = new AdapterTypePatternBased(
                                                          "org.omnaest.utils.beans.replicator.BeanReplicatorTest$TestClassDTO$TestClassDTOSub" );
    final Adapter adapter = new AdapterComposite( adapter3, adapter4 );
    return adapter;
  }
  
  /**
   * @param copy
   */
  private static void assertTestClassCopy( TestClassDTO copy )
  {
    assertNotNull( copy );
    assertEquals( 5, copy.getFieldInteger().intValue() );
    assertEquals( "10", copy.getFieldString() );
    
    //
    final TestClassDTOSub testClassDTOSub = copy.getTestClassSub();
    assertNotNull( testClassDTOSub );
    assertEquals( "subfield", testClassDTOSub.getFieldString() );
  }
  
  /**
   * @return
   */
  private static TestClass prepareTestClassInstance()
  {
    final String fieldString = "10";
    final Integer fieldInteger = 5;
    TestClassSub testClassSub = new TestClassSub( "subfield" );
    TestClass testClass = new TestClass( fieldString, fieldInteger, testClassSub );
    return testClass;
  }
  
}
