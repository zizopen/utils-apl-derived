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
package org.omnaest.utils.beans.replicator;

/**
 * @author Omnaest
 */
public class TestClass
{
  /* ********************************************** Variables ********************************************** */
  private final String  fieldString;
  private final Integer fieldInteger;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TestClass
   * @param fieldString
   * @param fieldInteger
   */
  public TestClass( String fieldString, Integer fieldInteger )
  {
    super();
    this.fieldString = fieldString;
    this.fieldInteger = fieldInteger;
  }
  
  protected TestClass()
  {
    super();
    this.fieldInteger = null;
    this.fieldString = null;
  }
  
  /**
   * @return the fieldString
   */
  public String getFieldString()
  {
    return this.fieldString;
  }
  
  /**
   * @return the fieldInteger
   */
  public Integer getFieldInteger()
  {
    return this.fieldInteger;
  }
  
}
