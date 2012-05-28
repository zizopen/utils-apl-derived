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
package org.omnaest.utils.xml;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {@link List} wrapper which acts as an {@link XmlRootElement} for any {@link List}. Since the exact type of the internal
 * {@link List} instance is determined at runtime, each of the objects have its own schema definition. This will cause some
 * overhead in comparison to a {@link List} which is wrapped not by its interface. <br>
 * <h1>Example:</h1> <br>
 * Code:<br>
 * 
 * <pre>
 * //
 * List&lt;Object&gt; list = new ArrayList&lt;Object&gt;();
 * list.add( String.valueOf( &quot;test&quot; ) );
 * list.add( Character.valueOf( 'c' ) );
 * list.add( Byte.valueOf( &quot;10&quot; ) );
 * list.add( Short.valueOf( &quot;1000&quot; ) );
 * list.add( Integer.valueOf( &quot;100000&quot; ) );
 * list.add( Long.valueOf( &quot;1000000000&quot; ) );
 * list.add( Float.valueOf( &quot;100000.111&quot; ) );
 * list.add( Double.valueOf( &quot;100000.111&quot; ) );
 * list.add( Boolean.valueOf( &quot;true&quot; ) );
 * list.add( newTestEntity( 0 ) );
 * list.add( newTestEntity( 1 ) );
 * list.add( newTestEntity( 3 ) );
 * 
 * //
 * JAXBList&lt;TestEntity&gt; jaxbList = JAXBList.newInstance( list );
 * 
 * //
 * final MarshallingConfiguration marshallingConfiguration = new MarshallingConfiguration();
 * marshallingConfiguration.setKnownTypes( TestEntity.class );
 * 
 * //
 * String xmlContent = JAXBXMLHelper.storeObjectAsXML( jaxbList, marshallingConfiguration );
 * </pre>
 * 
 * Entity:<br>
 * 
 * <pre>
 * &#064;XmlType
 * &#064;XmlAccessorType(XmlAccessType.FIELD)
 * protected static class TestEntity
 * {
 *   
 *   &#064;XmlElement
 *   private String fieldString  = null;
 *   
 *   &#064;XmlElement
 *   private int    fieldInteger = -1;
 *   
 * }
 * </pre>
 * 
 * Produced XML content:<br>
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-8&quot; standalone=&quot;yes&quot;?&gt;
 * &lt;list&gt;
 *     &lt;string&gt;test&lt;/string&gt;
 *     &lt;char&gt;99&lt;/char&gt;
 *     &lt;byte&gt;10&lt;/byte&gt;
 *     &lt;short&gt;1000&lt;/short&gt;
 *     &lt;int&gt;100000&lt;/int&gt;
 *     &lt;long&gt;1000000000&lt;/long&gt;
 *     &lt;float&gt;100000.11&lt;/float&gt;
 *     &lt;double&gt;100000.111&lt;/double&gt;
 *     &lt;boolean&gt;true&lt;/boolean&gt;
 *     &lt;object xsi:type=&quot;testEntity&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
 *         &lt;fieldString&gt;fieldString0&lt;/fieldString&gt;
 *         &lt;fieldInteger&gt;0&lt;/fieldInteger&gt;
 *     &lt;/object&gt;
 *     &lt;object xsi:type=&quot;testEntity&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
 *         &lt;fieldString&gt;fieldString1&lt;/fieldString&gt;
 *         &lt;fieldInteger&gt;1&lt;/fieldInteger&gt;
 *     &lt;/object&gt;
 *     &lt;object xsi:type=&quot;testEntity&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;
 *         &lt;fieldString&gt;fieldString3&lt;/fieldString&gt;
 *         &lt;fieldInteger&gt;3&lt;/fieldInteger&gt;
 *     &lt;/object&gt;
 * &lt;/list&gt;
 * </pre>
 * 
 * @see #newInstance(List)
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement(name = "list")
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBList<E> implements List<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElements({ @XmlElement(name = "string", type = String.class), @XmlElement(name = "byte", type = Byte.class),
      @XmlElement(name = "short", type = Short.class), @XmlElement(name = "int", type = Integer.class),
      @XmlElement(name = "long", type = Long.class), @XmlElement(name = "char", type = Character.class),
      @XmlElement(name = "float", type = Float.class), @XmlElement(name = "double", type = Double.class),
      @XmlElement(name = "boolean", type = Boolean.class), @XmlElement(name = "object") })
  protected List<E> list = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new instance of a {@link JAXBList} for a given {@link List}.
   * 
   * @param <E>
   * @param list
   * @return new instance or null if list param is null
   */
  public static <E> JAXBList<E> newInstance( List<E> list )
  {
    //
    JAXBList<E> result = null;
    
    //
    if ( list != null )
    {
      result = new JAXBList<E>( list );
    }
    
    //
    return result;
  }
  
  /**
   * @see #newInstance(Collection)
   * @param collection
   */
  protected JAXBList( List<E> collection )
  {
    super();
    this.list = collection;
  }
  
  /**
   * Used internally when JAXB does create a new default instance.
   */
  protected JAXBList()
  {
    super();
  }
  
  @Override
  public boolean add( E arg0 )
  {
    return this.list.add( arg0 );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> arg0 )
  {
    return this.list.addAll( arg0 );
  }
  
  @Override
  public void clear()
  {
    this.list.clear();
  }
  
  @Override
  public boolean contains( Object arg0 )
  {
    return this.list.contains( arg0 );
  }
  
  @Override
  public boolean containsAll( Collection<?> arg0 )
  {
    return this.list.containsAll( arg0 );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.list.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.list.iterator();
  }
  
  @Override
  public boolean remove( Object arg0 )
  {
    return this.list.remove( arg0 );
  }
  
  @Override
  public boolean removeAll( Collection<?> arg0 )
  {
    return this.list.removeAll( arg0 );
  }
  
  @Override
  public boolean retainAll( Collection<?> arg0 )
  {
    return this.list.retainAll( arg0 );
  }
  
  @Override
  public int size()
  {
    return this.list.size();
  }
  
  @Override
  public Object[] toArray()
  {
    return this.list.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] arg0 )
  {
    return this.list.toArray( arg0 );
  }
  
  @Override
  public void add( int index, E element )
  {
    this.list.add( index, element );
  }
  
  @Override
  public boolean addAll( int index, Collection<? extends E> c )
  {
    return this.list.addAll( index, c );
  }
  
  @Override
  public E get( int index )
  {
    return this.list.get( index );
  }
  
  @Override
  public int indexOf( Object o )
  {
    return this.list.indexOf( o );
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    return this.list.lastIndexOf( o );
  }
  
  @Override
  public ListIterator<E> listIterator()
  {
    return this.list.listIterator();
  }
  
  @Override
  public ListIterator<E> listIterator( int index )
  {
    return this.list.listIterator( index );
  }
  
  @Override
  public E remove( int index )
  {
    return this.list.remove( index );
  }
  
  @Override
  public E set( int index, E element )
  {
    return this.list.set( index, element );
  }
  
  @Override
  public List<E> subList( int fromIndex, int toIndex )
  {
    return this.list.subList( fromIndex, toIndex );
  }
  
}
