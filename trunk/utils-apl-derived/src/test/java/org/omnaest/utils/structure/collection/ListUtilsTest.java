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
package org.omnaest.utils.structure.collection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class ListUtilsTest
{
  
  @Test
  public void testMergeAll()
  {
    //
    @SuppressWarnings("unchecked")
    Collection<String>[] collections = new Collection[] { new ArrayList<String>(), new ArrayList<String>() };
    collections[0].add( "first value" );
    collections[0].add( "second value" );
    collections[1].add( "third value" );
    collections[1].add( "fourth value" );
    
    //
    List<String> mergedList = ListUtils.mergeAll( collections );
    assertEquals( 4, mergedList.size() );
    assertEquals( collections[0].toArray()[0], mergedList.get( 0 ) );
    assertEquals( collections[0].toArray()[1], mergedList.get( 1 ) );
    assertEquals( collections[1].toArray()[0], mergedList.get( 2 ) );
    assertEquals( collections[1].toArray()[1], mergedList.get( 3 ) );
  }
  
}
