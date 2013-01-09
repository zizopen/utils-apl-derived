/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
 * @author Omnaest
 */
public class ElementConverterChainTest
{
  /* ********************************************** Variables ********************************************** */
  private final ElementConverter<String, Integer> elementConverterFirst  = new ElementConverterStringToInteger();
  private final ElementConverter<Integer, Long>   elementConverterSecond = new ElementConverter<Integer, Long>()
                                                                         {
                                                                           @Override
                                                                           public Long convert( Integer element )
                                                                           {
                                                                             return Long.valueOf( String.valueOf( element ) );
                                                                           }
                                                                         };
  private final ElementConverter<Number, String>  elementConverterThird  = new ElementConverterNumberToString();
  private final ElementConverter<String, String>  elementConverter       = new ElementConverterChain<String, String>(
                                                                                                                      this.elementConverterFirst,
                                                                                                                      this.elementConverterSecond,
                                                                                                                      this.elementConverterThird );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testConvert()
  {
    //
    assertEquals( "100", this.elementConverter.convert( "100" ) );
  }
  
}
