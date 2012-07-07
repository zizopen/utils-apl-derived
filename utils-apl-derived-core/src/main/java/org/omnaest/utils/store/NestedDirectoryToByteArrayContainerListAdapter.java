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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.events.exception.ExceptionHandlerSerializable;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerBooleanState;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerIgnoring;
import org.omnaest.utils.structure.collection.list.ListAbstract;
import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * Adapter which takes a nested directory structure and allows to access it as a {@link List} of {@link ByteArrayContainer}s
 * 
 * @author Omnaest
 */
public class NestedDirectoryToByteArrayContainerListAdapter extends ListAbstract<ByteArrayContainer>
{
  /* ************************************************** Constants *************************************************** */
  private static final long                  serialVersionUID = 2675512592103223050L;
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final File                         baseDirectory;
  private final ExceptionHandlerSerializable exceptionHandler;
  
  /* *************************************************** Methods **************************************************** */
  
  public NestedDirectoryToByteArrayContainerListAdapter( File baseDirectory )
  {
    this( baseDirectory, new ExceptionHandlerIgnoring() );
  }
  
  public NestedDirectoryToByteArrayContainerListAdapter( File baseDirectory,
                                                         ExceptionHandlerSerializable exceptionHandlerSerializable )
  {
    super();
    this.exceptionHandler = exceptionHandlerSerializable;
    Assert.isNotNull( baseDirectory, "baseDirectory must not be null" );
    final boolean exists = baseDirectory.exists();
    Assert.isTrue( !exists || baseDirectory.isDirectory() );
    this.baseDirectory = baseDirectory;
  }
  
  @Override
  public boolean add( ByteArrayContainer byteArrayContainer )
  {
    if ( byteArrayContainer == null )
    {
      byteArrayContainer = new ByteArrayContainer();
    }
    
    final int index = this.size();
    final ExceptionHandlerBooleanState exceptionHandler = new ExceptionHandlerBooleanState();
    {
      final File file = this.determineFileForIndex( index );
      final ByteArrayContainer container = ByteArrayContainer.valueOf( byteArrayContainer );
      container.setExceptionHandler( exceptionHandler );
      container.writeTo( file );
    }
    return exceptionHandler.hasNoErrors();
  }
  
  @Override
  public void add( int index, ByteArrayContainer byteArrayContainer )
  {
    if ( byteArrayContainer == null )
    {
      byteArrayContainer = new ByteArrayContainer();
    }
    
    final File file = this.determineFileForIndex( index );
    byteArrayContainer.writeTo( file );
  }
  
  private Collection<File> determineFileCollection()
  {
    final IOFileFilter fileFilter = new NameFileFilter( new String[] { "0.dat", "1.dat", "2.dat", "3.dat", "4.dat", "5.dat",
        "6.dat", "7.dat", "8.dat", "9.dat" } );
    Collection<File> listFiles = FileUtils.listFiles( this.getBaseDirectory(), fileFilter, TrueFileFilter.INSTANCE );
    return listFiles;
  }
  
  private File determineFileForIndex( int index )
  {
    File file = this.getBaseDirectory();
    {
      final char[] indexTokens = String.valueOf( index ).toCharArray();
      final char[] directoryNames = Arrays.copyOf( indexTokens, indexTokens.length - 1 );
      final char fileName = indexTokens[indexTokens.length - 1];
      for ( char directoryName : directoryNames )
      {
        file = new File( file, String.valueOf( directoryName ) );
        if ( !file.exists() )
        {
          file.mkdir();
        }
      }
      file = new File( file, String.valueOf( fileName ) + ".dat" );
    }
    return file;
  }
  
  @Override
  public ByteArrayContainer get( int index )
  {
    ByteArrayContainer retval = null;
    try
    {
      final File file = this.determineFileForIndex( index );
      retval = new ByteArrayContainer().copyFrom( file );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    return retval;
  }
  
  @Override
  public int indexOf( Object o )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public int lastIndexOf( Object o )
  {
    throw new UnsupportedOperationException();
  }
  
  @Override
  public ByteArrayContainer remove( int index )
  {
    ByteArrayContainer retval = this.get( index );
    
    try
    {
      final File file = this.determineFileForIndex( index );
      deleteFileOrDirectory( file );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return retval;
  }
  
  private static void deleteFileOrDirectory( File file ) throws IOException
  {
    if ( file.exists() )
    {
      FileUtils.forceDelete( file );
    }
  }
  
  @Override
  public ByteArrayContainer set( int index, ByteArrayContainer byteArrayContainer )
  {
    final ByteArrayContainer retval = this.get( index );
    
    if ( byteArrayContainer == null )
    {
      byteArrayContainer = new ByteArrayContainer();
    }
    
    try
    {
      final File file = this.determineFileForIndex( index );
      byteArrayContainer.writeTo( file );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return retval;
  }
  
  @Override
  public int size()
  {
    int size = 0;
    try
    {
      Collection<File> listFiles = determineFileCollection();
      size = listFiles.size();
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
    
    return size;
  }
  
  @Override
  public void clear()
  {
    try
    {
      deleteFileOrDirectory( this.getBaseDirectory() );
    }
    catch ( Exception e )
    {
      this.exceptionHandler.handleException( e );
    }
  }
  
  private File getBaseDirectory()
  {
    if ( !this.baseDirectory.exists() )
    {
      this.baseDirectory.mkdir();
    }
    return this.baseDirectory;
  }
  
}
