/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.spring.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * {@link Qualifier} for all {@link ExtensionPoint}s. An {@link ExtensionPoint} allows to invoke methods of multiple instance of
 * the same service at once.<br>
 * Its best practice that an implementation uses the composite pattern to implement the common service type itself but dispatching
 * all method invocations to the other instances. <br>
 * <br>
 * <h1>Example:</h1>
 * 
 * <pre>
 * public class Client
 * {
 *   &#064;Autowired
 *   &#064;ExtensionPoint
 *   protected SampleService sampleService = null;
 *   
 *   public void clientCall()
 *   {
 *     this.sampleService.doSomething(); //Does invoke SampleServiceImpl1 and SampleServiceImpl2 through the SampleServiceExtensionPoint  
 *   }
 * }
 * 
 * public interface SampleService
 * {
 *   public void doSomething();
 * }
 * 
 * &#064;Service
 * public class SampleServiceImpl1 implements SampleService
 * {
 *   &#064;Override
 *   public void doSomething()
 *   {
 *     //Do something
 *   }
 * }
 * 
 * &#064;Service
 * public class SampleServiceImpl2 implements SampleService
 * {
 *   &#064;Override
 *   public void doSomething()
 *   {
 *     //Do something
 *   }
 * }
 * 
 * &#064;Service
 * public class SampleServiceExtensionPoint extends ExtensionPointTemplate&lt;SampleService&gt; implements SampleService
 * {
 *   &#064;Autowired
 *   public SampleServiceExtensionPoint( List&lt;SampleService&gt; serviceList )
 *   {
 *     super( serviceList );
 *   }
 *   
 *   &#064;Override
 *   public void doSomething()
 *   {
 *     this.executeOnAll( new OperationVoid&lt;SampleService&gt;()
 *     {
 *       &#064;Override
 *       public void execute( SampleService sampleService )
 *       {
 *         // 
 *         sampleService.doSomething();
 *       }
 *     } );
 *   }
 * }
 * </pre>
 * 
 * @see ExtensionPointTemplate
 * @author Omnaest
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Qualifier
public @interface ExtensionPoint
{
}
