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
package org.omnaest.utils.xml;

import javax.xml.stream.XMLEventReader;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.xml.XMLIteratorFactory.JAXBTypeContentConverter;
import org.omnaest.utils.xml.XMLIteratorFactory.JAXBTypeContentConverterFactory;

import de.odysseus.staxon.json.JsonXMLInputFactory;

/**
 * {@link JAXBTypeContentConverter} implementation using StAXON
 * 
 * @author Omnaest
 */
public class JAXBTypeContentConverterFactoryStAXONImpl implements JAXBTypeContentConverterFactory
{
  @Override
  public <E> ElementConverter<String, E> newElementConverter( final Class<? extends E> type,
                                                              final ExceptionHandler exceptionHandler )
  {
    return new XMLIteratorFactory.JAXBTypeContentConverter<E>( type, exceptionHandler )
    {
      @Override
      public E convert( String element )
      {
        //
        E retval = null;
        
        //
        try
        {
          //
          final XMLEventReader xmlEventReader = new JsonXMLInputFactory().createXMLEventReader( new ByteArrayContainer( element ).getInputStream() );
          retval = this.cachedElement.getValue().unmarshal( xmlEventReader );
        }
        catch ( Exception e )
        {
          if ( exceptionHandler != null )
          {
            exceptionHandler.handleException( e );
          }
        }
        
        //
        return retval;
      }
      
    };
  }
}
