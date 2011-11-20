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
package org.omnaest.utils.beans.adapter.source;

/**
 * Decorator for {@link SourcePropertyAccessor}
 * 
 * @author Omnaest
 */
public abstract class SourcePropertyAccessorDecorator implements SourcePropertyAccessor
{
  /* ********************************************** Constants ********************************************** */
  private static final long        serialVersionUID       = -3420846101586221728L;
  /* ********************************************** Variables ********************************************** */
  protected SourcePropertyAccessor sourcePropertyAccessor = null;
  
  /* ********************************************** Methods ********************************************** */
  public SourcePropertyAccessorDecorator()
  {
    super();
  }
  
  /**
   * @param sourcePropertyAccessor
   */
  public SourcePropertyAccessorDecorator( SourcePropertyAccessor sourcePropertyAccessor )
  {
    super();
    this.sourcePropertyAccessor = sourcePropertyAccessor;
  }
  
  /**
   * Sets the decorated {@link SourcePropertyAccessor}
   * 
   * @param sourcePropertyAccessor
   * @return this
   */
  public SourcePropertyAccessorDecorator setPropertyAccessorDecorator( SourcePropertyAccessor sourcePropertyAccessor )
  {
    this.sourcePropertyAccessor = sourcePropertyAccessor;
    return this;
  }
  
}
