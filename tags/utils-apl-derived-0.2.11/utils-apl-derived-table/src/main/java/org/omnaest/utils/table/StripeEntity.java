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
package org.omnaest.utils.table;

import java.util.Arrays;

/**
 * Immutable entity which contains the elements and the title of a stripe
 * 
 * @author Omnaest
 * @param <E>
 */
public class StripeEntity<E>
{
  private final String title;
  private final E[]    elements;
  
  public StripeEntity( String title, E[] elements )
  {
    super();
    this.title = title;
    this.elements = elements;
  }
  
  public String getTitle()
  {
    return this.title;
  }
  
  public E[] getElements()
  {
    return this.elements;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "StripeEntity [title=" );
    builder.append( this.title );
    builder.append( ", elements=" );
    builder.append( Arrays.toString( this.elements ) );
    builder.append( "]" );
    return builder.toString();
  }
  
}
