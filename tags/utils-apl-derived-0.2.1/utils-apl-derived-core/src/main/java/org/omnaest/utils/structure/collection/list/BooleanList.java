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
package org.omnaest.utils.structure.collection.list;

import java.util.List;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.structure.collection.list.decorator.ListDecorator;

/**
 * Special {@link List} wrapper for {@link Boolean} types which provides some further methods to analyze the state of the values.
 * 
 * @see #containsOnlyTrueValues()
 * @see #containsOnlyFalseValues()
 * @see #containsAtLeastOneTrueValue()
 * @see #containsAtLeastNumberOfTrueValues(int)
 * @author Omnaest
 */
public class BooleanList extends ListDecorator<Boolean>
{
  private static final long serialVersionUID = 3614309493148716282L;
  
  /**
   * @see BooleanList
   * @param list
   */
  public BooleanList( List<Boolean> list )
  {
    super( list );
    Assert.isNotNull( list, "list must not be null" );
  }
  
  public boolean containsAtLeastOneTrueValue()
  {
    return this.contains( true );
  }
  
  public boolean containsAtLeastOneFalseValue()
  {
    return this.contains( false );
  }
  
  public boolean containsOnlyFalseValues()
  {
    boolean retval = true;
    for ( Boolean value : this )
    {
      if ( value )
      {
        retval = false;
        break;
      }
    }
    return retval;
  }
  
  public boolean containsOnlyTrueValues()
  {
    boolean retval = true;
    for ( Boolean value : this )
    {
      if ( !value )
      {
        retval = false;
        break;
      }
    }
    return retval;
  }
  
  public boolean containsAtLeastNumberOfTrueValues( int numberOfTrueValues )
  {
    boolean retval = false;
    int counter = 0;
    for ( Boolean value : this )
    {
      if ( value )
      {
        counter++;
        if ( counter >= numberOfTrueValues )
        {
          retval = true;
          break;
        }
      }
    }
    return retval;
  }
  
}
