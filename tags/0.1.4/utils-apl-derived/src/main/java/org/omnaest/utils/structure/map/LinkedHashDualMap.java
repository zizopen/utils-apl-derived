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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dual map implementation which makes use of two {@link LinkedHashMap} instances to get an index on both elements.
 * 
 * @author Omnaest
 * @param <FIRSTELEMENT>
 * @param <SECONDELEMENT>
 */
public class LinkedHashDualMap<FIRSTELEMENT, SECONDELEMENT> implements DualMap<FIRSTELEMENT, SECONDELEMENT>
{
  /* ********************************************** Variables ********************************************** */
  protected Map<FIRSTELEMENT, SECONDELEMENT> firstElementToSecondElementMap = new LinkedHashMap<FIRSTELEMENT, SECONDELEMENT>();
  protected Map<SECONDELEMENT, FIRSTELEMENT> secondElementToFirstElementMap = new LinkedHashMap<SECONDELEMENT, FIRSTELEMENT>();
  
  /* ********************************************** Methods ********************************************** */
  
  @Override
  public void clear()
  {
    this.firstElementToSecondElementMap.clear();
    this.secondElementToFirstElementMap.clear();
  }
  
  @Override
  public boolean contains( Object element )
  {
    return this.firstElementToSecondElementMap.containsKey( element )
           || this.secondElementToFirstElementMap.containsKey( element );
  }
  
  @Override
  public boolean containsFirstElement( FIRSTELEMENT firstElement )
  {
    return this.firstElementToSecondElementMap.containsKey( firstElement );
  }
  
  @Override
  public boolean containsSecondElement( SECONDELEMENT secondElement )
  {
    return this.secondElementToFirstElementMap.containsKey( secondElement );
  }
  
  @Override
  public boolean isEmpty()
  {
    return this.firstElementToSecondElementMap.isEmpty() && this.secondElementToFirstElementMap.isEmpty();
  }
  
  @Override
  public List<FIRSTELEMENT> getFirstElementList()
  {
    return new ArrayList<FIRSTELEMENT>( this.firstElementToSecondElementMap.keySet() );
  }
  
  @Override
  public List<SECONDELEMENT> getSecondElementList()
  {
    return new ArrayList<SECONDELEMENT>( this.secondElementToFirstElementMap.keySet() );
  }
  
  @Override
  public DualMap<FIRSTELEMENT, SECONDELEMENT> put( FIRSTELEMENT firstElement, SECONDELEMENT secondElement )
  {
    //
    this.firstElementToSecondElementMap.put( firstElement, secondElement );
    this.secondElementToFirstElementMap.put( secondElement, firstElement );
    
    //
    return this;
  }
  
  @Override
  public void removeFirstElement( FIRSTELEMENT firstElement )
  {
    SECONDELEMENT secondelement = this.firstElementToSecondElementMap.remove( firstElement );
    this.secondElementToFirstElementMap.remove( secondelement );
  }
  
  @Override
  public void removeSecondElement( SECONDELEMENT secondElement )
  {
    FIRSTELEMENT firstelement = this.secondElementToFirstElementMap.remove( secondElement );
    this.firstElementToSecondElementMap.remove( firstelement );
  }
  
  @Override
  public int size()
  {
    return Math.max( this.firstElementToSecondElementMap.size(), this.secondElementToFirstElementMap.size() );
  }
  
  @Override
  public Map<FIRSTELEMENT, SECONDELEMENT> getFirstElementToSecondElementMap()
  {
    return new LinkedHashMap<FIRSTELEMENT, SECONDELEMENT>( this.firstElementToSecondElementMap );
  }
  
  @Override
  public Map<SECONDELEMENT, FIRSTELEMENT> getSecondElementToFirstElementMap()
  {
    return new LinkedHashMap<SECONDELEMENT, FIRSTELEMENT>( this.secondElementToFirstElementMap );
  }
  
  @Override
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAll( DualMap<? extends FIRSTELEMENT, ? extends SECONDELEMENT> firstElementAndSecondElementDualMap )
  {
    //
    if ( firstElementAndSecondElementDualMap != null )
    {
      //
      Map<? extends FIRSTELEMENT, ? extends SECONDELEMENT> firstElementToSecondElementMap = firstElementAndSecondElementDualMap.getFirstElementToSecondElementMap();
      if ( firstElementToSecondElementMap != null )
      {
        this.putAllFirstElementToSecondElement( firstElementToSecondElementMap );
      }
      
      //
      Map<? extends SECONDELEMENT, ? extends FIRSTELEMENT> secondElementToFirstElementMap = firstElementAndSecondElementDualMap.getSecondElementToFirstElementMap();
      if ( secondElementToFirstElementMap != null )
      {
        this.putAllSecondElementToFirstElement( secondElementToFirstElementMap );
      }
    }
    
    // 
    return this;
  }
  
  @Override
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAllFirstElementToSecondElement( Map<? extends FIRSTELEMENT, ? extends SECONDELEMENT> firstElementToSecondElementMap )
  {
    //
    if ( firstElementToSecondElementMap != null )
    {
      for ( FIRSTELEMENT firstElement : firstElementToSecondElementMap.keySet() )
      {
        this.put( firstElement, firstElementToSecondElementMap.get( firstElement ) );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public DualMap<FIRSTELEMENT, SECONDELEMENT> putAllSecondElementToFirstElement( Map<? extends SECONDELEMENT, ? extends FIRSTELEMENT> secondElementToFirstElementMap )
  {
    //
    if ( secondElementToFirstElementMap != null )
    {
      for ( SECONDELEMENT secondelement : secondElementToFirstElementMap.keySet() )
      {
        this.put( secondElementToFirstElementMap.get( secondelement ), secondelement );
      }
    }
    
    //
    return this;
  }
  
  @Override
  public FIRSTELEMENT getFirstElementBy( SECONDELEMENT secondElement )
  {
    return this.secondElementToFirstElementMap.get( secondElement );
  }
  
  @Override
  public SECONDELEMENT getSecondElementBy( FIRSTELEMENT firstElement )
  {
    return this.firstElementToSecondElementMap.get( firstElement );
  }
  
  @Override
  public DualMap<SECONDELEMENT, FIRSTELEMENT> invert()
  {
    //
    DualMap<SECONDELEMENT, FIRSTELEMENT> retval = new LinkedHashDualMap<SECONDELEMENT, FIRSTELEMENT>();
    
    //
    retval.putAllFirstElementToSecondElement( this.getSecondElementToFirstElementMap() );
    retval.putAllSecondElementToFirstElement( this.getFirstElementToSecondElementMap() );
    
    // 
    return retval;
  }
  
  @Override
  public String toString()
  {
    return String.format( "LinkedHashDualMap [firstElementToSecondElementMap=%s]", this.firstElementToSecondElementMap );
  }
  
}
