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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An {@link Converter} allows to declare a {@link Class} of an {@link ElementConverter} which should be used to translate a
 * return value or a single given parameter before storing it in the underlying structure.<br>
 * <br>
 * The instance of the {@link ElementConverter} must have a default constructor.<br>
 * <br>
 * If multiple {@link ElementConverter} types are specified they are chained in ascending index order (left to right). Please
 * ensure that the output of any left handed {@link ElementConverter} can be used as input for any following
 * {@link ElementConverter}. If any {@link Exception} occurs it is catched and null is returned.
 * 
 * @see ElementConverter
 * @author Omnaest
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Converter
{
  /**
   * @see Converter
   * @return
   */
  @SuppressWarnings("rawtypes")
  public Class<? extends ElementConverter>[] types();
}
