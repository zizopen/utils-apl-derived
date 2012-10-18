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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Collection wrapper which acts as an {@link XmlRootElement} for any {@link Collection}. Since the exact type of the internal
 * {@link Collection} instance is determined at runtime, each of the objects have its own schema definition. This will cause some
 * overhead in comparison to a {@link Collection} which is wrapped not by its interface. <br>
 * <br>
 * Example output:<br>
 * 
 * <pre>
 * &lt;collection&gt;
 *     &lt;string&gt;value1&lt;/string&gt;
 *     &lt;string&gt;value2&lt;/string&gt;
 *     &lt;string&gt;value3&lt;/string&gt;
 * &lt;/collection&gt
 * </pre>
 * 
 * See {@link JAXBList} for further details on conversion syntax. <br>
 * <br>
 * 
 * @see #newInstance(Collection)
 * @see JAXBList
 * @see JAXBMap
 * @see JAXBSet
 * @author Omnaest
 * @param <E>
 */
@XmlRootElement(name = "collection")
@XmlAccessorType(XmlAccessType.FIELD)
public class JAXBCollection<E> implements Collection<E>
{
  /* ********************************************** Variables ********************************************** */
  @XmlElements({ @XmlElement(name = "string", type = String.class), @XmlElement(name = "byte", type = Byte.class),
      @XmlElement(name = "short", type = Short.class), @XmlElement(name = "int", type = Integer.class),
      @XmlElement(name = "long", type = Long.class), @XmlElement(name = "char", type = Character.class),
      @XmlElement(name = "float", type = Float.class), @XmlElement(name = "double", type = Double.class),
      @XmlElement(name = "boolean", type = Boolean.class), @XmlElement(name = "boolean", type = Boolean.class),
      @XmlElement(name = "date", type = Date.class), @XmlElement(name = "calendar", type = Calendar.class),
      @XmlElement(name = "bigint", type = BigInteger.class), @XmlElement(name = "bigdecimal", type = BigDecimal.class),
      @XmlElement(name = "object") })
  protected Collection<E> collection = null;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Creates a new instance of a {@link JAXBCollection} for a given {@link Collection}.
   * 
   * @param <E>
   * @param collection
   * @return new instance or null if collection param is null
   */
  public static <E> JAXBCollection<E> newInstance( Collection<E> collection )
  {
    //
    JAXBCollection<E> result = null;
    
    //
    if ( collection != null )
    {
      result = new JAXBCollection<E>( collection );
    }
    
    //
    return result;
  }
  
  /**
   * @see #newInstance(Collection)
   * @param collection
   */
  protected JAXBCollection( Collection<E> collection )
  {
    super();
    this.collection = collection;
  }
  
  /**
   * Used internally when JAXB does create a new default instance.
   * 
   * @param list
   */
  protected JAXBCollection()
  {
    super();
  }
  
  @Override
  public boolean add( E arg0 )
  {
    return this.collection.add( arg0 );
  }
  
  @Override
  public boolean addAll( Collection<? extends E> arg0 )
  {
    return this.collection.addAll( arg0 );
  }
  
  @Override
  public void clear()
  {
    this.collection.clear();
  }
  
  @Override
  public boolean contains( Object arg0 )
  {
    return this.collection.contains( arg0 );
  }
  
  @Override
  public boolean containsAll( Collection<?> arg0 )
  {
    return this.collection.containsAll( arg0 );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.collection.isEmpty();
  }
  
  @Override
  public Iterator<E> iterator()
  {
    return this.collection.iterator();
  }
  
  @Override
  public boolean remove( Object arg0 )
  {
    return this.collection.remove( arg0 );
  }
  
  @Override
  public boolean removeAll( Collection<?> arg0 )
  {
    return this.collection.removeAll( arg0 );
  }
  
  @Override
  public boolean retainAll( Collection<?> arg0 )
  {
    return this.collection.retainAll( arg0 );
  }
  
  @Override
  public int size()
  {
    return this.collection.size();
  }
  
  @Override
  public Object[] toArray()
  {
    return this.collection.toArray();
  }
  
  @Override
  public <T> T[] toArray( T[] arg0 )
  {
    return this.collection.toArray( arg0 );
  }
  
}
