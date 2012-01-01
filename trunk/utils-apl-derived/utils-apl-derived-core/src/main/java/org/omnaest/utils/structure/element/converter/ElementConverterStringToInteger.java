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
 * @see Integer
 * @see ElementConverterTypeAware
 * @see ElementConverterNumberToString
 * @author Omnaest
 */
public class ElementConverterStringToInteger implements ElementConverterTypeAware<String, Integer>
{
  
  @Override
  public Integer convert( String element )
  {
    //    
    Integer retval = null;
    
    //
    if ( element != null )
    {
      try
      {
        retval = Integer.valueOf( element );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public SourceAndTargetType<String, Integer> getSourceAndTargetType()
  {
    return new SourceAndTargetType<String, Integer>( String.class, Integer.class );
  }
  
}
