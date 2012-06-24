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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.structure.map.decorator.MapDecorator;

/**
 * The {@link JAXBMap} is a artificial {@link XmlRootElement} for an arbitrary {@link Map} instance. It just stores an internal
 * map instance and delegates all {@link Map} based methods to it. <br>
 * <br>
 * Example output:<br>
 * 
 * <pre>
 * &lt;map&gt;
 *     &lt;entries&gt;
 *         &lt;entry&gt;
 *             &lt;key xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;key3&lt;/key&gt;
 *             &lt;value xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;value3&lt;/value&gt;
 *         &lt;/entry&gt;
 *         &lt;entry&gt;
 *             &lt;key xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;key2&lt;/key&gt;
 *             &lt;value xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;value2&lt;/value&gt;
 *         &lt;/entry&gt;
 *         &lt;entry&gt;
 *             &lt;key xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;key1&lt;/key&gt;
 *             &lt;value xsi:type=&quot;xs:string&quot; xmlns:xs=&quot;http://www.w3.org/2001/XMLSchema&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;&gt;value1&lt;/value&gt;
 *         &lt;/entry&gt;
 *     &lt;/entries&gt;
 * &lt;/map&gt;
 * </pre>
 * 
 * @see #newInstance(Map)
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
@XmlRootElement(name = "map")
public class JAXBMap<K, V> extends MapDecorator<K, V>
{
  
  /**
   * @see JAXBMap
   */
  protected JAXBMap()
  {
    this.map = new HashMap<K, V>();
  }
  
  /**
   * @see JAXBMap
   * @param map
   */
  protected JAXBMap( Map<K, V> map )
  {
    super( map );
  }
  
  /**
   * Creates a new {@link Map} wrapper.
   * 
   * @param <M>
   * @param <K>
   * @param <V>
   * @param map
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <M extends JAXBMap<K, V>, K, V> M newInstance( Map<K, V> map )
  {
    //
    M retmap = null;
    
    //
    if ( map != null )
    {
      retmap = (M) new JAXBMap<K, V>( map );
    }
    
    //
    return retmap;
  }
  
}
