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
package org.omnaest.utils.operation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;
import org.omnaest.utils.structure.collection.CollectionUtils.CollectionConverter;

public class ForEachTest
{
  
  @SuppressWarnings("unchecked")
  private Iterable<String>[] iterables = new Iterable[] { Arrays.asList( "a", "b" ), Arrays.asList( "c", "d" ) };
  
  @Test
  public void testExecuteOperationOfOECollectionConverterOfOR()
  {
    //
    final int[] executedCounter = new int[] { 0 };
    
    //    
    @SuppressWarnings("unchecked")
    Boolean retval = new ForEach<String>( this.iterables ).execute( new CollectionConverter<Boolean, Boolean>()
    {
      /* ********************************************** Variables ********************************************** */
      private boolean localRetval = true;
      
      /* ********************************************** Methods ********************************************** */
      @Override
      public Boolean result()
      {
        return this.localRetval;
      }
      
      @Override
      public void process( Boolean element )
      {
        this.localRetval &= element;
      }
    }, new Operation<Boolean, String>()
    {
      private Iterator<String> iterator = Arrays.asList( "a", "b", "c", "d" ).iterator();
      
      @Override
      public Boolean execute( String parameter )
      {
        //
        assertNotNull( parameter );
        assertEquals( this.iterator.next(), parameter );
        
        //
        executedCounter[0]++;
        
        // 
        return true;
      }
    } );
    
    //
    assertNotNull( retval );
    assertTrue( retval );
    
    //
    assertEquals( 4, executedCounter[0] );
  }
  
  @Test
  public void testExecuteOperationOfOEArray()
  {
    //
    final int[] executedCounter = new int[] { 0, 0 };
    
    //
    @SuppressWarnings("unchecked")
    Operation<Object, String>[] operations = new Operation[] { new Operation<Object, String>()
    {
      private Iterator<String> iterator = Arrays.asList( "a", "b", "c", "d" ).iterator();
      
      @Override
      public Object execute( String parameter )
      {
        //
        assertNotNull( parameter );
        assertEquals( this.iterator.next(), parameter );
        
        //
        executedCounter[0]++;
        
        //
        return null;
      }
    }, new Operation<Void, String>()
    {
      private Iterator<String> iterator = Arrays.asList( "a", "b", "c", "d" ).iterator();
      
      @Override
      public Void execute( String parameter )
      {
        //
        assertNotNull( parameter );
        assertEquals( this.iterator.next(), parameter );
        
        //
        executedCounter[1]++;
        
        // 
        return null;
      }
    } };
    new ForEach<String>( this.iterables ).execute( operations );
    
    //
    assertEquals( 4, executedCounter[0] );
    assertEquals( 4, executedCounter[1] );
  }
  
}
