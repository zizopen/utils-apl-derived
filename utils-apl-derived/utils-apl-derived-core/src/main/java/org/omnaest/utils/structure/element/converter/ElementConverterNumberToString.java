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

/**
 * @see ElementConverterStringToInteger
 * @see ElementConverterStringToLong
 * @see ElementConverterStringToBigInteger
 * @see ElementConverterStringToBigDecimal
 * @see ElementConverterStringToByte
 * @see ElementConverterStringToDouble
 * @see ElementConverterStringToFloat
 * @see ElementConverterStringToShort
 * @see ElementConverterTypeAware
 * @author Omnaest
 */
public class ElementConverterNumberToString implements ElementConverterTypeAwareSerializable<Number, String>
{
  private static final long serialVersionUID = -1906828641367140349L;

  @Override
  public String convert( Number element )
  {
    return element != null ? String.valueOf( element ) : null;
  }
  
  @Override
  public SourceAndTargetType<Number, String> getSourceAndTargetType()
  {
    return new SourceAndTargetType<Number, String>( Number.class, String.class );
  }
}
