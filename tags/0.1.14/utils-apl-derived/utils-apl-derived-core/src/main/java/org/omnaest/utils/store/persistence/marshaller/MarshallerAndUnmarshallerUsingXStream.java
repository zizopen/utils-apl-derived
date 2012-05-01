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
package org.omnaest.utils.store.persistence.marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.thoughtworks.xstream.XStream;

/**
 * Simple {@link MarshallerAndUnmarshaller} using the {@link XStream} which serializes to XML output.
 * 
 * @author Omnaest
 */
public class MarshallerAndUnmarshallerUsingXStream implements MarshallerAndUnmarshaller
{
  /* ********************************************** Beans / Services / References ********************************************** */
  protected final XStream xStream = new XStream();
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public byte[] marshal( Object object )
  {
    //      
    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
    //
    try
    {
      //
      this.xStream.toXML( object, byteArrayOutputStream );
    }
    catch ( Exception e )
    {
      throw new MarshallingException();
    }
    
    //
    return byteArrayOutputStream.toByteArray();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <E> E unmarshal( byte[] buffer )
  {
    //
    E retval = null;
    
    //
    try
    {
      //
      retval = (E) this.xStream.fromXML( new ByteArrayInputStream( buffer ) );
    }
    catch ( Exception e )
    {
      throw new UnMarshallingException();
    }
    
    //
    return retval;
  }
  
}
