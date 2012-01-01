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

import java.util.concurrent.Callable;

/**
 * {@link Callable} decorator which captures the duration of the {@link Callable#call()} method
 * 
 * @see #getDurationCapture()
 * @author Omnaest
 */
public class DurationCaptureCallableDecorator<V> implements Callable<V>
{
  /* ********************************************** Variables ********************************************** */
  protected DurationCapture durationCapture = DurationCapture.newInstance();
  protected Callable<V>     callable        = null;
  
  /* ********************************************** Methods ********************************************** */
  
  public DurationCaptureCallableDecorator( Callable<V> callable )
  {
    super();
    this.callable = callable;
  }
  
  @Override
  public V call() throws Exception
  {
    //
    V retval = null;
    
    //
    this.durationCapture.startTimeMeasurement();
    
    //
    if ( this.callable != null )
    {
      retval = this.callable.call();
    }
    
    //
    this.durationCapture.stopTimeMeasurement();
    
    //
    return retval;
  }
  
  public long getDurationInMilliseconds()
  {
    return this.durationCapture.getDurationInMilliseconds();
  }
  
  public DurationCapture getDurationCapture()
  {
    return this.durationCapture;
  }
  
}
