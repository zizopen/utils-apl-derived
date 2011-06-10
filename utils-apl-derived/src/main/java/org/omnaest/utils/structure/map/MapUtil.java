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

import java.util.LinkedHashMap;
import java.util.Map;

import org.omnaest.utils.structure.collection.CollectionUtil.ElementConverter;
import org.omnaest.utils.structure.collection.CollectionUtil.IdentityElementConverter;

/**
 * Helper class for map operations.
 * 
 * @author Omnaest
 */
public class MapUtil
{
  
  /**
   * Converts the key of a map into another key.
   * 
   * @param <KeyFrom>
   * @param <KeyTo>
   * @param <Value>
   * @param map
   * @param keyElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, Value> Map<KeyTo, Value> convertMapKey( Map<KeyFrom, Value> map,
                                                                         ElementConverter<KeyFrom, KeyTo> keyElementConverter )
  {
    return MapUtil.convertMap( map, keyElementConverter, new IdentityElementConverter<Value>() );
  }
  
  /**
   * Converts a given map into a new map with new types. Makes use of element converters.
   * 
   * @see ElementConverter
   * @param map
   * @param keyElementConverter
   * @param valueElementConverter
   * @return
   */
  public static <KeyFrom, KeyTo, ValueFrom, ValueTo> Map<KeyTo, ValueTo> convertMap( Map<KeyFrom, ValueFrom> map,
                                                                                     ElementConverter<KeyFrom, KeyTo> keyElementConverter,
                                                                                     ElementConverter<ValueFrom, ValueTo> valueElementConverter )
  {
    //
    Map<KeyTo, ValueTo> retmap = null;
    
    //
    if ( map != null )
    {
      //
      retmap = new LinkedHashMap<KeyTo, ValueTo>( map.size() );
      
      //
      for ( KeyFrom keyFrom : map.keySet() )
      {
        KeyTo keyTo = keyElementConverter.convert( keyFrom );
        ValueTo valueTo = valueElementConverter.convert( map.get( keyFrom ) );
        
        retmap.put( keyTo, valueTo );
      }
      
    }
    
    //
    return retmap;
  }
}
