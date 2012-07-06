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
package org.omnaest.utils.table2.impl.serializer;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table2.ImmutableTableSerializer.MarshallerJson;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.impl.serializer.XmlModel.MetaData;
import org.omnaest.utils.table2.impl.serializer.XmlModel.Row;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * {@link MarshallerJson} implementation
 * 
 * @see JsonUnmarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class JsonMarshallerImpl<E> extends MarshallerAbstract<E> implements MarshallerJson<E>
{
  
  private MarshallingConfiguration configuration = new MarshallingConfiguration();
  
  JsonMarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Table<E> to( Appendable appendable )
  {
    try
    {
      final XmlModel<E> xmlModel = new XmlModel<E>();
      final int rowSize = this.table.rowSize();
      Row<E>[] rows = new Row[rowSize];
      {
        for ( int index = 0; index < rowSize; index++ )
        {
          final E[] elements = this.table.row( index ).to().array();
          rows[index] = new Row<E>( index, elements );
        }
      }
      xmlModel.setRows( rows );
      {
        MetaData metaData = new MetaData();
        {
          if ( this.configuration.hasEnabledRowTitles() )
          {
            metaData.setRowTitleList( this.table.getRowTitleList() );
          }
          if ( this.configuration.hasEnabledColumnTitles() )
          {
            metaData.setColumnTitleList( this.table.getColumnTitleList() );
          }
          if ( this.configuration.hasEnabledTableName() )
          {
            metaData.setTableName( this.table.getTableName() );
          }
        }
        xmlModel.setMetaData( metaData );
      }
      
      final ObjectMapper objectMapper = new ObjectMapper();
      {
        final AnnotationIntrospector annotationIntrospector = new JaxbAnnotationIntrospector();
        objectMapper.configure( SerializationFeature.INDENT_OUTPUT, true );
        objectMapper.setAnnotationIntrospector( annotationIntrospector );
      }
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final String encoding = this.getEncoding();
      objectMapper.writeValue( byteArrayContainer.getOutputStreamWriter( encoding ), xmlModel );
      
      appendable.append( byteArrayContainer.toString( encoding ) );
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
  public MarshallerJson<E> using( Marshaller.MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
}
