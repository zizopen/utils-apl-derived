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
package org.omnaest.utils.web;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpSession;

import org.omnaest.utils.beans.adapter.source.DefaultValue;
import org.omnaest.utils.beans.adapter.source.PropertyNameTemplate;
import org.omnaest.utils.structure.element.converter.Converter;

/**
 * Super type interface for all {@link HttpSessionFacade}s. This allows to track all available {@link HttpSessionFacade}s by
 * searching for derivative types. <br>
 * <br>
 * The {@link HttpSessionFacade} supports following {@link Annotation}s:<br>
 * <ul>
 * <li>{@link Converter}</li>
 * <li>{@link PropertyNameTemplate}</li>
 * <li>{@link DefaultValue}</li>
 * </ul>
 * <br>
 * <br>
 * An example of an interface put on top of a {@link HttpSession} can look like:
 * 
 * <pre>
 * public static interface HttpSessionFacadeExample extends HttpSessionFacade
 * {
 *   public void setFieldString( String field );
 *   
 *   public String getFieldString();
 *   
 *   public void setFieldDouble( Double fieldDouble );
 *   
 *   &#064;DefaulValue(value = &quot;3.45&quot;)
 *   public Double getFieldDouble();
 *   
 *   &#064;PropertyNameTemplate(&quot;OTHERFIELD&quot;)
 *   public String getOtherField();
 *   
 *   &#064;Adapter(type = ElementConverterIdentity.class)
 *   public void setOtherField( String value );
 * }
 * </pre>
 * 
 * @see HttpSessionFacadeFactory
 * @author Omnaest
 */
public interface HttpSessionFacade
{
}
