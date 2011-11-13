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
package org.omnaest.utils.beans.adapter.source;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.adapter.PropertyAccessOption;

/**
 * {@link SourcePropertyAccessorDecorator} which will convert incoming keys based on a {@link PropertyAccessOption}
 * 
 * @see PropertyAccessOption
 * @see SourcePropertyAccessor
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorPropertyAccessOption extends SourcePropertyAccessorDecorator
{
  /* ********************************************** Variables ********************************************** */
  protected PropertyAccessOption propertyAccessOption = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param sourcePropertyAccessor
   * @param propertyAccessOption
   */
  public SourcePropertyAccessorDecoratorPropertyAccessOption( SourcePropertyAccessor sourcePropertyAccessor,
                                                              PropertyAccessOption propertyAccessOption )
  {
    super( sourcePropertyAccessor );
    this.propertyAccessOption = propertyAccessOption;
  }
  
  /**
   * @param propertyAccessOption
   */
  public SourcePropertyAccessorDecoratorPropertyAccessOption( PropertyAccessOption propertyAccessOption )
  {
    super();
    this.propertyAccessOption = propertyAccessOption;
  }
  
  @Override
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    String propertyNameProcessed = this.processPropertyNameWithTemplate( propertyName );
    
    //
    Assert.notNull( this.sourcePropertyAccessor );
    this.sourcePropertyAccessor.setValue( propertyNameProcessed, value, parameterType, propertyMetaInformation );
  }
  
  @Override
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    String propertyNameProcessed = this.processPropertyNameWithTemplate( propertyName );
    
    //
    Assert.notNull( this.sourcePropertyAccessor );
    return this.sourcePropertyAccessor.getValue( propertyNameProcessed, returnType, propertyMetaInformation );
  }
  
  protected String processPropertyNameWithTemplate( String propertyName )
  {
    //
    String retval = propertyName;
    
    //
    if ( this.propertyAccessOption != null )
    {
      if ( PropertyAccessOption.PROPERTY_LOWERCASE.equals( this.propertyAccessOption ) )
      {
        retval = StringUtils.lowerCase( propertyName );
      }
      else if ( PropertyAccessOption.PROPERTY_UPPERCASE.equals( this.propertyAccessOption ) )
      {
        retval = StringUtils.upperCase( propertyName );
      }
      else if ( PropertyAccessOption.PROPERTY_CAPITALIZED.equals( this.propertyAccessOption ) )
      {
        retval = StringUtils.capitalize( propertyName );
      }
    }
    
    //
    return retval;
  }
  
}
