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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.omnaest.utils.xml.XMLHelper;

/**
 * @see ElementHolderUnmodifiable
 * @author Omnaest
 */
public class ElementHolderUnmodifiableTest
{
  
  @Test
  public void testElementHolderJAXBCompliance()
  {
    //
    {
      //
      String value = "value";
      ElementHolderUnmodifiable<String> elementHolder = new ElementHolder<String>( value );
      
      //
      String objectAsXML = XMLHelper.storeObjectAsXML( elementHolder );
      assertNotNull( objectAsXML );
      //System.out.println( objectAsXML );
      
      //
      assertEquals( "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n<elementHolder>\n    <element xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">value</element>\n</elementHolder>\n",
                    objectAsXML );
    }
    
  }
  
}
