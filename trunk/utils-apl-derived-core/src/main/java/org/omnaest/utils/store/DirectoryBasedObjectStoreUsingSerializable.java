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
package org.omnaest.utils.store;

import java.io.File;
import java.io.Serializable;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.converter.ElementBidirectionalConverterSerializable;

/**
 * A simple {@link Object} store based on a nested directory structure using Java serialization. All elements have to subclass the
 * {@link Serializable} interface.
 * 
 * @author Omnaest
 * @param <E>
 */
public class DirectoryBasedObjectStoreUsingSerializable<E extends Serializable> extends DirectoryBasedObjectStoreAbstract<E>
{
  private static final long serialVersionUID = -3189372961705877115L;
  
  /**
   * @see DirectoryBasedObjectStore
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   */
  public DirectoryBasedObjectStoreUsingSerializable( File baseDirectory, ExceptionHandlerSerializable exceptionHandler )
  {
    super( new ElementBidirectionalConverterSerializable<ByteArrayContainer, E>()
    {
      private static final long serialVersionUID = -7311719190343731231L;
      
      @Override
      public E convert( ByteArrayContainer byteArrayContainer )
      {
        return byteArrayContainer.toDeserializedElement();
      }
      
      @Override
      public ByteArrayContainer convertBackwards( E element )
      {
        return new ByteArrayContainer().copyFromSerialized( element );
      }
    }, baseDirectory, exceptionHandler );
  }
  
}
