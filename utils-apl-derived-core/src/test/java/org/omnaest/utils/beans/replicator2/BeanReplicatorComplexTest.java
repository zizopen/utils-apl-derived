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
package org.omnaest.utils.beans.replicator2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.beans.replicator2.BeanReplicator.Declaration;
import org.omnaest.utils.beans.replicator2.BeanReplicator.DeclarationSupport;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.map.MapBuilder;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorComplexTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  private static class TestBeanFrom
  {
    private String              fieldString;
    private Date                date;
    private Date                dateToCalendar;
    private Map<String, String> map;
    private Map<String, String> mapToBean;
    private List<String>        list;
    
    private TestSubBeanFrom     testSubBean;
    private TestSubBeanFrom     testSubBeanFrom;
    private TestSubBeanFrom     testSubBeanFrom2;
    private TestSubBeanFrom     testSubBeanToInterface;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public TestSubBeanFrom getTestSubBean()
    {
      return this.testSubBean;
    }
    
    public void setTestSubBean( TestSubBeanFrom testSubBean )
    {
      this.testSubBean = testSubBean;
    }
    
    public TestSubBeanFrom getTestSubBeanFrom()
    {
      return this.testSubBeanFrom;
    }
    
    public void setTestSubBeanFrom( TestSubBeanFrom testSubBeanFrom )
    {
      this.testSubBeanFrom = testSubBeanFrom;
    }
    
    public TestSubBeanFrom getTestSubBeanFrom2()
    {
      return this.testSubBeanFrom2;
    }
    
    public void setTestSubBeanFrom2( TestSubBeanFrom testSubBeanFrom2 )
    {
      this.testSubBeanFrom2 = testSubBeanFrom2;
    }
    
    public Map<String, String> getMap()
    {
      return this.map;
    }
    
    public void setMap( Map<String, String> map )
    {
      this.map = map;
    }
    
    public Map<String, String> getMapToBean()
    {
      return this.mapToBean;
    }
    
    public void setMapToBean( Map<String, String> mapToBean )
    {
      this.mapToBean = mapToBean;
    }
    
    public TestSubBeanFrom getTestSubBeanToInterface()
    {
      return this.testSubBeanToInterface;
    }
    
    public void setTestSubBeanToInterface( TestSubBeanFrom testSubBeanToInterface )
    {
      this.testSubBeanToInterface = testSubBeanToInterface;
    }
    
    public Date getDate()
    {
      return this.date;
    }
    
    public void setDate( Date date )
    {
      this.date = date;
    }
    
    public Date getDateToCalendar()
    {
      return this.dateToCalendar;
    }
    
    public void setDateToCalendar( Date dateToCalendar )
    {
      this.dateToCalendar = dateToCalendar;
    }
    
    public List<String> getList()
    {
      return this.list;
    }
    
    public void setList( List<String> list )
    {
      this.list = list;
    }
    
  }
  
  private static class TestSubBeanFrom
  {
    private String fieldString;
    private String fieldOther;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public String getFieldOther()
    {
      return this.fieldOther;
    }
    
    public void setFieldOther( String fieldOther )
    {
      this.fieldOther = fieldOther;
    }
    
  }
  
  private static class MapBean
  {
    private String key1;
    private String key2;
    
    public String getKey1()
    {
      return this.key1;
    }
    
    public void setKey1( String key1 )
    {
      this.key1 = key1;
    }
    
    public String getKey2()
    {
      return this.key2;
    }
    
    public void setKey2( String key2 )
    {
      this.key2 = key2;
    }
    
  }
  
  private static class TestBeanTo
  {
    private String                 fieldString;
    private Date                   date;
    private Calendar               dateToCalendar;
    
    private List<String>           list;
    
    private Map<String, String>    map;
    private MapBean                mapToBean;
    
    private TestSubBeanToInterface testSubBeanToInterface;
    private TestSubBeanTo          testSubBean;
    private TestSubBeanTo          testSubBeanTo;
    private TestSubBeanTo          testSubBeanTo2;
    
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    public TestSubBeanTo getTestSubBean()
    {
      return this.testSubBean;
    }
    
    public void setTestSubBean( TestSubBeanTo testSubBean )
    {
      this.testSubBean = testSubBean;
    }
    
    public TestSubBeanTo getTestSubBeanTo()
    {
      return this.testSubBeanTo;
    }
    
    public void setTestSubBeanTo( TestSubBeanTo testSubBeanTo )
    {
      this.testSubBeanTo = testSubBeanTo;
    }
    
    public TestSubBeanTo getTestSubBeanTo2()
    {
      return this.testSubBeanTo2;
    }
    
    public void setTestSubBeanTo2( TestSubBeanTo testSubBeanTo2 )
    {
      this.testSubBeanTo2 = testSubBeanTo2;
    }
    
    public Map<String, String> getMap()
    {
      return this.map;
    }
    
    public void setMap( Map<String, String> map )
    {
      this.map = map;
    }
    
    public MapBean getMapToBean()
    {
      return this.mapToBean;
    }
    
    public void setMapToBean( MapBean mapToBean )
    {
      this.mapToBean = mapToBean;
    }
    
    public TestSubBeanToInterface getTestSubBeanToInterface()
    {
      return this.testSubBeanToInterface;
    }
    
    public void setTestSubBeanToInterface( TestSubBeanToInterface testSubBeanToInterface )
    {
      this.testSubBeanToInterface = testSubBeanToInterface;
    }
    
    public Date getDate()
    {
      return this.date;
    }
    
    public void setDate( Date date )
    {
      this.date = date;
    }
    
    public Calendar getDateToCalendar()
    {
      return this.dateToCalendar;
    }
    
    public void setDateToCalendar( Calendar dateToCalendar )
    {
      this.dateToCalendar = dateToCalendar;
    }
    
    public List<String> getList()
    {
      return this.list;
    }
    
    public void setList( List<String> list )
    {
      this.list = list;
    }
    
  }
  
  private static class TestSubBeanTo implements TestSubBeanToInterface
  {
    private String fieldString;
    private String fieldOther2;
    
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
    public String getFieldOther2()
    {
      return this.fieldOther2;
    }
    
    @Override
    public void setFieldOther2( String fieldOther2 )
    {
      this.fieldOther2 = fieldOther2;
    }
    
  }
  
  private static interface TestSubBeanToInterface
  {
    
    public abstract void setFieldOther2( String fieldOther2 );
    
    public abstract String getFieldOther2();
    
    public abstract void setFieldString( String fieldString );
    
    public abstract String getFieldString();
    
  }
  
  /* *************************************************** Methods **************************************************** */
  @Test
  public void testBasicReplication()
  {
    BeanReplicator<TestBeanFrom, TestBeanTo> beanReplicator = new BeanReplicator<TestBeanFrom, TestBeanTo>( TestBeanFrom.class,
                                                                                                            TestBeanTo.class ).setExceptionHandler( new ExceptionHandler()
    {
      @Override
      public void handleException( Exception e )
      {
        e.printStackTrace();
        fail();
      }
    } );
    beanReplicator.declare( new Declaration()
    {
      @Override
      public void declare( DeclarationSupport support )
      {
        support.addTypeMapping( TestBeanFrom.class, TestBeanTo.class );
        support.addTypeMapping( TestSubBeanFrom.class, TestSubBeanTo.class );
        support.addPropertyNameMapping( "testSubBeanFrom2", "testSubBeanTo2" );
        support.addPropertyNameMapping( "testSubBean", "fieldOther", "fieldOther2" );
        {
          final Class<?> typeFrom = TestSubBeanFrom.class;
          final Class<?> typeTo = TestSubBeanTo.class;
          final String propertyNameFrom = "testSubBeanFrom";
          final String propertyNameTo = "testSubBeanTo";
          support.addTypeAndPropertyNameMapping( typeFrom, propertyNameFrom, typeTo, propertyNameTo );
        }
        support.addPropertyNameMapping( "testSubBeanToInterface", "fieldOther", "fieldOther2" );
        {
          final Class<?> typeFrom = TestSubBeanFrom.class;
          final Class<?> typeTo = TestSubBeanToInterface.class;
          final String propertyNameFrom = "testSubBeanToInterface";
          final String propertyNameTo = "testSubBeanToInterface";
          support.addTypeAndPropertyNameMapping( typeFrom, propertyNameFrom, typeTo, propertyNameTo );
        }
      }
    } );
    
    TestBeanFrom testBeanFrom = generateTestBeanFrom();
    TestBeanTo testBeanTo = new TestBeanTo();
    beanReplicator.copy( testBeanFrom, testBeanTo );
    
    assertTestBeanTo( testBeanFrom, testBeanTo );
    
  }
  
  private static void assertTestBeanTo( TestBeanFrom testBeanFrom, TestBeanTo testBeanTo )
  {
    assertEquals( testBeanFrom.getFieldString(), testBeanTo.getFieldString() );
    assertEquals( testBeanFrom.getMap(), testBeanTo.getMap() );
    assertEquals( testBeanFrom.getMapToBean().get( "key1" ), testBeanTo.getMapToBean().getKey1() );
    assertEquals( testBeanFrom.getMapToBean().get( "key2" ), testBeanTo.getMapToBean().getKey2() );
    assertEquals( testBeanFrom.getDate(), testBeanTo.getDate() );
    assertEquals( testBeanFrom.getDateToCalendar(), testBeanTo.getDateToCalendar().getTime() );
    assertEquals( testBeanFrom.getList(), testBeanTo.getList() );
    
    assertNotNull( testBeanTo.getTestSubBean() );
    assertEquals( testBeanFrom.getTestSubBean().getFieldString(), testBeanTo.getTestSubBean().getFieldString() );
    assertEquals( testBeanFrom.getTestSubBean().getFieldOther(), testBeanTo.getTestSubBean().getFieldOther2() );
    assertNotNull( testBeanTo.getTestSubBeanTo() );
    assertEquals( testBeanFrom.getTestSubBeanFrom().getFieldString(), testBeanTo.getTestSubBeanTo().getFieldString() );
    assertNotNull( testBeanTo.getTestSubBeanTo2() );
    assertEquals( testBeanFrom.getTestSubBeanFrom2().getFieldString(), testBeanTo.getTestSubBeanTo2().getFieldString() );
    assertEquals( testBeanFrom.getTestSubBeanToInterface().getFieldString(), testBeanTo.getTestSubBeanToInterface()
                                                                                       .getFieldString() );
    assertEquals( testBeanFrom.getTestSubBeanToInterface().getFieldOther(), testBeanTo.getTestSubBeanToInterface()
                                                                                      .getFieldOther2() );
  }
  
  private static TestBeanFrom generateTestBeanFrom()
  {
    TestBeanFrom testBeanFrom = new TestBeanFrom();
    {
      testBeanFrom.setFieldString( "test" );
      TestSubBeanFrom testSubBean = new TestSubBeanFrom();
      {
        testSubBean.setFieldString( "testsub" );
        testSubBean.setFieldOther( "testother" );
      }
      Map<String, String> map = new MapBuilder<String, String>().linkedHashMap()
                                                                .put( "key1", "value1" )
                                                                .put( "key2", "value2" )
                                                                .build();
      testBeanFrom.setTestSubBean( testSubBean );
      testBeanFrom.setTestSubBeanFrom( testSubBean );
      testBeanFrom.setTestSubBeanFrom2( testSubBean );
      testBeanFrom.setTestSubBeanToInterface( testSubBean );
      testBeanFrom.setMap( map );
      testBeanFrom.setMapToBean( map );
      testBeanFrom.setDate( new Date() );
      testBeanFrom.setDateToCalendar( new Date() );
      testBeanFrom.setList( Arrays.asList( "a", "b", "c" ) );
    }
    return testBeanFrom;
  }
  
}
