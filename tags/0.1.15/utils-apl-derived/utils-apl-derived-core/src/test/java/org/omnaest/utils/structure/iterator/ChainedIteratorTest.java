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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see ChainedIterator
 * @author Omnaest
 */
public class ChainedIteratorTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @SuppressWarnings("unchecked")
  private final List<String>[] lists    = new List[] { Arrays.asList( "a", "b" ), Arrays.asList(), Arrays.asList( "b", "c" ) };
  private Iterator<String>     iterator = new ChainedIterator<String>( this.lists );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNextAndHasNext()
  {
    //
    assertEquals( Arrays.asList( "a", "b", "b", "c" ), ListUtils.valueOf( this.iterator ) );
  }
  
}
