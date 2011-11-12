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
package org.omnaest.utils.assertion;

/**
 * The {@link Assert} class offers assert methods which throw {@link RuntimeException}s if constraints are not fulfilled.
 * 
 * @author Omnaest
 */
public class Assert
{
  
  /**
   * @param object
   */
  public static void notNull( Object object )
  {
    notNull( object, "Object was null, but must be not null" );
  }
  
  /**
   * @param object
   * @param message
   */
  public static void notNull( Object object, String message )
  {
    if ( object == null )
    {
      throw new IllegalArgumentException( message );
    }
  }
  
  /**
   * @param expression
   */
  public static void assertTrue( boolean expression )
  {
    assertTrue( expression, "Expression must be true but was false" );
  }
  
  /**
   * @param expression
   * @param message
   */
  public static void assertTrue( boolean expression, String message )
  {
    if ( !expression )
    {
      throw new IllegalArgumentException( message );
    }
  }
}
