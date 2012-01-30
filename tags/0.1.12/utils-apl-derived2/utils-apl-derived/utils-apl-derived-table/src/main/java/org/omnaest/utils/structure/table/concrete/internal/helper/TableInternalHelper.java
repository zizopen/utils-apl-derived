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
package org.omnaest.utils.structure.table.concrete.internal.helper;

import java.lang.reflect.Method;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.internal.TableInternal;

/**
 * Helper for {@link TableInternal} instances
 * 
 * @author Omnaest
 */
public class TableInternalHelper
{
  
  /**
   * Extracts the {@link TableInternal} instance from the given {@link Table} instance by calling the internal getter called
   * getTableInternal
   * 
   * @param table
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <E> TableInternal<E> extractTableInternalFromTable( Table<E> table )
  {
    //    
    TableInternal<E> retval = null;
    
    //
    if ( table != null )
    {
      //
      try
      {
        //
        Method methodGetTableInternal = table.getClass().getDeclaredMethod( "getTableInternal" );
        
        //
        boolean accessible = methodGetTableInternal.isAccessible();
        {
          //
          methodGetTableInternal.setAccessible( true );
          retval = (TableInternal<E>) methodGetTableInternal.invoke( table );
        }
        methodGetTableInternal.setAccessible( accessible );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
}
