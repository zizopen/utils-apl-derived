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
package org.omnaest.utils.table.impl.persistence;

import java.io.File;

import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.store.DirectoryBasedObjectStore;
import org.omnaest.utils.store.DirectoryBasedObjectStoreUsingJAXB;
import org.omnaest.utils.table.TablePersistence;

/**
 * Simple {@link TablePersistence} which writes the complete data to {@link File}s using a {@link DirectoryBasedObjectStore}. The
 * performance of this solution is very limited.
 * 
 * @author Omnaest
 * @param <E>
 */
public class SimpleDirectoryBasedTablePersistenceUsingJAXB<E> extends SimpleDirectoryBasedTablePersistenceAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 2863001440265513534L;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SimpleDirectoryBasedTablePersistenceUsingJAXB
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   * @param elementType
   */
  public SimpleDirectoryBasedTablePersistenceUsingJAXB( File baseDirectory, ExceptionHandlerSerializable exceptionHandler,
                                                        Class<E[]> elementType )
  {
    super( new DirectoryBasedObjectStoreUsingJAXB<E[]>( baseDirectory, exceptionHandler, elementType ) );
  }
  
}
