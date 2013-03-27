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

import org.omnaest.utils.beans.result.BeanPropertyAccessor;

/**
 * {@link ElementConverter} for converting {@link BeanPropertyAccessor} instances to property names
 * 
 * @param <B>
 *          bean type
 * @author Omnaest
 */
public class ElementConverterBeanPropertyAccessorToProperty<B> implements ElementConverterSerializable<BeanPropertyAccessor<B>, String>
{
  
  private static final long serialVersionUID = -1705171833692159068L;

  @Override
  public String convert( BeanPropertyAccessor<B> beanPropertyAccessor )
  {
    return beanPropertyAccessor == null ? null : beanPropertyAccessor.getPropertyName();
  }
  
}
