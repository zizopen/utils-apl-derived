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

/**
 * @see ElementConverterTypeAware
 * @author Omnaest
 */
public class ElementConverterObjectToString implements ElementConverterTypeAwareSerializable<Object, String>
{
  private static final long serialVersionUID = 5530096365933057082L;

  @Override
  public String convert( Object element )
  {
    return element != null ? String.valueOf( element ) : null;
  }
  
  @Override
  public SourceAndTargetType<Object, String> getSourceAndTargetType()
  {
    return new SourceAndTargetType<Object, String>( Object.class, String.class );
  }
}
