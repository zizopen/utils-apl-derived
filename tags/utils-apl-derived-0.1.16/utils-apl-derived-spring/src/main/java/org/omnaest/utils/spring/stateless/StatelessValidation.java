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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The {@link StatelessValidation} annotation ensures that an annotated type will only have none or only final static members.<br>
 * <br>
 * To make the validation work a {@link StatelessValidatorBean} has to be declared and instantiated by Spring. The
 * {@link StatelessValidatorBean} will collect and scan all beans with the {@link StatelessValidation} at startup during
 * {@link PostConstruct} time.
 * 
 * @see StatelessValidatorBean
 * @author Omnaest
 */
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Qualifier
public @interface StatelessValidation
{
}
