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

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * A {@link HashIntersectionMap} is a {@link Map} which wraps segments of {@link HashMap}s and allows to create fast intersections
 * between two given {@link HashIntersectionMap}s
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public class HashIntersectionMap<K, V> extends MapAbstract<K, V>
{
  /* ************************************************** Constants *************************************************** */
  public static final int     DEFAULT_SEGMENT_SIZE = 16;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private StructureNode<K, V> rootNode             = null;
  private final int           tokenSize;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static interface Node<K, V>
  {
    /**
     * @param bitSet
     * @param createIfNotExisting
     * @param leafNode
     * @return {@link HashIntersectionMap.Node}
     */
    public Node<K, V> resolveChildNode( BitSet bitSet, boolean createIfNotExisting, boolean leafNode );
    
    /**
     * @return
     */
    public Map<K, V> newMapView();
  }
  
  private static class StructureNode<K, V> implements Node<K, V>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private Map<BitSet, Node<K, V>> bitSetToNodeMap = new HashMap<BitSet, Node<K, V>>();
    
    /* *************************************************** Methods **************************************************** */
    
    @Override
    public Node<K, V> resolveChildNode( BitSet bitSet, boolean createIfNotExisting, boolean leafNode )
    {
      //
      Node<K, V> retval = null;
      
      //
      retval = this.bitSetToNodeMap.get( bitSet );
      if ( createIfNotExisting && retval == null )
      {
        //
        if ( leafNode )
        {
          retval = new ContainerNode<K, V>();
        }
        else
        {
          retval = new StructureNode<K, V>();
        }
        
        //
        this.bitSetToNodeMap.put( bitSet, retval );
      }
      
      // 
      return retval;
    }
    
    @Override
    public Map<K, V> newMapView()
    {
      //
      final ElementConverter<Node<K, V>, Map<K, V>> elementConverter = new ElementConverter<Node<K, V>, Map<K, V>>()
      {
        @Override
        public Map<K, V> convert( Node<K, V> node )
        {
          return node.newMapView();
        }
      };
      final Collection<Node<K, V>> values = this.bitSetToNodeMap.values();
      final List<Map<K, V>> mapList = ListUtils.convert( values, elementConverter );
      return MapUtils.composite( mapList );
    }
    
  }
  
  private static class ContainerNode<K, V> implements Node<K, V>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final Map<K, V> map = new HashMap<K, V>();
    
    /* *************************************************** Methods **************************************************** */
    
    @Override
    public Node<K, V> resolveChildNode( BitSet bitSet, boolean createIfNotExisting, boolean leafNode )
    {
      return null;
    }
    
    public Map<K, V> getMap()
    {
      return this.map;
    }
    
    @Override
    public Map<K, V> newMapView()
    {
      // 
      return this.map;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  private static class HashCodeTokenizer implements Iterator<BitSet>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final int tokenSize;
    private int       hashCode;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see HashCodeTokenizer
     * @param hashCode
     * @param tokenSize
     */
    public HashCodeTokenizer( int hashCode, int tokenSize )
    {
      super();
      this.hashCode = hashCode;
      this.tokenSize = tokenSize;
    }
    
    /**
     * @return
     */
    public boolean hasNext()
    {
      return this.hashCode > 0;
    }
    
    /**
     * @return
     */
    public BitSet next()
    {
      //
      final BitSet bitSet = new BitSet();
      
      //      
      int value = this.hashCode;
      for ( int index = 0; value > 0 && index < this.tokenSize; index++ )
      {
        bitSet.set( index, ( value & 1 ) == 1 );
        value = value >> 1;
      }
      
      //
      this.hashCode = value;
      
      //
      return bitSet;
    }
    
    @Override
    public void remove()
    {
      throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "HashCodeTokenizer [hashCode=" );
      builder.append( this.hashCode );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Creates a new {@link HashIntersectionMap} with a segment size of {@value #DEFAULT_SEGMENT_SIZE}
   * 
   * @see HashIntersectionMap
   */
  public HashIntersectionMap()
  {
    this( HashIntersectionMap.DEFAULT_SEGMENT_SIZE );
  }
  
  /**
   * @see HashIntersectionMap
   * @param segmentSize
   */
  public HashIntersectionMap( int segmentSize )
  {
    super();
    int tokenSize = 2;
    while ( ( 2 ^ tokenSize ) < segmentSize )
    {
      tokenSize *= 2;
    }
    this.tokenSize = tokenSize;
  }
  
  public ContainerNode<K, V> resolveContainerNode( int hashCode, boolean createIfNotExisting )
  {
    //
    ContainerNode<K, V> retval = null;
    
    //
    Node<K, V> node = this.getRootNode();
    final HashCodeTokenizer hashCodeTokenizer = new HashCodeTokenizer( hashCode, this.tokenSize );
    for ( BitSet bitSet : IterableUtils.valueOf( hashCodeTokenizer ) )
    {
      //  
      boolean isLeafNode = !hashCodeTokenizer.hasNext();
      node = node.resolveChildNode( bitSet, createIfNotExisting, isLeafNode );
      
      //
      if ( node == null )
      {
        break;
      }
    }
    
    //
    if ( node instanceof ContainerNode )
    {
      retval = (ContainerNode<K, V>) node;
    }
    
    //
    return retval;
  }
  
  @Override
  public V get( Object key )
  {
    //
    V retval = null;
    
    //
    if ( key != null )
    {
      //
      final int hashCode = System.identityHashCode( key );
      final boolean createIfNotExisting = false;
      ContainerNode<K, V> containerNode = this.resolveContainerNode( hashCode, createIfNotExisting );
      if ( containerNode != null )
      {
        Map<K, V> map = containerNode.getMap();
        retval = map.get( key );
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public V put( K key, V value )
  {
    //
    V retval = null;
    
    //
    if ( key != null )
    {
      //
      final int hashCode = System.identityHashCode( key );
      final boolean createIfNotExisting = true;
      ContainerNode<K, V> containerNode = this.resolveContainerNode( hashCode, createIfNotExisting );
      if ( containerNode != null )
      {
        final Map<K, V> map = containerNode.getMap();
        retval = map.put( key, value );
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public V remove( Object key )
  {
    //
    V retval = null;
    
    //
    if ( key != null )
    {
      //
      final int hashCode = System.identityHashCode( key );
      final boolean createIfNotExisting = false;
      ContainerNode<K, V> containerNode = this.resolveContainerNode( hashCode, createIfNotExisting );
      if ( containerNode != null )
      {
        final Map<K, V> map = containerNode.getMap();
        retval = map.remove( key );
      }
    }
    
    // 
    return retval;
  }
  
  @Override
  public Set<K> keySet()
  {
    return this.getRootNode().newMapView().keySet();
  }
  
  @Override
  public Collection<V> values()
  {
    return this.getRootNode().newMapView().values();
  }
  
  private StructureNode<K, V> getRootNode()
  {
    if ( this.rootNode == null )
    {
      this.rootNode = new StructureNode<K, V>();
    }
    return this.rootNode;
  }
  
  public Map<K, V> intersection( HashIntersectionMap<K, V> map )
  {
    return null;//TODO
  }
}
