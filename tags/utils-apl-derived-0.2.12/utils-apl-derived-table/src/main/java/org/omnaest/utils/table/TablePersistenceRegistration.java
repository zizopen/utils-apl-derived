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
package org.omnaest.utils.table;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

import org.omnaest.utils.table.impl.persistence.SimpleFileBasedTablePersistence;

/**
 * Registration for {@link TablePersistence} instances
 * 
 * @see TablePersistence
 * @see #attach()
 * @see #attach(TablePersistence)
 * @see #attachToFile(File)
 * @see #detach(TablePersistence)
 * @author Omnaest
 * @param <E>
 */
public interface TablePersistenceRegistration<E> extends Serializable
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   * @param <E>
   */
  public static interface TablePersistenceAttacherTarget<E> extends Serializable
  {
    public Table<E> toDirectory( File directory );
  }
  
  /**
   * @author Omnaest
   * @param <E>
   */
  public static interface TablePersistenceAttacherXML<E> extends Serializable
  {
    /**
     * This uses the XStream library to serialize the elements of the {@link Table}. XStream has to be present when using this as
     * dependency.
     * 
     * @return
     */
    public TablePersistenceAttacherTarget<E> usingXStream();
    
    /**
     * The elements have to be of a type {@link JAXB} can handle. So every complex {@link Object} needs a {@link XmlRootElement}
     * annotation, primitives are captured without such an annotation.
     * 
     * @return
     */
    public TablePersistenceAttacherTarget<E> usingJAXB();
    
  }
  
  /**
   * @author Omnaest
   * @param <E>
   */
  public static interface TablePersistenceAttacher<E> extends Serializable
  {
    public TablePersistenceAttacherTarget<E> asSerialized();
    
    public TablePersistenceAttacherXML<E> asXML();
    
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * Attaches a {@link TablePersistence} and synchronizes the current {@link Table} with it immediately. <br>
   * <br>
   * The synchronization requires any table name and table columns to be already declared before this call.
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
   *          {@link File}
   * @return underlying {@link Table}
   */
  public Table<E> attachToFile( File file );
  
  /**
   * Predefined ways to attach the {@link Table} to e.g. a directory
   * 
   * @return
   */
  public TablePersistenceAttacher<E> attach();
}
