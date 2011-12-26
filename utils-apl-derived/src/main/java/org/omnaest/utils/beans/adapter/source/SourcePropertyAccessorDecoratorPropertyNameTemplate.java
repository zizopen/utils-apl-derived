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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * {@link SourcePropertyAccessorDecorator} which will listen to {@link PropertyNameTemplate} annotated {@link Method}s and
 * {@link Class}es.
 * 
 * @see SourcePropertyAccessor
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorPropertyNameTemplate extends SourcePropertyAccessorDecorator
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = 6165218599151510835L;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SourcePropertyAccessorDecoratorPropertyNameTemplate
   * @param sourcePropertyAccessor
   */
  public SourcePropertyAccessorDecoratorPropertyNameTemplate( SourcePropertyAccessor sourcePropertyAccessor )
  {
    super();
    this.sourcePropertyAccessor = sourcePropertyAccessor;
  }
  
  @Override
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    String propertyNameProcessed = SourcePropertyAccessorDecoratorPropertyNameTemplate.processPropertyNameWithTemplate( propertyName,
                                                                                                                        propertyMetaInformation );
    
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    this.sourcePropertyAccessor.setValue( propertyNameProcessed, value, parameterType, propertyMetaInformation );
  }
  
  @Override
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    String propertyNameProcessed = SourcePropertyAccessorDecoratorPropertyNameTemplate.processPropertyNameWithTemplate( propertyName,
                                                                                                                        propertyMetaInformation );
    
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    return this.sourcePropertyAccessor.getValue( propertyNameProcessed, returnType, propertyMetaInformation );
  }
  
  private static String processPropertyNameWithTemplate( String propertyName, PropertyMetaInformation propertyMetaInformation )
  {
    //
    String retval = propertyName;
    if ( propertyMetaInformation != null )
    {
      //
      PropertyNameTemplate propertyNameTemplate = propertyMetaInformation.getPropertyAnnotationAutowiredContainer()
                                                                         .getValue( PropertyNameTemplate.class );
      
      if ( propertyNameTemplate == null )
      {
        propertyNameTemplate = propertyMetaInformation.getClassAnnotationAutowiredContainer()
                                                      .getValue( PropertyNameTemplate.class );
      }
      
      //
      if ( propertyNameTemplate != null )
      {
        //
        final Class<? extends ElementConverter<?, String>>[] additionalArgumentConverterTypes = propertyNameTemplate.additionalArgumentConverterTypes();
        final String template = propertyNameTemplate.value();
        if ( template != null )
        {
          //
          final String TAG_PROPERTYNAME = "\\{(?iu)propertyname(?-iu)\\}";
          final String TAG_PARAMETER = "\\{(\\d)\\}";
          Assert.isTrue( Pattern.matches( "(" + TAG_PROPERTYNAME + "|" + TAG_PARAMETER + "|[^\\{\\}])+", template ),
                         "PropertyNameTemplate of property " + propertyName + " has an invalid format." );
          
          //
          String templateWithValues = template.replaceAll( TAG_PROPERTYNAME, propertyName );
          
          //
          StringBuffer stringBuffer = new StringBuffer();
          Matcher matcher = Pattern.compile( TAG_PARAMETER ).matcher( templateWithValues );
          while ( matcher.find() )
          {
            //
            String group = matcher.group( 1 );
            
            //
            Assert.isTrue( StringUtils.isNumeric( group ), "Parameter index position within PropertyNameTemplate of property "
                                                           + propertyName + " has to be a valid number. Found: " + group );
            int additionalArgumentIndexPosition = Integer.valueOf( group );
            
            //
            Object[] additionalArguments = propertyMetaInformation.getAdditionalArguments();
            int parameterIndexPositionMax = additionalArguments.length - 1;
            Assert.isTrue( additionalArgumentIndexPosition >= 0 && additionalArgumentIndexPosition <= parameterIndexPositionMax,
                           "Parameter index position within PropertyNameTemplate of property " + propertyName
                               + " has to be between 0 and " + parameterIndexPositionMax );
            
            //
            final Object additionalArgument = determineAdditionalArgument( additionalArgumentConverterTypes,
                                                                           additionalArgumentIndexPosition, additionalArguments );
            
            final String additionalArgumentString = String.valueOf( additionalArgument );
            matcher.appendReplacement( stringBuffer, additionalArgumentString );
          }
          matcher.appendTail( stringBuffer );
          
          //
          retval = stringBuffer.toString();
        }
      }
    }
    
    return retval;
  }
  
  private static Object determineAdditionalArgument( final Class<? extends ElementConverter<?, String>>[] additionalArgumentConverterTypes,
                                                     int additionalArgumentIndexPosition,
                                                     Object[] additionalArguments )
  {
    //
    Object additionalArgument = additionalArguments[additionalArgumentIndexPosition];
    
    //
    if ( additionalArgumentIndexPosition >= 0 && additionalArgumentIndexPosition < additionalArgumentConverterTypes.length )
    {
      //
      final Class<? extends ElementConverter<?, String>> elementConverterType = additionalArgumentConverterTypes[additionalArgumentIndexPosition];
      @SuppressWarnings("unchecked")
      final ElementConverter<Object, String> elementConverter = (ElementConverter<Object, String>) ReflectionUtils.createInstanceOf( elementConverterType );
      if ( elementConverter != null )
      {
        additionalArgument = elementConverter.convert( additionalArgument );
      }
    }
    
    //
    return additionalArgument;
  }
  
}
