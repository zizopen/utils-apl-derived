/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.download;

import java.util.ArrayList;

/**
 * A download manager manages different download instances.
 * 
 * @author Omnaest
 */
public class DownloadManager implements Runnable
{
  private volatile static DownloadManager downloadMangerInstance           = null;
  
  private ArrayList<Thread>               threadPoolAspirantList           = new ArrayList<Thread>( 0 );
  private ArrayList<Thread>               threadPoolActiveList             = new ArrayList<Thread>( 0 );
  
  private static final int                checkForNewThreadsTimeInterval   = 1000;                      //ms
  private static int                      maximalActiveThreadsAllowedCount = 10;
  private Thread                          threadPoolManager                = null;
  
  private DownloadManager()
  {
    super();
  }
  
  /**
   * Returns an instance of the download manager. If no instance exists, one is created. Normally there is only one instance
   * (singleton).
   * 
   * @return
   */
  public static DownloadManager getInstance()
  {
    DownloadManager retdm = null;
    try
    {
      if ( DownloadManager.downloadMangerInstance == null )
      {
        DownloadManager dm = new DownloadManager();
        synchronized ( DownloadManager.class )
        {
          if ( DownloadManager.downloadMangerInstance == null )
          {
            DownloadManager.downloadMangerInstance = dm;
          }
        }
      }
    }
    finally
    {
      retdm = DownloadManager.downloadMangerInstance;
    }
    return retdm;
  }
  
  /**
   * Returns a new instance of a downloadconnection.
   * 
   * @return
   */
  public DownloadConnection getDownloadConnection()
  {
    DownloadConnection retdc = new DownloadConnection();
    retdc.setDownloadManager( this );
    return retdc;
  }
  
  /**
   * Adds the given thread to the thread aspirant list. As early as possible the thread will then be started from the
   * threadpoolmanager.
   * 
   * @param thread
   */
  protected void startThreadPooled( Thread thread )
  {
    this.threadPoolAspirantList.add( thread );
    this.startThreadPoolManager();
  }
  
  private void startThreadPoolManager()
  {
    if ( this.threadPoolManager == null )
    {
      this.threadPoolManager = new Thread( this );
      this.threadPoolManager.start();
    }
  }
  
  public void run()
  {
    while ( this.threadPoolAspirantList.size() > 0 || this.threadPoolActiveList.size() > 0 )
    {
      //remove all stopped threads from the activepoollist
      ArrayList<Thread> removeThreadList = new ArrayList<Thread>( 0 );
      for ( Thread iThread : this.threadPoolActiveList )
      {
        if ( !iThread.isAlive() )
        {
          removeThreadList.add( iThread );
        }
      }
      for ( Thread iRemoveThread : removeThreadList )
      {
        this.threadPoolActiveList.remove( iRemoveThread );
      }
      
      //if there are aspirants and free places in the active list, move the first thread from 
      //the aspirant list to the active list, and activate this moved thread.
      if ( this.threadPoolActiveList.size() < DownloadManager.maximalActiveThreadsAllowedCount
           && this.threadPoolAspirantList.size() > 0 )
      {
        Thread thread = this.threadPoolAspirantList.get( 0 );
        this.threadPoolActiveList.add( thread );
        this.threadPoolAspirantList.remove( thread );
        thread.start();
      }
      
      //
      try
      {
        Thread.sleep( DownloadManager.checkForNewThreadsTimeInterval );
      }
      catch ( InterruptedException e )
      {
      }
    }
    this.threadPoolManager = null;
  }
}
