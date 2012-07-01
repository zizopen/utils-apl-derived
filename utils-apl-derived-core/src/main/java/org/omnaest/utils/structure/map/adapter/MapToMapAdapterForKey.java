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
package org.omnaest.utils.structure.map.adapter;

import java.util.Map;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;

/**
 * Special implementation of a {@link MapToMapAdapter} which only converts the keys of a {@link Map}
 * 
 * @author Omnaest
 */
public class MapToMapAdapterForKey<KEY_FROM, KEY_TO, VALUE> extends MapToMapAdapter<KEY_FROM, VALUE, KEY_TO, VALUE>
{
  
  private static final long serialVersionUID = 7471520105069297143L;
  
  public MapToMapAdapterForKey( Map<KEY_FROM, VALUE> sourceMap,
                                ElementConverter<KEY_FROM, KEY_TO> elementConverterKeySourceToAdapter,
                                ElementConverter<KEY_TO, KEY_FROM> elementConverterKeyAdapterToSource )
  {
    super( sourceMap, elementConverterKeySourceToAdapter, elementConverterKeyAdapterToSource,
           new ElementConverterIdentity<VALUE>(), new ElementConverterIdentity<VALUE>() );
  }
  
}
