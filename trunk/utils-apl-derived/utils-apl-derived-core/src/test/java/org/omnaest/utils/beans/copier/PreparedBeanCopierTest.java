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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.beans.copier.PreparedBeanCopier.Configuration;
import org.omnaest.utils.structure.collection.set.SetUtils;
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
                                                                                                                                                                              TestBeanTo.class ) );
  private TestBeanFrom                                   testBeanFrom         = new TestBeanFrom();
  {
    this.testBeanFrom.setFieldString( "test" );
    this.testBeanFrom.setFieldLong( 123l );
    this.testBeanFrom.setFieldLongIgnored( 12l );
    this.testBeanFrom.setFieldStringCommon( "testCommon" );
    this.testBeanFrom.setList( Arrays.asList( "a", "b", "c" ) );
    this.testBeanFrom.setSet( SetUtils.valueOf( "a", "b", "c2" ) );
    this.testBeanFrom.setMap( new MapBuilder<String, String>().linkedHashMap()
                                                              .put( "key1", "value1" )
                                                              .put( "key2", "value2" )
                                                              .build() );
  }
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  private static interface ITestBeanFrom
  {
    public String getFieldString();
    
    public Long getFieldLong();
    
    public String getFieldStringCommon();
    
    public List<String> getList();
    
    public Set<String> getSet();
    
    public Map<String, String> getMap();
  }
  
  private static interface ITestBeanTo
  {
    public String getFieldString();
    
    public Long getFieldLong();
    
    public String getFieldStringCommon();
    
    public List<String> getList();
    
    public Set<String> getSet();
    
    public Map<String, String> getMap();
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
  
  private static class TestBeanFrom extends TestBeanAbstract implements ITestBeanFrom
  {
    private String              fieldString      = null;
    private Long                fieldLong        = null;
    private Long                fieldLongIgnored = null;
    private List<String>        list             = null;
    private Set<String>         set              = null;
    private Map<String, String> map              = null;
    
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
    
  }
  
  private static class TestBeanTo extends TestBeanAbstract implements ITestBeanTo
  {
    private String              fieldString      = null;
    private Long                fieldLong        = null;
    private Long                fieldLongIgnored = null;
    private List<String>        list             = null;
    private Set<String>         set              = null;
    private Map<String, String> map              = null;
    
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
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testDeepCloneProperties()
  {
    //
    ITestBeanTo clone = this.preparedBeanCopier.deepCloneProperties( this.testBeanFrom );
    
    //
    assertNotNull( clone );
    assertEquals( this.testBeanFrom.getFieldString(), clone.getFieldString() );
    assertEquals( this.testBeanFrom.getFieldLong(), clone.getFieldLong() );
    assertEquals( this.testBeanFrom.getFieldStringCommon(), clone.getFieldStringCommon() );
    assertEquals( this.testBeanFrom.getList(), clone.getList() );
    assertEquals( this.testBeanFrom.getSet(), clone.getSet() );
    assertEquals( this.testBeanFrom.getMap(), clone.getMap() );
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
  public void testPerformance()
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
      clone.setMap( new MapBuilder<String, String>().linkedHashMap().putAll( this.testBeanFrom.getMap() ).build() );
    }
  }
  
}
