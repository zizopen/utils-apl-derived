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
package org.omnaest.utils.structure.element.filter;

import org.apache.commons.lang3.StringUtils;

/**
 * {@link ExcludingElementFilter} which filters / removes all blank elements. This {@link ExcludingElementFilter} can only be
 * applied to elements of type {@link String}
 * 
 * @author Omnaest
 */
public class ElementFilterNotBlank extends ExcludingElementFilter<String>
{
  @Override
  public boolean filter( String element )
  {
    return StringUtils.isBlank( element );
  }
}
