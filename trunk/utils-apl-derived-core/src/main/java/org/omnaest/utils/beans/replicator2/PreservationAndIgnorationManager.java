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

import com.thoughtworks.xstream.io.json.AbstractJsonWriter.Type;

/**
 * @author Omnaest
 */
interface PreservationAndIgnorationManager extends PreservationAndIgnorationDeclarer
{
  
  /**
   * @param type
   * @return
   */
  public boolean isPreservedType( Class<?> type );
  
  /**
   * Returns true if there is an preserved {@link Path} declared
   * 
   * @param path
   * @return
   */
  @SuppressWarnings("javadoc")
  public boolean isPreservedPath( Path path );
  
  /**
   * Returns true, if the given {@link Path} should be ignored and the traversal should leap over
   * 
   * @param subPath
   *          {@link Path}
   * @return
   */
  @SuppressWarnings("javadoc")
  public boolean isIgnoredPath( Path subPath );
  
  /**
   * Returns true if the traversal should ignore the given {@link Type} and not inject at all into the target
   * 
   * @param type
   * @return
   */
  public boolean isIgnoredType( Class<?> type );
}
