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
package org.omnaest.utils.structure.array;

import java.util.Arrays;
import java.util.List;

import org.omnaest.utils.structure.collection.CollectionUtil;
import org.omnaest.utils.structure.collection.CollectionUtil.ElementConverter;
import org.omnaest.utils.structure.collection.list.ListUtil;

/**
 * Helper methods for arrays.
 * 
 * @see CollectionUtil
 * @author Omnaest
 */
public class ArrayUtil
{
  /**
   * Converts a given array into a new array with a new element type.
   * 
   * @param arrayFrom
   * @param arrayTo
   *          : instance of the array later returned. Use the return value to set this list. The array will not be necessarily
   *          changed only by the call as parameter.
   * @param elementConverter
   * @return
   */
  
  public static <TO, FROM> TO[] convertArray( FROM[] arrayFrom, TO[] arrayTo, ElementConverter<FROM, TO> elementConverter )
  {
    List<TO> listTo = ListUtil.convertList( Arrays.asList( arrayFrom ), elementConverter );
    return listTo == null ? null : listTo.toArray( arrayTo );
  }
  
  /**
   * Converts a given array into a new list with a new element type, whereby all elements which convert to null will be excluded
   * in the target list.
   * 
   * @param listFrom
   * @param elementConverter
   * @return
   */
  public static <TO, FROM> TO[] convertArrayExcludingNullElements( FROM[] arrayFrom,
                                                                   TO[] arrayTo,
                                                                   ElementConverter<FROM, TO> elementConverter )
  {
    List<TO> listTo = ListUtil.convertListExcludingNullElements( Arrays.asList( arrayFrom ), elementConverter );
    return listTo == null ? null : listTo.toArray( arrayTo );
  }
}
