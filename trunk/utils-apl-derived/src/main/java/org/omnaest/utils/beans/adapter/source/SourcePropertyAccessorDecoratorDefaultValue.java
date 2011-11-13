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

import java.lang.reflect.Method;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.ObjectUtils;

/**
 * {@link SourcePropertyAccessorDecorator} which will listen to {@link DefaultValue} annotated {@link Method}s
 * 
 * @see SourcePropertyAccessor
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorDefaultValue extends SourcePropertyAccessorDecorator
{
  
  /**
   * @param sourcePropertyAccessor
   */
  public SourcePropertyAccessorDecoratorDefaultValue( SourcePropertyAccessor sourcePropertyAccessor )
  {
    super();
    this.sourcePropertyAccessor = sourcePropertyAccessor;
  }
  
  @Override
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Assert.notNull( this.sourcePropertyAccessor );
    
    //
    value = defaultObject( value, parameterType, propertyMetaInformation );
    this.sourcePropertyAccessor.setValue( propertyName, value, parameterType, propertyMetaInformation );
  }
  
  @Override
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Assert.notNull( this.sourcePropertyAccessor );
    
    //
    Object value = this.sourcePropertyAccessor.getValue( propertyName, returnType, propertyMetaInformation );
    return defaultObject( value, returnType, propertyMetaInformation );
  }
  
  /**
   * @param value
   * @param type
   * @param propertyMetaInformation
   * @return
   */
  private static Object defaultObject( Object value, Class<?> type, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Object retval = value;
    if ( value == null && propertyMetaInformation != null )
    {
      //
      DefaultValue defaultValueAnnotation = propertyMetaInformation.getPropertyAnnotationAutowiredContainer()
                                                                   .getValue( DefaultValue.class );
      
      //
      if ( defaultValueAnnotation != null )
      {
        //
        String defaultValueString = defaultValueAnnotation.value();
        retval = ObjectUtils.castTo( type, defaultValueString );
      }
    }
    
    return retval;
  }
  
}
