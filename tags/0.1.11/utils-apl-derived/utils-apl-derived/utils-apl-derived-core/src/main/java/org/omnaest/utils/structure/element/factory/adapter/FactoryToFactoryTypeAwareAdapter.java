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
package org.omnaest.utils.structure.element.factory.adapter;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.factory.Factory;
import org.omnaest.utils.structure.element.factory.FactoryTypeAware;

/**
 * Adapter which allows to use a {@link Factory} in combination with a given type as {@link FactoryTypeAware} instance
 * 
 * @see Factory
 * @see FactoryTypeAware
 * @author Omnaest
 */
public class FactoryToFactoryTypeAwareAdapter<E> implements FactoryTypeAware<E>
{
  /* ********************************************** Variables ********************************************** */
  private final Factory<E> factory;
  private final Class<E>   instanceType;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see FactoryToFactoryTypeAwareAdapter
   * @param factory
   * @param instanceType
   */
  public FactoryToFactoryTypeAwareAdapter( Factory<E> factory, Class<E> instanceType )
  {
    //
    super();
    this.factory = factory;
    this.instanceType = instanceType;
    
    //
    Assert.isNotNull( "Factory and instanceType must not be null", factory, instanceType );
  }
  
  @Override
  public E newInstance()
  {
    return this.factory.newInstance();
  }
  
  @Override
  public Class<?> getInstanceType()
  {
    return this.instanceType;
  }
}
