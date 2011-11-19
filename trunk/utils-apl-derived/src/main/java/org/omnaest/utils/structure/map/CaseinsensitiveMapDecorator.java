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
package org.omnaest.utils.structure.map;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Decorator for a {@link Map} with {@link String} based keys which will provide a caseinsensitive {@link #get(Object)} method.<br>
 * <br>
 * The {@link CaseinsensitiveMapDecorator} will first try to resolve the unmodified key from the underlying {@link Map}, if it
 * fails it tries the lowercased, uppercased and capitalized key. If this fails, too, it will iterate over the keyset of the
 * underlying {@link Map} and compares each key to the given one using caseinsensitive comparision.<br>
 * <br>
 * This behavior result in at least retaining the performance of the underlying {@link Map} if one of the special cases does
 * match, otherwise the performance is reduced due to the iteration over the whole keyset for each {@link #get(Object)} request.<br>
 * <br>
 * If the simple constructor is used or the useCacheForKeys flag is set to true, a {@link WeakHashMap} is used to cache known key
 * to key mappings, but only these which make a full iteration over the keys of the underlying {@link Map} necessary.
 * 
 * @author Omnaest
 */
public class CaseinsensitiveMapDecorator<V> extends MapDecorator<String, V>
{
  /* ********************************************** Variables ********************************************** */
  protected final Map<String, String> sourceKeyToTargetKeyTranslationMap;
  protected boolean                   fastHit = false;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see CaseinsensitiveMapDecorator
   * @param map
   * @param useCacheForKeys
   */
  public CaseinsensitiveMapDecorator( Map<String, V> map, boolean useCacheForKeys )
  {
    super( map );
    this.sourceKeyToTargetKeyTranslationMap = useCacheForKeys ? new WeakHashMap<String, String>() : null;
  }
  
  /**
   * @see CaseinsensitiveMapDecorator
   * @param map
   */
  public CaseinsensitiveMapDecorator( Map<String, V> map )
  {
    this( map, true );
  }
  
  /**
   * @param sourceKey
   * @return
   */
  public String tryTranslateKeyByCache( String sourceKey )
  {
    //
    String retval = null;
    
    //
    if ( this.sourceKeyToTargetKeyTranslationMap != null )
    {
      retval = this.sourceKeyToTargetKeyTranslationMap.get( sourceKey );
    }
    
    //
    return retval;
  }
  
  /**
   * @param sourceKey
   * @param targetKey
   */
  public void addKeyToKeyTranslationToCache( String sourceKey, String targetKey )
  {
    if ( this.sourceKeyToTargetKeyTranslationMap != null )
    {
      this.sourceKeyToTargetKeyTranslationMap.put( sourceKey, targetKey );
    }
  }
  
  @Override
  public V get( Object object )
  {
    //
    V retval = null;
    
    //
    this.fastHit = true;
    
    //
    if ( super.containsKey( object ) )
    {
      retval = super.get( object );
    }
    else if ( object instanceof String )
    {
      //
      String key = (String) object;
      String keyModified = null;
      
      //
      keyModified = key.toLowerCase();
      if ( super.containsKey( keyModified ) )
      {
        retval = super.get( keyModified );
      }
      else
      {
        //
        keyModified = key.toUpperCase();
        if ( super.containsKey( keyModified ) )
        {
          retval = super.get( keyModified );
        }
        else
        {
          //
          keyModified = StringUtils.capitalize( key );
          if ( super.containsKey( keyModified ) )
          {
            retval = super.get( keyModified );
          }
          else
          {
            //
            keyModified = this.tryTranslateKeyByCache( key );
            if ( super.containsKey( keyModified ) )
            {
              retval = super.get( keyModified );
            }
            else
            {
              //
              for ( String iKey : super.keySet() )
              {
                //
                if ( StringUtils.equalsIgnoreCase( key, iKey ) )
                {
                  //
                  retval = super.get( iKey );
                  this.addKeyToKeyTranslationToCache( key, iKey );
                  this.fastHit = false;
                  break;
                }
              }
            }
          }
        }
      }
    }
    
    // 
    return retval;
  }
  
  /**
   * @return the fastHit
   */
  protected boolean isFastHit()
  {
    return this.fastHit;
  }
}
