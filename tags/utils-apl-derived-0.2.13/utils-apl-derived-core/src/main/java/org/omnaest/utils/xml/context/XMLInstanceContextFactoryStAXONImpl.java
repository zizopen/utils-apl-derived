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

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.json.JsonXMLOutputFactory;

/**
 * {@link XMLInstanceContextFactory} which uses StAXON to generate any new instances
 * 
 * @see XMLInstanceContextFactory
 * @author Omnaest
 */
public final class XMLInstanceContextFactoryStAXONImpl implements XMLInstanceContextFactory
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private JsonXMLConfig jsonXMLConfig = new JsonXMLConfigBuilder().autoArray( true ).prettyPrint( true ).build();
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public XMLOutputFactory newXmlOutputFactory()
  {
    XMLOutputFactory retval = new JsonXMLOutputFactory( this.jsonXMLConfig );
    return retval;
  }
  
  @Override
  public XMLEventFactory newXmlEventFactory()
  {
    XMLEventFactory retval = XMLEventFactory.newInstance();
    return retval;
  }
  
  @Override
  public XMLInputFactory newXmlInputFactory()
  {
    final XMLInputFactory xmlInputFactory = new JsonXMLInputFactory( this.jsonXMLConfig );
    return xmlInputFactory;
  }
  
  /**
   * @param jsonXMLConfig
   * @return this
   */
  public XMLInstanceContextFactoryStAXONImpl setJsonXMLConfig( JsonXMLConfig jsonXMLConfig )
  {
    this.jsonXMLConfig = jsonXMLConfig;
    return this;
  }
}
