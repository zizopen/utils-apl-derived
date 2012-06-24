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
package org.omnaest.utils.structure.element.converter;

import org.omnaest.utils.structure.element.factory.concrete.FactoryTypeAwareReflectionBased;

/**
 * {@link ElementConverter} which converts a given {@link Class} type to an instance using {@link FactoryTypeAwareReflectionBased}
 * 
 * @see ElementConverterClassToClassInstanceFactory
 * @author Omnaest
 * @param <T>
 *          type
 */
public class ElementConverterClassToInstance<T> implements ElementConverterSerializable<Class<? extends T>, T>
{
  private static final long serialVersionUID = -8416694245473698768L;

  @Override
  public T convert( Class<? extends T> type )
  {
    return new FactoryTypeAwareReflectionBased<T>( type ).newInstance();
  }
}
