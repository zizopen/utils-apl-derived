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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @see ElementConverterHelper
 * @author Omnaest
 */
public class ElementConverterHelperTest
{
  
  @SuppressWarnings("unchecked")
  @Test
  public void testConvert()
  {
    {
      //
      final String element = "This is a test value";
      String convertedElement = ElementConverterHelper.convert( element );
      assertEquals( element, convertedElement );
    }
    {
      //
      final Class<? extends ElementConverter<?, ?>>[] elementConverterTypes = new Class[] { ElementConverterStringToAlphaNumericEncodedString.class };
      final String element = "This is a test value";
      
      //
      String convertedElement = ElementConverterHelper.convert( element, elementConverterTypes );
      
      //
      assertEquals( "This160is160a160test160value", convertedElement );
    }
    {
      //
      final Class<? extends ElementConverter<?, ?>>[] elementConverterTypes = new Class[] {
          ElementConverterStringToAlphaNumericEncodedString.class, ElementConverterAlphaNumericEncodedStringToDecodedString.class };
      final String element = "This is a test value";
      
      //
      String convertedElement = ElementConverterHelper.convert( element, elementConverterTypes );
      
      //
      assertEquals( element, convertedElement );
    }
  }
  
}
