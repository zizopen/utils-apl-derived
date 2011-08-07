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
package org.omnaest.utils.beans.result;

import java.lang.reflect.Method;

/**
 * Information object for methods of bean classes.
 * 
 * @author Omnaest
 */
public class BeanMethodInformation
{
  /* ********************************************** Variables ********************************************** */
  protected boolean isGetter            = false;
  protected boolean isSetter            = false;
  protected String  referencedFieldName = null;
  protected Method  method              = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * 
   */
  public BeanMethodInformation( boolean isGetter, boolean isSetter, String referencedFieldName, Method method )
  {
    super();
    this.isGetter = isGetter;
    this.isSetter = isSetter;
    this.referencedFieldName = referencedFieldName;
    this.method = method;
  }
  
  /**
   * Returns true, if the given method is a field access method.
   * 
   * @return
   */
  public boolean isFieldAccessMethod()
  {
    return this.isGetter || this.isSetter;
  }
  
  /**
   * Returns true, if the method has no parameters but a return type.
   * 
   * @return
   */
  public boolean isGetter()
  {
    return this.isGetter;
  }
  
  /**
   * Is true, if a method has only one parameter and begins with "set". A return type is optional.
   * 
   * @return
   */
  public boolean isSetter()
  {
    return this.isSetter;
  }
  
  /**
   * Get the field name which is referenced by this method.
   * 
   * @return
   */
  public String getReferencedFieldName()
  {
    return this.referencedFieldName;
  }
  
  /**
   * Returns the underlying {@link Method}.
   * 
   * @return
   */
  public Method getMethod()
  {
    return this.method;
  }
  
}
