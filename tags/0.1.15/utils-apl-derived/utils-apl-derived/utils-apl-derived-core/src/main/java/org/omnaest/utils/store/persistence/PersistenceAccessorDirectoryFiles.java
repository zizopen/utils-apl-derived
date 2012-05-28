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
package org.omnaest.utils.store.persistence;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.events.exception.ExceptionHandlerManager;
import org.omnaest.utils.events.exception.ExceptionHandlerManager.ExceptionHandlerRegistration;
import org.omnaest.utils.store.ElementStore.PersistenceAccessor;
import org.omnaest.utils.store.persistence.marshaller.MarshallerAndUnmarshaller;
import org.omnaest.utils.store.persistence.marshaller.MarshallerAndUnmarshallerUsingObjectStream;
import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * A {@link PersistenceAccessor} which uses a given directory to write {@link File}s to disk containing one marshalled element
 * each. <br>
 * To modify the marshaling syntax see {@link #setMarshallerAndUnmarshaller(MarshallerAndUnmarshaller)} <br>
 * <br>
 * Any {@link Exception} is catched and fowarded to registered {@link ExceptionHandler}s. See
 * {@link #getExceptionHandlerRegistration()} to register {@link ExceptionHandler}s.
 * 
 * @author Omnaest
 * @param <E>
 */
public class PersistenceAccessorDirectoryFiles<E> implements PersistenceAccessor<E>
{
  /* ********************************************** Variables ********************************************** */
  protected final File                directory;
  
  /* ********************************************** Beans / Services / References ********************************************** */
  protected MarshallerAndUnmarshaller marshallerAndUnmarshaller = new MarshallerAndUnmarshallerUsingObjectStream();
  protected ExceptionHandlerManager   exceptionHandlerManager   = new ExceptionHandlerManager();
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see PersistenceAccessorDirectoryFiles
   * @param directory
   */
  public PersistenceAccessorDirectoryFiles( File directory )
  {
    //
    super();
    this.directory = directory;
    
    //
    Assert.isNotNull( directory, "Given file reference must not be null" );
    if ( directory.exists() )
    {
      Assert.isTrue( this.directory.isDirectory(), directory + " is not a directory" );
      Assert.isTrue( this.directory.canWrite(), directory + " is not writable" );
      Assert.isTrue( this.directory.canRead(), directory + " is not readable" );
    }
    else
    {
      Assert.isTrue( directory.mkdir(), "Failed to create directory named " + directory );
    }
  }
  
  @Override
  public void add( long identifier, E element )
  {
    //
    try
    {
      //
      final byte[] buffer = this.marshallerAndUnmarshaller.marshal( element );
      
      //
      final File file = this.getFileForIdentifier( identifier );
      
      //
      final ByteArrayContainer byteArrayContainer = new ByteArrayContainer( buffer );
      byteArrayContainer.save( file );
      
    }
    catch ( Exception e )
    {
      this.exceptionHandlerManager.getExceptionHandler().handleException( e );
    }
  }
  
  /**
   * @param identifier
   * @return
   */
  protected File getFileForIdentifier( long identifier )
  {
    return new File( this.directory, "" + identifier );
  }
  
  @Override
  public void remove( long identifier )
  {
    //
    try
    {
      //
      final File file = this.getFileForIdentifier( identifier );
      if ( file != null && file.exists() )
      {
        file.delete();
      }
    }
    catch ( Exception e )
    {
      this.exceptionHandlerManager.getExceptionHandler().handleException( e );
    }
  }
  
  @Override
  public Map<E, Long> getElementToIdentifierMap()
  {
    //
    final Map<E, Long> retmap = new HashMap<E, Long>();
    
    //
    try
    {
      //
      final File[] listFiles = this.directory.listFiles();
      for ( File file : listFiles )
      {
        //
        final String name = file.getName();
        if ( StringUtils.isNumeric( name ) && file.isFile() )
        {
          //
          final ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
          byteArrayContainer.load( file );
          
          //
          final E element = this.marshallerAndUnmarshaller.unmarshal( byteArrayContainer.getContent() );
          if ( element != null )
          {
            //
            final long identifier = NumberUtils.toLong( name );
            retmap.put( element, identifier );
          }
        }
      }
      
    }
    catch ( Exception e )
    {
      this.exceptionHandlerManager.getExceptionHandler().handleException( e );
    }
    
    //
    return retmap;
  }
  
  /**
   * Sets a new {@link MarshallerAndUnmarshaller}. The default is the {@link MarshallerAndUnmarshallerUsingObjectStream} which
   * relies on {@link Serializable} elements
   * 
   * @see MarshallerAndUnmarshallerUsingObjectStream
   * @param marshallerAndUnmarshaller
   *          the {@link MarshallerAndUnmarshaller} to set
   * @return this
   */
  public PersistenceAccessorDirectoryFiles<E> setMarshallerAndUnmarshaller( MarshallerAndUnmarshaller marshallerAndUnmarshaller )
  {
    this.marshallerAndUnmarshaller = marshallerAndUnmarshaller;
    return this;
  }
  
  @Override
  public ExceptionHandlerRegistration getExceptionHandlerRegistration()
  {
    return this.exceptionHandlerManager.getExceptionHandlerRegistration();
  }
}
