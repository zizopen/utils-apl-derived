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
 * @see ElementConverterNumberToString
 * @author Omnaest
 */
public class ElementConverterStringToLong implements ElementConverterTypeAwareSerializable<String, Long>
{
  
  private static final long serialVersionUID = -4105026393418434436L;

  @Override
  public Long convert( String element )
  {
    //    
    Long retval = null;
    
    //
    if ( element != null )
    {
      try
      {
        retval = Long.valueOf( element );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public SourceAndTargetType<String, Long> getSourceAndTargetType()
  {
    return new SourceAndTargetType<String, Long>( String.class, Long.class );
  }
  
}
