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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.structure.element.cached.CachedElement.ValueResolver;

/**
 * @see ThreadLocalCachedElement
 * @author Omnaest
 */
public class ThreadLocalCachedElementTest
{
  /* ********************************************** Variables ********************************************** */
  private ThreadLocalCachedElement<String> threadLocalCachedElement = new ThreadLocalCachedElement<String>(
                                                                                                            new ValueResolver<String>()
                                                                                                            {
                                                                                                              @Override
                                                                                                              public String resolveValue()
                                                                                                              {
                                                                                                                return Thread.currentThread()
                                                                                                                             .getName();
                                                                                                              }
                                                                                                            } );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testGetValue() throws InterruptedException
  {
    //
    final Set<String> threadNameSet = new HashSet<String>();
    final Set<Thread> threadSet = new HashSet<Thread>();
    
    //
    for ( int ii = 0; ii < 5; ii++ )
    {
      //
      Thread thread = new Thread( new Runnable()
      {
        @Override
        public void run()
        {
          String value = ThreadLocalCachedElementTest.this.threadLocalCachedElement.getValue();
          threadNameSet.add( value );
        }
      }, "thread" + ii );
      
      //
      thread.start();
      threadSet.add( thread );
    }
    
    //
    for ( Thread thread : threadSet )
    {
      thread.join();
    }
    
    //
    assertEquals( 5, threadNameSet.size() );
    
    //
    for ( int ii = 0; ii < 5; ii++ )
    {
      assertEquals( true, threadNameSet.contains( "thread" + ii ) );
    }
  }
  
}
