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

import java.util.ArrayList;

/**
 * A {@link DurationCapture} will measure time intervals.
 * 
 * @author Omnaest
 */
public class DurationCapture
{
  /* ********************************************** Variables ********************************************** */

  private long                startTime         = 0;
  private long                stopTime          = 0;
  private long                intervalTime      = 0;
  private long                duration          = 0;
  private boolean             measurementActive = false;
  
  private ArrayList<Interval> intervalList      = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Saves the interval points and their data like name and time.
   * 
   * @author Omnaest
   */
  private class Interval
  {
    private String name       = null;
    private Long   duration   = null;
    private int    percentage = 0;
    
    public String getName()
    {
      return name;
    }
    
    public void setName( String name )
    {
      this.name = name;
    }
    
    public Long getDuration()
    {
      return duration;
    }
    
    public void setDuration( Long duration )
    {
      this.duration = duration;
    }
    
    public int getPercentage()
    {
      return percentage;
    }
    
    public void setPercentage( int percentage )
    {
      this.percentage = percentage;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Starts the measurement of time.
   * 
   * @return
   */
  public DurationCapture startTimeMeasurement()
  {
    this.resetTimers();
    this.initializeIntervalMap();
    this.startTime = System.currentTimeMillis();
    this.measurementActive = true;
    return this;
  }
  
  /**
   * Sets all timers back to default, what is zero.
   */
  private void resetTimers()
  {
    this.startTime = 0;
    this.stopTime = 0;
    this.intervalTime = 0;
    this.duration = 0;
    this.measurementActive = false;
  }
  
  /**
   * Stops the measurement of the time.
   * 
   * @return
   */
  public DurationCapture stopTimeMeasurement()
  {
    stopTime = System.currentTimeMillis();
    duration = stopTime - startTime;
    this.calculateIntervalStatistics();
    this.measurementActive = false;
    return this;
  }
  
  /**
   * Calculates the statistical data for the interval
   */
  private void calculateIntervalStatistics()
  {
    if ( this.intervalList != null )
    {
      for ( Interval iInterval : this.intervalList )
      {
        int percentage = 0;
        if ( this.duration > 0 )
        {
          percentage = Math.round( ( iInterval.duration * 100 ) / this.duration );
        }
        iInterval.setPercentage( percentage );
      }
    }
  }
  
  /**
   * Returns the needed time between measurement start and stop in milliseconds.
   * 
   * @return
   */
  public long getDuration()
  {
    return duration;
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds.
   * 
   * @return
   */
  public long getInterimTime()
  {
    return System.currentTimeMillis() - this.startTime;
  }
  
  /**
   * Sets the intervaltimer to zero
   */
  public void startIntervalTime()
  {
    this.intervalTime = System.currentTimeMillis();
  }
  
  /**
   * Sets a stop point for the interval time.
   * 
   * @param intervalName
   */
  public void saveIntervalTime( String intervalName )
  {
    if ( this.intervalList == null )
    {
      this.initializeIntervalMap();
    }
    long newIntervalTime = System.currentTimeMillis();
    long duration = newIntervalTime - this.intervalTime;
    
    //if there is already an interval with the same name, update the existing one, else create a new one
    if ( this.intervalList.contains( intervalName ) )
    {
      Interval interval = this.intervalList.get( this.intervalList.indexOf( intervalName ) );
      interval.setDuration( interval.getDuration() + duration );
    }
    else
    {
      Interval interval = new Interval();
      interval.setName( intervalName );
      interval.setDuration( duration );
      this.intervalList.add( interval );
    }
    //
    this.intervalTime = newIntervalTime;
  }
  
  /**
   * Returns the collected informations from the intervals.
   * 
   * @return
   */
  public String getIntervalMapLog()
  {
    String retval = null;
    StringBuffer sb = new StringBuffer();
    final String lineSeparator = "-------------------------------------------------------------------------------------------------\n";
    
    //intervals
    if ( this.intervalList != null )
    {
      sb.append( lineSeparator );
      for ( Interval iInterval : this.intervalList )
      {
        sb.append( iInterval.getName() + " : " + iInterval.getDuration() + " ms (" + iInterval.getPercentage() + "%)\n" );
      }
    }
    
    //summary
    sb.append( lineSeparator );
    sb.append( "Whole duration time: " + this.duration + " ms\n" );
    sb.append( lineSeparator );
    
    //return
    retval = sb.toString();
    return retval;
  }
  
  /**
   * Creates a intervalmap object, or clears the available object
   */
  public void initializeIntervalMap()
  {
    if ( this.intervalList == null )
    {
      this.intervalList = new ArrayList<Interval>( 0 );
    }
    this.intervalList.clear();
  }
  
  public boolean isMeasurementActive()
  {
    return measurementActive;
  }
  
  /* ********************************************** STATIC FACTORY METHOD PART ********************************************** */

  /**
   * Used by the factory method.
   * 
   * @see #createNewInstance
   */
  public static Class<? extends DurationCapture> implementationForDurationClass = DurationCapture.class;
  
  /**
   * Creates a new instance of this class.
   * 
   * @see #implementationForDurationClass
   */
  public static DurationCapture createNewInstance()
  {
    DurationCapture result = null;
    
    try
    {
      result = DurationCapture.implementationForDurationClass.newInstance();
    }
    catch ( Exception e )
    {
    }
    
    return result;
  }
}
