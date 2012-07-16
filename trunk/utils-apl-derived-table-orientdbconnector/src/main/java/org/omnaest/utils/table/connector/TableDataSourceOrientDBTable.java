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
package org.omnaest.utils.table.connector;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.table2.TableDataSource;

import com.google.common.base.Joiner;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * {@link TableDataSource} based on the class name of a {@link ODatabaseDocumentTx}
 * 
 * @author Omnaest
 * @param <E>
 */
public class TableDataSourceOrientDBTable<E> implements TableDataSource<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long         serialVersionUID = -8561039040807958186L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  
  private final String              tableName;
  private final String[]            columnTitles;
  private final String              whereClause;
  private final ODatabaseDocumentTx db;
  private final Class<E>            type;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see TableDataSourceOrientDBTable
   * @param elmentType
   * @param db
   * @param className
   * @param columnTitles
   * @param whereClause
   */
  public TableDataSourceOrientDBTable( Class<E> elmentType, ODatabaseDocumentTx db, String className, String[] columnTitles,
                                       String whereClause )
  {
    super();
    this.type = elmentType;
    this.db = db;
    this.whereClause = whereClause;
    
    Assert.isNotNull( db, "db must not be null" );
    Assert.isNotNull( className, "className must not be null" );
    
    this.tableName = className;
    this.columnTitles = columnTitles;
    
  }
  
  @Override
  public String getTableName()
  {
    return this.tableName;
  }
  
  @Override
  public String[] getColumnTitles()
  {
    return this.columnTitles;
  }
  
  @Override
  public Iterable<E[]> rowElements()
  {
    final Class<E> type = this.type;
    final String[] columnTitles = this.columnTitles;
    final List<ODocument> query = this.db.query( new OSQLSynchQuery<ODocument>(
                                                                                "select "
                                                                                    + Joiner.on( "," ).join( columnTitles )
                                                                                    + " from "
                                                                                    + this.tableName
                                                                                    + ( StringUtils.isNotBlank( this.whereClause ) ? " where "
                                                                                                                                     + this.whereClause
                                                                                                                                  : "" ) ) );
    
    final ElementConverter<ODocument, E[]> elementConverter = new ElementConverterSerializable<ODocument, E[]>()
    {
      private static final long serialVersionUID = -8569252765490137457L;
      
      @SuppressWarnings("unchecked")
      @Override
      public E[] convert( ODocument oDocument )
      {
        E[] retvals = (E[]) Array.newInstance( type, columnTitles.length );
        if ( oDocument != null )
        {
          int ii = 0;
          for ( String columnTitle : columnTitles )
          {
            retvals[ii++] = oDocument.field( columnTitle );
          }
        }
        return retvals;
      }
    };
    return IterableUtils.adapter( query, elementConverter );
  }
  
}
