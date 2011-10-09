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
package org.omnaest.utils.spring;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * A bean {@link Scope} using {@link Locale}s as separation aspect. A given {@link LocaleResolver} is used to resolve a
 * {@link Locale} for every request for a bean and for the creation of a {@link Locale} aware
 * {@link BeanScopeThreadContextManager} with calling {@link #newLocalAwareBeanScopeThreadContextManager()}. <br>
 * <br>
 * An example is:
 * 
 * <pre>
 * 
 * final Locale locale = new Locale( language, country );
 * BeanScopeThreadContextManager beanScopeThreadContextManager = localeBeanScope.newLocalAwareBeanScopeThreadContextManager( locale );
 *                  
 * threadPoolTaskExecutor.submit( new BeanScopeAwareRunnableDecorator( new Runnable(){...}, beanScopeThreadContextManager );
 * 
 * </pre>
 * 
 * @see BeanScopeThreadContextManager
 * @see BeanScopeAwareRunnableDecorator
 * @see BeanScopeAwareCallableDecorator
 * @see #SCOPE_LOCALE
 * @author Omnaest
 */
public class LocaleBeanScope implements Scope, ApplicationContextAware
{
  /* ********************************************** Constants ********************************************** */
  /** A predefined {@link Scope} type called: "locale" */
  public final static String                     SCOPE_LOCALE                           = "locale";
  
  /* ********************************************** Variables ********************************************** */
  private TrailingBeanIdentifierPatternBeanScope trailingBeanIdentifierPatternBeanScope = null;
  private LocaleResolver                         localeResolver                         = null;
  
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
    this( initialCapacity );
    
    //
    this.localeResolver = localeResolver;
  }
  
  /**
   * 
   */
  public LocaleBeanScope()
  {
    //
    super();
    
    //
    this.trailingBeanIdentifierPatternBeanScope = new TrailingBeanIdentifierPatternBeanScope();
  }
  
  /**
   * @param initialCapacity
   */
  public LocaleBeanScope( int initialCapacity )
  {
    //
    super();
    
    //
    this.trailingBeanIdentifierPatternBeanScope = new TrailingBeanIdentifierPatternBeanScope( initialCapacity );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is already aware of the {@link Locale} it manages. To resolve
   * the {@link Locale} the {@link LocaleResolver#resolveLocale()} is invoked every time by calling this method.
   * 
   * @return
   */
  public BeanScopeThreadContextManager newLocalAwareBeanScopeThreadContextManager()
  {
    //
    Assert.notNull( this.localeResolver,
                    "LocaleResolver instance is null but is necessary to resolve the right bean for the locale bean scope" );
    
    //    
    return this.newLocalAwareBeanScopeThreadContextManager( this.localeResolver );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is already aware of the {@link Locale} it manages. To resolve
   * the {@link Locale} the {@link LocaleResolver#resolveLocale()} is invoked every time by calling this method.
   * 
   * @param localeResolver
   *          {@link LocaleResolver}
   * @return
   */
  public BeanScopeThreadContextManager newLocalAwareBeanScopeThreadContextManager( LocaleResolver localeResolver )
  {
    //
    Assert.notNull( localeResolver,
                    "LocaleResolver instance is null but is necessary to resolve the right bean for the locale bean scope" );
    
    //
    Locale locale = this.localeResolver.resolveLocale();
    return this.newLocalAwareBeanScopeThreadContextManager( locale );
  }
  
  /**
   * Returns a {@link BeanScopeThreadContextManager} instance which is aware of the given {@link Locale}. This {@link Locale} it
   * used to manage the {@link Thread}s context.
   * 
   * @param locale
   *          {@link Locale}
   * @return
   */
  public BeanScopeThreadContextManager newLocalAwareBeanScopeThreadContextManager( Locale locale )
  {
    //    
    Assert.notNull( locale,
                    "The resolved locale for the current thread is null. Can the given LocaleResolver resolve the locale from the thread which invokes this method?" );
    
    //
    final String trailingPattern = locale.toString();
    return this.trailingBeanIdentifierPatternBeanScope.newTrailingBeanIdentifierPatternBeanScopeThreadContextManager( trailingPattern );
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
