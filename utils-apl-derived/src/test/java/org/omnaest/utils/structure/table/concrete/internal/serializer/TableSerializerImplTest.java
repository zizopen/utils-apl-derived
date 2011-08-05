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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.TableFiller;
import org.omnaest.utils.structure.table.concrete.ArrayTable;
import org.omnaest.utils.structure.table.serializer.TableMarshaller;
import org.omnaest.utils.structure.table.serializer.TableUnmarshaller;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerPlainText;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXLS;
import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerXML;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerPlainText;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXLS;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerXML;
import org.omnaest.utils.structure.table.subspecification.TableSerializable.TableSerializer;

/**
 * @see TableSerializerImpl
 * @author Omnaest
 */
@RunWith(value = Parameterized.class)
public class TableSerializerImplTest
{
  @Parameters
  public static Collection<Object[]> configurationDataCollection()
  {
    //
    List<Object[]> retlist = new ArrayList<Object[]>();
    retlist.add( new Object[] { new TableMarshallerPlainText<Object>(), new TableUnmarshallerPlainText<Object>(), true, true } );
    retlist.add( new Object[] { new TableMarshallerXML<Object>(), new TableUnmarshallerXML<Object>(), true, true } );
    retlist.add( new Object[] { new TableMarshallerCSV<Object>(), new TableUnmarshallerCSV<Object>(), true, true } );
    retlist.add( new Object[] { new TableMarshallerXLS<Object>(), new TableUnmarshallerXLS<Object>(), true, false } );
    
    //
    return retlist;
  }
  
  /**
   * @param tableMarshaller
   * @param tableUnmarshaller
   */
  public TableSerializerImplTest( TableMarshaller<Object> tableMarshaller, TableUnmarshaller<Object> tableUnmarshaller,
                                  boolean supportsOutputStream, boolean supportsAppendable )
  {
    super();
    this.tableMarshaller = tableMarshaller;
    this.tableUnmarshaller = tableUnmarshaller;
    this.supportsAppendable = supportsAppendable;
    this.supportsOutputStream = supportsOutputStream;
  }
  
  /* ********************************************** Variables ********************************************** */
  protected Table<Object>             tableBefore                     = new ArrayTable<Object>();
  protected Table<Object>             tableAfter                      = new ArrayTable<Object>();
  
  protected TableSerializer<Object>   tableSerializerForMarshalling   = new TableSerializerImpl<Object>( this.tableBefore );
  protected TableSerializer<Object>   tableSerializerForUnmarshalling = new TableSerializerImpl<Object>( this.tableAfter );
  
  protected TableMarshaller<Object>   tableMarshaller                 = null;
  protected TableUnmarshaller<Object> tableUnmarshaller               = null;
  
  protected boolean                   supportsOutputStream            = true;
  protected boolean                   supportsAppendable              = true;
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void prepareTable()
  {
    //
    int rows = 3;
    int columns = 2;
    TableFiller.fillTableWithMatrixNumbers( rows, columns, "Table1", this.tableBefore );
  }
  
  @Test
  public void testTableSerializerOutputStream()
  {
    //
    if ( this.supportsOutputStream )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      
      //
      this.tableSerializerForMarshalling.marshal( this.tableMarshaller ).writeTo( byteArrayContainer.getOutputStream() );
      this.tableSerializerForUnmarshalling.unmarshal( this.tableUnmarshaller ).from( byteArrayContainer.getInputStream() );
      
      //
      assertEquals( this.tableBefore, this.tableAfter );
    }
  }
  
  @Test
  public void testTableSerializerAppendable()
  {
    //
    if ( this.supportsAppendable )
    {
      //
      String tableContent = this.tableSerializerForMarshalling.marshal( this.tableMarshaller ).toString();
      this.tableSerializerForUnmarshalling.unmarshal( this.tableUnmarshaller ).from( tableContent );
      
      //
      assertEquals( this.tableBefore, this.tableAfter );
    }
  }
  
}
