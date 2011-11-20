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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link DurationCapture} will measure time intervals.
 * 
 * @see #newInstance()
 * @see DurationCaptureTypeFactory
 * @author Omnaest
 */
public class DurationCapture
{
  
  /* ********************************************** Variables ********************************************** */
  
  protected final Map<Object, Interval> intervalKeyToIntervalMap = new ConcurrentHashMap<Object, DurationCapture.Interval>();
  protected final Object                intervalDefaultKey       = "DEFAULT";
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Saves the interval points and their data like name and time.
   * 
   * @author Omnaest
   */
  public static class Interval
  {
    /* ********************************************** Variables ********************************************** */
    protected Object key       = null;
    protected long   duration  = 0l;
    
    protected long   startTime = 0;
    protected long   stopTime  = 0;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * Returns the duration in milliseconds which has passed since the start of the time measurement and now.
     */
    public long getInterimTimeInMilliseconds()
    {
      return System.currentTimeMillis() - this.startTime;
    }
    
    /**
     * Starts the measurement of time for this {@link Interval}.
     */
    public void startMeasurement()
    {
      //
      this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Stops the time measurement for this {@link Interval}.
     */
    public void stopMeasurement()
    {
      //
      this.stopTime = System.currentTimeMillis();
      
      //
      this.calculateDurationInMilliseconds();
    }
    
    /**
     * Resets the internal timer of this time measurement for this {@link Interval}.
     */
    public void reset()
    {
      this.duration = 0;
    }
    
    /**
     * Calculates the {@link #duration} field based on the {@link #startTime} and {@link #stopTime}.
     */
    protected void calculateDurationInMilliseconds()
    {
      this.duration += this.stopTime - this.startTime;
    }
    
    /**
     * @param key
     * @return this
     */
    protected Interval setKey( Object key )
    {
      //
      this.key = key;
      
      //
      return this;
    }
    
    public long getDurationInMilliseconds()
    {
      return this.duration;
    }
    
    public Object getKey()
    {
      return this.key;
    }
    
  }
  
  /**
   * Internal class for statistical data calculated for the {@link Interval} instances.
   * 
   * @see DurationCapture
   * @author Omnaest
   */
  protected static class IntervalStatistic
  {
    /* ********************************************** Variables ********************************************** */
    protected Interval interval               = null;
    protected long     durationInMilliseconds = 0;
    protected double   durationPercentage     = 0.0;
    
    /* ********************************************** Methods ********************************************** */
    public Interval getInterval()
    {
      return this.interval;
    }
    
    public void setInterval( Interval interval )
    {
      this.interval = interval;
    }
    
    public String getIntervalKeyAsString()
    {
      return String.valueOf( this.interval.getKey() );
    }
    
    public double getDurationPercentage()
    {
      return this.durationPercentage;
    }
    
    public void setDurationPercentage( double durationPercentage )
    {
      this.durationPercentage = durationPercentage;
    }
    
    public long getDurationInMilliseconds()
    {
      return this.durationInMilliseconds;
    }
    
