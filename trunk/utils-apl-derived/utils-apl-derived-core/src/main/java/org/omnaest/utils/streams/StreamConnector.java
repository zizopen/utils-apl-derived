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
package org.omnaest.utils.streams;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * This class offers methods to connect to streams to each other.
 * 
 * @author Omnaest
 */
public class StreamConnector
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  public static class TransferResult
  {
    /* ********************************************** Variables ********************************************** */
    protected boolean   successful          = false;
    protected int       transferSizeInBytes = 0;
    protected Exception exception           = null;
    
    /* ********************************************** Methods ********************************************** */

    /**
     * @param successful
     * @param transferSizeInBytes
     * @param exception
     */
    public TransferResult( boolean successful, int transferSizeInBytes, Exception exception )
    {
      super();
      this.successful = successful;
      this.transferSizeInBytes = transferSizeInBytes;
      this.exception = exception;
    }
    
    public boolean isSuccessful()
    {
      return this.successful;
    }
    
    public int getTransferSizeInBytes()
    {
      return this.transferSizeInBytes;
    }
    
    public Exception getException()
    {
      return this.exception;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Connects a given {@link InputStream} to a given {@link OutputStream} without closing the streams but flushing the
   * {@link OutputStream}.
   * 
   * @param inputStream
   * @param outputStream
   * @throws IOException
   */
  public static void connect( InputStream inputStream, OutputStream outputStream ) throws IOException
  {
    int reader = 0;
    BufferedInputStream bis = new BufferedInputStream( inputStream );
    BufferedOutputStream bos = new BufferedOutputStream( outputStream );
    while ( ( reader = bis.read() ) != -1 )
    {
      bos.write( reader );
    }
    bos.flush();
  }
  
  /**
   * Transfers the data from the given {@link InputStream} to the given {@link OutputStream} returning a {@link TransferResult}
   * 
   * @param inputStream
   * @param outputStream
   * @return {@link TransferResult}
   */
  public static TransferResult transfer( InputStream inputStream, OutputStream outputStream )
  {
    //
    TransferResult transferResult = null;
    
    //
    if ( inputStream != null && outputStream != null )
    {
      //
      int numberOfBytesRead = 0;
      
      //
      try
      {
        //        
        BufferedInputStream bis = new BufferedInputStream( inputStream );
        BufferedOutputStream bos = new BufferedOutputStream( outputStream );
        
        //
        byte[] buffer = new byte[10000];
        int bufferSizeUsed = 0;
        while ( ( bufferSizeUsed = bis.read( buffer ) ) != -1 )
        {
          //
          bos.write( buffer, 0, bufferSizeUsed );
          
          //
          numberOfBytesRead += bufferSizeUsed;
        }
        
        //
        bos.flush();
        
        //        
        Boolean successful = true;
        Integer transferSizeInBytes = numberOfBytesRead;
        Exception exception = null;
        transferResult = new TransferResult( successful, transferSizeInBytes, exception );
      }
      catch ( Exception e )
      {
        //
        Boolean successful = false;
        Integer transferSizeInBytes = numberOfBytesRead;
        Exception exception = e;
        transferResult = new TransferResult( successful, transferSizeInBytes, exception );
      }
    }
    else
    {
      //
      Boolean successful = false;
      Integer transferSizeInBytes = 0;
      Exception exception = null;
      transferResult = new TransferResult( successful, transferSizeInBytes, exception );
    }
    
    //
    return transferResult;
  }
  
  /**
   * Transfers the data of an InputStream into a StringBuffer using the given encoding.
   * 
   * @param source
   * @param destination
   * @param encoding
   *          - example: "utf-8"
   * @see Charset
   * @throws IOException
   */
  public static void connect( InputStream source, StringBuffer destination, String encoding ) throws IOException
  {
    //
    if ( source != null && destination != null )
    {
      //     
      InputStreamReader isr = new InputStreamReader( source, encoding );
      
      //
      char[] charBuffer = new char[100];
      int count = 0;
      while ( ( count = isr.read( charBuffer ) ) > 0 )
      {
        destination.append( charBuffer, 0, count );
      }
      source.close();
    }
  }
  
  /**
   * Transfers the data of a StringBuffer into an OutputStream.
   * 
   * @param source
   * @param destination
   * @param encoding
   *          , like "utf-8"
   * @throws IOException
   */
  public static void connect( StringBuffer source, OutputStream destination, String encoding ) throws IOException
  {
    if ( source != null && destination != null )
    {
      OutputStreamWriter osw = new OutputStreamWriter( destination, encoding );
      osw.write( source.toString() );
      osw.close();
    }
  }
  
}
