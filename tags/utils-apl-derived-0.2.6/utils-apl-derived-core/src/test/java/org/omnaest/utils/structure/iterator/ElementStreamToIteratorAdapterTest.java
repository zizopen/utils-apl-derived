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
package org.omnaest.utils.structure.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.omnaest.utils.structure.element.ElementStream;

/**
 * @see ElementStreamToIteratorAdapter
 * @author Omnaest
 */
public class ElementStreamToIteratorAdapterTest
{
  /* ********************************************** Variables ********************************************** */
  private List<String>                           valueList                      = Arrays.asList( "value1", "value2", "value3" );
  private Iterator<String>                       valueIterator                  = this.valueList.iterator();
  private ElementStream<String>                  elementStream                  = new ElementStream<String>()
                                                                                {
                                                                                  private Iterator<String> iterator = ElementStreamToIteratorAdapterTest.this.valueList.iterator();
                                                                                  
                                                                                  @Override
                                                                                  public String next()
                                                                                  {
                                                                                    return this.iterator.hasNext() ? this.iterator.next()
                                                                                                                  : null;
                                                                                  }
                                                                                };
  private ElementStreamToIteratorAdapter<String> elementStreamToIteratorAdapter = new ElementStreamToIteratorAdapter<String>(
                                                                                                                              this.elementStream );
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testNext()
  {
    while ( this.valueIterator.hasNext() )
    {
      assertTrue( this.elementStreamToIteratorAdapter.hasNext() );
      assertEquals( this.valueIterator.next(), this.elementStreamToIteratorAdapter.next() );
    }
    assertFalse( this.elementStreamToIteratorAdapter.hasNext() );
  }
  
}
