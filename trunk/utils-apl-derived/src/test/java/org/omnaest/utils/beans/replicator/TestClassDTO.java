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
public class TestClassDTO
{
  /* ********************************************** Variables ********************************************** */
  private String  fieldString;
  private Integer fieldInteger;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see TestClassDTO
   * @param fieldString
   * @param fieldInteger
   */
  public TestClassDTO( String fieldString, Integer fieldInteger )
  {
    super();
    this.fieldString = fieldString;
    this.fieldInteger = fieldInteger;
  }
  
  /**
   * @see TestClassDTO
   */
  protected TestClassDTO()
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
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "TestClassDTO [fieldString=" );
    builder.append( this.fieldString );
    builder.append( ", fieldInteger=" );
    builder.append( this.fieldInteger );
    builder.append( "]" );
    return builder.toString();
  }
  
  /**
   * @param fieldString
   *          the fieldString to set
   */
  public void setFieldString( String fieldString )
  {
    this.fieldString = fieldString;
  }
  
  /**
   * @param fieldInteger
   *          the fieldInteger to set
   */
  public void setFieldInteger( Integer fieldInteger )
  {
    this.fieldInteger = fieldInteger;
  }
}
