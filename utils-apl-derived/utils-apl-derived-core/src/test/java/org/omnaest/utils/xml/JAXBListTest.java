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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper.MarshallingConfiguration;

/**
 * @see JAXBList
 * @author Omnaest
 */
public class JAXBListTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  @XmlType
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class TestEntity
  {
    /* ********************************************** Variables ********************************************** */
    @XmlElement
    private String fieldString  = null;
    
    @XmlElement
    private int    fieldInteger = -1;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @return the fieldString
     */
    public String getFieldString()
    {
      return this.fieldString;
    }
    
    /**
     * @param fieldString
     *          the fieldString to set
     */
    public void setFieldString( String fieldString )
    {
      this.fieldString = fieldString;
    }
    
    /**
     * @return the fieldInteger
     */
    public int getFieldInteger()
    {
      return this.fieldInteger;
    }
    
    /**
     * @param fieldInteger
     *          the fieldInteger to set
     */
    public void setFieldInteger( int fieldInteger )
    {
      this.fieldInteger = fieldInteger;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.fieldInteger;
      result = prime * result + ( ( this.fieldString == null ) ? 0 : this.fieldString.hashCode() );
      return result;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
      if ( !( obj instanceof TestEntity ) )
      {
        return false;
      }
      TestEntity other = (TestEntity) obj;
      if ( this.fieldInteger != other.fieldInteger )
      {
        return false;
      }
      if ( this.fieldString == null )
      {
        if ( other.fieldString != null )
        {
          return false;
        }
      }
      else if ( !this.fieldString.equals( other.fieldString ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNewInstance()
  {
    //
    List<String> list = new ArrayList<String>();
    list.add( "value1" );
    list.add( "value2" );
    list.add( "value3" );
    
    //
    JAXBList<String> jaxbList = JAXBList.newInstance( list );
    
    //
    String xmlContent = JAXBXMLHelper.storeObjectAsXML( jaxbList );
    assertNotNull( xmlContent );
    
    //
    assertEquals( list, new ArrayList<String>( JAXBXMLHelper.cloneObject( jaxbList ) ) );
  }
  
  @Test
  public void testNewInstanceWithArbitraryObjects()
  {
    //
    List<Object> list = new ArrayList<Object>();
    list.add( String.valueOf( "test" ) );
    list.add( Character.valueOf( 'c' ) );
    list.add( Byte.valueOf( "10" ) );
    list.add( Short.valueOf( "1000" ) );
    list.add( Integer.valueOf( "100000" ) );
    list.add( Long.valueOf( "1000000000" ) );
    list.add( Float.valueOf( "100000.111" ) );
    list.add( Double.valueOf( "100000.111" ) );
    list.add( Boolean.valueOf( "true" ) );
    list.add( newTestEntity( 0 ) );
    list.add( newTestEntity( 1 ) );
    list.add( newTestEntity( 3 ) );
    
    //
    JAXBList<Object> jaxbList = JAXBList.newInstance( list );
    
    //
    final MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
    marshallingConfiguration.setKnownTypes( TestEntity.class );
    
    //
    String xmlContent = JAXBXMLHelper.storeObjectAsXML( jaxbList, marshallingConfiguration );
    
    // System.out.println( xmlContent );
    assertNotNull( xmlContent );
    
    //
    assertEquals( list, new ArrayList<Object>( JAXBXMLHelper.cloneObject( jaxbList, marshallingConfiguration ) ) );
  }
  
  private static TestEntity newTestEntity( int id )
  {
    //
    final TestEntity retval = new TestEntity();
    
    //
    retval.setFieldString( "fieldString" + id );
    retval.setFieldInteger( id );
    
    //
    return retval;
  }
}
