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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.omnaest.utils.time.DurationCapture;

/**
 * A download connection offers a connection to a set url, opens a stream and loads the content into a stream.
 * 
 * @author Omnaest
 */
public class DownloadConnection implements Runnable
{
  private URL                  url                    = null;
  private String               authenticationUser     = null;
  private String               authenticationPassword = null;
  private byte[]               content                = null;
  
  private DurationCapture      timeDuration           = DurationCapture.newInstance();
  private URLConnection        urlConnection          = null;
  
  private long                 estimatedByteSize      = 0;
  private long                 currentlyLoadedBytes   = 0;
  private String               contentEncoding        = null;
  private String               contentType            = null;
  
  private long                 timeExpired            = 0;
  
  private DownloadManager      downloadManager        = null;
  private Thread               thread                 = null;
  
  private boolean              abortDownload          = false;
  private String               standardEncoding       = "utf-8";
  private String               usedStringEncoding     = null;
  
  private ArrayList<Exception> exceptionList          = new ArrayList<Exception>( 0 );
  
  private DownloadMonitor      downloadMonitor        = null;
  
  public interface DownloadMonitor
  {
    public void monitoring( int processState );
  }
  
  public DownloadConnection setUrl( URL url )
  {
    this.url = url;
    return this;
  }
  
  public void run()
  {
    this.download();
    this.thread = null;
  }
  
  /**
   * Downloads the set url source through a single thread created. To find out, if the thread still runs, use isAlive()
   * 
   * @return
   */
  public DownloadConnection threadedDownload()
  {
    this.thread = new Thread( this );
    //if there is a downloadmanager uses the threadpool of the manager, else start the thread directly
    if ( this.downloadManager != null )
    {
      this.downloadManager.startThreadPooled( this.thread );
    }
    else
    {
      this.thread.start();
    }
    return this;
  }
  
  /**
   * Downloads the given url through a single thread created for this purpose. Use isAlive() to find out, if the download is still
   * in progress.
   * 
   * @param url
   * @return
   */
  public DownloadConnection threadedDownload( URL url )
  {
    DownloadConnection retval = null;
    if ( url != null )
    {
      this.setUrl( url );
      retval = this.threadedDownload();
    }
    return retval;
  }
  
  /**
   * Interrupts and cancels the current download immediately.
   */
  public void abortDownload()
  {
    this.abortDownload = true;
  }
  
  /**
   * If the download is initiated in threaded mode, this indicates if the current download thread is still active.
   * 
   * @return
   */
  public boolean isAlive()
  {
    boolean retval = false;
    if ( this.thread != null )
    {
      retval = true;
    }
    return retval;
  }
  
  /**
   * Returns the percentage of the currently loaded bytes towards the estimated bytes to load.
   * 
   * @return
   */
  public int processStatePercentage()
  {
    int retval = 0;
    
    if ( this.estimatedByteSize > 0 )
    {
      retval = Math.round( ( this.currentlyLoadedBytes * 100 ) / this.estimatedByteSize );
    }
    return retval;
  }
  
  /**
   * Loads the content from a given url source.
   * 
   * @param url
   */
  public DownloadConnection download( URL url )
  {
    this.setUrl( url );
    this.download();
    return this;
  }
  
  /**
   * Loads the content from the set url source.
   */
  public DownloadConnection download()
  {
    if ( this.url != null )
    {
      if ( this.openConnection() )
      {
        this.prepareDownload();
        this.downloadContent();
        this.closeConnection();
      }
    }
    return this;
  }
  
  public int getHTTPStatusCode()
  {
    //
    int retval = -1;
    
    //
    if ( this.urlConnection != null && this.urlConnection instanceof HttpURLConnection )
    {
      try
      {
        retval = ( (HttpURLConnection) this.urlConnection ).getResponseCode();
      }
      catch ( IOException e )
      {
        e.printStackTrace();
      }
    }
    
    //
    return retval;
  }
  
  private void prepareDownload()
  {
    //
    this.timeDuration.startTimeMeasurement( "prepareDownload" );
    
    //
    this.estimatedByteSize = this.urlConnection.getContentLength();
    this.contentEncoding = this.urlConnection.getContentEncoding();
    this.contentType = this.urlConnection.getContentType();
    
    //
    this.timeDuration.stopTimeMeasurement( "prepareDownload" );
    
  }
  
