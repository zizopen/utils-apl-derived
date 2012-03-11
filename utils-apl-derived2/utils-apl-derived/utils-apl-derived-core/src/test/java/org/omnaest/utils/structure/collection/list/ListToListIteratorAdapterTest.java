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
package org.omnaest.utils.structure.collection.list;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.junit.Before;
import org.junit.Test;

public class ListToListIteratorAdapterTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testListToListIteratorAdapter()
  {
    //
    List<String> naturalValueList = new ArrayList<String>();
    List<String> adapterValueList = new ArrayList<String>();
    
    for ( int ii = 0; ii < 10; ii++ )
    {
      naturalValueList.add( "value" + ii );
    }
    adapterValueList.addAll( naturalValueList );
    
    assertEquals( naturalValueList, adapterValueList );
    
    //
    ListIterator<String> naturalListIterator = naturalValueList.listIterator();
    ListIterator<String> adapterListIterator = new ListToListIteratorAdapter<String>( adapterValueList );
    
    //iteration next, previous
    while ( naturalListIterator.hasNext() )
    {
      assertEquals( true, adapterListIterator.hasNext() );
      assertEquals( naturalListIterator.next(), adapterListIterator.next() );
      assertEquals( naturalListIterator.previousIndex(), adapterListIterator.previousIndex() );
      assertEquals( naturalListIterator.nextIndex(), adapterListIterator.nextIndex() );
    }
    
    while ( naturalListIterator.hasPrevious() )
    {
      assertEquals( true, adapterListIterator.hasPrevious() );
      assertEquals( naturalListIterator.previous(), adapterListIterator.previous() );
    }
    
    assertEquals( naturalValueList, adapterValueList );
    
    //remove    
    int counter = 0;
    while ( naturalListIterator.hasNext() )
    {
      assertEquals( true, adapterListIterator.hasNext() );
      assertEquals( naturalListIterator.next(), adapterListIterator.next() );
      
      if ( counter++ % 3 == 0 )
      {
        naturalListIterator.remove();
        adapterListIterator.remove();
      }
    }
    
    assertEquals( naturalValueList, adapterValueList );
    
    while ( naturalListIterator.hasPrevious() )
    {
      assertEquals( true, adapterListIterator.hasPrevious() );
      assertEquals( naturalListIterator.previous(), adapterListIterator.previous() );
    }
    
    assertEquals( naturalValueList, adapterValueList );
    
    //add,set
    counter = 0;
    while ( naturalListIterator.hasNext() )
    {
      assertEquals( true, adapterListIterator.hasNext() );
      assertEquals( naturalListIterator.next(), adapterListIterator.next() );
      
      if ( counter++ % 3 == 0 )
      {
        naturalListIterator.add( "added" + counter );
        adapterListIterator.add( "added" + counter );
      }
    }
    
    assertEquals( naturalValueList, adapterValueList );
    
    while ( naturalListIterator.hasPrevious() )
    {
      assertEquals( true, adapterListIterator.hasPrevious() );
      assertEquals( naturalListIterator.previous(), adapterListIterator.previous() );
      
      if ( counter-- % 3 == 2 )
      {
        naturalListIterator.set( "set" + counter );
        adapterListIterator.set( "set" + counter );
      }
    }
    naturalListIterator.set( "set" + counter );
    adapterListIterator.set( "set" + counter );
    
    assertEquals( naturalValueList, adapterValueList );
    
  }
}
