/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.utils.structure.collection.set;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

/**
 * @see ReducedSet
 * @author Omnaest
 */
public class ReducedSetTest
{
  @Test
  public void test()
  {
    final Set<String> reductionSet1 = SetUtils.valueOf( "b", "c" );
    final Set<String> reductionSet2 = SetUtils.valueOf( "b", "d" );
    @SuppressWarnings("unchecked")
    final Iterable<Set<String>> reductionSets = Arrays.asList( reductionSet1, reductionSet2 );
    final Set<String> set = SetUtils.valueOf( "a", "b", "c", "d", "e" );
    
    Set<String> reducedSet = new ReducedSet<String>( set, reductionSets );
    assertEquals( 2, reducedSet.size() );
    assertEquals( SetUtils.valueOf( "a", "e" ), reducedSet );
    
    reductionSet2.add( "e" );
    assertEquals( SetUtils.valueOf( "a" ), reducedSet );
  }
}
