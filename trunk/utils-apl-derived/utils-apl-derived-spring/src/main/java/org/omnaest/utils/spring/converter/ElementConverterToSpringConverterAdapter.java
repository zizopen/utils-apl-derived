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
package org.omnaest.utils.spring.converter;

import java.io.Serializable;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.Assert;

/**
 * Adapter for {@link ElementConverter} to act as {@link Converter} used by Spring
 * 
 * @see SpringConverterToElementConverterAdapter
 * @see #ElementConverterToSpringConverterAdapter(ElementConverter)
 * @author Omnaest
 * @param <S>
 * @param <T>
 */
public class ElementConverterToSpringConverterAdapter<S, T> implements Converter<S, T>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long            serialVersionUID = -6136216057561091189L;
  /* ********************************************** Variables ********************************************** */
  private final ElementConverter<S, T> elementConverter;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ElementConverterToSpringConverterAdapter
   * @param elementConverter
   */
  public ElementConverterToSpringConverterAdapter( ElementConverter<S, T> elementConverter )
  {
    super();
    this.elementConverter = elementConverter;
    Assert.notNull( elementConverter, "Please provide a non null ElementConverter reference" );
  }
  
  @Override
  public T convert( S element )
  {
    return this.elementConverter.convert( element );
  }
  
}
