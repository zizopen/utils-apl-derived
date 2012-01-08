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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.operation.Operation;
import org.omnaest.utils.operation.foreach.ForEach;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterStringToPattern;
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
 * <br>
 * <br>
 * The {@link #setExcludingAnnotationTypes(Class...)} property allows to declare {@link Class} types of {@link Annotation}s which,
 * if present as field {@link Annotation}, will exclude a {@link Field} from validation. E.g. the @Autowired annotation from
 * Spring can be declared there to allow dependency injected fields.<br>
 * <br>
 * Using the {@link #setExcludingFieldnamePatterns(String...)} allows to specify regular expression based field name patterns
 * which should be excluded from validation. Example: "field.*"
 * 
 * @see StatelessValidation
 * @author Omnaest
 */
public class StatelessValidatorBean
{
  /* ********************************************** Variables ********************************************** */
  protected Set<Class<? extends Annotation>> excludingAnnotationTypeSet = null;
  protected boolean                          failForNonStaticFields     = true;
  protected boolean                          failForNonFinalFields      = true;
  protected String[]                         excludingFieldnamePatterns = null;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired(required = false)
  protected List<Object>                     validationCandidateList    = null;
  
  protected int                              counter                    = 0;
  
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
   * @param objectType
   * @throws IllegalArgumentException
   */
  public void validateBeanType( Class<?> objectType )
  {
    if ( objectType != null )
    {
      //
      final List<Pattern> patternList = this.excludingFieldnamePatterns != null ? ListUtils.convert( new ElementConverterStringToPattern(),
                                                                                                     this.excludingFieldnamePatterns )
                                                                               : null;
      
      //
      final List<Field> fieldList = ReflectionUtils.declaredFieldList( objectType );
      for ( final Field field : fieldList )
      {
        //
        final String fieldName = field.getName();
        
        //
        boolean matchesExlusionPattern = patternList != null
                                         && new ForEach<Pattern, Boolean>( patternList ).execute( new Operation<Boolean, Pattern>()
                                                                                                  {
                                                                                                    @Override
                                                                                                    public Boolean execute( Pattern pattern )
                                                                                                    {
                                                                                                      return pattern.matcher( fieldName )
                                                                                                                    .matches();
                                                                                                    }
                                                                                                  } )
                                                                                        .isAnyValueEqualTo( true );
        
        //
        boolean hasNoExcludingAnnotation = true;
        if ( this.excludingAnnotationTypeSet != null )
        {
          for ( Class<? extends Annotation> annotationType : this.excludingAnnotationTypeSet )
          {
            hasNoExcludingAnnotation &= !ReflectionUtils.hasAnnotation( field, annotationType );
            if ( !hasNoExcludingAnnotation )
            {
              break;
            }
          }
        }
        
        //
        if ( !matchesExlusionPattern && hasNoExcludingAnnotation )
        {
          //
          int modifiers = field.getModifiers();
          boolean isFinal = Modifier.isFinal( modifiers );
          boolean isStatic = Modifier.isStatic( modifiers );
          
          //
          boolean passesFinalValidation = !this.failForNonFinalFields || isFinal;
          boolean passesStaticValidation = !this.failForNonStaticFields || isStatic;
          
          //
          Assert.isTrue( "Encountered non final field '" + field.getName() + "' for type '" + objectType.getCanonicalName()
                         + "'. Ensure that @StatelessValidation annotated type do only use final fields.", passesFinalValidation );
          
          Assert.isTrue( "Encountered non static field '" + field.getName() + "' for type '" + objectType.getCanonicalName()
                             + "'. Ensure that @StatelessValidation annotated type do only use static fields.",
                         passesStaticValidation );
        }
      }
    }
  }
  
  /**
   * @param excludingAnnotationTypes
   */
  public void setExcludingAnnotationTypes( Class<? extends Annotation>... excludingAnnotationTypes )
  {
    this.excludingAnnotationTypeSet = SetUtils.valueOf( excludingAnnotationTypes );
  }
  
  /**
   * Similar to {@link #setExcludingAnnotationTypes(Class...)}
   * 
   * @param excludingAnnotationTypeNames
   */
  @SuppressWarnings("unchecked")
  public void setExcludingAnnotationTypes( String... excludingAnnotationTypeNames )
  {
    // 
    final Set<Class<? extends Annotation>> excludingAnnotationTypeSet = new LinkedHashSet<Class<? extends Annotation>>();
    for ( String typeName : excludingAnnotationTypeNames )
    {
      //
      final Class<? extends Annotation> type = ReflectionUtils.classForName( typeName );
      Assert.isNotNull( type, "Failed to resolve type for the given type name: " + typeName );
      
      //
      excludingAnnotationTypeSet.add( type );
    }
    
    //    
    this.setExcludingAnnotationTypes( excludingAnnotationTypeSet.toArray( new Class[0] ) );
  }
  
  /**
   * @param failForNonStaticFields
   */
  public void setFailForNonStaticFields( boolean failForNonStaticFields )
  {
    this.failForNonStaticFields = failForNonStaticFields;
  }
  
  /**
   * @param failForNonFinalFields
   */
  public void setFailForNonFinalFields( boolean failForNonFinalFields )
  {
    this.failForNonFinalFields = failForNonFinalFields;
  }
  
  /**
   * @see StatelessValidatorBean
   * @param excludingFieldnamePatterns
   */
  public void setExcludingFieldnamePatterns( String... excludingFieldnamePatterns )
  {
    this.excludingFieldnamePatterns = excludingFieldnamePatterns;
  }
  
}
