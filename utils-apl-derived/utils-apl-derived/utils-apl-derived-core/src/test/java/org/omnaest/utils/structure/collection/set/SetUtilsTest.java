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
package org.omnaest.utils.structure.collection.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.structure.collection.set.SetUtils;

public class SetUtilsTest
{
  
  @Test
  public void testMergeAllCollectionOfEArray()
  {
    //
    @SuppressWarnings("unchecked")
    Set<String>[] sets = new Set[] { new HashSet<String>(), new HashSet<String>() };
    sets[0].add( "first value" );
    sets[0].add( "second value" );
    sets[1].add( "third value" );
    sets[1].add( "fourth value" );
    
    //
    Set<String> mergedSets = SetUtils.mergeAll( sets );
    assertEquals( 4, mergedSets.size() );
    assertTrue( mergedSets.contains( sets[0].toArray()[0] ) );
    assertTrue( mergedSets.contains( sets[0].toArray()[1] ) );
    assertTrue( mergedSets.contains( sets[1].toArray()[0] ) );
    assertTrue( mergedSets.contains( sets[1].toArray()[1] ) );
  }
  
}
