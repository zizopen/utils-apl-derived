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
package org.omnaest.utils.spring.resource;

import java.io.Serializable;

import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;
import org.omnaest.utils.table2.Table;
import org.springframework.core.io.ResourceLoader;

/**
 * Extension of the {@link ResourceLoader} of Spring which allows to retrieve {@link Table} instances <br>
 * <br>
 * Spring configuration:
 * 
 * <pre>
 * &lt;bean class=&quot;org.omnaest.utils.spring.resource.implementation.ResourceLoaderTableImpl&quot; /&gt;
 * </pre>
 * 
 * @author Omnaest
 */
public interface ResourceLoaderTable extends Serializable
{
  
  /**
   * Returns a new {@link Table} instance for the location of a csv file
   * 
   * @param elementType
   * @param location
   * @param encoding
   * @param delimiter
   * @param quotationCharacter
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   * @see #getTableFromCSV(Class, String, CSVMarshallingConfiguration)
   * @return
   */
  public <E> Table<E> getTableFromCSV( Class<E> elementType,
                                       String location,
                                       String encoding,
                                       String delimiter,
                                       String quotationCharacter,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles );
  
  /**
   * Similar to {@link #getTableFromCSV(Class, String, String, String, String, boolean, boolean, boolean)} using the given
   * {@link CSVMarshallingConfiguration} instance
   * 
   * @param elementType
   * @param location
   * @param configuration
   *          {@link CSVMarshallingConfiguration}
   * @return {@link Table}
   */
  public <E> Table<E> getTableFromCSV( Class<? extends E> elementType, String location, CSVMarshallingConfiguration configuration );
  
}
