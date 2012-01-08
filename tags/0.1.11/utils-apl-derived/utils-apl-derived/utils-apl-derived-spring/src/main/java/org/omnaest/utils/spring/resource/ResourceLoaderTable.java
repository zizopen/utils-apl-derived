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

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
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
public interface ResourceLoaderTable
{
  
  /**
   * Returns a new {@link Table} instance for the location of a csv file
   * 
   * @param location
   * @param encoding
   * @param delimiter
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   * @return
   */
  public <E> Table<E> getTableFromCSV( String location,
                                       String encoding,
                                       String delimiter,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles );
  
  /**
   * Returns a new {@link Table} instance for the location of an Excel xls file
   * 
   * @param location
   * @param workSheetName
   * @param hasTableName
   * @param hasColumnTitles
   * @param hasRowTitles
   * @return
   */
  public <E> Table<E> getTableFromXLS( String location,
                                       String workSheetName,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles );
  
  /**
   * Returns a {@link Table} for the given resource location which is parsed using the given {@link TableUnmarshaller}
   * 
   * @see TableUnmarshaller
   * @param location
   * @param tableUnmarshaller
   * @return
   */
  public <E> Table<E> getTableFrom( String location, TableUnmarshaller<E> tableUnmarshaller );
  
}
