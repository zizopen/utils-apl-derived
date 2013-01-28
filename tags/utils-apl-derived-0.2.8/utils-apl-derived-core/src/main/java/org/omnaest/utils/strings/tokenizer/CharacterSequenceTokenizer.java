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

/**
 * {@link Iterable} over {@link CharSequence} tokens which are based on another tokenized underlying {@link CharSequence}. <br>
 * <br>
 * An instance of this {@link CharacterSequenceTokenizer} is thread safe, since it creates new {@link Iterator} instances for each
 * call to {@link #iterator()}. The {@link Iterator} instances therefore are NOT threadsafe.
 * 
 * @author Omnaest
 */
public interface CharacterSequenceTokenizer extends Iterable<CharSequence>
{
}
