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
package org.omnaest.utils.table.impl;

import java.util.Map;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table.Stripe;
import org.omnaest.utils.table.StripesTransformer;

/**
 * @see StripesTransformer
 * @author Omnaest
 * @param <E>
 */
class StripesTransformerImpl<E> implements StripesTransformer<E>
{
  private final Iterable<? extends Stripe<E>> stripeIterable;
  private static final long                   serialVersionUID = 2110974282111579037L;
  
  StripesTransformerImpl( Iterable<? extends Stripe<E>> stripeIterable )
  {
    this.stripeIterable = stripeIterable;
  }
  
  @Override
  public <T> Iterable<T> instancesOf( final Class<T> type )
  {
    final ElementConverter<Stripe<E>, T> elementConverter = new ElementConverterSerializable<Stripe<E>, T>()
    {
      private static final long serialVersionUID = 9082363234986556312L;
      
      @Override
      public T convert( Stripe<E> column )
      {
        return column.to().instanceOf( type );
      }
    };
    return IterableUtils.adapter( this.stripeIterable, elementConverter );
  }
  
  @Override
  public Iterable<Map<String, E>> maps()
  {
    final ElementConverter<Stripe<E>, Map<String, E>> elementConverter = new ElementConverterSerializable<Stripe<E>, Map<String, E>>()
    {
      private static final long serialVersionUID = -4832586956691329459L;
      
      @Override
      public Map<String, E> convert( Stripe<E> stripe )
      {
        return stripe.to().map();
      }
    };
    return IterableUtils.adapter( this.stripeIterable, elementConverter );
  }
}