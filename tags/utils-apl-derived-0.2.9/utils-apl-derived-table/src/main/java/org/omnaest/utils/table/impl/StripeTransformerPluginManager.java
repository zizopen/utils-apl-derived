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

import org.omnaest.utils.table.StripeTransformerPlugin;
import org.omnaest.utils.table.StripeTransformerPluginRegistration;

/**
 * Manager of {@link StripeTransformerPlugin} instances allowing to {@link #register(StripeTransformerPlugin)} and
 * {@link #resolveStripeTransformerPluginFor(Class)} instance of {@link StripeTransformerPlugin}s
 * 
 * @see StripeTransformerPlugin
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
interface StripeTransformerPluginManager<E> extends StripeTransformerPluginRegistration<E>
{
  /**
   * Resolves a {@link StripeTransformerPlugin} for the given type.
   * 
   * @param type
   * @return
   */
  public <T> StripeTransformerPlugin<E, T> resolveStripeTransformerPluginFor( Class<T> type );
}
