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
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * {@link SourcePropertyAccessorDecorator} which will listen to {@link Converter} annotated {@link Method}s.
 * 
 * @see SourcePropertyAccessor
 * @author Omnaest
 */
public class SourcePropertyAccessorDecoratorAdapter extends SourcePropertyAccessorDecorator
{
  /* ********************************************** Constants ********************************************** */
  private static final long serialVersionUID = -6289909653064728549L;
  
  /* ********************************************** Methods ********************************************** */
  /**
   * 
   */
  public SourcePropertyAccessorDecoratorAdapter()
  {
    super();
  }
  
  /**
   * @param sourcePropertyAccessor
   */
  public SourcePropertyAccessorDecoratorAdapter( SourcePropertyAccessor sourcePropertyAccessor )
  {
    super();
    this.sourcePropertyAccessor = sourcePropertyAccessor;
  }
  
  @Override
  public void setValue( String propertyName, Object value, Class<?> parameterType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Converter converter = propertyMetaInformation.getPropertyAnnotationAutowiredContainer().getValue( Converter.class );
    if ( converter != null )
    {
      value = SourcePropertyAccessorDecoratorAdapter.convertByAdapter( value, converter );
    }
    
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    this.sourcePropertyAccessor.setValue( propertyName, value, parameterType, propertyMetaInformation );
  }
  
  @Override
  public Object getValue( String propertyName, Class<?> returnType, PropertyMetaInformation propertyMetaInformation )
  {
    //
    Object retval = null;
    
    //
    Assert.isNotNull( this.sourcePropertyAccessor );
    retval = this.sourcePropertyAccessor.getValue( propertyName, returnType, propertyMetaInformation );
    
    //
    Converter converter = propertyMetaInformation.getPropertyAnnotationAutowiredContainer().getValue( Converter.class );
    if ( converter != null )
    {
      retval = SourcePropertyAccessorDecoratorAdapter.convertByAdapter( retval, converter );
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static Object convertByAdapter( Object value, Converter converter )
  {
    //
    Object retval = value;
    
    //
    if ( converter != null )
    {
      //   
      try
      {
        Class<? extends ElementConverter> type = converter.type();
        ElementConverter elementConverter = ReflectionUtils.createInstanceOf( type );
        if ( elementConverter != null )
        {
          retval = elementConverter.convert( value );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
}
