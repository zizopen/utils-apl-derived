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
import org.omnaest.utils.store.DirectoryBasedObjectStoreUsingSerializable;
import org.omnaest.utils.table.TablePersistence;

/**
 * Simple {@link TablePersistence} which writes the complete data to {@link File}s using a {@link DirectoryBasedObjectStore}. The
 * performance of this solution is very limited.
 * 
 * @author Omnaest
 * @param <E>
 */
public class SimpleDirectoryBasedTablePersistenceUsingSerializable<E> extends
    SimpleDirectoryBasedTablePersistenceAbstract<E>
{
  /* ************************************************** Constants *************************************************** */
  private static final long serialVersionUID = 1166090223383046706L;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see SimpleDirectoryBasedTablePersistenceUsingSerializable
   * @param baseDirectory
   *          {@link File}
   * @param exceptionHandler
   *          {@link ExceptionHandlerSerializable}
   */
  public SimpleDirectoryBasedTablePersistenceUsingSerializable( File baseDirectory,
                                                                      ExceptionHandlerSerializable exceptionHandler )
  {
    super( new DirectoryBasedObjectStoreUsingSerializable<E[]>( baseDirectory, exceptionHandler ) );
  }
  
}
