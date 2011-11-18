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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.operation.foreach.ForEach;
import org.omnaest.utils.operation.special.OperationBooleanResult;
import org.omnaest.utils.structure.collection.CollectionDecorator;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.set.SetDecorator;
import org.omnaest.utils.structure.iterator.IteratorDecorator;

/**
 * {@link DualMap} implementation which makes use of two {@link LinkedHashMap} instances to get an index on keys and values.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class LinkedHashDualMap<K, V> implements DualMap<K, V>
{
  /* ********************************************** Constants ********************************************** */
  protected static final long serialVersionUID = 8985215388337132591L;
  
  /* ********************************************** Variables ********************************************** */
  protected final Map<K, V>   keyToValueMap;
  protected final Map<V, K>   valueToKeyMap;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see LinkedHashDualMap
   */
  public LinkedHashDualMap()
  {
    this( new LinkedHashMap<K, V>(), new LinkedHashMap<V, K>() );
  }
  
  /**
   * @see LinkedHashDualMap
   * @param keyToValueMap
   * @param valueToKeyMap
   */
  protected LinkedHashDualMap( Map<K, V> keyToValueMap, Map<V, K> valueToKeyMap )
  {
    super();
    this.keyToValueMap = keyToValueMap;
    this.valueToKeyMap = valueToKeyMap;
  }
  
  @Override
  public void clear()
  {
    this.keyToValueMap.clear();
    this.valueToKeyMap.clear();
  }
  
  @Override
  public boolean contains( Object element )
  {
    return this.keyToValueMap.containsKey( element ) || this.valueToKeyMap.containsKey( element );
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.keyToValueMap.containsKey( key );
  }
  
  @Override
  public boolean containsValue( Object key )
  {
    return this.valueToKeyMap.containsKey( key );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.keyToValueMap.isEmpty() && this.valueToKeyMap.isEmpty();
  }
  
  @Override
  public V put( K key, V value )
  {
    //
    V retval = null;
    
    //
    retval = this.keyToValueMap.put( key, value );
    this.valueToKeyMap.put( value, key );
    
    //
    return retval;
  }
  
  @Override
  public int size()
  {
    return this.keyToValueMap.size();
  }
  
  @Override
  public DualMap<K, V> putAll( DualMap<? extends K, ? extends V> dualMap )
  {
    //
    if ( dualMap != null )
    {
      //
      this.keyToValueMap.putAll( dualMap );
      this.valueToKeyMap.putAll( dualMap.invert() );
    }
    
    // 
    return this;
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> map )
  {
    //
    if ( map != null )
    {
      for ( K firstElement : map.keySet() )
      {
        this.put( firstElement, map.get( firstElement ) );
      }
    }
  }
  
  @Override
  public DualMap<V, K> invert()
  {
    return new LinkedHashDualMap<V, K>( this.valueToKeyMap, this.keyToValueMap );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "LinkedHashDualMap [keyToValueMap=" );
    builder.append( this.keyToValueMap );
    builder.append( ", valueToKeyMap=" );
    builder.append( this.valueToKeyMap );
    builder.append( "]" );
    return builder.toString();
  }
  
  @Override
  public V get( Object key )
  {
    return this.keyToValueMap.get( key );
  }
  
  @Override
  public V remove( Object key )
  {
    //    
    V retval = null;
    
    //
    if ( key != null )
    {
      V value = this.keyToValueMap.remove( key );
      this.valueToKeyMap.remove( value );
    }
    
    // 
    return retval;
  }
  
  @Override
  public Set<K> keySet()
  {
    return new SetDecorator<K>( this.keyToValueMap.keySet() )
    {
      /* ********************************************** Constants ********************************************** */
      private static final long serialVersionUID = -3831726909173097066L;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public Iterator<K> iterator()
      {
        // 
        return new IteratorDecorator<K>( this.set.iterator() )
        {
          private K lastReturnedElement = null;
          
          @Override
          public boolean hasNext()
          {
            return this.iterator.hasNext();
          }
          
          @Override
          public K next()
          {
            return this.lastReturnedElement = this.iterator.next();
          }
          
          @Override
          public void remove()
          {
            this.iterator.remove();
            if ( this.lastReturnedElement != null )
            {
              LinkedHashDualMap.this.valueToKeyMap.remove( this.lastReturnedElement );
              this.lastReturnedElement = null;
            }
          }
        };
      }
      
      @Override
      public boolean remove( Object o )
      {
        return LinkedHashDualMap.this.remove( o ) != null;
      }
      
    };
  }
  
  @Override
  public Collection<V> values()
  {
    return new CollectionDecorator<V>( this.keyToValueMap.values() )
    {
      /* ********************************************** Constants ********************************************** */
      private static final long serialVersionUID = -393065672929244447L;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public Iterator<V> iterator()
      {
        return new IteratorDecorator<V>( this.collection.iterator() )
        {
          /* ********************************************** Variables ********************************************** */
          private V lastReturnedElement = null;
          
          /* ********************************************** Methods ********************************************** */
          
          @Override
          public V next()
          {
            return this.lastReturnedElement = super.next();
          }
          
          @Override
          public void remove()
          {
            if ( this.lastReturnedElement != null )
            {
              //
              LinkedHashDualMap.this.invert().remove( this.lastReturnedElement );
              
              //
              this.lastReturnedElement = null;
            }
          }
          
        };
      }
      
      @Override
      public boolean remove( Object o )
      {
        return LinkedHashDualMap.this.invert().remove( o ) != null;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public boolean removeAll( Collection<?> collection )
      {
        OperationBooleanResult<Object> operation = new OperationBooleanResult<Object>()
        {
          @Override
          public Boolean execute( Object object )
          {
            return remove( object );
          }
        };
        return new ForEach<Object, Boolean>( (Collection<Object>) collection ).execute( operation ).areAllValuesEqualTo( true );
      }
      
      @Override
      public boolean retainAll( Collection<?> collection )
      {
        //       
        boolean retval = true;
        
        //
        if ( collection != null )
        {
          //
          List<V> valueList = ListUtils.valueOf( this );
          valueList.removeAll( collection );
          
          //
          this.removeAll( valueList );
        }
        
        //
        return retval;
      }
      
      @Override
      public void clear()
      {
        LinkedHashDualMap.this.clear();
      }
      
    };
  }
  
  @Override
  public Set<Entry<K, V>> entrySet()
  {
    // 
    return new SetDecorator<Entry<K, V>>( this.keyToValueMap.entrySet() )
    {
      
      /* ********************************************** Constants ********************************************** */
      private static final long serialVersionUID = 7897572238594822950L;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator()
      {
        return new IteratorDecorator<Entry<K, V>>( super.iterator() )
        {
          /* ********************************************** Variables ********************************************** */
          private Entry<K, V> lastReturnedEntry = null;
          
          /* ********************************************** Methods ********************************************** */
          
          @Override
          public java.util.Map.Entry<K, V> next()
          {
            return this.lastReturnedEntry = super.next();
          }
          
          @Override
          public void remove()
          {
            if ( this.lastReturnedEntry != null )
            {
              //
              LinkedHashDualMap.this.remove( this.lastReturnedEntry.getKey() );
              LinkedHashDualMap.this.invert().remove( this.lastReturnedEntry.getValue() );
              
              //
              this.lastReturnedEntry = null;
            }
          }
          
        };
      }
      
      @Override
      public boolean add( java.util.Map.Entry<K, V> entry )
      {
        //
        if ( entry != null )
        {
          LinkedHashDualMap.this.put( entry.getKey(), entry.getValue() );
        }
        return entry != null;
      }
      
      @Override
      public boolean remove( Object object )
      {
        //
        boolean retval = object instanceof Entry;
        
        //
        if ( retval )
        {
          //
          Entry<?, ?> entry = (Entry<?, ?>) object;
          
          //
          retval &= LinkedHashDualMap.this.remove( entry.getKey() ) != null;
          retval &= LinkedHashDualMap.this.invert().remove( entry.getValue() ) != null;
        }
        
        // 
        return retval;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public boolean addAll( Collection<? extends Entry<K, V>> collection )
      {
        OperationBooleanResult<Entry<K, V>> operation = new OperationBooleanResult<Entry<K, V>>()
        {
          @Override
          public Boolean execute( Entry<K, V> entry )
          {
            //
            return add( entry );
          }
        };
        return new ForEach<Entry<K, V>, Boolean>( (Collection<Entry<K, V>>) collection ).execute( operation )
                                                                                        .areAllValuesEqualTo( true );
      }
      
      @Override
      public boolean retainAll( Collection<?> collection )
      {
        //
        boolean retval = true;
        
        //
        if ( collection != null )
        {
          //
          List<Entry<K, V>> valueList = ListUtils.valueOf( this );
          valueList.removeAll( collection );
          
          //
          this.removeAll( valueList );
        }
        
        //
        return retval;
      }
      
      @SuppressWarnings("unchecked")
      @Override
      public boolean removeAll( Collection<?> collection )
      {
        OperationBooleanResult<Object> operation = new OperationBooleanResult<Object>()
        {
          @Override
          public Boolean execute( Object object )
          {
            return remove( object );
          }
        };
        return new ForEach<Object, Boolean>( (Collection<Object>) collection ).execute( operation ).areAllValuesEqualTo( true );
      }
      
      @Override
      public void clear()
      {
        LinkedHashDualMap.this.clear();
      }
      
    };
  }
}
