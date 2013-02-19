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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.omnaest.utils.structure.container.Name;

/**
 * Special delegate implementation for a {@link Name} which can be translated via jaxb
 * 
 * @see Name
 * @author Omnaest
 */
@XmlRootElement(name = "enumeration")
@XmlAccessorType(XmlAccessType.NONE)
public class JAXBName implements Name
{
  /* ********************************************** Variables ********************************************** */
  @XmlValue
  private String name = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see JAXBName
   * @param name
   */
  public JAXBName( String name )
  {
    super();
    this.name = name;
  }
  
  /**
   * @see JAXBName
   * @param name
   *          {@link Name}
   */
  public JAXBName( Name name )
  {
    super();
    this.name = name != null ? name.name() : null;
  }
  
  /**
   * @see JAXBName
   * @param enumeration
   *          {@link Enum}
   */
  public JAXBName( @SuppressWarnings("rawtypes") Enum enumeration )
  {
    super();
    this.name = enumeration != null ? enumeration.name() : null;
  }
  
  /**
   * @see JAXBName
   */
  protected JAXBName()
  {
    super();
  }
  
  @Override
  public String name()
  {
    return this.name;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "JAXBNamedEntity [name=" );
    builder.append( this.name );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.name == null ) ? 0 : this.name.hashCode() );
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
    if ( !( obj instanceof Name ) )
    {
      return false;
    }
    Name other = (Name) obj;
    if ( this.name == null )
    {
      if ( other.name() != null )
      {
        return false;
      }
    }
    else if ( !this.name.equals( other.name() ) )
    {
      return false;
    }
    return true;
  }
  
}
