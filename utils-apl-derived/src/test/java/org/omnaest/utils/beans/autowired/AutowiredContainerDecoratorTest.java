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
package org.omnaest.utils.beans.autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see AutowiredContainerDecorator
 * @author Omnaest
 */
public class AutowiredContainerDecoratorTest
{
  /* ********************************************** Variables ********************************************** */
  protected AutowiredContainer<Object> autowiredContainer          = ClassMapToAutowiredContainerAdapter.newInstanceUsingLinkedHashMap();
  protected AutowiredContainer<Object> autowiredContainerDecorator = new AutowiredContainerDecorator<Object>(
                                                                                                              this.autowiredContainer );
  
  /* ********************************************** Methods ********************************************** */
  
  @Before
  public void setUp()
  {
    this.autowiredContainer.put( "text" );
    this.autowiredContainer.put( 1.45f );
  }
  
  @Test
  public void testJAXBCompliance()
  {
    //
    String objectAsXML = JAXBXMLHelper.storeObjectAsXML( this.autowiredContainerDecorator );
    assertNotNull( objectAsXML );
    
    //System.out.println( objectAsXML );
    @SuppressWarnings("unchecked")
    AutowiredContainerDecorator<Object> objectFromXML = JAXBXMLHelper.loadObjectFromXML( objectAsXML,
                                                                                         AutowiredContainerDecorator.class );
    assertNotNull( objectAsXML );
    assertEquals( this.autowiredContainerDecorator, objectFromXML );
    assertEquals( objectFromXML, this.autowiredContainerDecorator );
  }
  
}
