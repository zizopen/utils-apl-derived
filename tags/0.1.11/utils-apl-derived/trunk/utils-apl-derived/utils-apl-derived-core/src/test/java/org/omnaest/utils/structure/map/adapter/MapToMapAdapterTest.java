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
package org.omnaest.utils.structure.map.adapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Test;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.map.adapter.MapToMapAdapter;

public class MapToMapAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private Map<Integer, Double>              sourceMap                            = new LinkedHashMap<Integer, Double>();
  private ElementConverter<Integer, String> elementConverterKeySourceToAdapter   = new ElementConverter<Integer, String>()
                                                                                 {
                                                                                   @Override
                                                                                   public String convert( Integer element )
                                                                                   {
                                                                                     return element != null ? String.valueOf( element )
                                                                                                           : null;
                                                                                   }
                                                                                 };
  private ElementConverter<String, Integer> elementConverterKeyAdapterToSource   = new ElementConverter<String, Integer>()
                                                                                 {
                                                                                   
                                                                                   @Override
                                                                                   public Integer convert( String element )
                                                                                   {
                                                                                     return Integer.valueOf( element );
                                                                                   }
                                                                                 };
  private ElementConverter<Double, String>  elementConverterValueSourceToAdapter = new ElementConverter<Double, String>()
                                                                                 {
                                                                                   
                                                                                   @Override
                                                                                   public String convert( Double element )
                                                                                   {
                                                                                     return element != null ? String.valueOf( element )
                                                                                                           : null;
                                                                                   }
                                                                                 };
  private ElementConverter<String, Double>  elementConverterValueAdapterToSource = new ElementConverter<String, Double>()
                                                                                 {
                                                                                   
                                                                                   @Override
                                                                                   public Double convert( String element )
                                                                                   {
                                                                                     return Double.valueOf( element );
                                                                                   }
                                                                                 };
  private Map<String, String>               map                                  = new MapToMapAdapter<Integer, Double, String, String>(
                                                                                                                                         this.sourceMap,
                                                                                                                                         this.elementConverterKeySourceToAdapter,
                                                                                                                                         this.elementConverterKeyAdapterToSource,
                                                                                                                                         this.elementConverterValueSourceToAdapter,
                                                                                                                                         this.elementConverterValueAdapterToSource );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testGet()
  {
    //
    this.sourceMap.put( 1, 1.234 );
    this.sourceMap.put( 2, 2.234 );
    
    //
    assertEquals( "1.234", this.map.get( "1" ) );
    assertEquals( "2.234", this.map.get( "2" ) );
  }
  
  @Test
  public void testPut()
  {
    //
    this.map.put( "1", "1.234" );
    this.map.put( "2", "2.234" );
    
    //
    assertEquals( 1.234, this.sourceMap.get( 1 ), 0.001 );
    assertEquals( 2.234, this.sourceMap.get( 2 ), 0.001 );
  }
  
  @Test
  public void testRemove()
  {
    //
    this.map.put( "1", "1.234" );
    this.map.put( "2", "2.234" );
    assertTrue( this.map.containsKey( "1" ) );
    
    //
    String removedValue = this.map.remove( "1" );
    assertEquals( "1.234", removedValue );
    assertFalse( this.map.containsKey( "1" ) );
  }
  
  @Test
  public void testKeySet()
  {
    //
    this.map.put( "1", "1.234" );
    this.map.put( "2", "2.234" );
    
    //
    assertEquals( new LinkedHashSet<String>( Arrays.asList( "1", "2" ) ), this.map.keySet() );
  }
  
  @Test
  public void testValues()
  {
    //
    this.map.put( "1", "1.234" );
    this.map.put( "2", "2.234" );
    
    //
    assertEquals( new ArrayList<String>( Arrays.asList( "1.234", "2.234" ) ), this.map.values() );
  }
  
}
