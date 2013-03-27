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
 * @see ElementConverter
 * @author Omnaest
 */
public class ElementConverterGenericElementToString<E> implements ElementConverterSerializable<E, String>
{
  private static final long serialVersionUID = 8858918740245712774L;

  @Override
  public String convert( E element )
  {
    return element != null ? String.valueOf( element ) : null;
  }
}
