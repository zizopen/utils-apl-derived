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
package org.omnaest.utils.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @see ConcurrentWeakReferenceCache
 * @author Omnaest
 */
public class ConcurrentWeakReferenceCacheTest
{
  @Rule
  public ContiPerfRule                       contiPerfRule = new ContiPerfRule();
  
  /* ********************************************** Beans / Services / References ********************************************** */
  private final static Cache<String, Object> cache         = new ConcurrentWeakReferenceCache<String, Object>();
  
  /* ********************************************** Methods ********************************************** */
  
  @PerfTest(invocations = 200, threads = 5)
  @Test
  public void testPutAndGetMultithreaded()
  {
    //
    long id = Math.round( Math.random() * 1000 );
    String key = "key" + id;
    Object value = "value" + id;
    
    //
    cache.put( key + "", value );
    
    //
    final int numberOfCacheRequests = 1000;
    int cacheHits = 0;
    for ( int ii = 0; ii < numberOfCacheRequests; ii++ )
    {
      //Resolve elements from cache could actually fail if the jvm does clear the cache exactly at this point of time
      Object object = cache.get( key );
      if ( object != null )
      {
        assertEquals( value, object );
        cacheHits++;
      }
      else
      {
        cache.put( key, value );
      }
    }
    assertTrue( cacheHits > 0 );
  }
}
