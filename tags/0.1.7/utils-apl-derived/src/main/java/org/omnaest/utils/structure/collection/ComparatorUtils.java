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
package org.omnaest.utils.structure.collection;

import java.util.Comparator;
import java.util.List;

import org.springframework.util.Assert;

/**
 * Helper for {@link Comparator}
 * 
 * @author Omnaest
 */
public class ComparatorUtils
{
  /**
   * Returns a {@link Comparator} which uses {@link List#indexOf(Object)} to determine a comparing value for each element. If a
   * element is not present within the given {@link List} it is treated as being larger as an element being part of the
   * {@link List}.
   * 
   * @param list
   * @return
   */
  public static <T> Comparator<T> comparatorUsingListIndexPosition( final List<T> list )
  {
    Assert.notNull( list );
    return new Comparator<T>()
    {
      
      @Override
      public int compare( T element1, T element2 )
      {
        //
        int retval = 0;
        
        //
        int indexPosition1 = list.indexOf( element1 );
        int indexPosition2 = list.indexOf( element2 );
        
        //
        if ( indexPosition1 == indexPosition2 )
        {
          retval = 0;
        }
        else if ( indexPosition1 == -1 )
        {
          retval = 1;
        }
        else if ( indexPosition2 == -1 )
        {
          retval = -1;
        }
        else if ( indexPosition1 < indexPosition2 )
        {
          retval = -1;
        }
        else if ( indexPosition1 > indexPosition2 )
        {
          retval = 1;
        }
        
        //
        return retval;
      }
    };
  }
}
