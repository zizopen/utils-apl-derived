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
 * Encodes the given {@link String} using the {@link Codec#alphaNumeric()} codec
 * 
 * @see Codec#alphaNumeric()
 * @see ElementConverterAlphaNumericEncodedStringToDecodedString
 * @see ElementConverter
 * @author Omnaest
 */
public class ElementConverterStringToAlphaNumericEncodedString implements ElementConverterSerializable<String, String>
{
  
  private static final long serialVersionUID = -4688840461828045728L;
  
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
        retval = Codec.alphaNumeric().encode( element );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
}
