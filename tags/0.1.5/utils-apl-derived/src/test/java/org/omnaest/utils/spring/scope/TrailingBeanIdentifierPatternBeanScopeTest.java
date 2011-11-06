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
package org.omnaest.utils.spring.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.spring.mock.LocaleScopedBean;
import org.omnaest.utils.threads.FutureTaskManager;
import org.omnaest.utils.threads.FutureTaskManager.RunnableTaskSubmitter;
import org.omnaest.utils.web.HttpSessionResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @see TrailingBeanIdentifierPatternBeanScope
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "TrailingBeanIdentifierPatternScopeTestAC.xml" })
public class TrailingBeanIdentifierPatternBeanScopeTest
{
  /* ********************************************** Variables ********************************************** */
  @Autowired
  private ThreadPoolTaskExecutor                 threadPoolTaskExecutor                 = null;
  
  @Autowired
  private HttpSessionResolver                    httpSessionResolver                    = null;
  
  @Autowired
  private BeanScopeThreadContextManager          beanScopeThreadContextManager          = null;
  
  @Autowired
  @Qualifier("localeScopedBean")
  private LocaleScopedBean                       localeScopedBean                       = null;
  
  @Autowired
  private TrailingBeanIdentifierPatternBeanScope trailingBeanIdentifierPatternBeanScope = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    assertNotNull( this.threadPoolTaskExecutor );
    assertNotNull( this.httpSessionResolver );
    assertNotNull( this.beanScopeThreadContextManager );
    assertNotNull( this.localeScopedBean );
    assertNotNull( this.trailingBeanIdentifierPatternBeanScope );
    
    //
    HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
    Assert.assertTrue( httpSession instanceof MockHttpSession );
  }
  
  @Test
  public void testGet()
  {
    //
    final HttpSessionResolver httpSessionResolver = this.httpSessionResolver;
    final LocaleScopedBean localeScopedBean = this.localeScopedBean;
    final BeanScopeThreadContextManager beanScopeThreadContextManager = this.beanScopeThreadContextManager;
    final ThreadPoolTaskExecutor threadPoolTaskExecutor = this.threadPoolTaskExecutor;
    
    //
    final Multimap<String, String> localeToObjectMultimap = LinkedListMultimap.create();
    final AtomicInteger customBeanValueFoundCounter = new AtomicInteger();
    
    //    
    final AtomicInteger counter = new AtomicInteger();
    
    //
    final Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        //
        HttpSession httpSession = httpSessionResolver.resolveHttpSession();
        
        //        
        String language = null;
        String country = null;
        
        final int decision = counter.incrementAndGet() % 4;
        if ( decision == 0 )
        {
          //
          language = "en";
          country = "US";
        }
        else if ( decision == 1 )
        {
          //
          language = "de";
          country = "DE";
        }
        else if ( decision == 2 )
        {
          //
          language = "other";
          country = "Country";
        }
        else if ( decision == 3 )
        {
          //
          language = "other2";
          country = "Country";
        }
        
        //
        Locale locale = new Locale( language, country );
        httpSession.setAttribute( "locale", locale );
        
        //
        new BeanScopeAwareRunnableDecorator( new Runnable()
        {
          @Override
          public void run()
          {
            //
            synchronized ( localeScopedBean )
            {
              //
              localeToObjectMultimap.put( "" + decision, localeScopedBean.toString() );
              
              //
              if ( localeScopedBean.getValue() != null
                   && localeScopedBean.getValue().equals( httpSessionResolver.resolveHttpSession()
                                                                             .getAttribute( "locale" )
                                                                             .toString() ) )
              {
                customBeanValueFoundCounter.incrementAndGet();
              }
              
              //
              //              System.out.println();
              //              System.out.println( decission );
              //              System.out.println( localeScopedBean );
              //              System.out.println( localeScopedBean.getBeanName() );
              //              System.out.println( localeScopedBean.getValue() );
              //              
              //              //
              //              System.out.println( httpSessionResolver.resolveHttpSession().getAttribute( "locale" ) );
            }
          }
        }, beanScopeThreadContextManager ).run();
      }
    };
    
    //
    FutureTaskManager futureTaskManager = new FutureTaskManager();
    RunnableTaskSubmitter runnableTaskSubmitter = new RunnableTaskSubmitter()
    {
      @Override
      public Future<?> submitTask( Runnable runnable )
      {
        return threadPoolTaskExecutor.submit( runnable );
      }
    };
    
    //
    final int submitCount = 80;
    futureTaskManager.submitAndManage( runnableTaskSubmitter, runnable, submitCount );
    
    //
    futureTaskManager.waitForAllTasksToFinish();
    
    //
    Set<String> valueSet = new HashSet<String>();
    for ( String locale : localeToObjectMultimap.keySet() )
    {
      //
      Collection<String> collection = localeToObjectMultimap.get( locale );
      assertNotNull( collection );
      assertEquals( submitCount / 4, collection.size() );
      assertEquals( 1, new HashSet<String>( collection ).size() );
      
      //
      valueSet.add( collection.iterator().next() );
    }
    
    assertEquals( localeToObjectMultimap.keySet().size(), valueSet.size() );
    
    //
    assertEquals( submitCount / 2, customBeanValueFoundCounter.get() );
  }
  
  public void setThreadPoolTaskExecutor( ThreadPoolTaskExecutor threadPoolTaskExecutor )
  {
    this.threadPoolTaskExecutor = threadPoolTaskExecutor;
  }
  
  public void setHttpSessionResolver( HttpSessionResolver httpSessionResolver )
  {
    this.httpSessionResolver = httpSessionResolver;
  }
  
  public void setBeanScopeThreadContextManager( BeanScopeThreadContextManager beanScopeThreadContextManager )
  {
    this.beanScopeThreadContextManager = beanScopeThreadContextManager;
  }
  
  public void setLocaleScopedBean( LocaleScopedBean localeScopedBean )
  {
    this.localeScopedBean = localeScopedBean;
  }
  
  public void setTrailingBeanIdentifierPatternBeanScope( TrailingBeanIdentifierPatternBeanScope trailingBeanIdentifierPatternBeanScope )
  {
    this.trailingBeanIdentifierPatternBeanScope = trailingBeanIdentifierPatternBeanScope;
  }
  
}
