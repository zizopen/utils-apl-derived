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
 * @see BeanReplicator
 * @author Omnaest
 */
public class CopyException extends Exception
{
  private static final long serialVersionUID = 8967195180924402684L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final String      canonicalPath;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see CopyException
   * @param cause
   * @param canonicalPath
   */
  CopyException( String message, String canonicalPath )
  {
    super( message );
    this.canonicalPath = canonicalPath;
  }
  
  /**
   * @see CopyException
   * @param cause
   * @param canonicalPath
   */
  CopyException( Throwable cause, String canonicalPath )
  {
    super( "Copying failed at " + canonicalPath, cause );
    this.canonicalPath = canonicalPath;
  }
  
  /**
   * @return
   */
  public String getCanonicalPath()
  {
    return this.canonicalPath;
  }
  
}
