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
package org.omnaest.utils.spring.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.spring.scope.ThreadLocalBeanScope.ThreadLocalScopeControl;
import org.omnaest.utils.structure.element.ElementHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "ThreadLocalBeanScopeTestAC.xml" })
public class ThreadLocalBeanScopeTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  private ThreadLocalScopeControl threadLocalScopeControl = null;
  
  @Autowired
  private TestBean                testBean                = null;
  
  private ExecutorService         executorService         = Executors.newFixedThreadPool( 10 );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  public static class TestBean
  {
    /* ********************************************** Variables ********************************************** */
    private String field = null;
    
    /* ********************************************** Methods ********************************************** */
    public String getField()
    {
      return this.field;
    }
    
    public void setField( String field )
    {
      this.field = field;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    assertNotNull( this.threadLocalScopeControl );
    assertNotNull( this.testBean );
  }
  
  @Test
  public void testNewThreadLocalScopeControl() throws InterruptedException,
                                              ExecutionException
  {
    //
    this.testBean.setField( "test1" );
    assertEquals( "test1", this.testBean.getField() );
    
    //
    submitInterferenceRunnable();
    
    //
    final ElementHolder<String> rollingRunnableElementHolder = new ElementHolder<String>();
    this.executorService.submit( this.threadLocalScopeControl.runnableDecoratorRolling( new Runnable()
    {
      @Override
      public void run()
      {
        rollingRunnableElementHolder.setElement( ThreadLocalBeanScopeTest.this.testBean.getField() );
      }
    } ) );
    Future<String> rollingCallableValueFuture = this.executorService.submit( this.threadLocalScopeControl.callableDecoratorRolling( new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return ThreadLocalBeanScopeTest.this.testBean.getField();
      }
    } ) );
    
    //
    final ElementHolder<String> newRunnableElementHolder = new ElementHolder<String>();
    this.executorService.submit( this.threadLocalScopeControl.runnableDecorator( new Runnable()
    {
      @Override
      public void run()
      {
        newRunnableElementHolder.setElement( ThreadLocalBeanScopeTest.this.testBean.getField() );
      }
    } ) );
    Future<String> newCallableValueFuture = this.executorService.submit( this.threadLocalScopeControl.callableDecorator( new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return ThreadLocalBeanScopeTest.this.testBean.getField();
      }
    } ) );
    
    //
    submitInterferenceRunnable();
    submitInterferenceRunnable();
    submitInterferenceRunnable();
    
    //
    this.executorService.shutdown();
    this.executorService.awaitTermination( 1, TimeUnit.SECONDS );
    
    //
    assertEquals( "test1", rollingRunnableElementHolder.getElement() );
    assertEquals( "test1", rollingCallableValueFuture.get() );
    
    //
    assertEquals( null, newRunnableElementHolder.getElement() );
    assertEquals( null, newCallableValueFuture.get() );
    
    //
    assertEquals( "test1", this.testBean.getField() );
    this.threadLocalScopeControl.clearScopeForCurrentThread();
    assertFalse( StringUtils.equals( "test1", this.testBean.getField() ) );
  }
  
  private void submitInterferenceRunnable()
  {
    this.executorService.submit( this.threadLocalScopeControl.runnableDecorator( new Runnable()
    {
      @Override
      public void run()
      {
        ThreadLocalBeanScopeTest.this.testBean.setField( "otherValue" );
      }
    } ) );
  }
}
