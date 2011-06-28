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
package org.omnaest.utils.structure.collection.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.omnaest.utils.structure.collection.CollectionUtil;
import org.omnaest.utils.structure.collection.CollectionUtil.ElementConverter;

/**
 * Helper class for List.
 * 
 * @see CollectionUtil
 * @author Omnaest
 */
public class ListUtil
{
  /**
   * Converts a given list into a new list with a new element type.
   * 
   * @param listFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> List<TO> convertList( Collection<FROM> listFrom, ElementConverter<FROM, TO> elementConverter )
  {
    //
    List<TO> listTo = new ArrayList<TO>();
    
    //
    if ( listFrom != null && elementConverter != null )
    {
      for ( FROM iFrom : listFrom )
      {
        listTo.add( elementConverter.convert( iFrom ) );
      }
    }
    
    //
    return listTo;
  }
  
  /**
   * Converts a given list into a new list with a new element type, whereby all elements which convert to null will be excluded in
   * the target list.
   * 
   * @param listFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> List<TO> convertListExcludingNullElements( Collection<FROM> listFrom,
                                                                      ElementConverter<FROM, TO> elementConverter )
  {
    //
    List<TO> listTo = new ArrayList<TO>();
    
    //
    if ( listFrom != null && elementConverter != null )
    {
      for ( FROM iFrom : listFrom )
      {
        TO element = elementConverter.convert( iFrom );
        if ( element != null )
        {
          listTo.add( element );
        }
      }
    }
    
    //
    return listTo;
  }
}
