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
package org.omnaest.utils.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.Name;
import org.omnaest.utils.xml.JAXBXMLHelper.MarshallingConfiguration;

public class JAXBNameTest
{
  
  @XmlRootElement
  protected static enum TestEnum implements Name
  {
    ONE,
    TWO;
  }
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class EnumerationWrapper
  {
    
    @XmlElement(type = JAXBName.class)
    private Name enumeration;
    
    public void setEnumeration( Name enumeration )
    {
      this.enumeration = enumeration;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.enumeration == null ) ? 0 : this.enumeration.hashCode() );
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
      if ( !( obj instanceof EnumerationWrapper ) )
      {
        return false;
      }
      EnumerationWrapper other = (EnumerationWrapper) obj;
      if ( this.enumeration == null )
      {
        if ( other.enumeration != null )
        {
          return false;
        }
      }
      else if ( !this.enumeration.equals( other.enumeration ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  @Test
  public void test()
  {
    //
    EnumerationWrapper enumerationWrapper = new EnumerationWrapper();
    enumerationWrapper.setEnumeration( TestEnum.TWO );
    
    //    
    MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration().setExceptionHandler( new ExceptionHandler()
                                                                                                            {
                                                                                                              @Override
                                                                                                              public void handleException( Exception e )
                                                                                                              {
                                                                                                                e.printStackTrace();
                                                                                                              }
                                                                                                            } )
                                                                                      .setKnownTypes( TestEnum.class );
    String storeObjectAsXML = JAXBXMLHelper.storeObjectAsXML( enumerationWrapper, marshallingConfiguration );
    assertNotNull( storeObjectAsXML );
    assertFalse( StringUtils.isBlank( storeObjectAsXML ) );
    //System.out.println( storeObjectAsXML );
    
    //
    EnumerationWrapper enumerationWrapper2 = JAXBXMLHelper.loadObjectFromXML( storeObjectAsXML,
                                                                              EnumerationWrapper.class,
                                                                              marshallingConfiguration.asUnmarshallingConfiguration() );
    assertNotNull( enumerationWrapper2 );
    
    //
    EnumerationWrapper cloneObject = JAXBXMLHelper.cloneObject( enumerationWrapper, marshallingConfiguration );
    assertEquals( cloneObject, enumerationWrapper );
  }
  
}
