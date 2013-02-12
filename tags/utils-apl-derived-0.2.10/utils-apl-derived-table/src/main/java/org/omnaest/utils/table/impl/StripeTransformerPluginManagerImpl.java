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
import java.util.concurrent.ConcurrentHashMap;

import org.omnaest.utils.table.StripeTransformerPlugin;

/**
 * Concurrent implementation of a {@link StripeTransformerPluginManager}
 * 
 * @see StripeTransformerPluginManager
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class StripeTransformerPluginManagerImpl<E> implements StripeTransformerPluginManager<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                            serialVersionUID                 = -3091488682732608050L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<Class<?>, StripeTransformerPlugin<E, ?>> typeToStripeTransformerPluginMap = new ConcurrentHashMap<Class<?>, StripeTransformerPlugin<E, ?>>();
  
  /* *************************************************** Methods **************************************************** */
  
  @Override
  public StripeTransformerPluginManager<E> register( StripeTransformerPlugin<E, ?> stripeTransformerPlugin )
  {
    if ( stripeTransformerPlugin != null )
    {
      final Class<?> type = stripeTransformerPlugin.getType();
      if ( type != null )
      {
        this.typeToStripeTransformerPluginMap.put( type, stripeTransformerPlugin );
      }
    }
    return this;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> StripeTransformerPlugin<E, T> resolveStripeTransformerPluginFor( Class<T> type )
  {
    return (StripeTransformerPlugin<E, T>) ( type == null ? null : this.typeToStripeTransformerPluginMap.get( type ) );
  }
  
}
