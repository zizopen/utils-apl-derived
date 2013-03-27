/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.cluster.store;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.cluster.store.MarshallingStrategy.MarshallingException;
import org.omnaest.cluster.store.MarshallingStrategy.UnmarshallingException;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.codec.Codec;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.strings.StringTokenEncoder;
import org.omnaest.utils.strings.StringTokenEncoder.Configuration;
import org.omnaest.utils.strings.StringUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.structure.element.ElementHolder;

import com.google.common.io.Files;

public class ClusterStoreProviderFileBased extends ClusterStoreProviderAbstract implements ClusterStoreProvider
{
  private static final long   serialVersionUID    = 7052260249087562109L;
  private MarshallingStrategy marshallingStrategy = new MarshallingStrategyJavaSerialization();
  
  private File                directory;
  private ExceptionHandler    exceptionHandler    = null;
  private final IndexTable    indexTable;
  
  private static class IndexTable implements Serializable
  {
    private static final long        serialVersionUID  = -7254565782490315884L;
    private File                     directory;
    private File                     indexFile;
    private final ReadWriteLock      readWriteLock     = new ReentrantReadWriteLock();
    private ExceptionHandler         exceptionHandler  = null;
    private boolean                  backupBeforeWrite = false;
    private final StringTokenEncoder tokenEncoder      = StringUtils.tokenEncoder( new Configuration().setDelimiter( "_" )
                                                                                                      .setEncoderAndDecoder( Codec.escaping( "#",
                                                                                                                                             "_",
                                                                                                                                             "\n",
                                                                                                                                             "\r" ) ) );
    
    public static interface Transaction
    {
      public boolean execute( ByteArrayContainer dataFile );
    }
    
    public static class IndexEntry
    {
      private int            fileId;
      private final String[] qualifiers;
      private final String   typeName;
      
      public IndexEntry( int fileId, String typeName, String[] qualifiers )
      {
        super();
        this.fileId = fileId;
        this.typeName = typeName;
        this.qualifiers = qualifiers;
      }
      
      @SuppressWarnings("unused")
      public int getFileId()
      {
        return this.fileId;
      }
      
      public String[] getQualifiers()
      {
        return this.qualifiers;
      }
      
      @Override
      public String toString()
      {
        StringBuilder builder = new StringBuilder();
        builder.append( "IndexEntry [fileId=" );
        builder.append( this.fileId );
        builder.append( ", qualifiers=" );
        builder.append( Arrays.toString( this.qualifiers ) );
        builder.append( ", typeName=" );
        builder.append( this.typeName );
        builder.append( "]" );
        return builder.toString();
      }
      
      public String getTypeName()
      {
        return this.typeName;
      }
      
    }
    
    public IndexTable( File directory )
    {
      this.directory = directory;
      this.indexFile = new File( this.directory, "index.dat" );
    }
    
    public void executeDataFileWriteTransaction( Transaction transaction, String typeName, String... qualifiers )
    {
      if ( transaction != null )
      {
        Lock writeLock = this.readWriteLock.writeLock();
        writeLock.lock();
        try
        {
          List<IndexEntry> indexEntryList = this.readIndex();
          int fileId = ListUtils.indexOfNull( indexEntryList );
          if ( fileId < 0 )
          {
            fileId = indexEntryList.size();
            indexEntryList.add( new IndexEntry( fileId, typeName, qualifiers ) );
          }
          
          File newDataFile = determineDataFile( fileId );
          ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
          boolean commit = transaction.execute( byteArrayContainer );
          if ( commit )
          {
            //
            if ( this.backupBeforeWrite )
              try
              {
                final String backupSuffix = ".bak";
                if ( newDataFile.exists() )
                {
                  FileUtils.copyFile( newDataFile, new File( newDataFile.getAbsolutePath() + backupSuffix ) );
                }
                FileUtils.copyFile( this.indexFile, new File( this.indexFile.getAbsolutePath() + backupSuffix ) );
              }
              catch ( Exception e )
              {
                handleException( e );
              }
            
            //
            FileUtils.writeByteArrayToFile( newDataFile, byteArrayContainer.getContent() );
            this.writeIndex( indexEntryList );
          }
        }
        catch ( IOException e )
        {
          this.handleException( e );
        }
        finally
        {
          writeLock.unlock();
        }
      }
    }
    
    private void handleException( Exception e )
    {
      if ( this.exceptionHandler != null )
      {
        this.exceptionHandler.handleException( e );
      }
    }
    
    private File determineDataFile( int fileId )
    {
      return new File( this.directory, fileId + ".dat" );
    }
    
