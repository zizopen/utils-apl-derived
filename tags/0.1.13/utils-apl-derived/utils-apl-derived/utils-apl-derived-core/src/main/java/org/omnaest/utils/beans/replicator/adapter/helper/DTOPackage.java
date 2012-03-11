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
package org.omnaest.utils.beans.replicator.adapter.helper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Package level annotation which can be used in combination of <code>package-info.java</code> to annotate a {@link Package}<br>
 * <br>
 * An example of a <code>package-info.java</code> created within the <code>org.omnaest.utils.dtoexample</code> {@link Package}
 * looks like:<br>
 * 
 * <pre>
 * &#064;DTOPackage
 * package org.omnaest.utils.dtoexample;
 * 
 * import org.omnaest.utils.beans.replicator.BeanReplicator.DTOPackage;
 * </pre>
 * 
 * @author Omnaest
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface DTOPackage
{
}
