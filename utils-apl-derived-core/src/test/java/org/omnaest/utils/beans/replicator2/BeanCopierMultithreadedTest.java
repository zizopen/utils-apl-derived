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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.BooleanUtils;
import org.junit.Test;
import org.omnaest.utils.structure.element.ExceptionHandledResult;
import org.omnaest.utils.threads.FutureTaskManager;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanCopierMultithreadedTest
{
  /* ************************************************** Constants *************************************************** */
  private static final int           NUMBER_OF_INVOCATIONS = 100;
  private BeanCopier<TestSingleBean> beanCopier            = new BeanCopier<TestSingleBean>( TestSingleBean.class );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class TestSingleBean
  {
    private String  fieldString;
    private Double  fieldDouble;
    private Boolean fieldBoolean;
    private Long    fieldLong;
    private Integer fieldInteger;
    private Float   fieldFloat;
    
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
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "SimpleBean [fieldString=" );
      builder.append( this.fieldString );
      builder.append( ", fieldLong=" );
      builder.append( this.fieldLong );
      builder.append( "]" );
      return builder.toString();
    }
    
    public Double getFieldDouble()
    {
      return this.fieldDouble;
    }
    
    public void setFieldDouble( Double fieldDouble )
    {
      this.fieldDouble = fieldDouble;
    }
    
    public Boolean getFieldBoolean()
    {
      return this.fieldBoolean;
    }
    
    public void setFieldBoolean( Boolean fieldBoolean )
    {
      this.fieldBoolean = fieldBoolean;
    }
    
    public Integer getFieldInteger()
    {
      return this.fieldInteger;
    }
    
    public void setFieldInteger( Integer fieldInteger )
    {
      this.fieldInteger = fieldInteger;
    }
    
    public Float getFieldFloat()
    {
      return this.fieldFloat;
    }
    
    public void setFieldFloat( Float fieldFloat )
    {
      this.fieldFloat = fieldFloat;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testCloningMultithreaded()
  {
    ExecutorService executorService = Executors.newFixedThreadPool( 10 );
    FutureTaskManager futureTaskManager = new FutureTaskManager( executorService );
    futureTaskManager.submitAndManage( new Callable<Boolean>()
    {
      @Override
      public Boolean call() throws Exception
      {
        boolean retval = false;
        try
        {
          for ( int ii = 0; ii < NUMBER_OF_INVOCATIONS; ii++ )
          {
            final TestSingleBean simpleBean = newPreparedSimpleBean();
            final TestSingleBean clone = new TestSingleBean();
            BeanCopierMultithreadedTest.this.beanCopier.copy( simpleBean, clone );
            assertSimpleBean( simpleBean, clone );
          }
          retval = true;
        }
        catch ( Throwable e )
        {
        }
        return retval;
      }
    }, 100 );
    ExceptionHandledResult<List<Object>> result = futureTaskManager.waitForAllTasksToFinish();
    List<Object> resultList = result.getResult();
    for ( Object object : resultList )
    {
      assertTrue( BooleanUtils.isTrue( (Boolean) object ) );
    }
  }
  
  private static void assertSimpleBean( final TestSingleBean simpleBean, TestSingleBean clone )
  {
    assertEquals( simpleBean.getFieldString(), clone.getFieldString() );
    assertEquals( simpleBean.getFieldLong(), clone.getFieldLong() );
    assertEquals( simpleBean.getFieldInteger(), clone.getFieldInteger() );
    assertEquals( simpleBean.getFieldDouble(), clone.getFieldDouble() );
    assertEquals( simpleBean.getFieldFloat(), clone.getFieldFloat() );
    assertEquals( simpleBean.getFieldBoolean(), clone.getFieldBoolean() );
  }
  
  private static TestSingleBean newPreparedSimpleBean()
  {
    final TestSingleBean simpleBean = new TestSingleBean();
    final int random = (int) ( Math.random() * 10000 );
    simpleBean.setFieldString( "test" + random );
    simpleBean.setFieldLong( 123l + random );
    simpleBean.setFieldBoolean( true );
    simpleBean.setFieldDouble( 123.5 + random );
    simpleBean.setFieldFloat( 156.7f + random );
    simpleBean.setFieldInteger( 12 + random );
    return simpleBean;
  }
}
