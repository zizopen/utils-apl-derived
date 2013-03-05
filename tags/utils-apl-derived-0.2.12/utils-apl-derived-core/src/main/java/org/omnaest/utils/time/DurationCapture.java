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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.strings.StringUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectToString;

/**
 * A {@link DurationCapture} will measure time intervals.
 * 
 * @see #newInstance()
 * @see DurationCaptureTypeFactory
 * @author Omnaest
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class DurationCapture implements Serializable
{
  /* ********************************************** Constants ********************************************** */
  public static final Object            INTERVAL_DEFAULTKEY      = "DEFAULT";
  
  private static final long             serialVersionUID         = 6433066272269919387L;
  private static final String           DEFAULT_LINESEPARATOR    = System.getProperty( "line.separator" );
  
  /* ********************************************** Variables ********************************************** */
  protected final Map<Object, Interval> intervalKeyToIntervalMap = new ConcurrentHashMap<Object, DurationCapture.Interval>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Saves the interval points and their data like name and time.
   * 
   * @author Omnaest
   */
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Interval implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID = -2883159459488305456L;
    /* ********************************************** Variables ********************************************** */
    protected Object          key              = null;
    protected long            duration         = 0l;
    
    protected long            startTime        = 0;
    protected long            stopTime         = 0;
    
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
  @XmlAccessorType(XmlAccessType.FIELD)
  protected static class IntervalStatistic implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long serialVersionUID       = -5081536693492826365L;
    /* ********************************************** Variables ********************************************** */
    protected Interval        interval               = null;
    protected long            durationInMilliseconds = 0;
    protected double          durationPercentage     = 0.0;
    
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
   * @see DurationCapture
   */
  public DurationCapture()
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
    this.determineInterval( DurationCapture.INTERVAL_DEFAULTKEY ).startMeasurement();
    
    //
    return this;
  }
  
  /**
   * Sets the timer of the default {@link Interval} back to zero.
   */
  public void resetTimer()
  {
    this.determineInterval( DurationCapture.INTERVAL_DEFAULTKEY ).reset();
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
    this.determineInterval( DurationCapture.INTERVAL_DEFAULTKEY ).stopMeasurement();
    
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
      if ( interval.getKey() != DurationCapture.INTERVAL_DEFAULTKEY )
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
    return this.getDurationInMilliseconds( DurationCapture.INTERVAL_DEFAULTKEY );
  }
  
  /**
   * Returns the needed time between measurement start and stop in the given {@link TimeUnit}.
   * 
   * @see #getDurationInMilliseconds(Object)
   * @param timeUnit
   * @return
   */
  public long getDuration( TimeUnit timeUnit )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getDurationInMilliseconds(), TimeUnit.MILLISECONDS );
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
   * Returns the needed time between measurement start and stop for the given interval key in the given {@link TimeUnit}.
   * 
   * @see #getDurationInMilliseconds(Object)
   * @param timeUnit
   * @return
   */
  public long getDuration( Object intervalKey, TimeUnit timeUnit )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getDurationInMilliseconds( intervalKey ), TimeUnit.MILLISECONDS );
  }
  
  /**
   * Returns the time duration sum between measurement start and stop in milliseconds for all given interval keys. If no interval
   * key is specified {@link #getDurationInMilliseconds()} is returned.
   * 
   * @param intervalKeys
   * @return
   */
  public long getDurationInMilliseconds( Object... intervalKeys )
  {
    //
    long retval = 0;
    
    //
    //
    if ( intervalKeys.length > 0 )
    {
      //
      for ( Object intervalKey : intervalKeys )
      {
        retval += this.getDurationInMilliseconds( intervalKey );
      }
    }
    else
    {
      //
      retval = this.getDurationInMilliseconds();
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the time duration sum between measurement start and stop in the given {@link TimeUnit} for all given interval keys.
   * If no interval key is specified {@link #getDurationInMilliseconds()} is returned.
   * 
   * @param timeUnit
   * @param intervalKeys
   * @return
   */
  public long getDuration( TimeUnit timeUnit, Object... intervalKeys )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getDurationInMilliseconds( intervalKeys ), TimeUnit.MILLISECONDS );
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds.
   * 
   * @return
   */
  public long getInterimTimeInMilliseconds()
  {
    return this.getInterimTimeInMilliseconds( DurationCapture.INTERVAL_DEFAULTKEY );
  }
  
  /**
   * Returns the time since starting the measurement and now in the given {@link TimeUnit}.
   * 
   * @param timeUnit
   * @return
   */
  public long getInterimTime( TimeUnit timeUnit )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getInterimTimeInMilliseconds(), TimeUnit.MILLISECONDS );
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds for the given interval key
   * 
   * @return
   */
  public long getInterimTimeInMilliseconds( Object intervalKey )
  {
    return this.determineInterval( intervalKey ).getInterimTimeInMilliseconds();
  }
  
  /**
   * Returns the time since starting the measurement and now in milliseconds for the given interval key in the given
   * {@link TimeUnit}
   * 
   * @param timeUnit
   *          {@link TimeUnit}
   * @return
   */
  public long getInterimTime( Object intervalKey, TimeUnit timeUnit )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getInterimTimeInMilliseconds( intervalKey ), TimeUnit.MILLISECONDS );
  }
  
  /**
   * Returns the time sum since starting the measurement and now in milliseconds for all given interval keys. If no interval key
   * is given {@link #getInterimTimeInMilliseconds()} is returned instead.
   * 
   * @param intervalKeys
   * @return
   */
  public long getInterimTimeInMilliseconds( Object... intervalKeys )
  {
    //
    long retval = 0;
    
    //
    if ( intervalKeys.length > 0 )
    {
      //
      for ( Object intervalKey : intervalKeys )
      {
        retval += this.getInterimTimeInMilliseconds( intervalKey );
      }
    }
    else
    {
      //
      retval = this.getInterimTimeInMilliseconds();
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the time sum since starting the measurement and now in the given {@link TimeUnit} for all given interval keys. If no
   * interval key is given {@link #getInterimTime(TimeUnit)} is returned instead.
   * 
   * @param timeUnit
   *          {@link TimeUnit}
   * @param intervalKeys
   * @return
   */
  public long getInterimTime( TimeUnit timeUnit, Object... intervalKeys )
  {
    //
    if ( timeUnit == null )
    {
      timeUnit = TimeUnit.MILLISECONDS;
    }
    return timeUnit.convert( getInterimTimeInMilliseconds( intervalKeys ), TimeUnit.MILLISECONDS );
  }
  
  /**
   * Returns true, if there is an {@link Interval} instance for the {@link #INTERVAL_DEFAULTKEY}.
   * 
   * @return
   */
  protected boolean hasDefaultInterval()
  {
    return this.intervalKeyToIntervalMap.containsKey( DurationCapture.INTERVAL_DEFAULTKEY );
  }
  
  /**
   * Returns the collected statistical informations for the intervals durations.
   * 
   * @return
   */
  public String calculateIntervalStatisticLogMessage()
  {
    //
    String retval = null;
    
    //    
    final Map<Object, IntervalStatistic> intervalStatisticMap = this.calculateIntervalStatisticMap();
    
    //
    final int maximumWidth = Math.max( 20, StringUtils.maximumWidth( ListUtils.convert( intervalStatisticMap.keySet(),
                                                                                        new ElementConverterObjectToString() ) ) );
    final String lineSeparator = StringUtils.repeat( "-", maximumWidth + 35 ) + DEFAULT_LINESEPARATOR;
    final String ROW_FORMAT_STRING = "%-" + maximumWidth + "s : %5d ms (%6.2f%%) %s %n";
    
    //
    final StringBuffer sb = new StringBuffer();
    
    //
    if ( intervalStatisticMap.containsKey( DurationCapture.INTERVAL_DEFAULTKEY ) )
    {
      //
      IntervalStatistic intervalStatistic = intervalStatisticMap.get( DurationCapture.INTERVAL_DEFAULTKEY );
      String intervalKeyAsString = intervalStatistic.getIntervalKeyAsString();
      long durationInMilliseconds = intervalStatistic.getDurationInMilliseconds();
      double durationPercentage = intervalStatistic.getDurationPercentage();
      String percentageBar = StringUtils.percentageBar( durationPercentage * 0.01, 12 );
      
      //
      sb.append( lineSeparator );
      sb.append( String.format( ROW_FORMAT_STRING, intervalKeyAsString, durationInMilliseconds, durationPercentage, percentageBar ) );
      
      //
      intervalStatisticMap.remove( DurationCapture.INTERVAL_DEFAULTKEY );
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
        String intervalKeyAsString = intervalStatistic.getIntervalKeyAsString();
        long durationInMilliseconds = intervalStatistic.getDurationInMilliseconds();
        double durationPercentage = intervalStatistic.getDurationPercentage();
        String percentageBar = StringUtils.percentageBar( durationPercentage * 0.01, 12 );
        
        //
        sb.append( String.format( ROW_FORMAT_STRING, intervalKeyAsString, durationInMilliseconds, durationPercentage,
                                  percentageBar ) );
        
        //
        intervalDurationTimeSum += intervalStatistic.getDurationInMilliseconds();
      }
    }
    
    //summary
    if ( intervalStatisticMap.size() > 1 )
    {
      sb.append( lineSeparator );
      sb.append( "Whole interval key duration time: " + intervalDurationTimeSum + " ms\n" );
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
  
  /**
   * Returns a new {@link Map} instance with all {@link Interval} keys and the related {@link #getDuration(TimeUnit)} as value
   * 
   * @param timeUnit
   *          {@link TimeUnit}
   * @return
   */
  public Map<Object, Long> getIntervalKeyToDurationMap( TimeUnit timeUnit )
  {
    //    
    Map<Object, Long> retmap = new LinkedHashMap<Object, Long>();
    
    //
    for ( Object intervalKey : this.getIntervalKeyList() )
    {
      retmap.put( intervalKey, this.getDuration( intervalKey, timeUnit ) );
    }
    
    //
    return retmap;
  }
  
  /* ********************************************** STATIC FACTORY METHOD PART ********************************************** */
  
  /**
   * Used by the factory method.
   * 
   * @see #newInstance()
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
