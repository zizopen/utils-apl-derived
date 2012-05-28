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

import java.util.Map;
import java.util.concurrent.Callable;

import org.omnaest.utils.structure.map.ThreadLocalMap;
import org.omnaest.utils.threads.CallableDecorator;
import org.omnaest.utils.threads.RunnableDecorator;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Bean;

/**
 * {@link Scope} which creates new beans for any new {@link Thread} <br>
 * <br>
 * Within beans the {@link ThreadLocalScopeControl} can be injected to allow a more sophisticated control over the {@link Scope}.
 * E.g. does a {@link ThreadLocalScopeControl} allow to remove any bean references related to the current {@link Thread} or create
 * new {@link Runnable} or {@link Callable} decorators. <br>
 * <br>
 * Spring configuration example:<br>
 * 
 * <pre>
 *     &lt;context:annotation-config /&gt;
 * 
 *     &lt;bean class=&quot;org.somepackage.SomeBean&quot; scope=&quot;threadlocal&quot;&gt;
 *         &lt;aop:scoped-proxy /&gt;
 *     &lt;/bean&gt;
 * 
 *     &lt;bean class=&quot;org.springframework.beans.factory.config.CustomScopeConfigurer&quot;&gt;
 *         &lt;property name=&quot;scopes&quot;&gt;
 *             &lt;map&gt;
 *                 &lt;entry key=&quot;threadlocal&quot;&gt;
 *                     &lt;ref bean=&quot;threadLocalBeanScope&quot; /&gt;
 *                 &lt;/entry&gt;
 *             &lt;/map&gt;
 *         &lt;/property&gt;
 *     &lt;/bean&gt;
 * 
 *     &lt;bean id=&quot;threadLocalBeanScope&quot; class=&quot;org.omnaest.utils.spring.scope.ThreadLocalBeanScope&quot; /&gt;
 * </pre>
 * 
 * @see ThreadLocalScopeControl
 * @author Omnaest
 */
public class ThreadLocalBeanScope implements Scope
{
  /* ********************************************** Variables ********************************************** */
  private ThreadLocalMap<String, Object> threadLocalMap = new ThreadLocalMap<String, Object>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Control instance which can be used within beans to control the underlying {@link ThreadLocalBeanScope}
   * 
   * @see ThreadLocalBeanScope
   * @author Omnaest
   */
  public class ThreadLocalScopeControl
  {
    
    private ThreadLocalScopeControl()
    {
      super();
    }
    
    /**
     * Similar to {@link #callableDecorator(Callable)} but rolls over the bean references from the current {@link Thread} to the
     * new {@link Thread}
     * 
     * @param callable
     * @return
     */
    public <V> Callable<V> callableDecoratorRolling( Callable<V> callable )
    {
      //
      final Map<String, Object> beanMap = ThreadLocalBeanScope.this.threadLocalMap.getMap();
      return new CallableDecorator<V>( callable )
      {
        @Override
        public V call() throws Exception
        {
          //
          ThreadLocalBeanScope.this.threadLocalMap.setMap( beanMap );
          final V retval = super.call();
          clearScopeForCurrentThread();
          return retval;
        }
      };
    }
    
    /**
     * Similar to {@link #callableDecoratorRolling(Callable)} but for a {@link Runnable}
     * 
     * @param runnable
     * @return
     */
    public Runnable runnableDecoratorRolling( Runnable runnable )
    {
      //
      final Map<String, Object> beanMap = ThreadLocalBeanScope.this.threadLocalMap.getMap();
      return new RunnableDecorator( runnable )
      {
        @Override
        public void run()
        {
          //
          ThreadLocalBeanScope.this.threadLocalMap.setMap( beanMap );
          super.run();
          clearScopeForCurrentThread();
        }
      };
    }
    
    /**
     * Returns a {@link Callable} which call {@link #clearScopeForCurrentThread()} after the {@link Callable#call()} method has
     * finished
     * 
     * @param callable
     * @return
     */
    public <V> Callable<V> callableDecorator( Callable<V> callable )
    {
      return new CallableDecorator<V>( callable )
      {
        @Override
        public V call() throws Exception
        {
          //
          ThreadLocalBeanScope.this.threadLocalMap.remove();
          final V retval = super.call();
          clearScopeForCurrentThread();
          return retval;
        }
      };
    }
    
    /**
     * Similar to {@link #callableDecorator(Callable)} but for {@link Runnable}
     * 
     * @param runnable
     * @return
     */
    public Runnable runnableDecorator( Runnable runnable )
    {
      return new RunnableDecorator( runnable )
      {
        @Override
        public void run()
        {
          //
          ThreadLocalBeanScope.this.threadLocalMap.remove();
          super.run();
          clearScopeForCurrentThread();
        }
      };
    }
    
    /**
     * Clears all bean references related to the current {@link Thread}. It is important to call this method within thread pool
     * based environments. If this method is not called all bean references will be kept as long as the calling {@link Thread} is
     * alive. Thats why not calling this method can cause memory leaks within those environments.
     */
    public void clearScopeForCurrentThread()
    {
      ThreadLocalBeanScope.this.threadLocalMap.remove();
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public Object get( String beanName, ObjectFactory<?> objectFactory )
  {
    //
    Object bean = this.threadLocalMap.get( beanName );
    if ( bean == null )
    {
      bean = objectFactory.getObject();
      this.threadLocalMap.put( beanName, bean );
    }
    
    // 
    return bean;
  }
  
  @Override
  public String getConversationId()
  {
    return Thread.currentThread().getName();
  }
  
  @Override
  public void registerDestructionCallback( String arg0, Runnable arg1 )
  {
  }
  
  @Override
  public Object remove( String beanName )
  {
    // 
    return this.threadLocalMap.remove( beanName );
  }
  
  @Override
  public Object resolveContextualObject( String arg0 )
  {
    return null;
  }
  
  /**
   * Returns a new {@link ThreadLocalScopeControl} instance. <br>
   * <br>
   * This method is declared as factory method for spring, which means that the case a {@link ThreadLocalBeanScope} bean is
   * created with annotation config enabled, a {@link ThreadLocalScopeControl} factory is enabled the same time and any bean can
   * inject a bean of this type immediately.
   * 
   * @return
   */
  @Bean
  public ThreadLocalScopeControl newThreadLocalScopeControl()
  {
    return new ThreadLocalScopeControl();
  }
  
}
