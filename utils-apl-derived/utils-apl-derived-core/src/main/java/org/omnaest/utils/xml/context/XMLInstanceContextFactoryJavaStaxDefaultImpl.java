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
package org.omnaest.utils.xml.context;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

/**
 * Default {@link XMLInstanceContextFactory} implementation using the Java default StAX provider
 * 
 * @author Omnaest
 */
public class XMLInstanceContextFactoryJavaStaxDefaultImpl implements XMLInstanceContextFactory
{
  
  @Override
  public XMLOutputFactory newXmlOutputFactory()
  {
    return XMLOutputFactory.newInstance();
  }
  
  @Override
  public XMLEventFactory newXmlEventFactory()
  {
    return XMLEventFactory.newInstance();
  }
  
  @Override
  public XMLInputFactory newXmlInputFactory()
  {
    XMLInputFactory retval = XMLInputFactory.newInstance();
    return retval;
  }
}