  private synchronized boolean openConnection()
  {
    //
    this.timeDuration.startTimeMeasurement( "openConnection" );
    this.exceptionList.clear();
    
    //
    this.abortDownload = false;
    
    //
    boolean retval = false;
    
    //
    try
    {
      this.urlConnection = null;
      this.urlConnection = this.url.openConnection();
      
      if ( this.url.getProtocol().toLowerCase().matches( "http?" ) )
      {
        HttpURLConnection huc = (HttpURLConnection) this.urlConnection;
        
        //set properties
        huc.setInstanceFollowRedirects( true );
        huc.setDefaultUseCaches( true );
        
        //sets password authenticator if username and password have to be used
        if ( this.authenticationUser != null && this.authenticationPassword != null )
        {
          Authenticator.setDefault( new Authenticator()
          {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
              return new PasswordAuthentication( DownloadConnection.this.authenticationUser,
                                                 DownloadConnection.this.authenticationPassword.toCharArray() );
            }
          } );
        }
        
        //connect
        huc.connect();
        this.urlConnection = huc;
      }
      retval = true;
    }
    catch ( IOException e )
    {
      this.exceptionList.add( e );
      e.printStackTrace();
      this.urlConnection = null;
    }
    
    //
    this.timeDuration.stopTimeMeasurement( "openConnection" );
    
