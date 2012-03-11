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
package org.omnaest.utils.spring.stateless;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @see StatelessValidatorBean
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "StatelessValidatorBeanTestAC.xml" })
public class StatelessValidatorBeanTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  protected StatelessValidatorBean statelessValidatorBean = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @StatelessValidation
  protected static class TestClassSupertype
  {
  }
  
  @StatelessValidation
  protected static interface TestClassInterface
  {
  }
  
  protected static class TestClass implements TestClassInterface
  {
    @SuppressWarnings("unused")
    private final static String fieldPrivateStatic = "";
    
    @SuppressWarnings("unused")
    @Autowired
    private TestClass2          testClass2         = null;
  }
  
  protected static class TestClass2 extends TestClassSupertype
  {
    @SuppressWarnings("unused")
    private final static String fieldPrivateStatic = "";
    
    @SuppressWarnings("unused")
    private String              fieldExcluded      = null;
  }
  
  protected static class TestClass3
  {
    @SuppressWarnings("unused")
    private final static String fieldPrivateStatic = "";
  }
  
  @SuppressWarnings("unused")
  protected static class TestClassFailing
  {
    private final static String fieldPrivateStatic    = "";
    private String              fieldNonPrivateStatic = "";
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testAfterPropertiesSet()
  {
    assertEquals( 2, this.statelessValidatorBean.counter );
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testValidateBeanType()
  {
    this.statelessValidatorBean.validateBeanType( TestClassFailing.class );
  }
  
}
