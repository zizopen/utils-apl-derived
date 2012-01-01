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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;

public class JAXBXMLHelperTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @XmlRootElement
  public static class Mock
  {
    public String  fieldString  = "Hello world";
    public boolean fieldBoolean = true;
  }
  
  @Test
  public void testStoreAndLoadObjectAsXML()
  {
    try
    {
      //
      Mock mock = new Mock();
      
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      JAXBXMLHelper.storeObjectAsXML( mock, bos );
      bos.close();
      
      //
      byte[] buffer = bos.toByteArray();
      ByteArrayInputStream bis = new ByteArrayInputStream( buffer );
      Mock mockResult = JAXBXMLHelper.loadObjectFromXML( bis, Mock.class );
      
      //      
      assertEquals( mock.fieldString, mockResult.fieldString );
      assertEquals( mock.fieldBoolean, mockResult.fieldBoolean );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail();
    }
    
  }
  
}