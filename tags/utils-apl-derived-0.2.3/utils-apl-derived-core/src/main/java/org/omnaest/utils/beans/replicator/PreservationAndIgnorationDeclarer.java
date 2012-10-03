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

import java.io.Serializable;

/**
 * A {@link PreservationAndIgnorationDeclarer} allows to specify excluded types and pathes, as well as preserved types and paths.<br>
 * Additionally deepness levels can be specified to alter the traversal based on the deepness of instances. <br>
 * <br>
 * Preservation in this context means that an source instance is kept as it is and being injected as it is into the target
 * instance.<br>
 * Ignoring means to exclude an instance from being injected into the target at all.
 * 
 * @author Omnaest
 */
public interface PreservationAndIgnorationDeclarer extends Serializable
{
  /**
   * Similar to {@link #addPreservedType(Class)}
   * 
   * @see #addPreservedType(Class)
   * @param typeIterable
   */
  public void addAllPreservedTypes( Iterable<? extends Class<?>> typeIterable );
  
  /**
   * Adds types of instances where no clone is made and which are injected directly into the target
   * 
   * @see #addIgnoredType(Class)
   * @see #addPreservedPath(String)
   * @see #addAllPreservedTypes(Iterable)
   * @param type
   */
  public void addPreservedType( Class<?> type );
  
  /**
   * Adds a path for which the source instance will be injected into the target itself and no clone of it
   * 
   * @see #addPreservedType(Class)
   * @see #addIgnoredPath(String)
   * @param path
   */
  public void addPreservedPath( String path );
  
  /**
   * Sets the deepness level, beginning from all instances are preserved as they are and no clone operation is executed on them <br>
   * <br>
   * Levels are related to the number of tokens of the path. A level of 1 e.g. is related to the direct properties of the root
   * instance. <br>
   * Setting the level to 1 would only create a clone of the root instance but preserve all instances of its properties without
   * cloning them.
   * 
   * @see #addPreservedType(Class)
   * @see #addPreservedPath(String)
   * @see #setIgnoredDeepnessLevel(int)
   * @param deepnessLevel
   */
  public void setPreservedDeepnessLevel( int deepnessLevel );
  
  /**
   * Sets the deepness level, beginning from the traversal stops and no instances are set to the target instance <br>
   * <br>
   * Levels are related to the number of tokens of the path. A level of 1 e.g. is related to the direct properties of the root
   * instance. <br>
   * Setting the level to 1 would only allow to create the root instance but ignoring all its properties.
   * 
   * @see #addIgnoredType(Class)
   * @see #setPreservedDeepnessLevel(int)
   * @param deepnessLevel
   */
  public void setIgnoredDeepnessLevel( int deepnessLevel );
  
  /**
   * Similar to {@link #addIgnoredType(Class)}
   * 
   * @param typeIterable
   */
  public void addAllIgnoredTypes( Iterable<? extends Class<?>> typeIterable );
  
  /**
   * Adds a type to be ignored by the traversal at all. This means that any source type encountered with the given type is not
   * copied and not injected at all. This represents an exclusion mechanism.
   * 
   * @see #addPreservedType(Class)
   * @see #addAllIgnoredTypes(Iterable)
   * @param type
   */
  public void addIgnoredType( Class<?> type );
  
  /**
   * Adds a path to be ignored by the traversal when encountered. This is an exclusion mechanism.
   * 
   * @see #addPreservedPath(String)
   * @param path
   */
  public void addIgnoredPath( String path );
}
