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
package org.omnaest.utils.table.impl.serializer;

import java.io.Reader;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table.TableSerializer.UnmarshallerJson;
import org.omnaest.utils.table.impl.serializer.XmlModel.MetaData;
import org.omnaest.utils.table.impl.serializer.XmlModel.Row;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * {@link UnmarshallerJson} implementation
 * 
 * @see JsonMarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class JsonUnmarshallerImpl<E> extends UnmarshallerAbstract<E> implements UnmarshallerJson<E>
{
  
  private MarshallingConfiguration configuration = new MarshallingConfiguration();
  
  JsonUnmarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Table<E> from( Reader reader )
  {
    try
    {
      this.table.clear();
      
      final ObjectMapper objectMapper = new ObjectMapper();
      {
        final AnnotationIntrospector annotationIntrospector = new JaxbAnnotationIntrospector();
        objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
        objectMapper.setAnnotationIntrospector( annotationIntrospector );
      }
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer( reader );
      final String encoding = this.getEncoding();
      XmlModel<E> xmlModel = objectMapper.readValue( byteArrayContainer.toString( encoding ), XmlModel.class );
      
      final Row<E>[] rows = xmlModel.getRows();
      int rowIndex = 0;
      if ( rows != null )
      {
        for ( Row<E> row : rows )
        {
          final E[] elements = row.getElements();
          this.table.setRowElements( rowIndex++, elements );
        }
      }
      
      final MetaData metaData = xmlModel.getMetaData();
      final boolean hasMetaData = metaData != null;
      if ( hasMetaData )
      {
        final boolean hasEnabledTableName = this.configuration.hasEnabledTableName();
        final boolean hasEnabledRowTitles = this.configuration.hasEnabledRowTitles();
        final boolean hasEnabledColumnTitles = this.configuration.hasEnabledColumnTitles();
        if ( hasEnabledTableName )
        {
          final String tableName = metaData.getTableName();
          this.table.setTableName( tableName );
        }
        if ( hasEnabledRowTitles )
        {
          final List<String> rowTitleList = metaData.getRowTitleList();
          this.table.setRowTitles( rowTitleList );
        }
        if ( hasEnabledColumnTitles )
        {
          final List<String> columnTitleList = metaData.getColumnTitleList();
          this.table.setColumnTitles( columnTitleList );
        }
      }
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return this.table;
  }
  
  @Override
  protected String getEncoding()
  {
    return this.configuration.getEncoding();
  }
  
  @Override
  public UnmarshallerJson<E> using( MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
}
