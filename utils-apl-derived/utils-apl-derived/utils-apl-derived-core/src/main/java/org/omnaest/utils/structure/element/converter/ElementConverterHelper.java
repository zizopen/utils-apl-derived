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
package org.omnaest.utils.structure.element.converter;

import org.omnaest.utils.reflection.ReflectionUtils;

/**
 * Static helper for {@link ElementConverter}
 * 
 * @author Omnaest
 */
public class ElementConverterHelper
{
  /**
   * Converts a given element using one ore multiple {@link ElementConverter} instances. Any occurring {@link Exception} will be
   * catched up and null being returned.
   * 
   * @param element
   * @param elementConverterTypes
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <TO, FROM> TO convert( FROM element, Class<? extends ElementConverter<?, ?>>... elementConverterTypes )
  {
    //    
    Object retval = element;
    
    //
    if ( elementConverterTypes != null )
    {
      try
      {
        for ( Class<? extends ElementConverter<?, ?>> elementConverterType : elementConverterTypes )
        {
          //
          final ElementConverter<Object, Object> elementConverter = (ElementConverter<Object, Object>) ReflectionUtils.createInstanceOf( elementConverterType );
          retval = elementConverter.convert( retval );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return (TO) retval;
  }
}
