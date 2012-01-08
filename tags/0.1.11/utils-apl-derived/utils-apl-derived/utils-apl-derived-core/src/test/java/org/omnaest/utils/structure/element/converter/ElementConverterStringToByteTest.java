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
import org.omnaest.utils.structure.element.converter.ElementConverterTypeAware.SourceAndTargetType;

/**
 * @see ElementConverterStringToByte
 * @author Omnaest
 */
public class ElementConverterStringToByteTest
{
  
  /* ********************************************** Variables ********************************************** */
  private ElementConverterTypeAware<String, Byte> elementConverter = new ElementConverterStringToByte();
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testConvert()
  {
    assertEquals( (byte) 123, this.elementConverter.convert( "123" ).byteValue() );
  }
  
  @Test
  public void testGetSourceAndTargetType()
  {
    //
    final SourceAndTargetType<String, Byte> sourceAndTargetType = this.elementConverter.getSourceAndTargetType();
    
    //
    final Class<String> sourceType = sourceAndTargetType.getSourceType();
    final Class<Byte> targetType = sourceAndTargetType.getTargetType();
    
    //
    assertEquals( String.class, sourceType );
    assertEquals( Byte.class, targetType );
    
  }
  
}
