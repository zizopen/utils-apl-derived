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
package org.omnaest.utils.structure.element.cached;

import org.omnaest.utils.time.DurationCapture;

/**
 * Extension of the {@link CachedElement} which allows declare a duration after passing that the cache becomes dirty and is not be
 * used anymore.<br>
 * <br>
 * The intension of use are situation in which references can be ensured to be actual for a given period of time. But after
 * passing this period the cache should not be used anymore and a new element reference resolved again. <br>
 * <br>
 * Note: if the cached element gets a new value set to, the internal duration timer will be reseted and starts to count beginning
 * from zero again. <br>
 * <br>
 * If the given duration of validity is exceeded the internal reference of the actually cached element gets removed. This allows
 * the garbage collector to take action. But be aware that this mechanism needs active invocation to the
 * {@link #hasValueResolved()} or {@link #getValueFromCacheOnly()} or {@link #getValue()} method.
 * 
 * @author Omnaest
 * @param <T>
 */
public class CachedElementTimed<T> extends CachedElement<T>
{
  /* ********************************************** Variables ********************************************** */
  protected Long validCacheDurationInMilliseconds = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CachedElementTimed
   * @param valueResolver
   * @param validCacheDurationInMilliseconds
   */
  public CachedElementTimed( ValueResolver<T> valueResolver, Long validCacheDurationInMilliseconds )
  {
    super( valueResolver );
    this.validCacheDurationInMilliseconds = validCacheDurationInMilliseconds;
  }
  
  @Override
  protected CachedValue<T> newCachedValue()
  {
    final CachedValue<T> cachedValue = super.newCachedValue();
    return new CachedValue<T>()
    {
      protected DurationCapture durationCapture = DurationCapture.newInstance().startTimeMeasurement();
      
      @Override
      public T getValue()
      {
        //
        T retval = null;
        
        //
        if ( this.durationCapture.getInterimTimeInMilliseconds() <= CachedElementTimed.this.validCacheDurationInMilliseconds )
        {
          retval = cachedValue.getValue();
        }
        else
        {
          cachedValue.setValue( null );
        }
        
        //
        return retval;
      }
      
      @Override
      public void setValue( T value )
      {
        cachedValue.setValue( value );
        this.durationCapture.resetTimer();
      }
    };
    
  }
  
  public Long getValidCacheDurationInMilliseconds()
  {
    return this.validCacheDurationInMilliseconds;
  }
  
  public void setValidCacheDurationInMilliseconds( Long validCacheDurationInMilliseconds )
  {
    this.validCacheDurationInMilliseconds = validCacheDurationInMilliseconds;
  }
  
}
