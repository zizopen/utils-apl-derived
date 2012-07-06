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

import java.io.Reader;
import java.util.List;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer.UnmarshallerXml;
import org.omnaest.utils.table2.impl.serializer.XmlModel.MetaData;
import org.omnaest.utils.table2.impl.serializer.XmlModel.Row;
import org.omnaest.utils.xml.JAXBXMLHelper;
import org.omnaest.utils.xml.JAXBXMLHelper.UnmarshallingConfiguration;

/**
 * {@link UnmarshallerXml} implementation
 * 
 * @see XmlMarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class XmlUnmarshallerImpl<E> extends UnmarshallerAbstract<E> implements UnmarshallerXml<E>
{
  
  private MarshallingConfiguration configuration = new MarshallingConfiguration();
  
  XmlUnmarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
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
      
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer( reader );
      final Class<E> elementType = this.table.elementType();
      final Class<E[]> arrayType = ArrayUtils.arrayType( elementType );
      final UnmarshallingConfiguration unmarshallingConfiguration = new UnmarshallingConfiguration().setExceptionHandler( this.exceptionHandler )
                                                                                                    .setKnownTypes( arrayType,
                                                                                                                    elementType );
      XmlModel<E> xmlModel = JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.toString( this.getEncoding() ), XmlModel.class,
                                                              unmarshallingConfiguration );
      
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
  public UnmarshallerXml<E> using( MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
}
