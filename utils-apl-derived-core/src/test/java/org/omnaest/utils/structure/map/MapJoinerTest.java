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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.KeyExtractor;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.MapJoiner.JoinedValue;
import org.omnaest.utils.structure.map.MapJoiner.Predicate;

/**
 * @see MapJoiner
 * @author Omnaest
 */
public class MapJoinerTest
{
  
  @Test
  public void testNewInstance()
  {
    //
    final Map<String, Integer> map1 = new MapBuilder<String, Integer>().linkedHashMap()
                                                                       .put( "key1", 1 )
                                                                       .put( "key2", 2 )
                                                                       .put( "key3", 3 )
                                                                       .build();
    final Map<String, Long> map2 = new MapBuilder<String, Long>().linkedHashMap()
                                                                 .put( "key1", 1l )
                                                                 .put( "key2", 2l )
                                                                 .put( "key4", 4l )
                                                                 .put( "key6", 6l )
                                                                 .put( "key7", 7l )
                                                                 .build();
    final List<String> list = Arrays.asList( "1", "2", "3", "5", "7" );
    final KeyExtractor<String, String> keyExtractor = new KeyExtractor<String, String>()
    {
      private static final long serialVersionUID = 17485934L;
      
      @Override
      public String extractKey( String element )
      {
        return "key" + element;
      }
    };
    final Predicate<String, JoinedValue<Integer, Long>, String> predicate = new MapJoiner.PredicateIncludingKeySet<String, JoinedValue<Integer, Long>, String>(
                                                                                                                                                                SetUtils.convert( list,
                                                                                                                                                                                  new ElementConverter<String, String>()
                                                                                                                                                                                  {
                                                                                                                                                                                    @Override
                                                                                                                                                                                    public String convert( String element )
                                                                                                                                                                                    {
                                                                                                                                                                                      // 
                                                                                                                                                                                      return "key"
                                                                                                                                                                                             + element;
                                                                                                                                                                                    }
                                                                                                                                                                                  } ) );
    Map<String, JoinedValue<JoinedValue<Integer, Long>, String>> map = new MapJoiner().from( map1 )
                                                                                      .joinInner( map2 )
                                                                                      .joinInner( keyExtractor, list )
                                                                                      .where( predicate )
                                                                                      .getResultMap();
    assertNotNull( map );
    assertEquals( 2, map.size() );
    assertEquals( "key1", MapUtils.firstEntry( map ).getKey() );
    assertEquals( "key2", MapUtils.entryAt( map, 1 ).getKey() );
    assertEquals( 1, MapUtils.firstEntry( map ).getValue().getValueFirst().getValueFirst().intValue() );
    assertEquals( 1, MapUtils.firstEntry( map ).getValue().getValueFirst().getValueSecond().intValue() );
    assertEquals( "1", MapUtils.firstEntry( map ).getValue().getValueSecond() );
  }
  
}
