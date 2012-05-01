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
package org.omnaest.utils.structure.enumeration;

/**
 * Interface which abstracts the {@link Enum#name()} method which is the only method which can be shared across several
 * {@link Enum} implementations without conflicts.<br>
 * <br>
 * This allows to use {@link Enum}s as a container for options but keep the code points where it is used open to extension by
 * other {@link Enum}s implementing the same interface. This same interface should be a subtype from this one.
 * 
 * @author Omnaest
 */
public interface Enumeration
{
  /**
   * Returns the name of the {@link Enumeration}
   * 
   * @return
   */
  public String name();
  
}
