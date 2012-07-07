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
package org.omnaest.utils.store;

import java.io.Serializable;
import java.util.List;

/**
 * A {@link DirectoryBasedObjectStore} acts as {@link List} facade on a given directory structure. Every index position will be
 * encoded into a file path. <br>
 * <br>
 * E.g. the index position 1234 will be encoded into "1/2/3/4.dat". <br>
 * <br>
 * Any element will then have a related file on the file system. The encoding depends on the implementation.
 * 
 * @author Omnaest
 * @param <E>
 */
public interface DirectoryBasedObjectStore<E> extends List<E>, Serializable
{
}
