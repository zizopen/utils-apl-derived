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
 * Multifactory for all the internally used marshaller and unmarshaller instances. Modify this to enable to use other StAX
 * implementations like Staxon or Jettison
 * 
 * @author Omnaest
 */
public interface XMLInstanceContextFactory
{
  
  /**
   * @return new {@link XMLInputFactory}
   */
  public XMLInputFactory newXmlInputFactory();
  
  /**
   * @return new {@link XMLOutputFactory} instance
   */
  public XMLOutputFactory newXmlOutputFactory();
  
  /**
   * @return new {@link XMLEventFactory}
   */
  public XMLEventFactory newXmlEventFactory();
}
