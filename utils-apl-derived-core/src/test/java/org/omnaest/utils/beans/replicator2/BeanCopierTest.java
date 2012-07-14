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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanCopierTest
{
  /* ************************************************** Constants *************************************************** */
  private static final int           NUMBER_OF_INVOCATIONS = 10;
  private BeanCopier<TestSingleBean> beanCopier        = new BeanCopier<TestSingleBean>( TestSingleBean.class );
  
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
  public void testPerformanceNativeGetterSetter() throws IllegalAccessException,
                                                 InvocationTargetException
  {
    final TestSingleBean simpleBean = newPreparedSimpleBean();
    for ( int ii = 0; ii < NUMBER_OF_INVOCATIONS; ii++ )
    {
      TestSingleBean clone = new TestSingleBean();
      clone.setFieldString( simpleBean.getFieldString() );
      clone.setFieldLong( simpleBean.getFieldLong() );
      clone.setFieldInteger( simpleBean.getFieldInteger() );
      clone.setFieldDouble( simpleBean.getFieldDouble() );
      clone.setFieldFloat( simpleBean.getFieldFloat() );
      clone.setFieldBoolean( simpleBean.getFieldBoolean() );
      assertSimpleBean( simpleBean, clone );
    }
  }
  
  @Test
  public void testPerformanceBeanReplicator()
  {
    final TestSingleBean simpleBean = newPreparedSimpleBean();
    for ( int ii = 0; ii < NUMBER_OF_INVOCATIONS; ii++ )
    {
      final TestSingleBean clone = new TestSingleBean();
      this.beanCopier.copy( simpleBean, clone );
      assertSimpleBean( simpleBean, clone );
    }
  }
  
  @Test
  @Ignore
  public void testPerformanceCommonsBeanUtils() throws IllegalAccessException,
                                               InvocationTargetException
  {
    final TestSingleBean simpleBean = newPreparedSimpleBean();
    for ( int ii = 0; ii < NUMBER_OF_INVOCATIONS; ii++ )
    {
      TestSingleBean clone = new TestSingleBean();
      BeanUtils.copyProperties( clone, simpleBean );
      assertSimpleBean( simpleBean, clone );
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
    simpleBean.setFieldString( "test" );
    simpleBean.setFieldLong( 123l );
    simpleBean.setFieldBoolean( true );
    simpleBean.setFieldDouble( 123.5 );
    simpleBean.setFieldFloat( 156.7f );
    simpleBean.setFieldInteger( 12 );
    return simpleBean;
  }
}
