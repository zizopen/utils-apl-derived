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
package org.omnaest.utils.beans.copier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.beans.copier.PreparedBeanCopier.Configuration;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see PreparedBeanCopier
 * @author Omnaest
 */
public class PreparedBeanCopierTest
{
  /* ************************************************** Constants *************************************************** */
  private static final int                               COPY_TEST_ITERATIONS = 1000000;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private PreparedBeanCopier<ITestBeanFrom, ITestBeanTo> preparedBeanCopier   = new PreparedBeanCopier<ITestBeanFrom, ITestBeanTo>(
                                                                                                                                    ITestBeanFrom.class,
                                                                                                                                    TestBeanTo.class,
                                                                                                                                    new Configuration().addTypeToTypeMapping( ITestBeanFrom.class,
                                                                                                                                                                              TestBeanTo.class )
                                                                                                                                                       .addTypeToTypeMapping( TestSubBeanFrom.class,
                                                                                                                                                                              TestSubBeanTo.class ) );
  private TestBeanFrom                                   testBeanFrom         = new TestBeanFrom();
  {
    this.testBeanFrom.setFieldString( "test" );
    this.testBeanFrom.setFieldLong( 123l );
    this.testBeanFrom.setFieldLongIgnored( 12l );
    this.testBeanFrom.setFieldStringCommon( "testCommon" );
    this.testBeanFrom.setList( Arrays.asList( "a", "b", "c" ) );
    this.testBeanFrom.setCollection( Arrays.asList( "a", "b", "c3" ) );
    this.testBeanFrom.setSet( SetUtils.valueOf( "a", "b", "c2" ) );
    this.testBeanFrom.setMap( new MapBuilder<String, String>().linkedHashMap()
                                                              .put( "key1", "value1" )
                                                              .put( "key2", "value2" )
                                                              .build() );
    this.testBeanFrom.setTestSubBean( new TestSubBeanFrom().setFieldString( "test" ) );
  }
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  private static interface ITestBeanFrom
  {
    public String getFieldString();
    
    public Long getFieldLong();
    
    public String getFieldStringCommon();
    
    public String getFieldNonMatching1();
    
    public List<String> getList();
    
    public Set<String> getSet();
    
    public Collection<String> getCollection();
    
    public Map<String, String> getMap();
    
    public TestSubBeanFrom getTestSubBean();
    
  }
  
  private static interface ITestBeanTo
  {
    public String getFieldString();
    
    public Long getFieldLong();
    
    public String getFieldStringCommon();
    
    public String getFieldNonMatching2();
    
    public List<String> getList();
    
    public Set<String> getSet();
    
    public Collection<String> getCollection();
    
    public Map<String, String> getMap();
    
    public TestSubBeanTo getTestSubBean();
  }
  
  private static abstract class TestBeanAbstract
  {
    private String fieldStringCommon = null;
    
    public String getFieldStringCommon()
    {
      return this.fieldStringCommon;
    }
    
    public void setFieldStringCommon( String fieldStringCommon )
    {
      this.fieldStringCommon = fieldStringCommon;
    }
    
  }
  
