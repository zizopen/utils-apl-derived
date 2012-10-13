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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.xml.JAXBXMLHelper;

public class DurationCaptureTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testXMLSerialization() throws InterruptedException
  {
    DurationCapture durationCapture = new DurationCapture().startTimeMeasurement();
    DurationCapture cloneObject = JAXBXMLHelper.cloneObject( durationCapture );
    Thread.sleep( 10 );
    assertTrue( cloneObject.stopTimeMeasurement().getDurationInMilliseconds() > 0 );
  }
  
  @Test
  public void testObjectSerialization() throws InterruptedException
  {
    DurationCapture durationCapture = new DurationCapture().startTimeMeasurement();
    DurationCapture cloneObject = SerializationUtils.clone( durationCapture );
    Thread.sleep( 10 );
    final long durationInMilliseconds = cloneObject.stopTimeMeasurement().getDurationInMilliseconds();
    //System.out.println( durationInMilliseconds );
    //System.out.println( durationCapture );
    assertTrue( durationInMilliseconds > 0 );
  }
  
  @Test
  public void testTimeMeasurement()
  {
    //
    DurationCapture durationCapture = DurationCapture.newInstance();
    
    //
    durationCapture.startTimeMeasurement();
    
    List<String> tempStringList = new ArrayList<String>();
    for ( int ii = 0; ii < 10000000 && durationCapture.getInterimTimeInMilliseconds() < 100; ii++ )
    {
      //
      durationCapture.startTimeMeasurement( "Interval1" );
      tempStringList.add( "value1" + ii );
      durationCapture.stopTimeMeasurement( "Interval1" );
      
      //
      durationCapture.startTimeMeasurement( "Interval2" );
      tempStringList.add( "value2" + ii );
      durationCapture.stopTimeMeasurement( "Interval2" );
      
      //
      durationCapture.startTimeMeasurement( "Interval999" );
      tempStringList.add( "value3" + ii );
      durationCapture.stopTimeMeasurement( "Interval999" );
    }
    durationCapture.stopTimeMeasurement();
    
    //
    long duration = durationCapture.getDurationInMilliseconds();
    assertTrue( duration > 0 );
    
    //
    long duration2 = durationCapture.getDuration( TimeUnit.MICROSECONDS );
    assertEquals( duration, duration2 / 1000 );
    
    //
    Map<Object, Long> intervalKeyToDurationMap = durationCapture.getIntervalKeyToDurationMap( TimeUnit.MICROSECONDS );
    assertEquals( 4, intervalKeyToDurationMap.keySet().size() );
    
    //
    //System.out.println( durationCapture.calculateIntervalStatisticLogMessage() );
  }
  
}
