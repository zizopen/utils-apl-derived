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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.spring.mock.LocaleScopedBean;
import org.omnaest.utils.threads.FutureTaskManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * @see LocaleBeanScope
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "LocaleBeanScopeTestAC.xml" })
public class LocaleBeanScopeTest
{
  
  /* ********************************************** Variables ********************************************** */
  @Autowired
  private ThreadPoolTaskExecutor threadPoolTaskExecutor = null;
  
  @Autowired
  @Qualifier("localeScopedBean")
  private LocaleScopedBean       localeScopedBean       = null;
  
  @Autowired
  private LocaleBeanScope        localeBeanScope        = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    //
    assertNotNull( this.threadPoolTaskExecutor );
    assertNotNull( this.localeScopedBean );
    assertNotNull( this.localeBeanScope );
  }
  
  @Test
  public void testGet()
  {
    //
    final LocaleScopedBean localeScopedBean = this.localeScopedBean;
    final LocaleBeanScope localeBeanScope = this.localeBeanScope;
    
    //
    final ThreadPoolTaskExecutor threadPoolTaskExecutor = this.threadPoolTaskExecutor;
    final ExecutorService executorService = new ExecutorServiceAdapter( this.threadPoolTaskExecutor );
    
    //
    final Multimap<String, String> localeToObjectMultimap = LinkedListMultimap.create();
    final AtomicInteger customBeanValueFoundCounter = new AtomicInteger();
    final AtomicInteger customBeanLocaleInjectedCounter = new AtomicInteger();
    
    //    
    final AtomicInteger counter = new AtomicInteger();
    
    //
    final Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
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
        final Locale locale = new Locale( language, country );
        
        //
        try
        {
          threadPoolTaskExecutor.submit( localeBeanScope.newLocaleAwareRunnableDecorator( new Runnable()
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
                if ( localeScopedBean.getValue() != null && localeScopedBean.getValue().equals( locale.toString() ) )
                {
                  customBeanValueFoundCounter.incrementAndGet();
                }
                
                //
                if ( localeScopedBean.getLocale() != null && localeScopedBean.getLocale().equals( locale ) )
                {
                  customBeanLocaleInjectedCounter.incrementAndGet();
                }
              }
            }
          }, locale ) ).get();
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
      }
    };
    
    //
    FutureTaskManager futureTaskManager = new FutureTaskManager();
    
    //
    final int submitCount = 80;
    futureTaskManager.submitAndManage( executorService, runnable, submitCount );
    
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
    assertEquals( submitCount, customBeanLocaleInjectedCounter.get() );
    
  }
  
  public void setThreadPoolTaskExecutor( ThreadPoolTaskExecutor threadPoolTaskExecutor )
  {
    this.threadPoolTaskExecutor = threadPoolTaskExecutor;
  }
  
  public void setLocaleScopedBean( LocaleScopedBean localeScopedBean )
  {
    this.localeScopedBean = localeScopedBean;
  }
  
  public void setLocaleBeanScope( LocaleBeanScope localeBeanScope )
  {
    this.localeBeanScope = localeBeanScope;
  }
  
}
