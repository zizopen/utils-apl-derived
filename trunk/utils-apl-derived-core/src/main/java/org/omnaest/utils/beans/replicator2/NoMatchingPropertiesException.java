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
 * Special {@link CopyException} which indicates that for a source property no target property has been found
 * 
 * @see BeanReplicator
 * @author Omnaest
 */
public class NoMatchingPropertiesException extends CopyException
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -2680154268714706917L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<?>    sourceType;
  private final String      propertyNameSource;
  private final Class<?>    targetType;
  private final String      propertyNameTarget;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see NoMatchingPropertiesException
   * @param canonicalPath
   * @param sourceType
   * @param propertyNameSource
   * @param targetType
   * @param propertyNameTarget
   */
  NoMatchingPropertiesException( String canonicalPath, Class<?> sourceType, String propertyNameSource, Class<?> targetType,
                                 String propertyNameTarget )
  {
    super( "No matching target property at " + canonicalPath + " for source signature " + sourceType.getSimpleName()
           + " " + propertyNameSource + " and target signature " + targetType.getSimpleName() + " "
           + propertyNameTarget, canonicalPath );
    this.sourceType = sourceType;
    this.propertyNameSource = propertyNameSource;
    this.targetType = targetType;
    this.propertyNameTarget = propertyNameTarget;
  }
  
  public Class<?> getSourceType()
  {
    return this.sourceType;
  }
  
  public String getPropertyNameSource()
  {
    return this.propertyNameSource;
  }
  
  public Class<?> getTargetType()
  {
    return this.targetType;
  }
  
  public String getPropertyNameTarget()
  {
    return this.propertyNameTarget;
  }
  
}
