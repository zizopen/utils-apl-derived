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
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.omnaest.utils.assertion.AssertLogger;
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
public class LoggerInjectorBeanPostProcessorTest
{
  /* ************************************************** Constants *************************************************** */
  private static final Logger LOGGER_INSTANCE  = Mockito.mock( Logger.class );
  
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  
  private Logger              loggerByType;
  
  private Logger              loggerAlreadySet = LoggerInjectorBeanPostProcessorTest.LOGGER_INSTANCE;
  
  private AssertLogger        assertLoggerByType;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @Configuration
  static class ContextFactory
  {
    @Bean
    public LoggerInjectorBeanPostProcessor newLoggerInjectorBeanPostProcessor()
    {
      final LoggerInjectorBeanPostProcessor loggerInjectorBeanPostProcessor = new LoggerInjectorBeanPostProcessor();
      loggerInjectorBeanPostProcessor.setInjectingOnLoggerType( true );
      loggerInjectorBeanPostProcessor.setOverwritingExistingInstances( false );
      return loggerInjectorBeanPostProcessor;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Test
  public void testPostProcessAfterInitialization() throws Exception
  {
    assertNotNull( this.loggerByType );
    assertSame( LOGGER_INSTANCE, this.loggerAlreadySet );
    
    assertNotNull( this.assertLoggerByType );
    
  }
  
}
