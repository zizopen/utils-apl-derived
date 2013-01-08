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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.omnaest.utils.structure.element.factory.Factory;

/**
 * {@link Factory} for {@link HashMap} intances
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class HashMapFactory<K, V> implements Factory<Map<K, V>>, Serializable
{
  private static final long serialVersionUID = -7592547398553622032L;
  
  @Override
  public Map<K, V> newInstance()
  {
    return new HashMap<K, V>();
  }
}