  private static class TestSubBeanFrom
  {
    private String fieldString         = null;
    @SuppressWarnings("unused")
    private String fieldNonMatchingSub = null;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public TestSubBeanFrom setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
      return this;
    }
  }
  
  private static class TestSubBeanTo
  {
    private String fieldString = null;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public TestSubBeanTo setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
      return this;
    }
  }
  
  private static class TestBeanFrom extends TestBeanAbstract implements ITestBeanFrom
  {
    private String              fieldString      = null;
    private Long                fieldLong        = null;
    private Long                fieldLongIgnored = null;
    private List<String>        list             = null;
    private Set<String>         set              = null;
    private Collection<String>  collection       = null;
    private Map<String, String> map              = null;
    private TestSubBeanFrom     testSubBeanFrom  = null;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Long getFieldLong()
    {
      return this.fieldLong;
    }
    
    public void setFieldLong( Long fieldLong )
    {
      this.fieldLong = fieldLong;
    }
    
    public Long getFieldLongIgnored()
    {
      return this.fieldLongIgnored;
    }
    
    public void setFieldLongIgnored( Long fieldLongIgnored )
    {
      this.fieldLongIgnored = fieldLongIgnored;
    }
    
    public List<String> getList()
    {
      return this.list;
    }
    
    public void setList( List<String> list )
    {
      this.list = list;
    }
    
    public Map<String, String> getMap()
    {
      return this.map;
    }
    
    public void setMap( Map<String, String> map )
    {
      this.map = map;
    }
    
    public Set<String> getSet()
    {
      return this.set;
    }
    
    public void setSet( Set<String> set )
    {
      this.set = set;
    }
    
    public Collection<String> getCollection()
    {
      return this.collection;
    }
    
    public void setCollection( Collection<String> collection )
    {
      this.collection = collection;
    }
    
    public TestSubBeanFrom getTestSubBean()
    {
      return this.testSubBeanFrom;
    }
    
    public void setTestSubBean( TestSubBeanFrom testSubBeanFrom )
    {
      this.testSubBeanFrom = testSubBeanFrom;
    }
    
    @Override
    public String getFieldNonMatching1()
    {
      return null;
    }
    
  }
  
  private static class TestBeanTo extends TestBeanAbstract implements ITestBeanTo
  {
    private String              fieldString      = null;
    private Long                fieldLong        = null;
    private Long                fieldLongIgnored = null;
    private List<String>        list             = null;
    private Set<String>         set              = null;
    private Collection<String>  collection       = null;
    private Map<String, String> map              = null;
    private TestSubBeanTo       testSubBeanTo    = null;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public Long getFieldLong()
    {
      return this.fieldLong;
    }
    
    public void setFieldLong( Long fieldLong )
    {
      this.fieldLong = fieldLong;
    }
    
    public Long getFieldLongIgnored()
    {
      return this.fieldLongIgnored;
    }
    
    public void setFieldLongIgnored( Long fieldLongIgnored )
    {
      this.fieldLongIgnored = fieldLongIgnored;
    }
    
    public List<String> getList()
    {
      return this.list;
    }
    
    public void setList( List<String> list )
    {
      this.list = list;
    }
    
    public Map<String, String> getMap()
    {
      return this.map;
    }
    
    public void setMap( Map<String, String> map )
    {
      this.map = map;
    }
    
    public Set<String> getSet()
    {
      return this.set;
    }
    
    public void setSet( Set<String> set )
    {
      this.set = set;
    }
    
    public Collection<String> getCollection()
    {
      return this.collection;
    }
    
    public void setCollection( Collection<String> collection )
    {
      this.collection = collection;
    }
    
    public TestSubBeanTo getTestSubBean()
    {
      return this.testSubBeanTo;
    }
    
    public void setTestSubBean( TestSubBeanTo testSubBeanTo )
    {
      this.testSubBeanTo = testSubBeanTo;
    }
    
    @Override
    public String getFieldNonMatching2()
    {
      return null;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Before
  public void setUp()
  {
    //
    assertTrue( this.preparedBeanCopier.hasNonMatchingProperties() );
    //System.out.println( this.preparedBeanCopier.getNonMatchingPropertyNameList() );
    
    final List<String> nonMatchingPropertyNameList = this.preparedBeanCopier.getNonMatchingPropertyNameList();
    assertEquals( 5, nonMatchingPropertyNameList.size() );
    assertEquals( SetUtils.valueOf( "fieldNonMatching1", "fieldLongIgnored", "testSubBeanTo", "fieldNonMatching2",
                                    "fieldNonMatchingSub" ), SetUtils.valueOf( nonMatchingPropertyNameList ) );
    
  }
  
  @Test
  public void testDeepCloneProperties()
  {
    //
    ITestBeanTo clone = this.preparedBeanCopier.deepCloneProperties( this.testBeanFrom );
    
    //
    assertTestBeanClone( clone );
  }
  
  @Test
  @SuppressWarnings("unchecked")
  public void testDeepClonePropertiesSerializable()
  {
    //
    final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
    SerializationUtils.serialize( this.preparedBeanCopier, byteArrayContainer.getOutputStream() );
    PreparedBeanCopier<ITestBeanFrom, ITestBeanTo> preparedBeanCopierClone = (PreparedBeanCopier<ITestBeanFrom, ITestBeanTo>) SerializationUtils.deserialize( byteArrayContainer.getInputStream() );
    
    //
    ITestBeanTo clone = preparedBeanCopierClone.deepCloneProperties( this.testBeanFrom );
    assertTestBeanClone( clone );
  }
  
  private void assertTestBeanClone( ITestBeanTo clone )
  {
    assertNotNull( clone );
    assertEquals( this.testBeanFrom.getFieldString(), clone.getFieldString() );
    assertEquals( this.testBeanFrom.getFieldLong(), clone.getFieldLong() );
    assertEquals( this.testBeanFrom.getFieldStringCommon(), clone.getFieldStringCommon() );
    assertEquals( this.testBeanFrom.getList(), clone.getList() );
    assertEquals( this.testBeanFrom.getSet(), clone.getSet() );
    assertEquals( this.testBeanFrom.getCollection(), clone.getCollection() );
    assertEquals( this.testBeanFrom.getMap(), clone.getMap() );
    assertNotNull( clone.getTestSubBean() );
    assertEquals( this.testBeanFrom.getTestSubBean().getFieldString(), clone.getTestSubBean().getFieldString() );
    assertNull( ( (TestBeanTo) clone ).getFieldLongIgnored() );
  }
  
  @Test
  @Ignore("Long running performance test")
  public void testPerformancePreparedBeanCopier()
  {
    for ( int ii = 0; ii < PreparedBeanCopierTest.COPY_TEST_ITERATIONS; ii++ )
    {
      ITestBeanTo clone = this.preparedBeanCopier.deepCloneProperties( this.testBeanFrom );
      assertNotNull( clone );
    }
  }
  
  @Test
  @Ignore("Long running performance test")
  public void testPerformanceCommonsBeanUtils() throws IllegalAccessException,
                                               InvocationTargetException
  {
    for ( int ii = 0; ii < PreparedBeanCopierTest.COPY_TEST_ITERATIONS; ii++ )
    {
      Object clone = new TestBeanTo();
      BeanUtils.copyProperties( clone, this.testBeanFrom );
      assertNotNull( clone );
    }
  }
  
  @Test
  @Ignore("Long running performance test")
  public void testPerformanceDirectGetterSetter()
  {
    for ( int ii = 0; ii < PreparedBeanCopierTest.COPY_TEST_ITERATIONS; ii++ )
    {
      TestBeanTo clone = new TestBeanTo();
      clone.setFieldLong( this.testBeanFrom.getFieldLong() );
      clone.setFieldLongIgnored( this.testBeanFrom.getFieldLongIgnored() );
      clone.setFieldString( this.testBeanFrom.getFieldString() );
      clone.setFieldStringCommon( this.testBeanFrom.getFieldStringCommon() );
      clone.setList( new ArrayList<String>( this.testBeanFrom.getList() ) );
      clone.setSet( new LinkedHashSet<String>( this.testBeanFrom.getSet() ) );
      clone.setCollection( new LinkedHashSet<String>( this.testBeanFrom.getCollection() ) );
      clone.setMap( new MapBuilder<String, String>().linkedHashMap().putAll( this.testBeanFrom.getMap() ).build() );
      clone.setTestSubBean( new TestSubBeanTo().setFieldString( this.testBeanFrom.getTestSubBean().getFieldString() ) );
    }
  }
  
}
