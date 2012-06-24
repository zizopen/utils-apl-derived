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
package org.omnaest.utils.structure.table.concrete.internal.serializer;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.common.CSVMarshallingConfiguration;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;

/**
 * @see TableSerializerImpl
 * @author Omnaest
 */
public class TableSerializerImplUrlConnectionTest
{
  
  @SuppressWarnings("unused")
  @Ignore("Needs access to the internet")
  @Test
  public void testTableSerializerUrl() throws MalformedURLException
  {
    
    //
    final String yahooFinanceHistoricalDataUrlString = "http://ichart.finance.yahoo.com/table.csv?s=%5EGDAXI&d=8&e=10&f=2011&g=d&a=10&b=26&c=1990&ignore=.csv";
    final String yahooFinanceSingleStockQuoteUrlString = "http://download.finance.yahoo.com/d/quotes.csv?s=%5EGDAXI&f=sl1d1t1c1ohgv&e=.csv";
    
    //
    Table<Object> table = new TableSerializerImpl<Object>( new ArrayTable<Object>() ).unmarshal( new TableUnmarshallerCSV<Object>().setConfiguration( new CSVMarshallingConfiguration().setEncoding( "utf-8" )
                                                                                                                                                                                    .setDelimiter( "," )
                                                                                                                                                                                    .setHasEnabledTableName( false )
                                                                                                                                                                                    .setHasEnabledColumnTitles( false )
                                                                                                                                                                                    .setHasEnabledRowTitles( false ) ) )
                                                                                     .from( new URL(
                                                                                                     yahooFinanceHistoricalDataUrlString ) );
    
    //
    System.out.println( table );
    
  }
}
