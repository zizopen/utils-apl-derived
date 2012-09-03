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
package org.omnaest.utils.store;

import java.io.File;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;
import org.omnaest.utils.xml.JAXBXMLHelper;
import org.omnaest.utils.xml.JAXBXMLHelper.JAXBContextBasedUnmarshaller;
import org.omnaest.utils.xml.JAXBXMLHelper.MarshallingConfiguration;
import org.omnaest.utils.xml.JAXBXMLHelper.UnmarshallingConfiguration;

/**
 * A simple {@link Object} store based on a nested directory structure using {@link JAXB}
 * 
 * @author Omnaest
 * @param <E>
 */
public class DirectoryBasedObjectStoreUsingJAXB<E> extends DirectoryBasedObjectStoreAbstract<E>
{
  private static final long serialVersionUID = -3189372961705877115L;
  
  /**
   * @see DirectoryBasedObjectStoreUsingJAXB
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   * @param elementType
   *          {@link Class}
   */
  public DirectoryBasedObjectStoreUsingJAXB( File baseDirectory, final ExceptionHandlerSerializable exceptionHandler,
                                             final Class<E> elementType )
  {
    super( new ElementBidirectionalConverterSerializable<ByteArrayContainer, E>()
    {
      private static final long serialVersionUID = -8113632185474907757L;
      
      @Override
      public E convert( ByteArrayContainer byteArrayContainer )
      {
        final UnmarshallingConfiguration unmarshallingConfiguration = newUnmarshallingConfiguration( exceptionHandler,
                                                                                                     elementType );
        final JAXBContextBasedUnmarshaller<E> jaxbContextBasedUnmarshaller = JAXBXMLHelper.newJAXBContextBasedUnmarshaller( elementType,
                                                                                                                            unmarshallingConfiguration );
        
        JAXBElement<E> element = jaxbContextBasedUnmarshaller.unmarshalAsJAXBElement( byteArrayContainer.getInputStream() );
        return element != null ? element.getValue() : null;
      }
      
      private UnmarshallingConfiguration newUnmarshallingConfiguration( final ExceptionHandlerSerializable exceptionHandler,
                                                                        final Class<E> elementType )
      {
        final UnmarshallingConfiguration retval = new UnmarshallingConfiguration();
        retval.setExceptionHandler( exceptionHandler );
        if ( ArrayUtils.isArrayType( elementType ) )
        {
          retval.setKnownTypes( elementType, ArrayUtils.componentType( elementType ) );
        }
        else
        {
          retval.setKnownTypes( elementType );
        }
        return retval;
      }
      
      private MarshallingConfiguration newMarshallingConfiguration( final ExceptionHandlerSerializable exceptionHandler,
                                                                    final Class<E> elementType )
      {
        final MarshallingConfiguration retval = new MarshallingConfiguration();
        retval.setExceptionHandler( exceptionHandler );
        if ( ArrayUtils.isArrayType( elementType ) )
        {
          retval.setKnownTypes( elementType, ArrayUtils.componentType( elementType ) );
        }
        else
        {
          retval.setKnownTypes( elementType );
        }
        return retval;
      }
      
      @Override
      public ByteArrayContainer convertBackwards( E element )
      {
        final QName name = new QName( "element" );
        final Class<E> declaredType = elementType;
        final E value = element;
        MarshallingConfiguration marshallingConfiguration = this.newMarshallingConfiguration( exceptionHandler, elementType );
        return new ByteArrayContainer().copyFrom( JAXBXMLHelper.storeObjectAsXML( new JAXBElement<E>( name, declaredType, value ),
                                                                                  marshallingConfiguration ) );
      }
    }, baseDirectory, exceptionHandler );
  }
}
