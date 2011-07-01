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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ListToTypeAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static interface TestInterface
  {
    public void setFieldStringList( List<String> fieldStringList );
    
    public List<String> getFieldStringList();
    
    public void setFieldDouble( Double fieldDouble );
    
    public Double getFieldDouble();
    
    public void setFieldString( String fieldString );
    
    public String getFieldString();
  }
  
  protected static class TestClass implements TestInterface
  {
    /* ********************************************** Variables ********************************************** */
    protected String       fieldString     = "";
    protected Double       fieldDouble     = 0.1;
    protected List<String> fieldStringList = new ArrayList<String>();
    
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
    public List<String> getFieldStringList()
    {
      return this.fieldStringList;
    }
    
    @Override
    public void setFieldStringList( List<String> fieldStringList )
    {
      this.fieldStringList = fieldStringList;
    }
    
  }
  
  @Test
  public void testNewInstance()
  {
    //
    TestInterface testInterface = new TestClass();
    
    //
    List<Object> list = new ArrayList<Object>();
    
    //
    TestInterface testInterfaceAdapter = ListToTypeAdapter.newInstance( list, TestInterface.class );
    
    //
    BeanUtils.copyPropertyValues( testInterface, testInterfaceAdapter );
    
    //
    assertEquals( 3, list.size() );
    assertEquals( testInterface, testInterfaceAdapter );
  }
  
}
