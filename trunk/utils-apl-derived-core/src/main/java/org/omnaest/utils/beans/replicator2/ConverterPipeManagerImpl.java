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
import java.util.List;
import java.util.Map;

import org.omnaest.utils.beans.replicator2.BeanReplicator.ConverterPipeDeclarer;
import org.omnaest.utils.beans.replicator2.BeanReplicator.PipeBuilder;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * @see ConverterPipeManager
 * @see ConverterPipeContainer
 * @see ConverterPipeDeclarer
 * @author Omnaest
 */
@SuppressWarnings("javadoc")
class ConverterPipeManagerImpl implements ConverterPipeManager, ConverterPipeContainer
{
  /* ************************************************** Constants *************************************************** */
  private static final long            serialVersionUID     = -5610874488869665883L;
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private Map<TypeAndType, Pipe<?, ?>> typeAndTypeToPipeMap = new HashMap<TypeAndType, Pipe<?, ?>>();
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see PipeBuilder
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  private static final class PipeBuilderImpl<FROM, TO> implements BeanReplicator.PipeBuilder<FROM, TO>
  {
    /* ************************************** Variables / State (internal/hiding) ************************************* */
    private final List<ElementConverter<?, ?>> elementConverterList;
    private final Class<FROM>                  typeFrom;
    private final ConverterPipeContainer       converterPipeContainer;
    
    /* *************************************************** Methods **************************************************** */
    
    /**
     * @see PipeBuilderImpl
     * @param typeFrom
     * @param converterPipeContainer
     *          {@link ConverterPipeContainer}
     */
    PipeBuilderImpl( Class<FROM> typeFrom, ConverterPipeContainer converterPipeContainer )
    {
      super();
      this.typeFrom = typeFrom;
      this.converterPipeContainer = converterPipeContainer;
      this.elementConverterList = null;
    }
    
    /**
     * @see PipeBuilderImpl
     * @param typeFrom
     * @param elementConverterList
     * @param converterPipeContainer
     */
    private PipeBuilderImpl( Class<FROM> typeFrom, List<ElementConverter<?, ?>> elementConverterList,
                             ConverterPipeContainer converterPipeContainer )
    {
      super();
      this.typeFrom = typeFrom;
      this.elementConverterList = elementConverterList;
      this.converterPipeContainer = converterPipeContainer;
    }
    
    @Override
    public void to( Class<TO> typeTo )
    {
      @SuppressWarnings("unchecked")
      final ElementConverter<Object, Object>[] elementConverters = this.elementConverterList.toArray( new ElementConverter[0] );
      final Pipe<FROM, TO> pipe = new Pipe<FROM, TO>( this.typeFrom, typeTo, elementConverters );
      this.converterPipeContainer.add( pipe );
    }
    
    @Override
    public <OVER> BeanReplicator.PipeBuilder<FROM, OVER> over( ElementConverter<FROM, OVER> elementConverter )
    {
      return new ConverterPipeManagerImpl.PipeBuilderImpl<FROM, OVER>( this.typeFrom,
                                                                       ListUtils.addToNewList( this.elementConverterList,
                                                                                               elementConverter ),
                                                                       this.converterPipeContainer );
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  @Override
  public <FROM> BeanReplicator.PipeBuilder<FROM, FROM> addConverterPipeFrom( Class<FROM> typeFrom )
  {
    return new ConverterPipeManagerImpl.PipeBuilderImpl<FROM, FROM>( typeFrom, this );
  }
  
  @Override
  public void add( Pipe<?, ?> pipe )
  {
    final TypeAndType key = pipe.getTypeAndType();
    final Pipe<?, ?> value = pipe;
    this.typeAndTypeToPipeMap.put( key, value );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Pipe<Object, Object> resolveConverterPipeFor( Class<?> typeFrom, Class<?> typeTo )
  {
    return (Pipe<Object, Object>) this.typeAndTypeToPipeMap.get( new TypeAndType( typeFrom, typeTo ) );
  }
  
}
