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

import java.util.Locale;
import java.util.concurrent.Callable;

import org.omnaest.utils.spring.scope.TrailingBeanIdentifierPatternBeanScope.ScopedBeanCreationPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * A bean {@link Scope} using {@link Locale}s as separation aspect. A given {@link LocaleResolver} is used to resolve a
 * {@link Locale} for every request for a bean and for the creation of a {@link Locale} aware
 * {@link BeanScopeThreadContextManager} with calling {@link #newLocaleAwareBeanScopeThreadContextManager()}. <br>
 * <br>
 * An example is:
 * 
 * <pre>
 * 
 * final Locale locale = new Locale( language, country );
 * BeanScopeThreadContextManager beanScopeThreadContextManager = localeBeanScope.newLocaleAwareBeanScopeThreadContextManager( locale );
 *                  
 * threadPoolTaskExecutor.submit( new BeanScopeAwareRunnableDecorator( new Runnable(){...}, beanScopeThreadContextManager );
 * 
 * </pre>
 * 
 * An example for an annotated bean looks like:
 * 
 * <pre>
 * &#064;Service("localeScopedBean")
 * &#064;Scope(value = LocaleBeanScope.LOCALE, proxyMode = ScopedProxyMode.TARGET_CLASS)
 * public class LocaleScopedBean implements LocaleAware
 * {
 * ...
 * }
 * </pre>
 * 
 * Whereby the {@link LocaleAware} interface is optional. If it is present the {@link Locale} will be injected into the created
 * bean instance the first time it is used. <br>
 * <br>
 * Note: The {@link LocaleBeanScope} allows to use a regular {@link ThreadLocal} as well as an {@link InheritableThreadLocal}
 * instance and therefore {@link Thread} to {@link Thread} inheritance.
 * 
 * @see BeanScopeThreadContextManager
 * @see BeanScopeAwareRunnableDecorator
 * @see BeanScopeAwareCallableDecorator
 * @see #LOCALE
 * @author Omnaest
 */
public class LocaleBeanScope implements Scope, ApplicationContextAware
{
  /* ********************************************** Constants ********************************************** */
  /** A predefined {@link Scope} type called: "locale" */
  public final static String                     LOCALE                                 = "locale";
  
  /* ********************************************** Variables ********************************************** */
  private TrailingBeanIdentifierPatternBeanScope trailingBeanIdentifierPatternBeanScope = null;
  private LocaleResolver                         localeResolver                         = null;
  private ThreadLocal<Locale>                    threadLocalLocale                      = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Resolver for a {@link Locale}
   * 
   * @see LocaleBeanScope
   * @see #resolveLocale()
   * @author Omnaest
   */
  public static interface LocaleResolver
  {
    
    /**
     * Resolves the {@link Locale} for the current {@link Thread}
     * 
     * @return
     */
    public Locale resolveLocale();
  }
  
  /**
   * If a spring bean is declared under the {@link LocaleBeanScope} it can implements this interface to get the {@link Locale}
   * injected after creating the bean instance.
   * 
   * @author Omnaest
   */
  public static interface LocaleAware
  {
    /**
     * Sets the {@link Locale}
     */
    public void setLocale( Locale locale );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param initialCapacity
   *          : number of estimated spring beans the {@link Scope} should handle
   * @param localeResolver
   *          {@link LocaleResolver}
   */
  public LocaleBeanScope( int initialCapacity, LocaleResolver localeResolver )
  {
    //
    this( initialCapacity, localeResolver, false );
  }
  
/**
   * @param initialCapacity
   *          : number of estimated spring beans the {@link Scope} should handle
   * @param localeResolver
   *          {@link LocaleResolver
   *          @param threadLocalInheritance
   */
  public LocaleBeanScope( int initialCapacity, LocaleResolver localeResolver, boolean threadLocalInheritance )
  {
    //
    this( initialCapacity, threadLocalInheritance );
    
    //
    this.localeResolver = localeResolver;
    
  }
  
  /**
   * 
   */
  public LocaleBeanScope()
  {
    this( false );
  }
  
  /**
   * @param threadLocalInheritance
   */
  public LocaleBeanScope( boolean threadLocalInheritance )
  {
    this( 32, threadLocalInheritance );
  }
  
  /**
   * @param initialCapacity
   * @param threadLocalInheritance
   */
  public LocaleBeanScope( int initialCapacity, boolean threadLocalInheritance )
  {
    //
    super();
    
    //
    if ( threadLocalInheritance )
    {
      this.threadLocalLocale = new InheritableThreadLocal<Locale>();
    }
    else
    {
      this.threadLocalLocale = new ThreadLocal<Locale>();
    }
    
    //
    this.trailingBeanIdentifierPatternBeanScope = new TrailingBeanIdentifierPatternBeanScope( initialCapacity );
    this.trailingBeanIdentifierPatternBeanScope.setScopedBeanCreationPostProcessor( new ScopedBeanCreationPostProcessor()
    {
      @Override
      public void processBean( Object bean )
      {
        if ( bean instanceof LocaleAware )
        {
          LocaleAware localeAware = (LocaleAware) bean;
          localeAware.setLocale( LocaleBeanScope.this.threadLocalLocale.get() );
        }
      }
    } );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is already aware of the {@link Locale} it manages. To resolve
   * the {@link Locale} the {@link LocaleResolver#resolveLocale()} is invoked every time by calling this method.
   * 
   * @return
   */
  public BeanScopeThreadContextManager newLocaleAwareBeanScopeThreadContextManager()
  {
    //
    Assert.notNull( this.localeResolver,
                    "LocaleResolver instance is null but is necessary to resolve the right bean for the locale bean scope" );
    
    //    
    return this.newLocaleAwareBeanScopeThreadContextManager( this.localeResolver );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is already aware of the {@link Locale} it manages. To resolve
   * the {@link Locale} the {@link LocaleResolver#resolveLocale()} is invoked every time by calling this method.
   * 
   * @param localeResolver
   *          {@link LocaleResolver}
   * @return
   */
  public BeanScopeThreadContextManager newLocaleAwareBeanScopeThreadContextManager( LocaleResolver localeResolver )
  {
    //
    Assert.notNull( localeResolver,
                    "LocaleResolver instance is null but is necessary to resolve the right bean for the locale bean scope" );
    
    //
    Locale locale = this.localeResolver.resolveLocale();
    return this.newLocaleAwareBeanScopeThreadContextManager( locale );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is aware of the given {@link Locale}. This {@link Locale} it
   * used to manage the {@link Thread}s context.
   * 
   * @param locale
   *          {@link Locale}
   * @return
   */
  public BeanScopeThreadContextManager newLocaleAwareBeanScopeThreadContextManager( final Locale locale )
  {
    //    
    Assert.notNull( locale,
                    "The resolved locale for the current thread is null. Can the given LocaleResolver resolve the locale from the thread which invokes this method?" );
    
    //
    final String trailingPattern = locale.toString();
    return new BeanScopeThreadContextManager()
    {
      /* ********************************************** Variables ********************************************** */
      private BeanScopeThreadContextManager beanScopeThreadContextManager = LocaleBeanScope.this.trailingBeanIdentifierPatternBeanScope.newTrailingBeanIdentifierPatternBeanScopeThreadContextManager( trailingPattern );
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public void removeCurrentThreadFromBeanScope()
      {
        //
        LocaleBeanScope.this.threadLocalLocale.remove();
        
        //
        this.beanScopeThreadContextManager.removeCurrentThreadFromBeanScope();
      }
      
      @Override
      public void addCurrentThreadToBeanScope()
      {
        //
        LocaleBeanScope.this.threadLocalLocale.set( locale );
        
        //
        this.beanScopeThreadContextManager.addCurrentThreadToBeanScope();
      }
    };
    
  }
  
  /**
   * Returns the internally resolved {@link Locale} for the current {@link Thread}. This does not trigger any resolving process if
   * no {@link Locale} is yet determined.
   * 
   * @return
   */
  public Locale getResolvedLocaleForTheCurrentThread()
  {
    return this.threadLocalLocale.get();
  }
  
  /**
   * Returns a new instance of a {@link Runnable} which manages the {@link Locale} awareness of the running {@link Thread}. To
   * resolve the {@link Locale} the {@link LocaleResolver} is used.
   * 
   * @param runnable
   * @param localeResolver
   * @return
   */
  public Runnable newLocaleAwareRunnableDecorator( Runnable runnable, LocaleResolver localeResolver )
  {
    return new BeanScopeAwareRunnableDecorator( runnable, this.newLocaleAwareBeanScopeThreadContextManager( localeResolver ) );
  }
  
  /**
   * Returns a new instance of a {@link Callable} which manages the {@link Locale} awareness of the running {@link Thread}. To
   * resolve the {@link Locale} the {@link LocaleResolver} is used.
   * 
   * @param callable
   * @param localeResolver
   * @return
   */
  public <V> Callable<V> newLocaleAwareCallableDecorator( Callable<V> callable, LocaleResolver localeResolver )
  {
    return new BeanScopeAwareCallableDecorator<V>( callable, this.newLocaleAwareBeanScopeThreadContextManager( localeResolver ) );
  }
  
  /**
   * Returns a new instance of a {@link Runnable} which manages the {@link Locale} awareness of the running {@link Thread}.
   * 
   * @param runnable
   * @return
   */
  public Runnable newLocaleAwareRunnableDecorator( Runnable runnable )
  {
    return new BeanScopeAwareRunnableDecorator( runnable, this.newLocaleAwareBeanScopeThreadContextManager() );
  }
  
  /**
   * Returns a new instance of a {@link Callable} which manages the {@link Locale} awareness of the running {@link Thread}. To
   * resolve the {@link Locale} the internal {@link LocaleResolver} is used.
   * 
   * @param callable
   * @return
   */
  public <V> Callable<V> newLocaleAwareCallableDecorator( Callable<V> callable )
  {
    return new BeanScopeAwareCallableDecorator<V>( callable, this.newLocaleAwareBeanScopeThreadContextManager() );
  }
  
  /**
   * Returns a new instance of a {@link Runnable} which manages the {@link Locale} awareness of the running {@link Thread}. To
   * resolve the {@link Locale} the {@link #getResolvedLocaleForTheCurrentThread()} is used statically.
   * 
   * @param runnable
   * @return
   */
  public Runnable newLocaleAwareRunnableDecoratorForLocaleOfCurrentThread( Runnable runnable )
  {
    return new BeanScopeAwareRunnableDecorator(
                                                runnable,
                                                this.newLocaleAwareBeanScopeThreadContextManager( this.getResolvedLocaleForTheCurrentThread() ) );
  }
  
  /**
   * Returns a new instance of a {@link Callable} which manages the {@link Locale} awareness of the running {@link Thread}. To
   * resolve the {@link Locale} the {@link #getResolvedLocaleForTheCurrentThread()} is used statically.
   * 
   * @param callable
   * @return
   */
  public <V> Callable<V> newLocaleAwareCallableDecoratorForLocaleOfCurrentThread( Callable<V> callable )
  {
    return new BeanScopeAwareCallableDecorator<V>(
                                                   callable,
                                                   this.newLocaleAwareBeanScopeThreadContextManager( this.getResolvedLocaleForTheCurrentThread() ) );
  }
  
  /**
   * Returns a new instance of a {@link Runnable} which manages the {@link Locale} awareness of the running {@link Thread}.
   * 
   * @param runnable
   * @param locale
   * @return
   */
  public Runnable newLocaleAwareRunnableDecorator( Runnable runnable, Locale locale )
  {
    return new BeanScopeAwareRunnableDecorator( runnable, this.newLocaleAwareBeanScopeThreadContextManager( locale ) );
  }
  
  /**
   * Returns a new instance of a {@link Callable} which manages the {@link Locale} awareness of the running {@link Thread}.
   * 
   * @param callable
   * @param locale
   * @return
   */
  public <V> Callable<V> newLocaleAwareCallableDecorator( Callable<V> callable, Locale locale )
  {
    return new BeanScopeAwareCallableDecorator<V>( callable, this.newLocaleAwareBeanScopeThreadContextManager( locale ) );
  }
  
  @Override
  public Object get( String name, ObjectFactory<?> objectFactory )
  {
    return this.trailingBeanIdentifierPatternBeanScope.get( name, objectFactory );
  }
  
  @Override
  public Object remove( String name )
  {
    return this.trailingBeanIdentifierPatternBeanScope.remove( name );
  }
  
  @Override
  public void registerDestructionCallback( String name, Runnable callback )
  {
    this.trailingBeanIdentifierPatternBeanScope.registerDestructionCallback( name, callback );
  }
  
  @Override
  public Object resolveContextualObject( String key )
  {
    return this.trailingBeanIdentifierPatternBeanScope.resolveContextualObject( key );
  }
  
  @Override
  public String getConversationId()
  {
    return this.trailingBeanIdentifierPatternBeanScope.getConversationId();
  }
  
  @Override
  public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException
  {
    this.trailingBeanIdentifierPatternBeanScope.setApplicationContext( applicationContext );
  }
  
  public void setBeanNameAndTrailingPatternSeparator( String beanNameAndTrailingPatternSeparator )
  {
    this.trailingBeanIdentifierPatternBeanScope.setBeanNameAndTrailingPatternSeparator( beanNameAndTrailingPatternSeparator );
  }
}
