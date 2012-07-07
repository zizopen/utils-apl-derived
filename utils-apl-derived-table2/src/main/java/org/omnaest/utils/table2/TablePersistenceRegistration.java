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
package org.omnaest.utils.table2;

import java.io.File;
import java.io.Serializable;

import org.omnaest.utils.table2.impl.persistence.SimpleDirectoryBasedTablePersistenceUsingSerializable;
import org.omnaest.utils.table2.impl.persistence.SimpleDirectoryBasedTablePersistenceUsingXStream;
import org.omnaest.utils.table2.impl.persistence.SimpleFileBasedTablePersistence;

/**
 * Registration for {@link TablePersistence} instances
 * 
 * @author Omnaest
 * @param <E>
 */
public interface TablePersistenceRegistration<E> extends Serializable
{
  /**
   * Attaches a {@link TablePersistence} and synchronizes the current {@link Table} with it immediately
   * 
   * @param tablePersistence
   * @return underlying {@link Table}
   */
  public Table<E> attach( TablePersistence<E> tablePersistence );
  
  /**
   * Detaches a {@link TablePersistence}
   * 
   * @param tablePersistence
   * @return underlying {@link Table}
   */
  public Table<E> detach( TablePersistence<E> tablePersistence );
  
  /**
   * Attaches the {@link Table} to a {@link SimpleFileBasedTablePersistence}
   * 
   * @param file
   * @return underlying {@link Table}
   */
  public Table<E> attachToFile( File file );
  
  /**
   * Attaches the {@link Table} to a {@link SimpleDirectoryBasedTablePersistenceUsingSerializable}
   * 
   * @param directory
   * @return underlying {@link Table}
   */
  public Table<E> attachToDirectoryUsingSerializable( File directory );
  
  /**
   * Attaches the {@link Table} to a {@link SimpleDirectoryBasedTablePersistenceUsingXStream}
   * 
   * @param directory
   * @return underlying {@link Table}
   */
  public Table<E> attachToDirectoryUsingXStream( File directory );
}
