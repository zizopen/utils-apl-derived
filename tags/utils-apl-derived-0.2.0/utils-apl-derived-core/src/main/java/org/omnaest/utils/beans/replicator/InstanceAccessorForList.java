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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.omnaest.utils.operation.foreach.Range;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * @author Omnaest
 */
class InstanceAccessorForList implements InstanceAccessor
{
  private static final long serialVersionUID = -516323037234035344L;
  
  private final Class<?>    type;
  
  /**
   * @see InstanceAccessorForList
   * @param type
   */
  InstanceAccessorForList( Class<?> type )
  {
    this.type = type;
  }
  
  @Override
  public PropertyAccessor getPropertyAccessor( final String propertyName, Object instance )
  {
    final List<Object> list = determineListInstanceFrom( instance );
    final int index = Integer.valueOf( propertyName );
    return new PropertyAccessor()
    {
      private static final long serialVersionUID = 704781840105684736L;
      
      @Override
      public void setValue( Object value )
      {
        ListUtils.set( list, index, value );
      }
      
      @Override
      public Object getValue()
      {
        return ListUtils.get( list, index );
      }
      
      @Override
      public Class<?> getType()
      {
        final Object value = this.getValue();
        return value != null ? value.getClass() : null;
      }
      
      @Override
      public String getPropertyName()
      {
        return propertyName;
      }
      
    };
  }
  
  @Override
  public Iterable<String> getPropertyNameIterable( Object instance )
  {
    final List<Object> list = determineListInstanceFrom( instance );
    final Iterable<? extends Number> range = new Range( 0, list.size() - 1 );
    final ElementConverter<Number, String> elementConverter = new ElementConverterNumberToString();
    return IterableUtils.<Number, String> adapter( range, elementConverter );
  }
  
  @SuppressWarnings("unchecked")
  private static List<Object> determineListInstanceFrom( Object instance )
  {
    return (List<Object>) instance;
  }
  
  @Override
  public Class<?> getType()
  {
    return this.type;
  }
  
  @Override
  public Map<String, Object> determineFactoryMetaInformation( Object instance )
  {
    int size = 0;
    if ( instance instanceof Collection )
    {
      size = CollectionUtils.size( instance );
    }
    final Map<String, Object> retmap = MapUtils.builder().put( "size", (Object) size ).buildAs().hashMap();
    return retmap;
  }
  
}
