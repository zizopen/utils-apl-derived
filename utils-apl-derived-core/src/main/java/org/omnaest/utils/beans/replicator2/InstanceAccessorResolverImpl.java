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

import java.util.HashMap;
import java.util.Map;

import org.omnaest.utils.structure.array.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;

/**
 * @see InstanceAccessorResolver
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class InstanceAccessorResolverImpl implements InstanceAccessorResolver
{
  /* ************************************************** Constants *************************************************** */
  private static final long               serialVersionUID          = 7646339051909377254L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<Class<?>, InstanceAccessor> typeToInstanceAccessorMap = new HashMap<Class<?>, InstanceAccessor>();
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see InstanceAccessorResolverImpl
   */
  public InstanceAccessorResolverImpl()
  {
    super();
  }
  
  @Override
  public InstanceAccessor resolveInstanceAccessor( Class<?> type )
  {
    InstanceAccessor retval = this.typeToInstanceAccessorMap.get( type );
    if ( retval == null )
    {
      if ( Map.class.isAssignableFrom( type ) )
      {
        retval = new InstanceAccessorForMap();
      }
      else if ( ArrayUtils.isArrayType( type ) )
      {
        retval = new InstanceAccessorForArray( type );
      }
      else if ( ListUtils.isListType( type ) )
      {
        retval = new InstanceAccessorForList( type );
      }
      else
      {
        retval = new InstanceAccessorArbitraryObject( type );
      }
      
      this.typeToInstanceAccessorMap.put( type, retval );
    }
    return retval;
  }
  
}
