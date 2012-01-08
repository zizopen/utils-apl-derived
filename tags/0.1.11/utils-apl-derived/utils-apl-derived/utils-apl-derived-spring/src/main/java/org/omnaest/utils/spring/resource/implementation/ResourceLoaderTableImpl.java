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
package org.omnaest.utils.spring.resource.implementation;

import java.io.InputStream;

import org.omnaest.utils.spring.resource.ResourceLoaderTable;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXLS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @see ResourceLoader
 * @author Omnaest
 */
public class ResourceLoaderTableImpl implements ResourceLoaderTable
{
  /* ********************************************** Variables ********************************************** */
  @Autowired
  protected ResourceLoader resourceLoader = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public <E> Table<E> getTableFrom( String location, TableUnmarshaller<E> tableUnmarshaller )
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
        table = new ArrayTable<E>().serializer().unmarshal( tableUnmarshaller ).from( inputStream );
      }
    }
    catch ( Exception e )
    {
    }
    
    //
    return table;
  }
  
  @Override
  public <E> Table<E> getTableFromXLS( String location,
                                       String workSheetName,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles )
  {
    TableUnmarshaller<E> tableUnmarshaller = new TableUnmarshallerXLS<E>( workSheetName, hasTableName, hasColumnTitles,
                                                                          hasRowTitles );
    return this.getTableFrom( location, tableUnmarshaller );
  }
  
  @Override
  public <E> Table<E> getTableFromCSV( String location,
                                       String encoding,
                                       String delimiter,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles )
  {
    //
    return this.getTableFrom( location, new TableUnmarshallerCSV<E>( encoding, delimiter, hasTableName, hasColumnTitles,
                                                                     hasRowTitles ) );
  }
  
}
