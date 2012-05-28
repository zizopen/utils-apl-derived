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
 * {@link ElementConverter} which abstracts a chain of other {@link ElementConverter} instances
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public class ElementConverterChain<FROM, TO> implements ElementConverter<FROM, TO>
{
  /* ********************************************** Beans / Services / References ********************************************** */
  private final ElementConverter<?, ?>[] elementConverters;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ElementConverterChain
   * @param elementConverterFirst
   * @param elementConverterSecond
   */
  public <T> ElementConverterChain( ElementConverter<FROM, ? extends T> elementConverterFirst,
                                    ElementConverter<T, ? extends TO> elementConverterSecond )
  {
    super();
    this.elementConverters = new ElementConverter<?, ?>[] { elementConverterFirst, elementConverterSecond };
  }
  
  /**
   * @see ElementConverterChain
   * @param elementConverterFirst
   * @param elementConverterSecond
   * @param elementConverterThird
   */
  public <T1, T2> ElementConverterChain( ElementConverter<FROM, ? extends T1> elementConverterFirst,
                                         ElementConverter<T1, ? extends T2> elementConverterSecond,
                                         ElementConverter<T2, ? extends TO> elementConverterThird )
  {
    super();
    this.elementConverters = new ElementConverter<?, ?>[] { elementConverterFirst, elementConverterSecond, elementConverterThird };
  }
  
  /**
   * @see ElementConverterChain
   * @param elementConverterFirst
   * @param elementConverterSecond
   * @param elementConverterThird
   * @param elementConverterFourth
   */
  public <T1, T2, T3> ElementConverterChain( ElementConverter<FROM, ? extends T1> elementConverterFirst,
                                             ElementConverter<T1, ? extends T2> elementConverterSecond,
                                             ElementConverter<T2, ? extends T3> elementConverterThird,
                                             ElementConverter<T3, ? extends TO> elementConverterFourth )
  {
    super();
    this.elementConverters = new ElementConverter<?, ?>[] { elementConverterFirst, elementConverterSecond, elementConverterThird,
        elementConverterFourth };
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public TO convert( FROM element )
  {
    //
    Object retval = element;
    
    //
    for ( ElementConverter<Object, Object> elementConverter : (ElementConverter<Object, Object>[]) this.elementConverters )
    {
      //
      retval = elementConverter.convert( retval );
    }
    
    // 
    return (TO) retval;
  }
  
}
