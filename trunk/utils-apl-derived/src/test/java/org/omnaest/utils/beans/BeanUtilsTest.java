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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BeanUtilsTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected class TestBeanImpl implements TestBean
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
  }
  
  protected static interface TestBean
  {
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    public void setFieldString( String fieldString );
    
    public String getFieldString();
  }
  
  @Test
  public void testDeterminePropertyNames()
  {
    //
    String[] propertyNames = BeanUtils.determinePropertyNames( TestBean.class );
    
    //
    Assert.assertArrayEquals( new String[] { "fieldString", "fieldDouble" }, propertyNames );
  }
}
