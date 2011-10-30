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
  protected boolean isGetter                        = false;
  protected boolean isSetter                        = false;
  protected boolean isGetterWithAdditionalArguments = false;
  protected boolean isSetterWithAdditionalArguments = false;
  protected String  referencedFieldName             = null;
  protected Method  method                          = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param isGetter
   * @param isSetter
   * @param isGetterWithAdditionalArguments
   * @param isSetterWithAdditionalArguments
   * @param referencedFieldName
   * @param method
   */
  public BeanMethodInformation( boolean isGetter, boolean isSetter, boolean isGetterWithAdditionalArguments,
                                boolean isSetterWithAdditionalArguments, String referencedFieldName, Method method )
  {
    super();
    this.isGetter = isGetter;
    this.isSetter = isSetter;
    this.isGetterWithAdditionalArguments = isGetterWithAdditionalArguments;
    this.isSetterWithAdditionalArguments = isSetterWithAdditionalArguments;
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
  
  /**
   * Returns true, if a method begins with "get" or "is" and has more than one parameter and at least two and has a return type.
   * 
   * @return
   */
  public boolean isGetterWithAdditionalArguments()
  {
    return this.isGetterWithAdditionalArguments;
  }
  
  /**
   * Returns true, if a method begins with "set" and has at least two parameter or more.
   * 
   * @return
   */
  public boolean isSetterWithAdditionalArguments()
  {
    return this.isSetterWithAdditionalArguments;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "BeanMethodInformation [isGetter=" );
    builder.append( this.isGetter );
    builder.append( ", isSetter=" );
    builder.append( this.isSetter );
    builder.append( ", isGetterWithAdditionalArguments=" );
    builder.append( this.isGetterWithAdditionalArguments );
    builder.append( ", isSetterWithAdditionalArguments=" );
    builder.append( this.isSetterWithAdditionalArguments );
    builder.append( ", referencedFieldName=" );
    builder.append( this.referencedFieldName );
    builder.append( ", method=" );
    builder.append( this.method );
    builder.append( "]" );
    return builder.toString();
  }
  
}
