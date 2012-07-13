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

import java.io.Serializable;
import java.util.Arrays;

import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
class Pipe<FROM, TO> implements Serializable
{
  private static final long                        serialVersionUID = -5157086773922721837L;
  
  private final Class<FROM>                        typeFrom;
  private final Class<TO>                          typeTo;
  private final ElementConverter<Object, Object>[] elementConverters;
  
  Pipe( Class<FROM> typeFrom, Class<TO> typeTo, ElementConverter<Object, Object>[] elementConverters )
  {
    super();
    this.typeFrom = typeFrom;
    this.typeTo = typeTo;
    this.elementConverters = elementConverters;
  }
  
  @SuppressWarnings("unchecked")
  public TO convert( FROM instance )
  {
    Object retval = instance;
    for ( ElementConverter<Object, Object> elementConverter : this.elementConverters )
    {
      retval = elementConverter.convert( retval );
    }
    return (TO) retval;
  }
  
  public TypeAndType getTypeAndType()
  {
    return new TypeAndType( this.typeFrom, this.typeTo );
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "Pipe [typeFrom=" );
    builder.append( this.typeFrom );
    builder.append( ", typeTo=" );
    builder.append( this.typeTo );
    builder.append( ", elementConverters=" );
    builder.append( Arrays.toString( this.elementConverters ) );
    builder.append( "]" );
    return builder.toString();
  }
  
}
