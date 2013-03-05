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
package org.omnaest.utils.beans.replicator;

import java.io.Serializable;

class PropertyNameAndType implements Serializable
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = -1968836324042260832L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final String      propertyName;
  private final Class<?>    type;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see PropertyNameAndType
   * @param propertyName
   * @param type
   */
  PropertyNameAndType( String propertyName, Class<?> type )
  {
    super();
    this.propertyName = propertyName;
    this.type = type;
  }
  
  public String getPropertyName()
  {
    return this.propertyName;
  }
  
  public Class<?> getType()
  {
    return this.type;
  }
  
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.propertyName == null ) ? 0 : this.propertyName.hashCode() );
    result = prime * result + ( ( this.type == null ) ? 0 : this.type.hashCode() );
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
    if ( !( obj instanceof PropertyNameAndType ) )
    {
      return false;
    }
    PropertyNameAndType other = (PropertyNameAndType) obj;
    if ( this.propertyName == null )
    {
      if ( other.propertyName != null )
      {
        return false;
      }
    }
    else if ( !this.propertyName.equals( other.propertyName ) )
    {
      return false;
    }
    if ( this.type == null )
    {
      if ( other.type != null )
      {
        return false;
      }
    }
    else if ( !this.type.equals( other.type ) )
    {
      return false;
    }
    return true;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "PropertyNameAndType [propertyName=" );
    builder.append( this.propertyName );
    builder.append( ", type=" );
    builder.append( this.type );
    builder.append( "]" );
    return builder.toString();
  }
  
}
