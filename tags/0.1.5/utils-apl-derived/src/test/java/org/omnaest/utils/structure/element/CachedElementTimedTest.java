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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.omnaest.utils.structure.element.CachedElement.ValueResolver;

/**
 * @see CachedElementTimed
 * @author Omnaest
 */
public class CachedElementTimedTest
{
  /* ********************************************** Variables ********************************************** */
  private Long                     validCacheDurationInMilliseconds = 50l;
  private ValueResolver<Long>      valueResolver                    = new ValueResolver<Long>()
                                                                    {
                                                                      private long counter = 0;
                                                                      
                                                                      @Override
                                                                      public Long resolveValue()
                                                                      {
                                                                        // 
                                                                        return this.counter++;
                                                                      }
                                                                    };
  private CachedElementTimed<Long> cachedElementTimed               = new CachedElementTimed<Long>(
                                                                                                    this.valueResolver,
                                                                                                    this.validCacheDurationInMilliseconds );
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testCachedElementTimed() throws InterruptedException
  {
    //
    Long value = this.cachedElementTimed.getValue();
    assertTrue( this.cachedElementTimed.getValue() == value );
    
    //
    Thread.sleep( this.validCacheDurationInMilliseconds + 10 );
    
    //
    assertTrue( this.cachedElementTimed.getValue() > value );
  }
  
}
