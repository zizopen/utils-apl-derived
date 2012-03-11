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
package org.omnaest.utils.structure.element.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.omnaest.utils.assertion.Assert;

/**
 * Abstract {@link ElementConverterTypeAware} implementation which allows to resolve the
 * {@link ElementConverterTypeAware#getSourceAndTargetType()} based on the generic types of the {@link ElementConverter}. <br>
 * <br>
 * This only works for direct type derivates of this {@link Class}
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public abstract class ElementConverterTypeAwareGenericsBased<FROM, TO> implements ElementConverterTypeAware<FROM, TO>
{
  
  @SuppressWarnings("unchecked")
  @Override
  public org.omnaest.utils.structure.element.converter.ElementConverterTypeAware.SourceAndTargetType<FROM, TO> getSourceAndTargetType()
  {
    //
    Class<? extends FROM> sourceType = null;
    Class<? extends TO> targetType = null;
    
    //
    final Type genericSuperclass = this.getClass().getGenericSuperclass();
    if ( genericSuperclass instanceof ParameterizedType )
    {
      final ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
      Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
      if ( actualTypeArguments != null && actualTypeArguments.length == 2 )
      {
        sourceType = (Class<? extends FROM>) actualTypeArguments[0];
        targetType = (Class<? extends TO>) actualTypeArguments[1];
      }
      
    }
    
    //
    Assert.isNotNull( "Failed to resolve source and target type from generic super type", targetType, sourceType );
    
    //
    return new SourceAndTargetType<FROM, TO>( sourceType, targetType );
  }
  
}
