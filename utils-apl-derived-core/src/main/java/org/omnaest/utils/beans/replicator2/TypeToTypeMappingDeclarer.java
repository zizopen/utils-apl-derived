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
package org.omnaest.utils.beans.replicator2;

/**
 * @author Omnaest
 */
interface TypeToTypeMappingDeclarer
{
  
  /**
   * @param typeFrom
   * @param typeTo
   */
  public void addTypeMapping( Class<?> typeFrom, Class<?> typeTo );
  
  /**
   * @param propertyNameFrom
   * @param propertyNameTo
   */
  public void addPropertyNameMapping( String propertyNameFrom, String propertyNameTo );
  
  /**
   * @param path
   * @param propertyNameFrom
   * @param propertyNameTo
   */
  public void addPropertyNameMapping( String path, String propertyNameFrom, String propertyNameTo );
  
  /**
   * @param typeFrom
   * @param propertyNameFrom
   * @param typeTo
   * @param propertyNameTo
   */
  public void addTypeAndPropertyNameMapping( Class<?> typeFrom, String propertyNameFrom, Class<?> typeTo, String propertyNameTo );
  
  /**
   * @param path
   * @param typeFrom
   * @param propertyNameFrom
   * @param typeTo
   * @param propertyNameTo
   */
  public void addTypeAndPropertyNameMapping( String path,
                                             Class<?> typeFrom,
                                             String propertyNameFrom,
                                             Class<?> typeTo,
                                             String propertyNameTo );
  
  /**
   * @param path
   * @param typeFrom
   * @param typeTo
   */
  public void addTypeMappingForPath( String path, Class<?> typeFrom, Class<?> typeTo );
}
