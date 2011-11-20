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

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * A {@link PropertyNameTemplate} allows to declare a mapping template string which is normally used to map methods of interfaces
 * to properties of an underlying structure.<br>
 * <br>
 * The annotation can be put to {@link Class} types as well as to properties and to {@link Method}s. If the {@link Annotation} is
 * present at all locations the same time, the {@link Annotation} on the {@link Method} is preferred on the {@link Annotation} of
 * the property and that is preferred to the {@link Class} {@link Annotation}. <br>
 * <br>
 * The template syntax offers to ways of dynamic behavior:<br>
 * <ul>
 * <li>Using {propertyname} as placeholder, which will be replaced with the automatically determined property name</li>
 * <li>Using {0},{1},... to use the values given as additional parameter arguments</li>
 * </ul>
 * Be aware, that the index counting of the {0} replacer starts at 0 and maps to the first additional parameter. This means that a
 * regular parameter e.g. for a setter method is not captured by this and the counting will start with the second parameter of a
 * setter method.This ensures, that getters and setter can share one {@link PropertyNameTemplate}. Furthermore ensure that all
 * additional parameters are compatible to {@link String#valueOf(Object)}. <br>
 * <br>
 * Example:<br>
 * 
 * <pre>
 * &#064;PropertyNameTemplate(&quot;{propertyname}_class&quot;)
 * public interface ExampleInterface
 * {
 *   
 *   //This will map to &quot;field_class&quot; because of the class type annotation
 *   public void setField( String value );
 *   
 *   //This will map to &quot;field_class&quot; because of the class type annotation
 *   public String getField();
 *   
 *   //This will map to &quot;fieldWithTemplateAndMore&quot; because of the property annotation
 *   &#064;PropertyNameTemplate(&quot;{propertyname}AndMore&quot;)
 *   public void setFieldWithTemplate( String value );
 *   
 *   //This will map to &quot;fieldWithTemplateAndMore&quot; because of the property annotation of the corresponding setter
 *   public String getFieldWithTemplate();
 *   
 *   //This will map to &quot;fieldWithTemplateAndAdditionalArgumentsAndMore_abc_&quot; for the given tagValue &quot;abc&quot;. 
 *   //The name template of the corresponding getter is ignored. The &quot;{0}&quot; token is replaced by the given tag value.
 *   //Notice that the index position 0 maps to the second argument here, since this is a setter and the first parameter is the value to set.
 *   &#064;PropertyNameTemplate(&quot;{propertyname}AndMore_{0}_&quot;)
 *   public void setFieldWithTemplateAndAdditionalArguments( String value, String tagValue );
 *   
 *   //This will map to &quot;fieldWithTemplateAndAdditionalArgumentsAndMore(abc)&quot; for the given tagValue &quot;abc&quot;. 
 *   //The name template of the corresponding setter is ignored. The &quot;{0}&quot; token is replaced by the given tag value.
 *   &#064;PropertyNameTemplate(&quot;{propertyname}AndMore({0})&quot;)
 *   public String getFieldWithTemplateAndAdditionalArguments( String tagValue );
 * }
 * </pre>
 * 
 * @author Omnaest
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface PropertyNameTemplate
{
  public String value();
}
