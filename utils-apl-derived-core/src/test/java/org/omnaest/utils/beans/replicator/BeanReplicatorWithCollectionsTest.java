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
package org.omnaest.utils.beans.replicator;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.ComparatorUtils;
import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorWithCollectionsTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @SuppressWarnings("unused")
  private static class SubBean
  {
    private String first;
    private String second;
    
    public SubBean()
    {
      super();
    }
    
    public SubBean( String first, String second )
    {
      super();
      this.first = first;
      this.second = second;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "SubBean [first=" );
      builder.append( this.first );
      builder.append( ", second=" );
      builder.append( this.second );
      builder.append( "]" );
      return builder.toString();
    }
    
    public String getFirst()
    {
      return this.first;
    }
    
    public void setFirst( String first )
    {
      this.first = first;
    }
    
    public String getSecond()
    {
      return this.second;
    }
    
    public void setSecond( String second )
    {
      this.second = second;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.first == null ) ? 0 : this.first.hashCode() );
      result = prime * result + ( ( this.second == null ) ? 0 : this.second.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof SubBean ) )
      {
        return false;
      }
      SubBean other = (SubBean) obj;
      if ( this.first == null )
      {
        if ( other.first != null )
        {
          return false;
        }
      }
      else if ( !this.first.equals( other.first ) )
      {
        return false;
      }
      if ( this.second == null )
      {
        if ( other.second != null )
        {
          return false;
        }
      }
      else if ( !this.second.equals( other.second ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  private static class TestSimpleBean
  {
    private Map<String, Object>       map;
    private Set<String>               set;
    private List<SubBean>             list;
    private Iterable<String>          iterable;
    private Collection<String>        collection;
    private String[]                  array;
    private SortedSet<String>         sortedSet;
    private SortedMap<String, Object> sortedMap;
    
    public Map<String, Object> getMap()
    {
      return this.map;
    }
    
    public void setMap( Map<String, Object> map )
    {
      this.map = map;
    }
    
    public Set<String> getSet()
    {
      return this.set;
    }
    
    public void setSet( Set<String> set )
    {
      this.set = set;
    }
    
    public Iterable<String> getIterable()
    {
      return this.iterable;
    }
    
    public void setIterable( Iterable<String> iterable )
    {
      this.iterable = iterable;
    }
    
    public Collection<String> getCollection()
    {
      return this.collection;
    }
    
    public void setCollection( Collection<String> collection )
    {
      this.collection = collection;
    }
    
    public String[] getArray()
    {
      return this.array;
    }
    
    public void setArray( String[] array )
    {
      this.array = array;
    }
    
    public SortedSet<String> getSortedSet()
    {
      return this.sortedSet;
    }
    
    public void setSortedSet( SortedSet<String> sortedSet )
    {
      this.sortedSet = sortedSet;
    }
    
    public SortedMap<String, Object> getSortedMap()
    {
      return this.sortedMap;
    }
    
    public void setSortedMap( SortedMap<String, Object> sortedMap )
    {
      this.sortedMap = sortedMap;
    }
    
    public List<SubBean> getList()
    {
      return this.list;
    }
    
    public void setList( List<SubBean> list )
    {
      this.list = list;
    }
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @SuppressWarnings("unchecked")
  @Test
  public void testMappingOfVariousCollectionTypes()
  {
    TestSimpleBean testSimpleBean = new TestSimpleBean();
    {
      Map<String, Object> map = MapUtils.builder()
                                        .<String, Object> put( "key1", "value1" )
                                        .put( "key2", "value2" )
                                        .buildAs()
                                        .linkedHashMap();
      testSimpleBean.setMap( map );
      testSimpleBean.setSortedMap( MapUtils.builder()
                                           .putAll( map )
                                           .buildAs()
                                           .treeMap( ComparatorUtils.reversedComparator( ComparatorUtils.NATURAL_COMPARATOR ) ) );
      testSimpleBean.setSet( SetUtils.valueOf( "a", "b", "c", "d" ) );
      testSimpleBean.setSortedSet( new TreeSet<String>( SetUtils.valueOf( "b", "c", "d", "a" ) ) );
      testSimpleBean.setList( ListUtils.valueOf( new SubBean( "a", "b" ), new SubBean( "c", "d" ) ) );
      testSimpleBean.setCollection( ListUtils.valueOf( "a", "b", "c", "d" ) );
      testSimpleBean.setIterable( ListUtils.valueOf( "a", "b", "c", "d" ) );
      testSimpleBean.setArray( new String[] { "a", "b", "c", "d" } );
    }
    
    BeanCopier<TestSimpleBean> beanCopier = new BeanCopier<TestSimpleBean>( TestSimpleBean.class );
    TestSimpleBean clone = beanCopier.clone( testSimpleBean );
    
    assertNotNull( clone );
    
    assertEquals( testSimpleBean.getMap(), clone.getMap() );
    assertNotSame( testSimpleBean.getMap(), clone.getMap() );
    
    assertEquals( testSimpleBean.getSortedMap(), clone.getSortedMap() );
    assertNotSame( testSimpleBean.getSortedMap(), clone.getSortedMap() );
    
    assertEquals( testSimpleBean.getSet(), clone.getSet() );
    assertNotSame( testSimpleBean.getSet(), clone.getSet() );
    
    assertEquals( testSimpleBean.getSortedSet(), clone.getSortedSet() );
    assertNotSame( testSimpleBean.getSortedSet(), clone.getSortedSet() );
    
    assertEquals( testSimpleBean.getList(), clone.getList() );
    assertNotSame( testSimpleBean.getList(), clone.getList() );
    
    assertEquals( testSimpleBean.getCollection(), clone.getCollection() );
    assertNotSame( testSimpleBean.getCollection(), clone.getCollection() );
    
    assertEquals( testSimpleBean.getIterable(), clone.getIterable() );
    assertNotSame( testSimpleBean.getIterable(), clone.getIterable() );
    
    assertArrayEquals( testSimpleBean.getArray(), clone.getArray() );
    assertNotSame( testSimpleBean.getArray(), clone.getArray() );
  }
  
}
