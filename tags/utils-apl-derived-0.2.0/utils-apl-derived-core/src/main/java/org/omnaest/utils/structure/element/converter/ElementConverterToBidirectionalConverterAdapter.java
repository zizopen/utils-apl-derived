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

/**
 * Adapter for two {@link ElementConverter} instances acting like an {@link ElementBidirectionalConverter} <br>
 * <br>
 * If any given {@link ElementConverter} is null, the respective transformation direction will throw an
 * {@link UnsupportedOperationException}
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class ElementConverterToBidirectionalConverterAdapter<FROM, TO> implements ElementBidirectionalConverterSerializable<FROM, TO>
{
  private static final long          serialVersionUID = -6037579287436286751L;
  
  private ElementConverter<FROM, TO> elementConverter;
  private ElementConverter<TO, FROM> elementConverterReverse;
  
  /**
   * @see ElementConverterToBidirectionalConverterAdapter
   * @param elementConverter
   * @param elementConverterReverse
   */
  public ElementConverterToBidirectionalConverterAdapter( ElementConverter<FROM, TO> elementConverter,
                                                   ElementConverter<TO, FROM> elementConverterReverse )
  {
    super();
    this.elementConverter = elementConverter;
    this.elementConverterReverse = elementConverterReverse;
  }
  
  @Override
  public FROM convertBackwards( TO element )
  {
    if ( this.elementConverterReverse != null )
    {
      return this.elementConverterReverse.convert( element );
    }
    throw new UnsupportedOperationException();
  }
  
  @Override
  public TO convert( FROM element )
  {
    if ( this.elementConverter != null )
    {
      return this.elementConverter.convert( element );
    }
    throw new UnsupportedOperationException();
  }
  
}
