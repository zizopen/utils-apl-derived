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
package org.omnaest.utils.strings.tokenizer;

import java.util.Iterator;

import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IteratorUtils;

/**
 * Decorator of a {@link CharacterSequenceTokenizer} which allows to use an {@link ElementConverter} to modify the output type of
 * the {@link Iterator#next()} method.
 * 
 * @author Omnaest
 */
public class ConvertingCharacterSequenceTokenizerDecorator<TO> implements Iterable<TO>
{
  /* ********************************************** Variables ********************************************** */
  private final ElementConverter<CharSequence, TO> elementConverter;
  private final CharacterSequenceTokenizer         characterSequenceTokenizer;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ConvertingCharacterSequenceTokenizerDecorator
   * @param characterSequenceTokenizer
   * @param elementConverter
   */
  public ConvertingCharacterSequenceTokenizerDecorator( CharacterSequenceTokenizer characterSequenceTokenizer,
                                                        ElementConverter<CharSequence, TO> elementConverter )
  {
    this.characterSequenceTokenizer = characterSequenceTokenizer;
    this.elementConverter = elementConverter;
  }
  
  @Override
  public Iterator<TO> iterator()
  {
    // 
    final Iterator<CharSequence> iterator = this.characterSequenceTokenizer.iterator();
    final Iterator<TO> convertingIteratorDecorator = IteratorUtils.adapter( iterator, this.elementConverter );
    
    //
    return convertingIteratorDecorator;
  }
  
}
