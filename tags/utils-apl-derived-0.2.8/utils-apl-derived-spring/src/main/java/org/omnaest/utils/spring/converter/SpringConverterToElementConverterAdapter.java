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
 * Adapter for {@link Converter} used by Spring to act as {@link ElementConverter}
 * 
 * @see ElementConverterToSpringConverterAdapter
 * @author Omnaest
 * @param <S>
 * @param <T>
 */
public class SpringConverterToElementConverterAdapter<S, T> implements ElementConverter<S, T>, Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long       serialVersionUID = -7540648499245439765L;
  /* ********************************************** Variables ********************************************** */
  protected final Converter<S, T> converter;
  
  /* ********************************************** Methods ********************************************** */
  
  private SpringConverterToElementConverterAdapter( Converter<S, T> converter )
  {
    super();
    this.converter = converter;
    Assert.notNull( converter, "Please provide a non null Spring converter instance" );
  }
  
  @Override
  public T convert( S element )
  {
    return this.converter.convert( element );
  }
  
}
