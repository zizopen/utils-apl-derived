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

/**
 * The {@link DefaultValues} {@link Annotation} allows to declare a value which is used as default value, if a given or returned
 * value of a method is null.<br>
 * <br>
 * The values are specified as {@link String} and will be converted to any type which has a single {@link String} parameter
 * constructor or a valueOf method like {@link Double#valueOf(String)} .<br>
 * <br>
 * Example:
 * 
 * <pre>
 * protected interface ExampleInterface
 * {
 *   &#064;DefaultValue(value = &quot;defaultValueSet&quot;)
 *   public void setFieldString( String value );
 *   
 *   &#064;DefaultValue(value = &quot;defaultValueGet&quot;)
 *   public String getFieldString();
 *   
 *   &#064;DefaultValue(value = &quot;1.23&quot;)
 *   public void setFieldDouble( Double value );
 *   
 *   &#064;DefaultValue(value = &quot;3.45&quot;)
 *   public Double getFieldDouble();
 * }
 * </pre>
 * 
 * @author Omnaest
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DefaultValues
{
  public String[] values();
}
