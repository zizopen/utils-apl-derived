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
package org.omnaest.utils.spring;

import java.io.InputStream;

import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Extension of the {@link ResourceLoader} of Spring which allows to retrieve {@link Table} instances
 * 
 * @author Omnaest
 */
public class ResourceLoaderTable
{
  /* ********************************************** Variables ********************************************** */
  @Autowired
  private ResourceLoader resourceLoader = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Returns a new {@link Table} instance for the location
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
                                       boolean hasRowTitles )
  {
    //
    Table<E> table = null;
    
    //
    try
    {
      if ( location != null )
      {
        //
        Resource resource = this.resourceLoader.getResource( location );
        InputStream inputStream = resource.getInputStream();
        
        //
        table = new ArrayTable<E>().serializer()
                                   .unmarshal( new TableUnmarshallerCSV<E>( encoding, delimiter, hasTableName, hasColumnTitles,
                                                                            hasRowTitles ) )
                                   .from( inputStream );
      }
    }
    catch ( Exception e )
    {
    }
    
    //
    return table;
  }
  
  public void setResourceLoader( ResourceLoader resourceLoader )
  {
    this.resourceLoader = resourceLoader;
  }
  
}
