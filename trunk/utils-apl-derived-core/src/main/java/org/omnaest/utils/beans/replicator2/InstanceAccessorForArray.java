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
package org.omnaest.utils.beans.replicator2;

import org.omnaest.utils.operation.foreach.Range;
import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;
import org.omnaest.utils.structure.iterator.IterableUtils;

/**
 * {@link InstanceAccessor} for arrays
 * 
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class InstanceAccessorForArray implements InstanceAccessor
{
  /* ************************************************** Constants *************************************************** */
  private static final long     serialVersionUID = 2333101719267731829L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final Class<Object[]> type;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see InstanceAccessorForArray
   * @param type
   */
  @SuppressWarnings("unchecked")
  InstanceAccessorForArray( Class<?> type )
  {
    super();
    this.type = (Class<Object[]>) type;
  }
  
  @Override
  public PropertyAccessor getPropertyAccessor( final String propertyName, final Object instance )
  {
    final Object[] array = determineArrayInstanceFrom( instance );
    final int index = Integer.valueOf( propertyName );
    return new PropertyAccessor()
    {
      private static final long serialVersionUID = -3691382969720287591L;
      
      @Override
      public void setValue( Object value )
      {
        if ( array != null )
        {
          array[index] = value;
        }
      }
      
      @Override
      public Object getValue()
      {
        return array != null ? array[index] : null;
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
      
      @Override
      public Object getFactoryParameter()
      {
        return ArrayUtils.length( array );
      }
    };
  }
  
  @Override
  public Iterable<String> getPropertyNameIterable( Object instance )
  {
    final Object[] array = determineArrayInstanceFrom( instance );
    final Iterable<? extends Number> range = new Range( 0, array.length - 1 );
    final ElementConverter<Number, String> elementConverter = new ElementConverterNumberToString();
    return IterableUtils.<Number, String> adapter( range, elementConverter );
  }
  
  private static Object[] determineArrayInstanceFrom( Object instance )
  {
    return (Object[]) instance;
  }
  
  @Override
  public Class<?> getType()
  {
    return this.type;
  }
  
}
