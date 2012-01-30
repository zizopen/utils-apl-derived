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
package org.omnaest.utils.structure.collection.decorator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.omnaest.utils.structure.collection.decorator.CollectionDecorator;
import org.omnaest.utils.xml.JAXBXMLHelper;

/**
 * @see CollectionDecorator
 * @author Omnaest
 */
public class CollectionDecoratorTest
{
  
  @Test
  public void testCollectionDecoratorJAXBCompatibility()
  {
    //
    Collection<String> stringList = Arrays.asList( "a", "b" );
    
    //
    Collection<String> decoratedList = new CollectionDecorator<String>( stringList );
    assertEquals( stringList, new ArrayList<String>( decoratedList ) );
    
    //
    //System.out.println( XMLHelper.storeObjectAsXML( decoratedList ) );
    
    //
    @SuppressWarnings("unchecked")
    Collection<String> fromXML = JAXBXMLHelper.loadObjectFromXML( JAXBXMLHelper.storeObjectAsXML( decoratedList ),
                                                              CollectionDecorator.class );
    assertEquals( stringList, new ArrayList<String>( fromXML ) );
  }
  
}
