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
package org.omnaest.utils.spring.mock;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.web.HttpSessionResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpSession;

@Configuration
public class MockHttpSessionResolverFactory
{
  @Bean
  public HttpSessionResolver newHttpSessionResolver()
  {
    return new HttpSessionResolver()
    {
      /* ********************************************** Variables ********************************************** */
      private final ThreadLocal<HttpSession> threadLocalHttpSession = new ThreadLocal<HttpSession>()
                                                                    {
                                                                      
                                                                      @Override
                                                                      protected HttpSession initialValue()
                                                                      {
                                                                        return new MockHttpSession();
                                                                      }
                                                                      
                                                                    };
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public HttpSession resolveHttpSession()
      {
        return this.threadLocalHttpSession.get();
      }
    };
  }
}
