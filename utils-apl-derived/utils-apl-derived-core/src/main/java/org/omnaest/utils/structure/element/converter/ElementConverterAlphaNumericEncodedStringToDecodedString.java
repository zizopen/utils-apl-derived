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

import org.omnaest.utils.codec.Codec;

/**
 * Decodes the given {@link String} using the {@link Codec#AlphaNumeric} codec
 * 
 * @see Codec#AlphaNumeric
 * @see ElementConverterStringToAlphaNumericEncodedString
 * @see ElementConverter
 * @author Omnaest
 */
public class ElementConverterAlphaNumericEncodedStringToDecodedString implements ElementConverter<String, String>
{
  
  @Override
  public String convert( String element )
  {
    //    
    String retval = null;
    
    //
    if ( element != null )
    {
      try
      {
        retval = Codec.AlphaNumeric.decode( element );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
}
