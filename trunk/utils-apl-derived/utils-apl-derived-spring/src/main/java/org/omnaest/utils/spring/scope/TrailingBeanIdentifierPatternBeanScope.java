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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.spring.session.implementation.HttpSessionAndServletRequestResolverServiceBean;
import org.omnaest.utils.web.HttpSessionResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * A special {@link CustomScopeConfigurer} to allow
 * 
 * @see CustomScopeConfigurer
 * @see Scope
 * @author Omnaest
 */
public class TrailingBeanIdentifierPatternBeanScope implements Scope, ApplicationContextAware
{
  /* ********************************************** Variables ********************************************** */
  private ThreadLocal<String>             threadLocalTrailingPattern          = new ThreadLocal<String>();
  private Map<String, Object>             beanIdentifierToBeanMap             = new ConcurrentHashMap<String, Object>();
  private String                          beanNameAndTrailingPatternSeparator = "_";
  private ScopedBeanCreationPostProcessor scopedBeanCreationPostProcessor     = null;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private ApplicationContext              applicationContext                  = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TrailingBeanIdentifierPatternBeanScope
   * @see TrailingBeanIdentifierPatternBeanScopeThreadContextManager
   * @author Omnaest
   */
  public static interface TrailingPatternResolver
  {
    /**
     * Resolves the trailing pattern
     * 
     * @return
     */
    public String resolveTrailingPattern();
  }
  
  /**
   * Resolves the trailing pattern by a given {@link HttpSession} attributes value
   * 
   * @author Omnaest
   */
  public static class TrailingPatternBySessionAttributeResolver implements TrailingPatternResolver
  {
    /* ********************************************** Variables ********************************************** */
    private HttpSessionResolver httpSessionResolver  = new HttpSessionAndServletRequestResolverServiceBean();
    private String              sessionAttributeName = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public TrailingPatternBySessionAttributeResolver( String sessionAttributeName )
    {
      super();
      this.sessionAttributeName = sessionAttributeName;
    }
    
    @Override
    public String resolveTrailingPattern()
    {
      //
      String retval = null;
      
      //
      if ( this.httpSessionResolver != null )
      {
        //
        HttpSession httpSession = this.httpSessionResolver.resolveHttpSession();
        if ( httpSession != null )
        {
          try
          {
            //
            Object value = httpSession.getAttribute( this.sessionAttributeName );
            Assert.notNull( value, "Empty session attribute" );
            
            //            
            retval = String.valueOf( value );
          }
          catch ( Exception e )
          {
            throw new RuntimeException( "Session attribute used as trailing pattern can not be transformed to a String", e );
          }
        }
      }
      
      //
      return retval;
    }
    
    public void setHttpSessionResolver( HttpSessionResolver httpSessionResolver )
    {
      this.httpSessionResolver = httpSessionResolver;
    }
    
  }
  
  /**
   * @see TrailingBeanIdentifierPatternBeanScope
   * @see BeanScopeThreadContextManager
   * @author Omnaest
   */
  public class TrailingBeanIdentifierPatternBeanScopeThreadContextManager implements BeanScopeThreadContextManager
  {
    /* ********************************************** Variables ********************************************** */
    private TrailingPatternResolver trailingPatternResolver = null;
    
    /* ********************************************** Methods ********************************************** */
    
    protected TrailingBeanIdentifierPatternBeanScopeThreadContextManager( TrailingPatternResolver trailingPatternResolver )
    {
      //
      super();
      this.trailingPatternResolver = trailingPatternResolver;
    }
    
    @Override
    public void addCurrentThreadToBeanScope()
    {
      TrailingBeanIdentifierPatternBeanScope.this.threadLocalTrailingPattern.set( this.trailingPatternResolver.resolveTrailingPattern() );
    }
    
    @Override
    public void removeCurrentThreadFromBeanScope()
    {
      TrailingBeanIdentifierPatternBeanScope.this.threadLocalTrailingPattern.remove();
    }
    
  }
  
  /**
   * Allows to post process newly created or from {@link ApplicationContext} resolved beans. This will not affect beans which are
   * already managed.
   * 
   * @author Omnaest
   */
  public static interface ScopedBeanCreationPostProcessor
  {
    /**
     * Process the given spring bean. This is only called when the bean is created or resolved from the {@link ApplicationContext}
     * 
     * @param bean
     */
    public void processBean( Object bean );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TrailingBeanIdentifierPatternBeanScope
   */
  public TrailingBeanIdentifierPatternBeanScope()
  {
    super();
    
    //       
    this.beanIdentifierToBeanMap = new ConcurrentHashMap<String, Object>();
  }
  
