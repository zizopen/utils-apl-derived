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
package org.omnaest.utils.structure.table.serializer.unmarshaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.table.Table;
import org.omnaest.utils.structure.table.concrete.ArrayTable;

/**
 * @see TableUnmarshallerPlainText
 * @author Omnaest
 */
@RunWith(value = Parameterized.class)
public class TableUnmarshallerPlainTextTest
{
  @Parameters
  public static Collection<Object[]> configurationDataCollection()
  {
    //
    List<Object[]> retlist = new ArrayList<Object[]>();
    retlist.add( new Object[] { new TableConfiguration( true, true, true ) } );
    retlist.add( new Object[] { new TableConfiguration( true, true, false ) } );
    retlist.add( new Object[] { new TableConfiguration( true, false, true ) } );
    retlist.add( new Object[] { new TableConfiguration( true, false, false ) } );
    retlist.add( new Object[] { new TableConfiguration( false, true, true ) } );
    retlist.add( new Object[] { new TableConfiguration( false, true, false ) } );
    retlist.add( new Object[] { new TableConfiguration( false, false, true ) } );
    retlist.add( new Object[] { new TableConfiguration( false, false, false ) } );
    
    //
    return retlist;
  }
  
  public TableUnmarshallerPlainTextTest( TableConfiguration tableConfiguration )
  {
    super();
    this.tableConfiguration = tableConfiguration;
  }
  
  /* ********************************************** Variables ********************************************** */
  protected TableUnmarshallerPlainText<String> tableUnmarshallerPlainText = new TableUnmarshallerPlainText<String>();
  protected Table<String>                      table                      = new ArrayTable<String>();
  protected TableConfiguration                 tableConfiguration         = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see TableUnmarshallerPlainTextTest
   * @author Omnaest
   */
  protected static class TableConfiguration
  {
    /* ********************************************** Variables ********************************************** */
    protected boolean hasColumnTitles;
    protected boolean hasRowTitles;
    protected boolean hasTableName;
    
    /* ********************************************** Methods ********************************************** */
    public TableConfiguration( boolean hasColumnTitles, boolean hasRowTitles, boolean hasTableName )
    {
      super();
      this.hasColumnTitles = hasColumnTitles;
      this.hasRowTitles = hasRowTitles;
      this.hasTableName = hasTableName;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testUnmarshalTableOfEInputStream()
  {
    //
    ByteArrayContainer byteArrayContainer = new ByteArrayContainer(
                                                                    TableUnmarshallerPlainTextTest.createTableContent( this.tableConfiguration ) );
    
    //
    this.tableUnmarshallerPlainText.unmarshal( this.table, byteArrayContainer.getInputStream() );
    
    //
    TableUnmarshallerPlainTextTest.assertTable( this.table, this.tableConfiguration );
  }
  
  @Test
  public void testUnmarshalTableOfECharSequence()
  {
    //
    this.tableUnmarshallerPlainText.unmarshal( this.table,
                                               TableUnmarshallerPlainTextTest.createTableContent( this.tableConfiguration ) );
    
    //
    //System.out.println( this.table );
    
    //
    
    TableUnmarshallerPlainTextTest.assertTable( this.table, this.tableConfiguration );
  }
  
  private static String createTableContent( TableConfiguration tableConfiguration )
  {
    //
    boolean hasColumnTitles = tableConfiguration.hasColumnTitles;
    boolean hasRowTitles = tableConfiguration.hasRowTitles;
    boolean hasTableName = tableConfiguration.hasTableName;
    
    //    
    StringBuilder retval = new StringBuilder();
    
    //
    if ( hasTableName )
    {
      retval.append( "===Table1===\n" );
    }
    else
    {
      retval.append( "------------\n" );
    }
    
    //
    if ( hasColumnTitles )
    {
      if ( hasRowTitles )
      {
        retval.append( "!  !c0 !c1 !\n" );
      }
      else
      {
        retval.append( "!c0 !c1 !\n" );
      }
    }
    
    //
    if ( hasRowTitles )
    {
      retval.append( "!r0!0:0|0:1|\n" );
      retval.append( "!r1!1:0|1:1|\n" );
      retval.append( "!r2!2:0|2:1|\n" );
    }
    else
    {
      retval.append( "|0:0|0:1|\n" );
      retval.append( "|1:0|1:1|\n" );
      retval.append( "|2:0|2:1|\n" );
    }
    
    //
    retval.append( "------------\n" );
    
    //
    return retval.toString();
  }
  
  /**
   * @param table
   * @param configuration.isHasTableName()
   * @param configuration.isHasRowTitles()
   * @param configuration.isHasColumnTitles()
   */
  private static void assertTable( Table<String> table, TableConfiguration tableConfiguration )
  {
    //
    boolean hasColumnTitles = tableConfiguration.hasColumnTitles;
    boolean hasRowTitles = tableConfiguration.hasRowTitles;
    boolean hasTableName = tableConfiguration.hasTableName;
    
    //
    Assert.assertNotNull( table );
    
    //
    if ( hasTableName )
    {
      Assert.assertEquals( "Table1", table.getTableName() );
    }
    else
    {
      Assert.assertEquals( null, table.getTableName() );
    }
    
    //
    Assert.assertEquals( 3, table.getTableSize().getRowSize() );
    Assert.assertEquals( 2, table.getTableSize().getColumnSize() );
    
    //
    if ( hasRowTitles )
    {
      Assert.assertEquals( Arrays.asList( "r0", "r1", "r2" ), table.getRowTitleValueList() );
    }
    else
    {
      Assert.assertEquals( Arrays.asList( null, null, null ), table.getRowTitleValueList() );
    }
    
    //
    if ( hasColumnTitles )
    {
      Assert.assertEquals( Arrays.asList( "c0", "c1" ), table.getColumnTitleValueList() );
    }
    else
    {
      Assert.assertEquals( Arrays.asList( null, null ), table.getColumnTitleValueList() );
      
    }
    
    Assert.assertEquals( Arrays.asList( "0:0", "0:1" ), table.getRow( 0 ).getCellElementList() );
    Assert.assertEquals( Arrays.asList( "1:0", "1:1" ), table.getRow( 1 ).getCellElementList() );
    Assert.assertEquals( Arrays.asList( "2:0", "2:1" ), table.getRow( 2 ).getCellElementList() );
  }
  
}
