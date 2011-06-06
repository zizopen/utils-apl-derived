/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
/*******************************************************************************
 * Copyright (c) 2011 Danny Kunz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Danny Kunz - initial API and implementation
 ******************************************************************************/


import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLHelper
{
  public static void storeObjectAsXML( Object object, OutputStream outputStream )
  {
    // 
    try
    {
      JAXBContext context = JAXBContext.newInstance( object.getClass() );
      Marshaller m = context.createMarshaller();
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
      m.marshal( object, outputStream );
      outputStream.flush();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }
  
  @SuppressWarnings("unchecked")
  public static <E> E loadObjectFromXML( InputStream inputStream, Class<E> typeClazz )
  {
    //
    E retval = null;
    
    //
    try
    {
      JAXBContext context = JAXBContext.newInstance( typeClazz );
      Unmarshaller um = context.createUnmarshaller();
      retval = (E) um.unmarshal( inputStream );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    //
    return retval;
  }
}
