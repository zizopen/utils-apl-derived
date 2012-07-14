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

import org.omnaest.utils.events.exception.ExceptionHandler;

/**
 * The {@link BeanCopier} is a simple {@link BeanReplicator} which has the same source and target type
 * 
 * @see BeanReplicator
 * @author Omnaest
 * @param <B>
 */
public class BeanCopier<B> extends BeanReplicator<B, B>
{
  
  private static final long serialVersionUID = -5810117294888896465L;
  
  /**
   * @see BeanCopier
   * @param type
   */
  public BeanCopier( Class<B> type )
  {
    super( type, type );
  }
  
  @Override
  public BeanCopier<B> declare( org.omnaest.utils.beans.replicator2.BeanReplicator.Declaration declaration )
  {
    super.declare( declaration );
    return this;
  }
  
  @Override
  public BeanCopier<B> setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    super.setExceptionHandler( exceptionHandler );
    return this;
  }
  
}
