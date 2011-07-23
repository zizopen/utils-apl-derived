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
package org.omnaest.utils.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.time.DurationCaptureTypeFactory.DurationCaptureAware;

/**
 * @see DurationCaptureTypeFactory
 * @author Omnaest
 */
public class DurationCaptureTypeFactoryTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  protected static class TestClass
  {
    protected DurationCapture durationCaptureInternal = DurationCapture.newInstance();
    
    public List<String> generateRandomStringList()
    {
      //
      this.durationCaptureInternal.startTimeMeasurement();
      List<String> retlist = new ArrayList<String>();
      for ( int ii = 0; ii < 100000; ii++ )
      {
        retlist.add( "value" + ii );
      }
      this.durationCaptureInternal.stopTimeMeasurement();
      
      //
      return retlist;
    }
    
    public DurationCapture getDurationCaptureInternal()
    {
      return this.durationCaptureInternal;
    }
  }
  
  @Test
  public void testDurationCapture()
  {
    //
    TestClass testClass = new TestClass();
    
    //
    TestClass stubInstance = DurationCaptureTypeFactory.newStubInstance( testClass );
    
    //
    List<String> stringListOriginal = testClass.generateRandomStringList();
    List<String> stringListStub = stubInstance.generateRandomStringList();
    
    //
    assertEquals( stringListOriginal, stringListStub );
    
    //
    assertTrue( stubInstance instanceof DurationCaptureAware );
    DurationCaptureAware durationCaptureAware = (DurationCaptureAware) stubInstance;
    DurationCapture durationCapture = durationCaptureAware.getDurationCapture();
    
    assertNotNull( durationCapture );
    
    //
    List<Object> intervalKeyList = durationCapture.getIntervalKeyList();
    assertEquals( 1, intervalKeyList.size() );
    assertEquals( "generateRandomStringList", intervalKeyList.get( 0 ) );
    
    //
    long durationInMillisecondsAroundMethod = durationCapture.getDurationInMilliseconds( "generateRandomStringList" );
    long durationInMillisecondsWithinMethod = testClass.getDurationCaptureInternal().getDurationInMilliseconds();
    
    //
    assertTrue( durationInMillisecondsWithinMethod * 3.0 > durationInMillisecondsAroundMethod );
    
    //
    //    System.out.println( durationCapture.calculateIntervalStatisticLogMessage() );
    //    System.out.println( testClass.getDurationCaptureInternal().calculateIntervalStatisticLogMessage() );
    
  }
}
