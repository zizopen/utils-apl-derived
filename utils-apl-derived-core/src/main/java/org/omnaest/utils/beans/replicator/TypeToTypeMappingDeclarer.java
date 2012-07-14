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
package org.omnaest.utils.beans.replicator;

/**
 * @author Omnaest
 */
public interface TypeToTypeMappingDeclarer
{
  
  /**
   * Adds a generally applied mapping from a source type to another target type regardless where the type is located
   * 
   * @see #addTypeMappingForPath(String, Class, Class)
   * @see #addTypeAndPropertyNameMapping(Class, String, Class, String)
   * @param typeFrom
   * @param typeTo
   */
  public void addTypeMapping( Class<?> typeFrom, Class<?> typeTo );
  
  /**
   * Adds a generally applied mapping from one property name to another regardless where the properties are located.
   * 
   * @see #addPropertyNameMapping(String, String)
   * @param propertyNameFrom
   * @param propertyNameTo
   */
  public void addPropertyNameMapping( String propertyNameFrom, String propertyNameTo );
  
  /**
   * Adds a mapping from one property name to another for the specific path
   * 
   * @param path
   * @param propertyNameFrom
   * @param propertyNameTo
   */
  public void addPropertyNameMapping( String path, String propertyNameFrom, String propertyNameTo );
  
  /**
   * Adds a mapping for any property having the given name and type to another property with a new name and a new type
   * 
   * @param typeFrom
   * @param propertyNameFrom
   * @param typeTo
   * @param propertyNameTo
   */
  public void addTypeAndPropertyNameMapping( Class<?> typeFrom, String propertyNameFrom, Class<?> typeTo, String propertyNameTo );
  
  /**
   * Similar to {@link #addTypeAndPropertyNameMapping(Class, String, Class, String)} but allows to further specify a path
   * 
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
   * Adds a type to type mapping for a special path
   * 
   * @param path
   * @param typeFrom
   * @param typeTo
   */
  public void addTypeMappingForPath( String path, Class<?> typeFrom, Class<?> typeTo );
}
