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
package org.omnaest.utils.structure.element.factory.concrete;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;

/**
 * A {@link FactoryTypeAware} which uses {@link ReflectionUtils#newInstanceOf(Class, Object...)} to create new instances.
 * 
 * @author Omnaest
 * @param <E>
 */
public class FactoryTypeAwareReflectionBased<E> implements FactoryTypeAware<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Class<? extends E> type;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see FactoryTypeAwareReflectionBased
   * @param type
   */
  public FactoryTypeAwareReflectionBased( Class<? extends E> type )
  {
    super();
    this.type = type;
    Assert.isNotNull( type, "The given type must not be null" );
  }
  
  @Override
  public E newInstance()
  {
    return ReflectionUtils.newInstanceOf( this.type );
  }
  
  @Override
  public Class<?> getInstanceType()
  {
    return this.type;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "FactoryTypeAwareReflectionBased [type=" );
    builder.append( this.type );
    builder.append( "]" );
    return builder.toString();
  }
  
}
