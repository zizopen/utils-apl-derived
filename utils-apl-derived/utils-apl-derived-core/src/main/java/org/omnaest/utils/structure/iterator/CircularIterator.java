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
package org.omnaest.utils.structure.iterator;

import java.util.Iterator;

/**
 * A special {@link Iterator} which will go through the underlying structure and starts from the beginning if the last element has
 * been passed. The only case the {@link CircularIterator} will return false for {@link #hasNext()} is the case the underlying
 * structure is empty.
 * 
 * @author Omnaest
 * @param <E>
 */
public interface CircularIterator<E> extends Iterator<E>
{
}
