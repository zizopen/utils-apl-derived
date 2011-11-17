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
package org.omnaest.utils.structure.map;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

/**
 * @see CaseinsensitiveMapDecorator
 * @author Omnaest
 */
public class CaseinsensitiveMapDecoratorTest
{
  /* ********************************************** Variables ********************************************** */
  private final Map<String, Object> map          = new MapBuilder<String, Object>().linkedHashMap()
                                                                                   .put( "kEy0", "value0" )
                                                                                   .put( "key1", "value1" )
                                                                                   .put( "KEY2", "value2" )
                                                                                   .put( "Key3", "value3" )
                                                                                   .put( "kEy4", "value4" )
                                                                                   .build();
  
  private final Map<String, Object> mapDecorator = new CaseinsensitiveMapDecorator<Object>( this.map );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testGet()
  {
    assertEquals( this.mapDecorator, this.map );
    
    assertEquals( "value0", this.mapDecorator.get( "kEy0" ) );
    assertEquals( "value1", this.mapDecorator.get( "KeY1" ) );
    assertEquals( "value2", this.mapDecorator.get( "key2" ) );
    assertEquals( "value3", this.mapDecorator.get( "key3" ) );
    assertEquals( "value4", this.mapDecorator.get( "key4" ) );
  }
}
