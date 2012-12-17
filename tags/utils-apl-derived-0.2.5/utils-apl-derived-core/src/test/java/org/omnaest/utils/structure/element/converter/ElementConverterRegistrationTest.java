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

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

/**
 * @see ElementConverterRegistration
 * @author Omnaest
 */
public class ElementConverterRegistrationTest
{
  
  @SuppressWarnings("rawtypes")
  @Test
  public void testDetermineElementConverterFor()
  {
    {
      //
      final Class<Double> sourceType = Double.class;
      final Class<String> targetType = String.class;
      
      ElementConverter elementConverter = ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      
      //
      assertTrue( ( elementConverter instanceof ElementConverterNumberToString ) );
    }
    {
      //
      final Class<Boolean> sourceType = boolean.class;
      final Class<String> targetType = String.class;
      
      ElementConverter elementConverter = ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      
      //
      assertTrue( ( elementConverter instanceof ElementConverterBooleanToString ) );
    }
    {
      //
      final Class<Long> sourceType = Long.class;
      final Class<Long> targetType = long.class;
      
      ElementConverter elementConverter = ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      
      //
      assertTrue( ( elementConverter instanceof ElementConverterIdentitiyCast ) );
    }
    {
      //
      final Class<Double> sourceType = double.class;
      final Class<Double> targetType = Double.class;
      
      ElementConverter elementConverter = ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      
      //
      assertTrue( ( elementConverter instanceof ElementConverterIdentitiyCast ) );
    }
    {
      //
      final Class<String> sourceType = String.class;
      final Class<BigDecimal> targetType = BigDecimal.class;
      
      ElementConverter elementConverter = ElementConverterRegistration.determineElementConverterFor( sourceType, targetType );
      
      //
      assertTrue( ( elementConverter instanceof ElementConverterStringToBigDecimal ) );
    }
  }
  
}
