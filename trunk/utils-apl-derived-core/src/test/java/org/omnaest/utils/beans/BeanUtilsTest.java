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
package org.omnaest.utils.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.Required;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.structure.map.MapBuilderOld;
import org.omnaest.utils.structure.map.UnderlyingMapAware;

/**
 * @see BeanUtils
 * @author Omnaest
 */
public class BeanUtilsTest
{
  
  @Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestBeanImpl implements TestBean
  {
    /* ********************************************** Variables ********************************************** */
    protected String fieldString = null;
    protected Double fieldDouble = null;
    
    /* ********************************************** Methods ********************************************** */
    @Override
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    @Override
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    @Override
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    @Override
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.fieldDouble == null ) ? 0 : this.fieldDouble.hashCode() );
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestBeanImpl ) )
      {
        return false;
      }
      TestBeanImpl other = (TestBeanImpl) obj;
      if ( this.fieldDouble == null )
      {
        if ( other.fieldDouble != null )
        {
          return false;
        }
      }
      else if ( !this.fieldDouble.equals( other.fieldDouble ) )
      {
        return false;
      }
      if ( this.fieldString == null )
      {
        if ( other.fieldString != null )
        {
          return false;
        }
      }
      else if ( !this.fieldString.equals( other.fieldString ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  protected static interface TestBean
  {
    public void setFieldDouble( Double fieldDouble );
    
    @TestAnnotation(value = "value")
    public Double getFieldDouble();
    
    public void setFieldString( String fieldString );
    
    public String getFieldString();
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  protected static @interface TestAnnotation
  {
    public String value();
  }
  
  protected static class TestSuperClass
  {
    private String                    fieldString             = null;
    private List<ITestValue>          listTestValue           = null;
    private Map<TestValue, TestValue> testValueToTestValueMap = null;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public List<ITestValue> getListTestValue()
    {
      return this.listTestValue;
    }
    
    public void setListTestValue( List<ITestValue> listTestValue )
    {
      this.listTestValue = listTestValue;
    }
    
    public Map<TestValue, TestValue> getTestValueToTestValueMap()
    {
      return this.testValueToTestValueMap;
    }
    
    public void setTestValueToTestValueMap( Map<TestValue, TestValue> testValueToTestValueMap )
    {
      this.testValueToTestValueMap = testValueToTestValueMap;
    }
    
  }
  
  protected static class TestSubClass extends TestSuperClass
  {
    private Double fieldDouble = null;
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
  }
  
  protected static interface ITestValue
  {
    
  }
  
  protected static class TestValue implements ITestValue
  {
    private String more     = null;
    private String moreMore = null;
    
    public TestValue()
    {
      super();
    }
    
    public TestValue( String more, String moreMore )
    {
      super();
      this.more = more;
      this.moreMore = moreMore;
    }
    
    public String getMore()
    {
      return this.more;
    }
    
    public void setMore( String more )
    {
      this.more = more;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.more == null ) ? 0 : this.more.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestValue ) )
      {
        return false;
      }
      TestValue other = (TestValue) obj;
      if ( this.more == null )
      {
        if ( other.more != null )
        {
          return false;
        }
      }
      else if ( !this.more.equals( other.more ) )
      {
        return false;
      }
      return true;
    }
    
    /**
     * @return the moreMore
     */
    public String getMoreMore()
    {
      return this.moreMore;
    }
    
    /**
     * @param moreMore
     *          the moreMore to set
     */
    public void setMoreMore( String moreMore )
    {
      this.moreMore = moreMore;
    }
    
  }
  
  protected static class TestValueDTO extends TestValue
  {
  }
  
  protected static class TestValueOther implements ITestValue
  {
    private String more  = null;
    private String other = null;
    
    /**
     * @return the more
     */
    public String getMore()
    {
      return this.more;
    }
    
    /**
     * @param more
     *          the more to set
     */
    public void setMore( String more )
    {
      this.more = more;
    }
    
    /**
     * @return the other
     */
    public String getOther()
    {
      return this.other;
    }
    
    /**
     * @param other
     *          the other to set
     */
    public void setOther( String other )
    {
      this.other = other;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testDeterminePropertyNames()
  {
    //
    String[] propertyNames = BeanUtils.propertyNamesForMethodAccess( TestBean.class );
    
    //
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNames );
  }
  
  @Test
  public void testCopyPropertyValues()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    {
      //    
      TestBeanImpl beanDestination = new TestBeanImpl();
      assertTrue( !beanSource.equals( beanDestination ) );
      
      //
      BeanUtils.copyPropertyValues( beanSource, beanDestination );
      
      //
      assertEquals( beanSource, beanDestination );
    }
    
    //
    {
      //    
      TestBeanImpl beanDestination = new TestBeanImpl();
      assertTrue( !beanSource.equals( beanDestination ) );
      
      //
      BeanUtils.copyPropertyValues( beanSource, beanDestination, "fieldDouble" );
      
      //
      assertTrue( !beanSource.equals( beanDestination ) );
      assertEquals( beanSource.getFieldDouble(), beanDestination.getFieldDouble() );
    }
  }
  
  @Test
  public void testBeanMethodInformationSet()
  {
    //
    Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.beanMethodInformationSet( TestBean.class );
    assertNotNull( beanMethodInformationSet );
    assertEquals( 4, beanMethodInformationSet.size() );
    
    //
    for ( BeanMethodInformation beanMethodInformation : beanMethodInformationSet )
    {
      //
      if ( StringUtils.equals( "fieldString", beanMethodInformation.getPropertyName() ) )
      {
        if ( beanMethodInformation.isSetter() )
        {
          //
          assertFalse( beanMethodInformation.isGetter() );
          assertFalse( beanMethodInformation.isGetterWithAdditionalArguments() );
          assertFalse( beanMethodInformation.isSetterWithAdditionalArguments() );
          assertEquals( "public abstract void org.omnaest.utils.beans.BeanUtilsTest$TestBean.setFieldString(java.lang.String)",
                        beanMethodInformation.getMethod().toGenericString() );
        }
      }
    }
  }
  
  @Test
  public void testPropertyNameToBeanPropertyAccessorMap()
  {
    //
    assertEquals( 2, BeanUtils.propertyNameToBeanPropertyAccessorMap( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.propertyNameToBeanPropertyAccessorMap( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testBeanPropertyAccessorSet()
  {
    //
    assertEquals( 2, BeanUtils.beanPropertyAccessorSet( TestBean.class ).size() );
    assertEquals( 2, BeanUtils.beanPropertyAccessorSet( TestBeanImpl.class ).size() );
  }
  
  @Test
  public void testBeanPropertyAccessor() throws Throwable
  {
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.beanPropertyAccessor( TestBeanImpl.class,
                                                                                                TestBeanImpl.class.getDeclaredFields()[0] );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.beanPropertyAccessor( TestBeanImpl.class,
                                                                                                TestBeanImpl.class.getDeclaredFields()[0].getName() );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.beanPropertyAccessor( TestBeanImpl.class,
                                                                                                TestBeanImpl.class.getDeclaredMethod( "getFieldString" ) );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
    
    //
    {
      BeanPropertyAccessor<TestBeanImpl> beanPropertyAccessor = BeanUtils.beanPropertyAccessor( TestBeanImpl.class,
                                                                                                TestBeanImpl.class.getDeclaredField( "fieldString" ) );
      
      assertNotNull( beanPropertyAccessor );
      assertEquals( true, beanPropertyAccessor.hasGetterAndSetter() );
    }
  }
  
  @Test
  public void testPropertyNamesForMethodAccess()
  {
    String[] propertyNamesForMethodAccess = BeanUtils.propertyNamesForMethodAccess( TestBean.class );
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNamesForMethodAccess );
  }
  
  @Test
  public void testNumberOfProperties()
  {
    Assert.assertEquals( 2, BeanUtils.propertyNamesForMethodAccess( TestBean.class ).length );
  }
  
  @Test
  public void testTransformBeanIntoMap()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> map = BeanUtils.transformBeanIntoMap( beanSource );
    assertNotNull( map );
    assertEquals( 2, map.size() );
    assertEquals( beanSource.getFieldString(), map.get( "fieldString" ) );
    assertEquals( beanSource.getFieldDouble(), map.get( "fieldDouble" ) );
  }
  
  @Test
  public void testPropertyTypeToBeanPropertyAccessorSetMap()
  {
    //
    Map<Class<?>, Set<BeanPropertyAccessor<TestBean>>> propertyTypeToBeanPropertyAccessorSetMap = BeanUtils.propertyTypeToBeanPropertyAccessorSetMap( TestBean.class );
    assertEquals( 2, propertyTypeToBeanPropertyAccessorSetMap.size() );
    assertEquals( 1, propertyTypeToBeanPropertyAccessorSetMap.get( String.class ).size() );
    assertEquals( 1, propertyTypeToBeanPropertyAccessorSetMap.get( Double.class ).size() );
  }
  
  @Test
  public void testPropertyNameToBeanPropertyValueMap()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> propertynameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( beanSource );
    assertNotNull( propertynameToBeanPropertyValueMap );
    assertEquals( 2, propertynameToBeanPropertyValueMap.size() );
    assertEquals( beanSource.getFieldString(), propertynameToBeanPropertyValueMap.get( "fieldString" ) );
    assertEquals( beanSource.getFieldDouble(), propertynameToBeanPropertyValueMap.get( "fieldDouble" ) );
  }
  
  @Test
  public void testPropertyNameToBeanPropertyValueMapForPropertyNames()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> propertynameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( beanSource,
                                                                                                           "fieldString" );
    assertNotNull( propertynameToBeanPropertyValueMap );
    assertEquals( 1, propertynameToBeanPropertyValueMap.size() );
    assertEquals( beanSource.getFieldString(), propertynameToBeanPropertyValueMap.get( "fieldString" ) );
    
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testPropertyNameToBeanPropertyValueMapForPropertyNamesOrder()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    Map<String, Object> propertynameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( beanSource,
                                                                                                           "fieldString",
                                                                                                           "fieldDouble" );
    assertEquals( Arrays.asList( "fieldString", "fieldDouble" ),
                  new ArrayList<String>( propertynameToBeanPropertyValueMap.keySet() ) );
    assertEquals( Arrays.asList( beanSource.getFieldString(), beanSource.getFieldDouble() ),
                  new ArrayList<Object>( propertynameToBeanPropertyValueMap.values() ) );
    
  }
  
  @Test
  public void testBeanPropertyAccessors()
  {
    //      
    BeanPropertyAccessors<TestBean> beanPropertyAccessors = BeanUtils.beanPropertyAccessors( TestBean.class );
    assertEquals( 2, beanPropertyAccessors.size() );
    
    //
    TestBeanImpl testBeanSource = new TestBeanImpl();
    testBeanSource.setFieldString( "lala" );
    testBeanSource.setFieldDouble( 1.6 );
    
    //
    {
      //
      TestBeanImpl testBeanDestination = new TestBeanImpl();
      
      //
      beanPropertyAccessors.copyPropertyValues( testBeanSource, testBeanDestination );
      
      //
      assertEquals( 2, beanPropertyAccessors.size() );
      assertEquals( testBeanSource, testBeanDestination );
    }
    
    //
    {
      //
      TestBeanImpl testBeanDestination = new TestBeanImpl();
      
      //
      beanPropertyAccessors.remove( 1 );
      beanPropertyAccessors.copyPropertyValues( testBeanSource, testBeanDestination );
      
      //
      assertEquals( 1, beanPropertyAccessors.size() );
      assertTrue( testBeanSource.getFieldString().equals( testBeanDestination.getFieldString() )
                  || testBeanSource.getFieldDouble().equals( testBeanDestination.getFieldDouble() ) );
      assertFalse( testBeanSource.getFieldString().equals( testBeanDestination.getFieldString() )
                   && testBeanSource.getFieldDouble().equals( testBeanDestination.getFieldDouble() ) );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testPropertyValueList()
  {
    //
    TestBeanImpl beanSource = new TestBeanImpl();
    beanSource.setFieldString( "value1" );
    beanSource.setFieldDouble( 1.3 );
    
    //
    List<TestBeanImpl> propertyValueList = BeanUtils.propertyValueList( beanSource, "fieldString", "fieldDouble" );
    
    assertEquals( Arrays.asList( beanSource.getFieldString(), beanSource.getFieldDouble() ), propertyValueList );
  }
  
  @Test
  public void testCloneBean()
  {
    //
    TestBean testBean = new TestBeanImpl();
    testBean.setFieldString( "value1" );
    testBean.setFieldDouble( 1.234 );
    
    //
    TestBean clonedBean = BeanUtils.cloneBean( testBean );
    assertEquals( testBean, clonedBean );
  }
  
  @Test
  public void testCloneBeanUsingInstanceOfMap()
  {
    //
    TestBean testBean = new TestBeanImpl();
    testBean.setFieldString( "value1" );
    testBean.setFieldDouble( 1.234 );
    
    //
    boolean underlyingMapAware = true;
    TestBean clonedBean = BeanUtils.cloneBeanUsingInstanceOfMap( testBean, underlyingMapAware );
    
    assertEquals( testBean.getFieldDouble(), clonedBean.getFieldDouble() );
    assertEquals( testBean.getFieldString(), clonedBean.getFieldString() );
    
    //
    assertTrue( clonedBean instanceof UnderlyingMapAware );
  }
  
  @Test
  public void testPropertyNameToBeanPropertyAnnotationSetMap()
  {
    //
    Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap = BeanUtils.propertyNameToBeanPropertyAnnotationSetMap( TestBean.class );
    
    //
    assertNotNull( propertyNameToBeanPropertyAnnotationSetMap );
    assertEquals( 2, propertyNameToBeanPropertyAnnotationSetMap.size() );
    assertEquals( 1, propertyNameToBeanPropertyAnnotationSetMap.get( "fieldDouble" ).size() );
    assertEquals( "value",
                  ( (TestAnnotation) propertyNameToBeanPropertyAnnotationSetMap.get( "fieldDouble" ).iterator().next() ).value() );
  }
  
  @Test
  public void testPropertyNameToBeanPropertyAnnotationMap()
  {
    //
    Map<String, TestAnnotation> propertyNameToBeanPropertyAnnotationMap = BeanUtils.propertyNameToBeanPropertyAnnotationMap( TestBean.class,
                                                                                                                             TestAnnotation.class );
    
    //
    assertNotNull( propertyNameToBeanPropertyAnnotationMap );
    assertEquals( 2, propertyNameToBeanPropertyAnnotationMap.size() );
    assertEquals( null, propertyNameToBeanPropertyAnnotationMap.get( "fieldString" ) );
    assertEquals( "value", propertyNameToBeanPropertyAnnotationMap.get( "fieldDouble" ).value() );
  }
  
  @Test
  @PerfTest(invocations = 100)
  @Required(average = 20)
  public void testCloneBeanUsingNestedfMapPerformance()
  {
    //
    TestSubClass testBean = prepareTestSubClass();
    
    //
    TestSubClass clonedBean = BeanUtils.cloneBeanUsingNestedfMap( testBean );
    assertNotNull( clonedBean );
  }
  
  @Test
  public void testCloneBeanUsingNestedfMap()
  {
    //
    TestSubClass testBean = prepareTestSubClass();
    
    //
    TestSubClass clonedBean = BeanUtils.cloneBeanUsingNestedfMap( testBean );
    
    //
    assertEquals( testBean.getFieldDouble(), clonedBean.getFieldDouble() );
    assertEquals( testBean.getFieldString(), clonedBean.getFieldString() );
    assertEquals( testBean.getListTestValue(), clonedBean.getListTestValue() );
    assertEquals( testBean.getTestValueToTestValueMap(), clonedBean.getTestValueToTestValueMap() );
    assertNotSame( testBean.getTestValueToTestValueMap(), clonedBean.getTestValueToTestValueMap() );
    assertEquals( testBean.getTestValueToTestValueMap().entrySet().iterator().next(), clonedBean.getTestValueToTestValueMap()
                                                                                                .entrySet()
                                                                                                .iterator()
                                                                                                .next() );
    assertNotSame( testBean.getTestValueToTestValueMap().entrySet().iterator().next(), clonedBean.getTestValueToTestValueMap()
                                                                                                 .entrySet()
                                                                                                 .iterator()
                                                                                                 .next() );
    assertNotSame( testBean.getListTestValue(), clonedBean.getListTestValue() );
    assertNotSame( testBean.getListTestValue().get( 0 ), clonedBean.getListTestValue().get( 0 ) );
    
  }
  
  @Test
  public void testCloneBeanUsingNestedfMapWithSourceTypeToDestinationTypeMap()
  {
    //
    {
      //
      TestSubClass testBean = prepareTestSubClass();
      
      //    
      Map<Class<?>, Class<?>> sourceTypeTodestinationTypeMap = new MapBuilderOld<Class<?>, Class<?>>().hashMap()
                                                                                                   .put( TestValue.class,
                                                                                                         TestValueDTO.class )
                                                                                                   .build();
      
      //
      TestSubClass clonedBean = BeanUtils.cloneBeanUsingNestedfMap( testBean, sourceTypeTodestinationTypeMap );
      
      //
      assertEquals( testBean.getListTestValue(), clonedBean.getListTestValue() );
      assertTrue( clonedBean.getListTestValue().get( 0 ) instanceof TestValueDTO );
    }
    
    //
    {
      //
      TestSubClass testBean = prepareTestSubClass();
      
      //    
      Map<Class<?>, Class<?>> sourceTypeTodestinationTypeMap = new MapBuilderOld<Class<?>, Class<?>>().hashMap()
                                                                                                   .put( TestValue.class,
                                                                                                         TestValueOther.class )
                                                                                                   .build();
      
      //
      TestSubClass clonedBean = BeanUtils.cloneBeanUsingNestedfMap( testBean, sourceTypeTodestinationTypeMap );
      
      //
      assertTrue( clonedBean.getListTestValue().get( 0 ) instanceof TestValueOther );
      assertEquals( ( (TestValue) testBean.getListTestValue().get( 0 ) ).getMore(),
                    ( (TestValueOther) clonedBean.getListTestValue().get( 0 ) ).getMore() );
      assertNull( ( (TestValueOther) clonedBean.getListTestValue().get( 0 ) ).getOther() );
    }
    
  }
  
  private static TestSubClass prepareTestSubClass()
  {
    //
    TestSubClass testBean = new TestSubClass();
    testBean.setFieldString( "value1" );
    testBean.setFieldDouble( 1.234 );
    List<ITestValue> listTestValue = new ArrayList<ITestValue>( Arrays.asList( new TestValue( "a", "aa" ), new TestValue( "b",
                                                                                                                          "bb" ) ) );
    testBean.setListTestValue( listTestValue );
    
    Map<TestValue, TestValue> testValueToTestValueMap = new HashMap<TestValue, TestValue>();
    testValueToTestValueMap.put( new TestValue( "key1", "keykey1" ), new TestValue( "value1", "valuevalue1" ) );
    testValueToTestValueMap.put( new TestValue( "key2", "keykey2" ), new TestValue( "value2", "valuevalue2" ) );
    testBean.setTestValueToTestValueMap( testValueToTestValueMap );
    return testBean;
  }
}
