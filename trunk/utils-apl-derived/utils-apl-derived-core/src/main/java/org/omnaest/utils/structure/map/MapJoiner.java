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
package org.omnaest.utils.structure.map;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.omnaest.utils.structure.map.MapUtils.KeyExtractor;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * A {@link MapJoiner} allows to join two or multiple {@link Map} instances by their keys. Or additionally with {@link List}
 * instances in combination with a {@link KeyExtractor}.
 * 
 * @see MapUtils#joiner()
 * @author Omnaest
 */
public class MapJoiner
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   * @param <K>
   * @param <VL>
   */
  public static interface From<K, VL>
  {
    
    /**
     * Does an inner join with the previously given left {@link Map} and the now given right {@link Map}
     * 
     * @param map
     * @return {@link JoinResult}
     */
    public <VR> JoinResult<K, VL, VR> joinInner( Map<K, ? extends VR> map );
    
    /**
     * Similar to {@link #joinInner(Map)} but using a given {@link Iterable} and a related {@link KeyExtractor}
     * 
     * @param keyExtractor
     *          {@link KeyExtractor}
     * @param iterable
     *          {@link Iterable}
     * @return {@link JoinResult}
     */
    public <VR> JoinResult<K, VL, VR> joinInner( KeyExtractor<? extends K, VR> keyExtractor, Iterable<? extends VR> iterable );
    
  }
  
  /**
   * @see JoinResult#where(Predicate)
   * @author Omnaest
   * @param <K>
   * @param <VL>
   * @param <VR>
   */
  public static interface Predicate<K, VL, VR>
  {
    
    /**
     * Returns true if the given key and the values should be kept within the result
     * 
     * @param key
     * @param valueTuple
     * @return
     */
    public boolean accept( K key, TupleTwo<VL, VR> valueTuple );
    
  }
  
  /**
   * {@link Predicate} which uses a given {@link Set} of keys to return true for any result value which has a key contained within
   * that given {@link Set}.
   * 
   * @author Omnaest
   * @param <K>
   * @param <VL>
   * @param <VR>
   */
  public static class PredicateIncludingKeySet<K, VL, VR> implements Predicate<K, VL, VR>
  {
    /* ********************************************** Variables ********************************************** */
    private final Set<K> keySet;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see PredicateIncludingKeySet
     * @param keySet
     */
    public PredicateIncludingKeySet( Set<K> keySet )
    {
      super();
      this.keySet = keySet;
    }
    
    @Override
    public boolean accept( K key, TupleTwo<VL, VR> valueTuple )
    {
      return this.keySet != null && this.keySet.contains( key );
    }
    
  }
  
  /**
   * @author Omnaest
   * @param <K>
   * @param <VL>
   * @param <VR>
   */
  public static interface JoinResult<K, VL, VR> extends From<K, TupleTwo<VL, VR>>
  {
    /**
     * Returns the result {@link Map}
     * 
     * @return
     */
    public Map<K, TupleTwo<VL, VR>> getResultMap();
    
    /**
     * Filters the current {@link JoinResult} using a given {@link Predicate}
     * 
     * @param predicate
     *          {@link Predicate}
     * @return new {@link JoinResult}
     */
    public JoinResult<K, VL, VR> where( Predicate<K, VL, VR> predicate );
  }
  
  /**
   * @see From
   * @author Omnaest
   * @param <K>
   * @param <VL>
   */
  private static class MapJoinerImpl<K, VL> implements From<K, VL>
  {
    /* ********************************************** Variables ********************************************** */
    protected final Map<K, VL> map;
    
    /* ********************************************** Methods ********************************************** */
    
    public MapJoinerImpl( Map<K, VL> map )
    {
      this.map = map;
    }
    
    @Override
    public <VR> JoinResult<K, VL, VR> joinInner( Map<K, ? extends VR> map )
    {
      //
      final Map<K, TupleTwo<VL, VR>> retmap = new LinkedHashMap<K, TupleTwo<VL, VR>>();
      
      //
      if ( this.map != null && map != null )
      {
        //
        final Set<K> keySet = new LinkedHashSet<K>( this.map.keySet() );
        keySet.retainAll( map.keySet() );
        for ( K key : keySet )
        {
          //
          final VL vl = this.map.get( key );
          final VR vr = map.get( key );
          retmap.put( key, new TupleTwo<VL, VR>( vl, vr ) );
        }
      }
      
      //
      return new JoinResultImpl<K, VL, VR>( retmap );
    }
    
    @Override
    public <VR> JoinResult<K, VL, VR> joinInner( KeyExtractor<? extends K, VR> keyExtractor, Iterable<? extends VR> iterable )
    {
      //
      return this.joinInner( MapUtils.valueOf( keyExtractor, iterable ) );
    }
    
  }
  
  /**
   * @see JoinResult
   * @author Omnaest
   * @param <K>
   * @param <VL>
   * @param <VR>
   */
  private static class JoinResultImpl<K, VL, VR> extends MapJoinerImpl<K, TupleTwo<VL, VR>> implements JoinResult<K, VL, VR>
  {
    
    public JoinResultImpl( Map<K, TupleTwo<VL, VR>> map )
    {
      super( map );
    }
    
    @Override
    public Map<K, TupleTwo<VL, VR>> getResultMap()
    {
      //
      return this.map;
    }
    
    @Override
    public JoinResult<K, VL, VR> where( Predicate<K, VL, VR> predicate )
    {
      //
      final Map<K, TupleTwo<VL, VR>> retmap = new LinkedHashMap<K, TupleTwo<VL, VR>>();
      
      //
      if ( predicate != null )
      {
        //
        for ( Entry<K, TupleTwo<VL, VR>> entry : this.map.entrySet() )
        {
          if ( entry != null )
          {
            // 
            final K key = entry.getKey();
            final TupleTwo<VL, VR> value = entry.getValue();
            boolean accept = predicate.accept( key, value );
            if ( accept )
            {
              retmap.put( key, value );
            }
          }
        }
      }
      
      //
      return new JoinResultImpl<K, VL, VR>( retmap );
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see MapJoiner
   * @param map
   *          {@link Map}
   * @return {@link From}
   */
  public <K, VL> From<K, VL> from( Map<K, VL> map )
  {
    return new MapJoinerImpl<K, VL>( map );
  }
  
  /**
   * @see MapJoiner
   * @param keyExtractor
   *          {@link KeyExtractor}
   * @param iterable
   *          {@link Iterable}
   * @return {@link From}
   */
  public <K, VL> From<K, VL> from( KeyExtractor<K, VL> keyExtractor, Iterable<VL> iterable )
  {
    return new MapJoinerImpl<K, VL>( MapUtils.valueOf( keyExtractor, iterable ) );
  }
  
}