    public void executeDataFileReadTransaction( Transaction transaction, String typeName, String... qualifiers )
    {
      if ( transaction != null )
      {
        Lock readLock = this.readWriteLock.readLock();
        readLock.lock();
        try
        {
          Map<List<String>, Integer> qualifierToFileIdMap = this.readIndexAsQualifierToFileIdMap();
          Integer fileId = qualifierToFileIdMap.get( Arrays.asList( ArrayUtils.add( qualifiers, 0, typeName ) ) );
          if ( fileId != null )
          {
            File dataFile = this.determineDataFile( fileId );
            if ( dataFile.exists() )
            {
              final byte[] fileData = FileUtils.readFileToByteArray( dataFile );
              ByteArrayContainer byteArrayContainer = new ByteArrayContainer( fileData );
              transaction.execute( byteArrayContainer );
            }
          }
        }
        catch ( IOException e )
        {
          this.handleException( e );
        }
        finally
        {
          readLock.unlock();
        }
      }
    }
    
    private Map<List<String>, Integer> readIndexAsQualifierToFileIdMap()
    {
      List<IndexEntry> indexEntryList = this.readIndex();
      return readIndexAsQualifierToFileIdMap( indexEntryList );
    }
    
    private static Map<List<String>, Integer> readIndexAsQualifierToFileIdMap( List<IndexEntry> indexEntryList )
    {
      Map<List<String>, Integer> retmap = new HashMap<List<String>, Integer>();
      
      int index = 0;
      for ( IndexEntry indexEntry : indexEntryList )
      {
        List<String> key = Arrays.asList( ArrayUtils.add( indexEntry.getQualifiers(), 0, indexEntry.getTypeName() ) );
        Integer value = index++;
        retmap.put( key, value );
      }
      return retmap;
    }
    
    private void writeIndex( List<IndexEntry> indexEntryList )
    {
      try
      {
        List<String> lines = new ArrayList<String>();
        if ( indexEntryList != null )
        {
          for ( IndexEntry indexEntry : indexEntryList )
          {
            final String[] fullQualifiers = ArrayUtils.add( indexEntry.getQualifiers(), 0, indexEntry.getTypeName() );
            lines.add( this.tokenEncoder.encode( fullQualifiers ) );
          }
        }
        FileUtils.writeLines( this.indexFile, lines );
      }
      catch ( Exception e )
      {
        this.handleException( e );
      }
    }
    
    private List<IndexEntry> readIndex()
    {
      List<IndexEntry> retlist = new ArrayList<IndexEntry>();
      
      if ( this.indexFile.exists() )
      {
        try
        {
          List<String> lines = FileUtils.readLines( this.indexFile );
          if ( lines != null )
          {
            int index = 0;
            for ( String line : lines )
            {
              String[] tokens = this.tokenEncoder.decode( line );
              if ( tokens.length > 0 )
              {
                final int fileId = index++;
                final String[][] qualifierGroups = StringUtils.splitAsArrayGroups( tokens, 0, 1 );
                final String typeName = qualifierGroups[0][0];
                final String[] qualifiers = qualifierGroups[1];
                retlist.add( new IndexEntry( fileId, typeName, qualifiers ) );
              }
              else
              {
                retlist.add( null );
              }
            }
          }
        }
        catch ( Exception e )
        {
          this.handleException( e );
        }
      }
      
      return retlist;
    }
    
    public void setExceptionHandler( ExceptionHandler exceptionHandler )
    {
      this.exceptionHandler = exceptionHandler;
    }
    
    public void remove( String typeName, String[] qualifiers )
    {
      Lock writeLock = this.readWriteLock.writeLock();
      writeLock.lock();
      try
      {
        List<IndexEntry> indexEntryList = this.readIndex();
        Map<List<String>, Integer> qualifierToFileIdMap = readIndexAsQualifierToFileIdMap( indexEntryList );
        Integer fileId = qualifierToFileIdMap.get( Arrays.asList( ArrayUtils.add( qualifiers, 0, typeName ) ) );
        if ( fileId != null && fileId >= 0 )
        {
          final File dataFile = determineDataFile( fileId );
          FileUtils.deleteQuietly( dataFile );
          indexEntryList.remove( fileId.intValue() );
          this.writeIndex( indexEntryList );
        }
      }
      catch ( Exception e )
      {
        this.handleException( e );
      }
      finally
      {
        writeLock.unlock();
      }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ClusterStoreIdentifier<?>[] determineClusterStoreIdentifiers()
    {
      List<ClusterStoreIdentifier<?>> retlist = new ArrayList<ClusterStoreProvider.ClusterStoreIdentifier<?>>();
      
      Lock readLock = this.readWriteLock.readLock();
      readLock.lock();
      try
      {
        List<IndexEntry> indexEntryList = this.readIndex();
        for ( IndexEntry indexEntry : indexEntryList )
        {
          String[] qualifiers = indexEntry.getQualifiers();
          String typeName = indexEntry.getTypeName();
          if ( qualifiers != null && qualifiers.length > 0 )
          {
            try
            {
              final Class<?> type = Class.forName( typeName );
              retlist.add( new ClusterStoreIdentifier( type, qualifiers ) );
            }
            catch ( Exception e )
            {
            }
          }
        }
        
      }
      finally
      {
        readLock.unlock();
      }
      
      return retlist.toArray( new ClusterStoreIdentifier[0] );
    }
    
    public void clear()
    {
      Lock writeLock = this.readWriteLock.writeLock();
      writeLock.lock();
      try
      {
        Collection<Integer> fileIdCollection = this.readIndexAsQualifierToFileIdMap().values();
        for ( Integer fileId : fileIdCollection )
        {
          final File dataFile = determineDataFile( fileId );
          FileUtils.deleteQuietly( dataFile );
        }
        this.writeIndex( new ArrayList<ClusterStoreProviderFileBased.IndexTable.IndexEntry>() );
      }
      finally
      {
        writeLock.unlock();
      }
    }
    
    public Lock getWriteLock()
    {
      return this.readWriteLock.writeLock();
    }
    
    public Lock getReadLock()
    {
      return this.readWriteLock.readLock();
    }
    
    public IndexTable setBackupBeforeWrite( boolean backupBeforeWrite )
    {
      this.backupBeforeWrite = backupBeforeWrite;
      return this;
    }
  }
  
