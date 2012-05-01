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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * @see PatternBasedCharacterSequenceTokenizer
 * @author Omnaest
 */
public class PatternBasedCharacterSequenceTokenizerTest
{
  /* ********************************************** Variables ********************************************** */
  private static final String                                   regexDelimiter                                = ";";
  private static final CharSequence                             charSequence                                  = "column1;column2;column3";
  private static final ElementConverter<CharSequence, String>   elementConverter                              = new ElementConverter<CharSequence, String>()
                                                                                                              {
                                                                                                                @Override
                                                                                                                public String convert( CharSequence element )
                                                                                                                {
                                                                                                                  return String.valueOf( element );
                                                                                                                }
                                                                                                              };
  private PatternBasedCharacterSequenceTokenizer                characterSequenceTokenizer                    = new PatternBasedCharacterSequenceTokenizer(
                                                                                                                                                            charSequence,
                                                                                                                                                            regexDelimiter );
  private ConvertingCharacterSequenceTokenizerDecorator<String> convertingCharacterSequenceTokenizerDecorator = new ConvertingCharacterSequenceTokenizerDecorator<String>(
                                                                                                                                                                           this.characterSequenceTokenizer,
                                                                                                                                                                           elementConverter );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testIterator()
  {
    //
    final List<String> tokenList = ListUtils.valueOf( this.convertingCharacterSequenceTokenizerDecorator );
    assertEquals( Arrays.asList( "column1", "column2", "column3" ), tokenList );
  }
  
}
