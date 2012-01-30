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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @see FutureSimple
 * @author Omnaest
 */
public class FutureSimpleTest
{
  
  @Test
  public void testGet()
  {
    //
    final Future<String> future = new FutureSimple<String>();
    final AtomicBoolean firstConsumptionIsNull = new AtomicBoolean( false );
    final AtomicReference<String> secondConsumptionValue = new AtomicReference<String>( null );
    final CountDownLatch countDownLatch = new CountDownLatch( 1 );
    
    //
    class Consumer implements Runnable
    {
      
      @Override
      public void run()
      {
        //        
        try
        {
          String value = future.get( 10, TimeUnit.MICROSECONDS );
          firstConsumptionIsNull.set( value == null );
          countDownLatch.countDown();
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
        
        //        
        try
        {
          String value = future.get();
          secondConsumptionValue.set( value );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
        }
        
      }
      
    }
    
    class Producer implements Runnable
    {
      @Override
      public void run()
      {
        try
        {
          //
          countDownLatch.await();
          
          //
          ( (FutureSimple<String>) future ).setValue( "test" );
        }
        catch ( InterruptedException e )
        {
          e.printStackTrace();
        }
      }
    }
    
    //
    Thread threadConsumer = new Thread( new Consumer() );
    Thread threadProducer = new Thread( new Producer() );
    
    //
    threadConsumer.start();
    threadProducer.start();
    
    //
    try
    {
      //
      threadConsumer.join();
      threadProducer.join();
    }
    catch ( Exception e )
    {
      Assert.fail( e.getMessage() );
    }
    
    //
    assertTrue( firstConsumptionIsNull.get() );
    assertNotNull( secondConsumptionValue.get() );
    assertEquals( "test", secondConsumptionValue.get() );
  }
  
}
