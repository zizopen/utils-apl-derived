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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerEPrintStackTrace;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.xml.JAXBXMLHelper.MarshallingConfiguration;
import org.omnaest.utils.xml.JAXBXMLHelper.UnmarshallingConfiguration;

/**
 * @see JAXBXMLHelper
 * @author Omnaest
 */
public class JAXBXMLHelperTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  @XmlRootElement
  public static class Mock
  {
    public String  fieldString  = "Hello world";
    public boolean fieldBoolean = true;
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testStoreAndLoadObjectAsXML()
  {
    try
    {
      //
      Mock mock = new Mock();
      
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      JAXBXMLHelper.storeObjectAsXML( mock, byteArrayContainer.getOutputStream() );
      
      //
      //System.out.println( byteArrayContainer );
      
      //
      Mock mockResult = JAXBXMLHelper.loadObjectFromXML( byteArrayContainer.getInputStream(), Mock.class );
      
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
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.NONE)
  public static class TestDomain
  {
    @XmlElement
    private Object object;
    
    public TestDomain( SubDomain object )
    {
      super();
      this.object = object;
    }
    
    public TestDomain()
    {
      super();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.object == null ) ? 0 : this.object.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestDomain ) )
      {
        return false;
      }
      TestDomain other = (TestDomain) obj;
      if ( this.object == null )
      {
        if ( other.object != null )
        {
          return false;
        }
      }
      else if ( !this.object.equals( other.object ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  public static class SubDomain
  {
    private String field = "test";
    
    public SubDomain()
    {
      super();
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.field == null ) ? 0 : this.field.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof SubDomain ) )
      {
        return false;
      }
      SubDomain other = (SubDomain) obj;
      if ( this.field == null )
      {
        if ( other.field != null )
        {
          return false;
        }
      }
      else if ( !this.field.equals( other.field ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  @Test
  public void testComplexType()
  {
    final SubDomain subDomain = new SubDomain();
    TestDomain testDomain = new TestDomain( subDomain );
    
    String objectAsXML = JAXBXMLHelper.storeObjectAsXML( testDomain,
                                                         new MarshallingConfiguration().setKnownTypes( SubDomain.class )
                                                                                       .setExceptionHandler( new ExceptionHandlerEPrintStackTrace() ) );
    
    //System.out.println( objectAsXML );
    final TestDomain clone = JAXBXMLHelper.loadObjectFromXML( objectAsXML,
                                                              TestDomain.class,
                                                              new UnmarshallingConfiguration().setKnownTypes( SubDomain.class )
                                                                                              .setExceptionHandler( new ExceptionHandlerEPrintStackTrace() ) );
    assertEquals( testDomain, clone );
  }
  
}
