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

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table.Table;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table.ImmutableTableSerializer.MarshallerXml;
import org.omnaest.utils.table.impl.serializer.XmlModel.MetaData;
import org.omnaest.utils.table.impl.serializer.XmlModel.Row;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * {@link MarshallerXml} implementation
 * 
 * @see XmlUnmarshallerImpl
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class XmlMarshallerImpl<E> extends MarshallerAbstract<E> implements MarshallerXml<E>
{
  
  private MarshallingConfiguration configuration = new MarshallingConfiguration();
  
  XmlMarshallerImpl( Table<E> table, ExceptionHandler exceptionHandler )
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
          rows[index] = new Row<E>( elements );
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
      
      final Class<E> elementType = this.table.elementType();
      final JAXBXMLHelper.MarshallingConfiguration configuration = new JAXBXMLHelper.MarshallingConfiguration().setExceptionHandler( this.exceptionHandler )
                                                                                                               .setKnownTypes( ArrayUtils.arrayType( elementType ) );
      String objectAsXML = JAXBXMLHelper.storeObjectAsXML( xmlModel, configuration );
      
      appendable.append( objectAsXML );
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
  public MarshallerXml<E> using( Marshaller.MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
}