  /**
   * @param initialCapacity
   *          : capacity of estimated number of beans
   * @see TrailingBeanIdentifierPatternBeanScope
   */
  public TrailingBeanIdentifierPatternBeanScope( int initialCapacity )
  {
    super();
    
    //       
    this.beanIdentifierToBeanMap = new ConcurrentHashMap<String, Object>( initialCapacity );
  }
  
  /**
   * Creates a new instance of a {@link TrailingBeanIdentifierPatternBeanScopeThreadContextManager}
   * 
   * @param trailingPattern
   *          : the pattern the {@link BeanScopeThreadContextManager} sets to the calling {@link Thread}
   * @return
   */
  public TrailingBeanIdentifierPatternBeanScopeThreadContextManager newTrailingBeanIdentifierPatternBeanScopeThreadContextManager( final String trailingPattern )
  {
    return new TrailingBeanIdentifierPatternBeanScopeThreadContextManager( new TrailingPatternResolver()
    {
      @Override
      public String resolveTrailingPattern()
      {
        return trailingPattern;
      }
    } );
  }
  
  /**
   * Creates a new instance of a {@link TrailingBeanIdentifierPatternBeanScopeThreadContextManager}
   * 
   * @param trailingPattern
   *          : the pattern the {@link BeanScopeThreadContextManager} sets to the calling {@link Thread}
   * @return
   */
  public TrailingBeanIdentifierPatternBeanScopeThreadContextManager newTrailingBeanIdentifierPatternBeanScopeThreadContextManager( TrailingPatternResolver trailingPatternResolver )
  {
    return trailingPatternResolver != null ? new TrailingBeanIdentifierPatternBeanScopeThreadContextManager(
                                                                                                             trailingPatternResolver )
                                          : null;
  }
  
  @Override
  public Object get( String name, ObjectFactory<?> objectFactory )
  {
    //
    Object retval = null;
    
    //
    final String beanIdentifier = this.determineBeanIdentifier( name );
    
    //
    retval = this.beanIdentifierToBeanMap.get( beanIdentifier );
    if ( retval == null )
    {
      //
      if ( this.applicationContext.containsBean( beanIdentifier ) )
      {
        retval = this.applicationContext.getBean( beanIdentifier );
      }
      else
      {
        //
        retval = objectFactory.getObject();
        this.beanIdentifierToBeanMap.put( beanIdentifier, retval );
      }
      
      //
      if ( this.scopedBeanCreationPostProcessor != null )
      {
        this.scopedBeanCreationPostProcessor.processBean( retval );
      }
    }
    
    // 
    return retval;
  }
  
  private String determineBeanIdentifier( String name )
  {
    //
    final String trailingPattern = this.threadLocalTrailingPattern.get();
    
    //
    final String separator = ".";
    final int indexOfSeparator = name.indexOf( separator );
    if ( indexOfSeparator > 0 && indexOfSeparator < name.length() - 1 )
    {
      name = name.substring( indexOfSeparator + 1 );
    }
    
    return name + this.beanNameAndTrailingPatternSeparator + trailingPattern;
  }
  
  @Override
  public Object remove( String name )
  {
    //
    Object retval = null;
    
    //
    final String beanIdentifier = this.determineBeanIdentifier( name );
    retval = this.beanIdentifierToBeanMap.remove( beanIdentifier );
    
    // 
    return retval;
  }
  
  @Override
  public void registerDestructionCallback( String name, Runnable callback )
  {
  }
  
  @Override
  public Object resolveContextualObject( String key )
  {
    return null;
  }
  
  @Override
  public String getConversationId()
  {
    return null;
  }
  
  @Override
  public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException
  {
    this.applicationContext = applicationContext;
  }
  
  public void setBeanNameAndTrailingPatternSeparator( String beanNameAndTrailingPatternSeparator )
  {
    this.beanNameAndTrailingPatternSeparator = beanNameAndTrailingPatternSeparator;
  }
  
  /**
   * @see ScopedBeanCreationPostProcessor
   * @param scopedBeanCreationPostProcessor
   * @return this
   */
  public TrailingBeanIdentifierPatternBeanScope setScopedBeanCreationPostProcessor( ScopedBeanCreationPostProcessor scopedBeanCreationPostProcessor )
  {
    this.scopedBeanCreationPostProcessor = scopedBeanCreationPostProcessor;
    return this;
  }
  
}
