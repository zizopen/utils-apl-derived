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
package org.omnaest.utils.structure.map;

import java.util.Map;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.IdentityElementConverter;

/**
 * Special implementation of a {@link MapToMapAdapter} which only converts the values of a {@link Map}
 * 
 * @author Omnaest
 */
public class MapToMapAdapterForValue<KEY, VALUE_FROM, VALUE_TO> extends MapToMapAdapter<KEY, VALUE_FROM, KEY, VALUE_TO>

{
  
  public MapToMapAdapterForValue( Map<KEY, VALUE_FROM> sourceMap,
                                  ElementConverter<VALUE_FROM, VALUE_TO> elementConverterValueSourceToAdapter,
                                  ElementConverter<VALUE_TO, VALUE_FROM> elementConverterValueAdapterToSource )
  {
    super( sourceMap, new IdentityElementConverter<KEY>(), new IdentityElementConverter<KEY>(),
           elementConverterValueSourceToAdapter, elementConverterValueAdapterToSource );
  }
  
}
