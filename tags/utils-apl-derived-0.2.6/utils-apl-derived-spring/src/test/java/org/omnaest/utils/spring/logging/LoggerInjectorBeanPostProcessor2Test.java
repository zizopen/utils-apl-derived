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
package org.omnaest.utils.spring.logging;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.omnaest.utils.assertion.AssertLogger;
import org.omnaest.utils.spring.logging.LoggerInjectorBeanPostProcessor.LoggerInject;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

/**
 * @see LoggerInjectorBeanPostProcessor
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
public class LoggerInjectorBeanPostProcessor2Test
{
  /* ************************************************** Constants *************************************************** */
  private static final Logger LOGGER_INSTANCE  = Mockito.mock( Logger.class );
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  
  @LoggerInject
  private Logger              loggerAlreadySet = LoggerInjectorBeanPostProcessor2Test.LOGGER_INSTANCE;
  
  @LoggerInject
  private Logger              loggerInject;
  
  @LoggerInject
  private AssertLogger        assertLoggerInject;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @Configuration
  static class ContextFactory
  {
    @Bean
    public LoggerInjectorBeanPostProcessor newLoggerInjectorBeanPostProcessor()
    {
      final LoggerInjectorBeanPostProcessor loggerInjectorBeanPostProcessor = new LoggerInjectorBeanPostProcessor();
      loggerInjectorBeanPostProcessor.setInjectingOnLoggerType( false );
      loggerInjectorBeanPostProcessor.setInjectingOnLoggerAnnotation( true );
      loggerInjectorBeanPostProcessor.setOverwritingExistingInstances( true );
      return loggerInjectorBeanPostProcessor;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testPostProcessAfterInitialization() throws Exception
  {
    
    assertNotNull( this.loggerInject );
    assertNotSame( LOGGER_INSTANCE, this.loggerAlreadySet );
    
    assertNotNull( this.assertLoggerInject );
  }
  
}
