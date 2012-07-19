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
import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerCsv.CSVMarshallingConfiguration;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.impl.ArrayTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @see ResourceLoader
 * @author Omnaest
 */
public class ResourceLoaderTableImpl implements ResourceLoaderTable
{
  private static final long serialVersionUID = -5636329784509801515L;
  /* ********************************************** Variables ********************************************** */
  @Autowired
  protected ResourceLoader  resourceLoader   = null;
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public <E> Table<E> getTableFromCSV( Class<E> elementType,
                                       String location,
                                       String encoding,
                                       String delimiter,
                                       String quotationCharacter,
                                       boolean hasTableName,
                                       boolean hasColumnTitles,
                                       boolean hasRowTitles )
  {
    //
    CSVMarshallingConfiguration configuration = new CSVMarshallingConfiguration().setDelimiter( delimiter )
                                                                                 .setEncoding( encoding )
                                                                                 .setQuotationCharacter( quotationCharacter )
                                                                                 .setHasEnabledColumnTitles( hasColumnTitles )
                                                                                 .setHasEnabledRowTitles( hasRowTitles )
                                                                                 .setHasEnabledTableName( hasTableName );
    return this.getTableFromCSV( elementType, location, configuration );
  }
  
  @Override
  public <E> Table<E> getTableFromCSV( Class<? extends E> elementType, String location, CSVMarshallingConfiguration configuration )
  {
    Table<E> table = null;
    try
    {
      if ( location != null )
      {
        Resource resource = this.resourceLoader.getResource( location );
        InputStream inputStream = resource.getInputStream();
        
        table = new ArrayTable<E>( elementType ).serializer().unmarshal().asCsv().using( configuration ).from( inputStream );
      }
    }
    catch ( Exception e )
    {
    }
    return table;
  }
  
}