  public ClusterStoreProviderFileBased( File directory )
  {
    super();
    Assert.isNotNull( directory );
    if ( !directory.exists() )
    {
      directory.mkdirs();
    }
    Assert.isTrue( directory.isDirectory() );
    this.directory = directory;
    this.indexTable = new IndexTable( this.directory );
  }
  
  public ClusterStoreProviderFileBased()
  {
    this( Files.createTempDir() );
  }
  
  public ClusterStoreProviderFileBased setMarshallingStrategy( MarshallingStrategy marshallingStrategy )
  {
    this.marshallingStrategy = marshallingStrategy;
    return this;
  }
  
  @Override
  public <T> ClusterStore<T> getClusterStore( final ClusterStoreIdentifier<T> clusterStoreIdentifier )
  {
    Assert.isNotNull( clusterStoreIdentifier, "clusterStoreIdentifier must not be null" );
    final Class<T> type = clusterStoreIdentifier.getType();
    final String typeName = type.getName();
    final String[] qualifiers = clusterStoreIdentifier.getQualifiers();
    final IndexTable indexTable = this.indexTable;
    final MarshallingStrategy marshallingStrategy = this.marshallingStrategy;
    return new ClusterStore<T>()
    {
      @Override
      public T get()
      {
        final ElementHolder<T> retval = new ElementHolder<T>();
        IndexTable.Transaction transaction = new IndexTable.Transaction()
        {
          @Override
          public boolean execute( ByteArrayContainer dataFile )
          {
            try
            {
              retval.setElement( marshallingStrategy.unmarshal( dataFile.getContent(), type ) );
            }
            catch ( UnmarshallingException e )
            {
              handleException( e );
            }
            return true;
          }
        };
        indexTable.executeDataFileReadTransaction( transaction, typeName, qualifiers );
        return retval.getElement();
      }
      
      @Override
      public void set( final T instance )
      {
        IndexTable.Transaction transaction = new IndexTable.Transaction()
        {
          @Override
          public boolean execute( ByteArrayContainer dataFile )
          {
            boolean retval = false;
            try
            {
              byte[] data = marshallingStrategy.marshal( instance );
              dataFile.setContent( data );
              retval = true;
            }
            catch ( MarshallingException e )
            {
              handleException( e );
            }
            return retval;
          }
        };
        indexTable.executeDataFileWriteTransaction( transaction, typeName, qualifiers );
      }
      
      @Override
      public void remove()
      {
        indexTable.remove( typeName, qualifiers );
      }
    };
  }
  
  private void handleException( Exception e )
  {
    if ( this.exceptionHandler != null )
    {
      this.exceptionHandler.handleException( e );
    }
  }
  
  public ClusterStoreProviderFileBased setExceptionHandler( ExceptionHandler exceptionHandler )
  {
    this.exceptionHandler = exceptionHandler;
    this.indexTable.setExceptionHandler( exceptionHandler );
    return this;
  }
  
  @Override
  public <T> ClusterStoreIdentifier<?>[] getClusterStoreIdentifiers()
  {
    return this.indexTable.determineClusterStoreIdentifiers();
  }
  
  @Override
  public void clear()
  {
    this.indexTable.clear();
  }
  
  @Override
  public void executeWriteAtomical( Runnable runnable )
  {
    final Lock writeLock = this.indexTable.getWriteLock();
    writeLock.lock();
    try
    {
      runnable.run();
    }
    finally
    {
      writeLock.unlock();
    }
  }
  
  @Override
  public void executeReadAtomical( Runnable runnable )
  {
    final Lock readLock = this.indexTable.getReadLock();
    readLock.lock();
    try
    {
      runnable.run();
    }
    finally
    {
      readLock.unlock();
    }
  }
  
  public ClusterStoreProviderFileBased setBackupBeforeWrite( boolean backupBeforeWrite )
  {
    this.indexTable.setBackupBeforeWrite( backupBeforeWrite );
    return this;
  }
}
