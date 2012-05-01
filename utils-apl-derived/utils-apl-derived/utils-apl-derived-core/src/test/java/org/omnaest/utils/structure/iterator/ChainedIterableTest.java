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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see ChainedIterable
 * @author Omnaest
 */
public class ChainedIterableTest
{
  /* ********************************************** Variables ********************************************** */
  @SuppressWarnings("unchecked")
  private Iterable<String>[] iterables = new Iterable[] { Arrays.asList( "a", "b" ), new ArrayList<String>(),
      Arrays.asList( "c", "d" )       };
  private Iterable<String>   iterable  = new ChainedIterable<String>( this.iterables );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testIterator()
  {
    assertEquals( Arrays.asList( "a", "b", "c", "d" ), ListUtils.valueOf( this.iterable ) );
  }
}
