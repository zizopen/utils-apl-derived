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
package org.omnaest.utils.structure.collection.list;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see ListDecorator
 * @author Omnaest
 */
public class ListDecoratorTest
{
  
  @Test
  public void jaxbCompatibility()
  {
    //
    List<String> stringList = Arrays.asList( "a", "b" );
    
    //
    ListDecorator<String> decoratedList = new ListDecorator<String>( stringList );
    assertEquals( stringList, decoratedList );
    
    //
    //System.out.println( XMLHelper.storeObjectAsXML( decoratedList ) );
    
    //
    @SuppressWarnings("unchecked")
    List<String> fromXML = JAXBXMLHelper.loadObjectFromXML( JAXBXMLHelper.storeObjectAsXML( decoratedList ), ListDecorator.class );
    assertEquals( stringList, fromXML );
  }
}
