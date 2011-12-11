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
package org.omnaest.utils.beans.replicator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.proxy.BeanProperty;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.tuple.TupleTwo;

/**
 * The {@link BeanReplicator} allows to make a deep copy of an {@link Object} graph
 * 
 * @author Omnaest
 */
public class BeanReplicator
{
  /* ********************************************** Variables ********************************************** */
  protected final Set<Adapter<?, ?>> adapterSet = new LinkedHashSet<Adapter<?, ?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static abstract class Adapter<FROM, TO>
  {
    /* ********************************************** Variables ********************************************** */
    private final BeanProperty                                                                                        beanProperty;
    private final Map<TupleTwo<BeanPropertyAccessors<Object>, BeanPropertyAccessors<Object>>, ElementConverter<?, ?>> mapping = new LinkedHashMap<TupleTwo<BeanPropertyAccessors<Object>, BeanPropertyAccessors<Object>>, ElementConverter<?, ?>>();
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see Adapter
     * @param sourceType
     * @param targetType
     */
    public Adapter( Class<FROM> sourceType, Class<TO> targetType )
    {
      super();
      this.beanProperty = new BeanProperty();
      
      final FROM source = this.beanProperty.newInstanceOfTransitivlyCapturedType( sourceType );
      final TO target = this.beanProperty.newInstanceOfTransitivlyCapturedType( targetType );
      
      //
      this.declare( source, target );
    }
    
    protected class MappingTo
    {
      /* ********************************************** Variables ********************************************** */
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom;
      
      /* ********************************************** Methods ********************************************** */
      
      protected MappingTo( BeanPropertyAccessors<Object> beanPropertyAccessorsFrom )
      {
        super();
        this.beanPropertyAccessorsFrom = beanPropertyAccessorsFrom;
      }
      
      public MappingUsing to( Object... to )
      {
        BeanPropertyAccessors<Object> beanPropertyAccessorsTo = Adapter.this.beanProperty.accessor.of( to );
        return new MappingUsing( this.beanPropertyAccessorsFrom, beanPropertyAccessorsTo );
      }
    }
    
    protected class MappingUsing
    {
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom;
      private final BeanPropertyAccessors<Object> beanPropertyAccessorsTo;
      
      protected MappingUsing( BeanPropertyAccessors<Object> beanPropertyAccessorsFrom,
                              BeanPropertyAccessors<Object> beanPropertyAccessorsTo )
      {
        super();
        this.beanPropertyAccessorsFrom = beanPropertyAccessorsFrom;
        this.beanPropertyAccessorsTo = beanPropertyAccessorsTo;
      }
      
      public void using( ElementConverter<?, ?> elementConverter )
      {
        TupleTwo<BeanPropertyAccessors<Object>, BeanPropertyAccessors<Object>> key = new TupleTwo<BeanPropertyAccessors<Object>, BeanPropertyAccessors<Object>>(
                                                                                                                                                                 this.beanPropertyAccessorsFrom,
                                                                                                                                                                 this.beanPropertyAccessorsTo );
        ElementConverter<?, ?> value = elementConverter;
        Adapter.this.mapping.put( key, value );
      }
      
    }
    
    protected MappingTo map( Object... from )
    {
      final BeanPropertyAccessors<Object> beanPropertyAccessorsFrom = this.beanProperty.accessor.of( from );
      Assert.isFalse( beanPropertyAccessorsFrom.isEmpty(), "There was no property method of the source proxy called" );
      return new MappingTo( beanPropertyAccessorsFrom );
    }
    
    /**
     * @param source
     * @param target
     */
    public abstract void declare( FROM source, TO target );
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see BeanReplicator
   * @param adapters
   */
  public BeanReplicator( Adapter<?, ?>... adapters )
  {
    super();
    this.adapterSet.addAll( Arrays.asList( adapters ) );
  }
  
  /**
   * @see BeanReplicator
   */
  public BeanReplicator()
  {
    super();
  }
  
  public <R> R copy( Object bean )
  {
    //
    R retval = null;
    
    //
    if ( bean != null )
    {
      
    }
    
    //
    return retval;
  }
  
}
