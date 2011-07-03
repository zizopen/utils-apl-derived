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
package org.omnaest.utils.structure.table.view;

import org.omnaest.utils.structure.table.Table;

/**
 * View of an underlying {@link Table}. A {@link TableView} does not have an own data structure, instead it relies on the data
 * structure of another existing {@link Table}. This implies that all modifications made to a {@link TableView} are made to the
 * underlying {@link Table} and vice versa.
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TableView<E> extends Table<E>
{
  
}
