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
import java.util.Iterator;
import java.util.List;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterSerializable;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.iterator.IteratorUtils;
import org.omnaest.utils.table.TablePersistence;
import org.omnaest.utils.tuple.KeyValue;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

/**
 * @see TablePersistence
 * @author Omnaest
 * @param <E>
 */
public class TablePersistenceOrientDB<E> implements TablePersistence<E>
{
  private static final long         serialVersionUID = -7728718249099360075L;
  
  private static final String       fieldId          = "id";
  
  private final ODatabaseDocumentTx db;
  private final Class<E>            elementType;
  private String                    className;
  private String[]                  columnTitles;
  
  /**
   * @see TablePersistenceOrientDB
   * @param oDatabaseDocumentTx
   * @param elementType
   */
  public TablePersistenceOrientDB( ODatabaseDocumentTx oDatabaseDocumentTx, Class<E> elementType )
  {
    super();
    this.db = oDatabaseDocumentTx;
    this.elementType = elementType;
  }
  
  @Override
  public void update( int id, E[] elements )
  {
    final ODocument firstElement = findById( id );
    if ( firstElement != null && this.columnTitles != null )
    {
      for ( int ii = 0; ii < this.columnTitles.length; ii++ )
      {
        final String columnTitle = this.columnTitles[ii];
        firstElement.field( columnTitle, elements[ii] );
      }
      firstElement.save();
    }
  }
  
  private ODocument findById( int id )
  {
    List<ODocument> result = this.db.query( new OSQLSynchQuery<ODocument>( "select * from " + this.className + " where "
                                                                           + fieldId + " = " + id ) );
    ODocument firstElement = ListUtils.firstElement( result );
    return firstElement;
  }
  
  @Override
  public void add( int id, E[] elements )
  {
    ODocument oDocument = new ODocument( this.className );
    oDocument.field( fieldId, id, OType.INTEGER );
    if ( this.columnTitles != null )
    {
      for ( int ii = 0; ii < this.columnTitles.length; ii++ )
      {
        final String columnTitle = this.columnTitles[ii];
        oDocument.field( columnTitle, elements[ii] );
      }
    }
    oDocument.save();
  }
  
  @Override
  public void remove( int id )
  {
    final ODocument document = findById( id );
    if ( document != null )
    {
      document.delete();
    }
  }
  
  @Override
  public void removeAll()
  {
    for ( ODocument document : this.db.browseClass( this.className ) )
    {
      document.delete();
    }
  }
  
  @Override
  public Iterable<KeyValue<Integer, E[]>> allElements()
  {
    final Class<E> elementType = this.elementType;
    final String[] columnTitles = this.columnTitles;
    if ( elementType == null || columnTitles == null || this.className == null )
    {
      return IterableUtils.empty();
    }
    final ORecordIteratorClass<ODocument> iteratorClass = this.db.browseClass( this.className );
    return new Iterable<KeyValue<Integer, E[]>>()
    {
      @Override
      public Iterator<KeyValue<Integer, E[]>> iterator()
      {
        Iterator<ODocument> iterator = iteratorClass.iterator();
        ElementConverterSerializable<ODocument, KeyValue<Integer, E[]>> elementConverter = new ElementConverterSerializable<ODocument, KeyValue<Integer, E[]>>()
        {
          private static final long serialVersionUID = -4568268163128760428L;
          
          @SuppressWarnings("unchecked")
          @Override
          public KeyValue<Integer, E[]> convert( ODocument document )
          {
            final int id = document.field( fieldId );
            final E[] elements = (E[]) Array.newInstance( elementType, columnTitles.length );
            for ( int ii = 0; ii < columnTitles.length; ii++ )
            {
              final String columnTitle = columnTitles[ii];
              elements[ii] = (E) document.field( columnTitle );
            }
            return new KeyValue<Integer, E[]>( id, elements );
          }
        };
        return IteratorUtils.adapter( iterator, elementConverter );
      }
    };
  }
  
  @Override
  public void setTableName( String tableName )
  {
    this.className = tableName;
  }
  
  @Override
  public void setColumnTitles( String[] columnTitles )
  {
    this.columnTitles = columnTitles;
  }
  
}
