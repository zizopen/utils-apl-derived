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
package org.omnaest.utils.beans;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.omnaest.utils.beans.BeanMethodNameUtils.BeanMethodInformation;

/**
 * Helper class for Java beans.
 * 
 * @author Omnaest
 */
public class BeanUtils
{
  
  /**
   * Determines the property names of a given bean class.
   * 
   * @param clazz
   * @return
   */
  public static String[] determinePropertyNames( Class<?> clazz )
  {
    //
    String[] retvals = null;
    
    //
    Set<String> propertyNameSet = new HashSet<String>();
    if ( clazz != null )
    {
      //
      Method[] methods = clazz.getMethods();
      if ( methods != null )
      {
        for ( Method method : methods )
        {
          if ( method != null )
          {
            //
            BeanMethodInformation beanMethodInformation = BeanMethodNameUtils.determineBeanMethodInformation( method );
            
            //
            boolean isFieldAccessMethod = beanMethodInformation.isFieldAccessMethod();
            if ( isFieldAccessMethod )
            {
              //
              propertyNameSet.add( beanMethodInformation.getReferencedFieldName() );
            }
          }
        }
      }
      
      //
      propertyNameSet.remove( "class" );
      
      //
      retvals = propertyNameSet.toArray( new String[0] );
    }
    
    //
    return retvals;
  }
}
