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
import java.lang.reflect.Type;
import java.util.Map;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterHelper;

/**
 * {@link SourcePropertyAccessorDecorator} which will listen to {@link DefaultValue} annotated {@link Method}s
 * 
 * @see SourcePropertyAccessor
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorDefaultValue extends SourcePropertyAccessorDecorator
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 1054528663696959420L;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * @see SourcePropertyAccessorDecoratorDefaultValue
   * @param sourcePropertyAccessor
   */
  public SourcePropertyAccessorDecoratorDefaultValue( SourcePropertyAccessor sourcePropertyAccessor )
  {
    super( sourcePropertyAccessor );
  }
  
  @Override
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    
    //
    value = defaultObject( value, parameterType, propertyMetaInformation, propertyName );
    this.sourcePropertyAccessor.setValue( propertyName, value, parameterType, propertyMetaInformation );
  }
  
  @Override
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    
    //
    Object value = this.sourcePropertyAccessor.getValue( propertyName, returnType, propertyMetaInformation );
    return defaultObject( value, returnType, propertyMetaInformation, propertyName );
  }
  
  /**
   * @param value
   * @param type
   * @param propertyMetaInformation
   * @return
   */
  private static Object defaultObject( Object value,
                                       Class<?> type,
                                       PropertyMetaInformation propertyMetaInformation,
                                       String propertyName )
  {
    //
    Object retval = value;
    if ( value == null && propertyMetaInformation != null && type != null )
    {
      //
      DefaultValue defaultValueAnnotation = propertyMetaInformation.getPropertyAnnotationAutowiredContainer()
                                                                   .getValue( DefaultValue.class );
      
      //
      DefaultValues defaultValuesAnnotation = propertyMetaInformation.getPropertyAnnotationAutowiredContainer()
                                                                     .getValue( DefaultValues.class );
      
      //
      if ( defaultValueAnnotation != null )
      {
        //
        final String defaultValue = defaultValueAnnotation.value();
        final Class<? extends ElementConverter<String, ?>>[] defaultValueConverterTypes = defaultValueAnnotation.defaultValueConverterTypes();
        retval = ElementConverterHelper.convert( defaultValue, defaultValueConverterTypes );
        retval = ObjectUtils.castTo( type, retval );
      }
      else if ( defaultValuesAnnotation != null )
      {
        //
        String[] defaultValues = defaultValuesAnnotation.values();
        
        //
        @SuppressWarnings("unchecked")
        Class<Object> wrapperType = (Class<Object>) type;
        
        {
          //
          if ( propertyMetaInformation.getGenericType() != null )
          {
            //
            Type[] actualTypeArguments = propertyMetaInformation.getGenericType().getActualTypeArguments();
            if ( actualTypeArguments != null && actualTypeArguments.length >= 1 )
            {
              if ( actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof Class )
              {
                //
                Class<?> elementType = (Class<?>) actualTypeArguments[0];
                retval = ObjectUtils.castArrayTo( wrapperType, elementType, defaultValues );
              }
              else if ( Map.class.isAssignableFrom( wrapperType ) && actualTypeArguments.length == 2
                        && actualTypeArguments[0] instanceof Class && actualTypeArguments[1] instanceof Class )
              {
                //
                Class<?> keyType = (Class<?>) actualTypeArguments[0];
                Class<?> valueType = (Class<?>) actualTypeArguments[1];
                retval = ObjectUtils.castArrayToMap( wrapperType, keyType, valueType, defaultValues );
              }
            }
            
          }
          else if ( type.isArray() )
          {
            //
            Class<?> elementType = type.getComponentType();
            retval = ObjectUtils.castArrayTo( wrapperType, elementType, defaultValues );
          }
        }
      }
    }
    
    return retval;
  }
}