    //
    return retval;
  }
  
  private void downloadContent()
  {
    //
    this.timeDuration.startTimeMeasurement( "downloadContent" );
    
    //
    try
    {
      InputStream is = this.urlConnection.getInputStream();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      
      int readByte = 0;
      this.currentlyLoadedBytes = 0;
      this.downloadMonitoring( this.processStatePercentage() );
      while ( ( readByte = is.read() ) >= 0 && !this.abortDownload )
      {
        bos.write( readByte );
        
        //processstate
        this.currentlyLoadedBytes++;
        this.downloadMonitoring( this.processStatePercentage() );
      }
      
      is.close();
      bos.close();
      this.content = bos.toByteArray();
      
      //
      if ( this.abortDownload )
      {
        this.exceptionList.add( new Exception( "Download aborted by wish." ) );
      }
      
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      this.exceptionList.add( e );
    }
    
    //
    this.timeDuration.stopTimeMeasurement( "downloadContent" );
  }
  
  private void downloadMonitoring( int processState )
  {
    if ( this.downloadMonitor != null )
    {
      this.downloadMonitor.monitoring( processState );
    }
  }
  
  private void closeConnection()
  {
    //
    this.timeDuration.startTimeMeasurement( "closeConnection" );
    
    //
    if ( this.url.getProtocol().toLowerCase().matches( "http?" ) )
    {
      HttpURLConnection huc = (HttpURLConnection) this.urlConnection;
      this.url = huc.getURL();
      huc.disconnect();
    }
    
    //
    if ( this.estimatedByteSize > 0 && this.content.length != this.estimatedByteSize )
    {
      this.exceptionList.add( new IOException() );
    }
    
    //
    this.timeDuration.stopTimeMeasurement( "closeConnection" );
    this.timeExpired = this.timeDuration.stopTimeMeasurement().getDurationInMilliseconds();
  }
  
  /**
   * Returns the loaded content in a byte[]. This can be accessed while a threaded download is still running.
   * 
   * @return
   */
  public byte[] getContentAsBytes()
  {
    synchronized ( this.content )
    {
      return this.content;
    }
  }
  
  /**
   * Returns the content as a String encoded by the determined content encoding.
   * 
   * @return
   */
  public String getContentAsString()
  {
    String retval = null;
    ByteArrayInputStream bais = new ByteArrayInputStream( this.content );
    InputStreamReader isr = null;
    if ( this.contentEncoding != null && !this.contentEncoding.trim().equals( "" ) )
    {
      try
      {
        isr = new InputStreamReader( bais, this.contentEncoding );
        this.usedStringEncoding = this.contentEncoding;
      }
      catch ( UnsupportedEncodingException e )
      {
      }
    }
    if ( isr == null && this.standardEncoding != null )
    {
      try
      {
        isr = new InputStreamReader( bais, this.standardEncoding );
        this.usedStringEncoding = this.standardEncoding;
      }
      catch ( UnsupportedEncodingException e )
      {
      }
    }
    if ( isr == null )
    {
      isr = new InputStreamReader( bais );
      this.usedStringEncoding = System.getProperty( "file.encoding" );
    }
    
    //
    BufferedReader br = new BufferedReader( isr );
    StringBuffer sb = new StringBuffer();
    String line = null;
    try
    {
      boolean firstline = true;
      while ( ( line = br.readLine() ) != null )
      {
        if ( !firstline )
        {
          sb.append( "\n" );
        }
        sb.append( line );
        firstline = false;
      }
    }
    catch ( IOException e )
    {
    }
    retval = sb.toString();
    
    return retval;
  }
  
  /**
   * Returns true, if no error has occurred during the download.
   * 
   * @return
   */
  public boolean isDownloadSuccessful()
  {
    boolean retval = false;
    if ( this.exceptionList != null && this.exceptionList.size() == 0 )
    {
      retval = true;
    }
    return retval;
  }
  
  /**
   * Saves the content into the given file. The destination file will be overwritten or created if possible. <br>
   * <br>
   * Returns true if no error occurred.
   * 
   * @param file
   */
  public boolean saveContentToFile( File file )
  {
    boolean retval = false;
    try
    {
      if ( this.content != null )
      {
        FileOutputStream fos = new FileOutputStream( file );
        fos.write( this.content );
        fos.close();
        retval = true;
      }
    }
    catch ( FileNotFoundException e )
    {
    }
    catch ( IOException e )
    {
    }
    return retval;
  }
  
  /**
   * Returns the size that was estimated to receive, by the http content size determined.
   * 
   * @return
   */
  public long getContentEstimatedSize()
  {
    return this.estimatedByteSize;
  }
  
  /**
   * Returns the current size of the loaded amount of content.
   * 
   * @return
   */
  public long getContentCurrentSize()
  {
    return this.currentlyLoadedBytes;
  }
  
  /**
   * Returns the current size of the downloaded content. If the download is in progress, this size is not available.
   * 
   * @return
   */
  public long getContentSize()
  {
    long retval = -1;
    if ( !this.isAlive() && this.content != null )
    {
      retval = this.content.length;
    }
    return retval;
  }
  
  public String getAuthenticationUser()
  {
    return this.authenticationUser;
  }
  
  public void setAuthenticationUser( String authenticationUser )
  {
    this.authenticationUser = authenticationUser;
  }
  
  public String getAuthenticationPassword()
  {
    return this.authenticationPassword;
  }
  
  public void setAuthenticationPassword( String authenticationPassword )
  {
    this.authenticationPassword = authenticationPassword;
  }
  
  public DownloadManager getDownloadManager()
  {
    return this.downloadManager;
  }
  
  public void setDownloadManager( DownloadManager downloadManager )
  {
    this.downloadManager = downloadManager;
  }
  
  public String getContentEncoding()
  {
    return this.contentEncoding;
  }
  
  public void setContentEncoding( String contentEncoding )
  {
    this.contentEncoding = contentEncoding;
  }
  
  public String getContentType()
  {
    return this.contentType;
  }
  
  public void setContentType( String contentType )
  {
    this.contentType = contentType;
  }
  
  public long getTimeExpired()
  {
    long retval = this.timeExpired;
    if ( retval == 0 )
    {
      retval = this.timeDuration.getInterimTimeInMilliseconds();
    }
    return retval;
  }
  
  /**
   * @returns the downloadspeed in kilobyte / second
   */
  public long getDownloadSpeed()
  {
    long retval = 0;
    long timeExpired = this.getTimeExpired();
    if ( timeExpired > 0 )
    {
      retval = Math.round( ( this.currentlyLoadedBytes * 1000 ) / ( 1024 * this.getTimeExpired() ) );
    }
    return retval;
  }
  
  public String getStandardEncoding()
  {
    return this.standardEncoding;
  }
  
  public void setStandardEncoding( String standardEncoding )
  {
    this.standardEncoding = standardEncoding;
  }
  
  public String getUsedStringEncoding()
  {
    return this.usedStringEncoding;
  }
  
  public void setUsedStringEncoding( String usedStringEncoding )
  {
    this.usedStringEncoding = usedStringEncoding;
  }
  
  public URL getUrl()
  {
    return this.url;
  }
  
  public DownloadConnection setDownloadMonitor( DownloadMonitor downloadMonitor )
  {
    this.downloadMonitor = downloadMonitor;
    return this;
  }
}
