/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.cluster.store;

import java.io.InputStream;
import java.io.OutputStream;

import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.xml.JAXBXMLHelper;

public class MarshallingStrategyXMLJAXB implements MarshallingStrategy
{
  
  private static final long serialVersionUID = 61592880817699221L;
  
  @Override
  public byte[] marshal( Object object ) throws MarshallingException
  {
    try
    {
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      final OutputStream outputStream = byteArrayContainer.getOutputStream();
      JAXBXMLHelper.storeObjectAsXML( object, outputStream );
      outputStream.close();
      return byteArrayContainer.getContent();
    }
    catch ( Exception exception )
    {
      throw new MarshallingException( exception );
    }
  }
  
  @Override
  public <T> T unmarshal( byte[] data, Class<T> type ) throws UnmarshallingException
  {
    try
    {
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer( data );
      final InputStream inputStream = byteArrayContainer.getInputStream();
      T retval = JAXBXMLHelper.loadObjectFromXML( inputStream, type );
      inputStream.close();
      return retval;
    }
    catch ( Exception exception )
    {
      throw new UnmarshallingException( exception );
    }
  }
  
}
