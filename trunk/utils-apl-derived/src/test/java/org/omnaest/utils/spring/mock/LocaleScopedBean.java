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

import java.util.Locale;

import org.omnaest.utils.spring.scope.LocaleBeanScope;
import org.omnaest.utils.spring.scope.LocaleBeanScope.LocaleAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service("localeScopedBean")
@Scope(value = LocaleBeanScope.LOCALE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LocaleScopedBean implements BeanNameAware, LocaleAware
{
  /* ********************************************** Variables ********************************************** */
  private String value    = null;
  private String beanName = null;
  private Locale locale   = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public void setValue( String value )
  {
    this.value = value;
  }
  
  @Override
  public void setBeanName( String name )
  {
    this.beanName = name;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public String getBeanName()
  {
    return this.beanName;
  }
  
  @Override
  public void setLocale( Locale locale )
  {
    this.locale = locale;
  }
  
  public Locale getLocale()
  {
    return this.locale;
  }
}
