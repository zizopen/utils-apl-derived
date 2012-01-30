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
package org.omnaest.utils.spring.session.threadcontext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.spring.session.HttpSessionService;
import org.omnaest.utils.structure.element.ElementHolder;
import org.omnaest.utils.structure.element.ExceptionHandledResult;
import org.omnaest.utils.threads.FutureTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "RequestContextAwareCallableDecoratorTestAC.xml" })
public class RequestContextAwareCallableDecoratorTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  protected HttpSessionService httpSessionService = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
    mockHttpServletRequest.setSession( new MockHttpSession() );
    RequestAttributes requestAttributes = new ServletRequestAttributes( mockHttpServletRequest );
    RequestContextHolder.setRequestAttributes( requestAttributes, false );
    assertEquals( requestAttributes, RequestContextHolder.getRequestAttributes() );
  }
  
  @Test
  public void testCall()
  {
    //
    final ExecutorService executorService = newExecutorService();
    
    //
    {
      //
      final ElementHolder<HttpSession> elementHolderHttpSession = new ElementHolder<HttpSession>();
      Runnable runnable = new Runnable()
      {
        @Override
        public void run()
        {
          elementHolderHttpSession.setElement( RequestContextAwareCallableDecoratorTest.this.httpSessionService.resolveHttpSession() );
        }
      };
      runnable = new RequestContextAwareRunnableDecorator( runnable );
      final Future<?> future = executorService.submit( runnable );
      FutureTaskManager.waitForTaskToFinish( future );
      
      //
      assertNotNull( elementHolderHttpSession.getElement() );
    }
    //
    {
      //
      Callable<HttpSession> callable = new Callable<HttpSession>()
      {
        @Override
        public HttpSession call() throws Exception
        {
          return RequestContextAwareCallableDecoratorTest.this.httpSessionService.resolveHttpSession();
        }
      };
      callable = new RequestContextAwareCallableDecorator<HttpSession>( callable );
      final Future<HttpSession> future = executorService.submit( callable );
      ExceptionHandledResult<HttpSession> exceptionHandledResult = FutureTaskManager.waitForTaskToFinish( future );
      
      //
      assertNotNull( exceptionHandledResult.getResult() );
    }
  }
  
  /**
   * @return
   */
  private static ExecutorService newExecutorService()
  {
    //
    final int corePoolSize = 2;
    final int maximumPoolSize = corePoolSize;
    final long keepAliveTime = 1;
    final TimeUnit unit = TimeUnit.SECONDS;
    final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>( maximumPoolSize );
    
    ExecutorService executorService = new ThreadPoolExecutor( corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue );
    return executorService;
  }
}
