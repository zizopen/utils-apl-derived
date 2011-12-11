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
package org.omnaest.utils.spring.stateless;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.annotation.PostConstruct;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This bean collects all beans with the {@link StatelessValidation} annotation and scans them for non final static fields. If any
 * none final static field is encountered the bean throws an {@link IllegalArgumentException}.<br>
 * After scanning the references to any bean is dropped. <br>
 * <br>
 * Be aware that only types which are directly annotated with {@link StatelessValidation} or any of their supertypes are annotated
 * will be considered. Annotations on interfaces are not resolved. <br>
 * <br>
 * Configuration:<br>
 * 
 * <pre>
 * &lt;context:annotation-config /&gt;
 * &lt;bean class=&quot;org.omnaest.utils.spring.stateless.StatelessValidatorBean&quot; /&gt;
 * </pre>
 * 
 * @see StatelessValidation
 * @author Omnaest
 */
public class StatelessValidatorBean
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired(required = false)
  protected List<Object> validationCandidateList = null;
  
  protected int          counter                 = 0;
  
  /* ********************************************** Methods ********************************************** */
  
  @PostConstruct
  public void afterPropertiesSet()
  {
    //
    try
    {
      //
      if ( this.validationCandidateList != null )
      {
        for ( final Object object : this.validationCandidateList )
        {
          if ( object != null )
          {
            //
            final Class<? extends Object> objectType = object.getClass();
            
            //
            boolean hasStatelessValidationAnnotation = ReflectionUtils.hasAnnotationIncludingInterfaces( objectType,
                                                                                                         StatelessValidation.class );
            if ( hasStatelessValidationAnnotation )
            {
              this.validateBeanType( objectType );
              this.counter++;
            }
          }
        }
      }
    }
    finally
    {
      //
      this.validationCandidateList = null;
    }
  }
  
  /**
   * @throws IllegalArgumentException
   * @param objectType
   */
  public void validateBeanType( Class<?> objectType )
  {
    if ( objectType != null )
    {
      List<Field> fieldList = ReflectionUtils.declaredFieldList( objectType );
      for ( Field field : fieldList )
      {
        int modifiers = field.getModifiers();
        boolean isFinal = Modifier.isFinal( modifiers );
        boolean isStatic = Modifier.isStatic( modifiers );
        
        //
        Assert.isTrue( "Encountered non final static field '" + field.getName() + "' for type '" + objectType.getCanonicalName()
                       + "'. Ensure that @StatelessValidation annotated type do only use final static fields.", isFinal, isStatic );
      }
    }
  }
}
