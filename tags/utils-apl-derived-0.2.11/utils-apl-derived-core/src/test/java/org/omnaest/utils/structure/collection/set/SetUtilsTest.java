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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;

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
  
  @Test
  public void testAdd()
  {
    assertEquals( Arrays.asList( "a", "b", "c", "d" ),
                  ListUtils.valueOf( SetUtils.add( SetUtils.valueOf( "a", "b" ), "b", "c", "d" ) ) );
  }
  
  @Test
  public void testRemoveAllAsNewSet()
  {
    assertEquals( SetUtils.valueOf( "a" ),
                  SetUtils.removeAllAsNewSet( SetUtils.valueOf( "a", "b", "c" ), ListUtils.valueOf( "c", "b" ) ) );
  }
  
  @Test
  public void testDelta()
  {
    final SetDelta<String> setDelta = SetUtils.delta( SetUtils.valueOf( "a", "b", "c" ), SetUtils.valueOf( "a2", "b", "c2" ) );
    assertEquals( SetUtils.valueOf( "a", "c" ), setDelta.getRemovedElementSet() );
    assertEquals( SetUtils.valueOf( "a2", "c2" ), setDelta.getAddedElementSet() );
    assertEquals( SetUtils.valueOf( "b" ), setDelta.getRetainedElementSet() );
  }
  
  @Test
  public void testIntersection() throws Exception
  {
    assertEquals( SetUtils.valueOf( "b" ), SetUtils.intersection( Arrays.asList( "a", "b" ), Arrays.asList( "b", "c" ) ) );
    assertEquals( SetUtils.valueOf( "b", "c" ),
                  SetUtils.intersection( SetUtils.valueOf( "a", "b", "c", "d" ), SetUtils.valueOf( "b", "c", "e" ) ) );
  }
  
  @Test
  public void testRetainAll() throws Exception
  {
    assertEquals( SetUtils.valueOf( "b", "c" ),
                  SetUtils.retainAll( SetUtils.valueOf( "a", "b", "c", "d" ), Arrays.asList( "b", "c", "e" ) ) );
    assertEquals( SetUtils.valueOf(), SetUtils.retainAll( SetUtils.valueOf( "a", "b", "c", "d" ), null ) );
    assertEquals( null, SetUtils.retainAll( null, Arrays.asList( "b", "c", "e" ) ) );
  }
  
  @Test
  public void testAddAll() throws Exception
  {
    assertEquals( SetUtils.valueOf( "a", "b", "c", "d" ),
                  SetUtils.addAll( SetUtils.valueOf( "a", "b" ), Arrays.asList( "b", "c", "d" ) ) );
    assertEquals( SetUtils.valueOf( "b", "c", "d" ), SetUtils.addAll( null, Arrays.asList( "b", "c", "d" ) ) );
    assertEquals( SetUtils.valueOf( "a", "b" ), SetUtils.addAll( SetUtils.valueOf( "a", "b" ), null ) );
  }
}
