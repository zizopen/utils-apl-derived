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
package org.omnaest.utils.structure.array;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ArrayToListAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private String[]     array = new String[] { "a", "b", "c", "d", "a" };
  private List<String> list  = new ArrayToListAdapter<String>( this.array );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testGetAndSet()
  {
    //
    assertEquals( this.array.length, this.list.size() );
    
    //
    assertEquals( "a", this.list.get( 0 ) );
    assertEquals( "b", this.list.get( 1 ) );
    assertEquals( "c", this.list.get( 2 ) );
    assertEquals( "d", this.list.get( 3 ) );
    assertEquals( "a", this.list.get( 4 ) );
    
    //
    assertEquals( Arrays.asList( this.array ), this.list );
    
    //
    this.list.set( 2, "x" );
    
    //
    assertEquals( "a", this.list.get( 0 ) );
    assertEquals( "b", this.list.get( 1 ) );
    assertEquals( "x", this.list.get( 2 ) );
    assertEquals( "d", this.list.get( 3 ) );
    assertEquals( "a", this.list.get( 4 ) );
    
    //
    assertEquals( 0, this.list.indexOf( "a" ) );
    assertEquals( 4, this.list.lastIndexOf( "a" ) );
    assertEquals( -1, this.list.lastIndexOf( "y" ) );
  }
  
}