    public void setDurationInMilliseconds( long durationInMilliseconds )
    {
      this.durationInMilliseconds = durationInMilliseconds;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Use {@link DurationCapture#newInstance()} for creating an instance of this class.
   */
  protected DurationCapture()
  {
  }
  
  /**
   * Starts the time measurement which will relate to the given key.
   * 
   * @param intervalKey
   * @return this
   */
  public DurationCapture startTimeMeasurement( Object intervalKey )
  {
    //
    this.determineInterval( intervalKey ).startMeasurement();
    
    //
    return this;
  }
  
  /**
   * Returns an {@link Interval} for the given key.
   * 
   * @param key
   * @return
   */
  protected Interval determineInterval( Object key )
  {
    //
    Interval retval = null;
    
    //
    if ( key == null )
    {
      key = this;
    }
    
    //
    if ( !this.intervalKeyToIntervalMap.containsKey( key ) )
    {
      this.intervalKeyToIntervalMap.put( key, new Interval().setKey( key ) );
    }
    
    //
    retval = this.intervalKeyToIntervalMap.get( key );
    
    //
    return retval;
  }
  
  /**
   * Starts the measurement of time.
   * 
   * @return
   */
  public DurationCapture startTimeMeasurement()
  {
    //
    this.determineInterval( this.intervalDefaultKey ).startMeasurement();
    
    //
    return this;
  }
  
  /**
   * Sets the timer of the default {@link Interval} back to zero.
   */
  public void resetTimer()
  {
    this.determineInterval( this.intervalDefaultKey ).reset();
  }
  
  /**
   * Resets the timer of all contained {@link Interval} instances.
   */
  public void resetTimers()
  {
    //
    for ( Object intervalKey : this.intervalKeyToIntervalMap.keySet() )
    {
      //
      Interval interval = this.intervalKeyToIntervalMap.get( intervalKey );
      
      //
      interval.reset();
    }
  }
  
  /**
   * Stops the measurement of the time for the default {@link Interval}.
   * 
   * @return
   */
  public DurationCapture stopTimeMeasurement()
  {
    //
    this.determineInterval( this.intervalDefaultKey ).stopMeasurement();
    
    //
    return this;
  }
  
  /**
   * Stops the time measurement for the respective {@link Interval}.
   * 
   * @param intervalKey
   * @return this
   */
  public DurationCapture stopTimeMeasurement( Object intervalKey )
  {
    //
    this.determineInterval( intervalKey ).stopMeasurement();
    
    //
    return this;
  }
  
  /**
   * Calculates the statistical data for the interval and returns a map with the intervalKeys and a {@link IntervalStatistic}
   * instance.
   */
  protected Map<Object, IntervalStatistic> calculateIntervalStatisticMap()
  {
    //
    Map<Object, IntervalStatistic> retmap = new LinkedHashMap<Object, DurationCapture.IntervalStatistic>();
    
    //
    long durationInMillisecondsSum = 0;
    for ( Object intervalKey : this.intervalKeyToIntervalMap.keySet() )
    {
      //
      Interval interval = this.intervalKeyToIntervalMap.get( intervalKey );
      
      //
      if ( interval.getKey() != this.intervalDefaultKey )
      {
        //
        long durationInMilliseconds = interval.getDurationInMilliseconds();
        
        //
        durationInMillisecondsSum += durationInMilliseconds;
      }
    }
    
    //  
    List<Object> intervalKeyList = new ArrayList<Object>( this.intervalKeyToIntervalMap.keySet() );
    Collections.sort( intervalKeyList, new Comparator<Object>()
    {
      @Override
      public int compare( Object o1, Object o2 )
      {
        return String.valueOf( o1 ).compareTo( String.valueOf( o2 ) );
      }
    } );
    
    //
    for ( Object intervalKey : intervalKeyList )
    {
      //
      Interval interval = this.intervalKeyToIntervalMap.get( intervalKey );
      
      //
      IntervalStatistic intervalStatistic = new IntervalStatistic();
      {
        //
        long durationInMilliseconds = interval.getDurationInMilliseconds();
        
        //
        double durationPercentage = ( durationInMilliseconds * 100.0 ) / durationInMillisecondsSum;
        
        //
        intervalStatistic.setDurationPercentage( durationPercentage );
        intervalStatistic.setInterval( interval );
        intervalStatistic.setDurationInMilliseconds( durationInMilliseconds );
      }
      
      //
      retmap.put( interval.getKey(), intervalStatistic );
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns the needed time between measurement start and stop in milliseconds.
   * 
   * @see #getDurationInMilliseconds(Object)
   * @return
   */
  public long getDurationInMilliseconds()
  {
    return this.getDurationInMilliseconds( this.intervalDefaultKey );
  }
  
  /**
   * Returns the needed time between measurement start and stop in milliseconds.
   * 
   * @param intervalKey
   * @see #getDurationInMilliseconds()
   * @return
   */
  public long getDurationInMilliseconds( Object intervalKey )
  {
    return this.determineInterval( intervalKey ).getDurationInMilliseconds();
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds.
   * 
   * @return
   */
  public long getInterimTimeInMilliseconds()
  {
    return this.getInterimTimeInMilliseconds( this.intervalDefaultKey );
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds.
   * 
   * @return
   */
  public long getInterimTimeInMilliseconds( Object intervalKey )
  {
    return this.determineInterval( intervalKey ).getInterimTimeInMilliseconds();
  }
  
  /**
   * Returns true, if there is an {@link Interval} instance for the {@link #intervalDefaultKey}.
   * 
   * @return
   */
  protected boolean hasDefaultInterval()
  {
    return this.intervalKeyToIntervalMap.containsKey( this.intervalDefaultKey );
  }
  
  /**
   * Returns the collected statistical informations for the intervals durations.
   * 
   * @return
   */
  public String calculateIntervalStatisticLogMessage()
  {
    String retval = null;
    StringBuffer sb = new StringBuffer();
    final String lineSeparator = "-------------------------------------------------------------------------------------------------\n";
    
    //    
    Map<Object, IntervalStatistic> intervalStatisticMap = this.calculateIntervalStatisticMap();
    
    //
    if ( intervalStatisticMap.containsKey( this.intervalDefaultKey ) )
    {
      //
      IntervalStatistic intervalStatistic = intervalStatisticMap.get( this.intervalDefaultKey );
      
      //
      sb.append( lineSeparator );
      sb.append( String.format( "%s : %d ms (%3.2f%%)\n", intervalStatistic.getIntervalKeyAsString(),
                                intervalStatistic.getDurationInMilliseconds(), intervalStatistic.getDurationPercentage() ) );
      
      //
      intervalStatisticMap.remove( this.intervalDefaultKey );
    }
    
    //    
    long intervalDurationTimeSum = 0;
    if ( intervalStatisticMap.size() > 0 )
    {
      //
      sb.append( lineSeparator );
      for ( Object intervalKey : intervalStatisticMap.keySet() )
      {
        //
        IntervalStatistic intervalStatistic = intervalStatisticMap.get( intervalKey );
        
        //
        sb.append( String.format( "%s : %d ms (%3.2f%%)\n", intervalStatistic.getIntervalKeyAsString(),
                                  intervalStatistic.getDurationInMilliseconds(), intervalStatistic.getDurationPercentage() ) );
        
        //
        intervalDurationTimeSum += intervalStatistic.getDurationInMilliseconds();
      }
    }
    
    //summary
    if ( intervalStatisticMap.size() > 1 )
    {
      sb.append( lineSeparator );
      sb.append( "Whole interval duration time: " + intervalDurationTimeSum + " ms\n" );
    }
    
    //
    sb.append( lineSeparator );
    
    //return
    retval = sb.toString();
    return retval;
  }
  
  /**
   * Returns all available {@link Interval} keys.
   * 
   * @return
   */
  public List<Object> getIntervalKeyList()
  {
    return new ArrayList<Object>( this.intervalKeyToIntervalMap.keySet() );
  }
  
  /**
   * Returns a new {@link Map} instance with all {@link Interval} keys and the related {@link #getDurationInMilliseconds(Object)}
   * as value
   * 
   * @return
   */
  public Map<Object, Long> getIntervalKeyToDurationInMillisecondsMap()
  {
    //    
    Map<Object, Long> retmap = new LinkedHashMap<Object, Long>();
    
    //
    for ( Object intervalKey : this.getIntervalKeyList() )
    {
      retmap.put( intervalKey, this.getDurationInMilliseconds( intervalKey ) );
    }
    
    //
    return retmap;
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
  public static DurationCapture newInstance()
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
  
  @Override
  public String toString()
  {
    return this.calculateIntervalStatisticLogMessage();
  }
  
}
