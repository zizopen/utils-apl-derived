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
package org.omnaest.utils.structure.container;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.download.DownloadConnection;
import org.omnaest.utils.download.URIHelper;
import org.omnaest.utils.download.URLHelper;
import org.omnaest.utils.streams.StreamConnector;

/**
 * This class is a simple container to hold a byte array.<br>
 * It additionally offers some simple methods to load the byte array from different ways.
 * 
 * @author Omnaest
 */
public class ByteArrayContainer
{
  /* ********************************************** Constants ********************************************** */
  private final String DEFAULTENCODING    = "utf-8";
  private final String DEFAULTZIPFILENAME = "data.dat";
  
  /* ********************************************** Variables ********************************************** */
  private byte[]       content            = null;
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Default {@link ByteArrayContainer} creating an instance with no content.
   */
  public ByteArrayContainer()
  {
    super();
  }
  
  /**
   * Creates an {@link ByteArrayContainer} with a copied byte array content.
   * 
   * @param content
   */
  public ByteArrayContainer( byte[] content )
  {
    super();
    this.copy( content );
  }
  
  /**
   * @see #copy(String)
   * @param content
   */
  public ByteArrayContainer( String content )
  {
    super();
    this.copy( content );
  }
  
  /**
   * @see #copy(String, String)
   * @param content
   */
  public ByteArrayContainer( String content, String encoding )
  {
    super();
    this.copy( content, encoding );
  }
  
  /**
   * @see #copy(StringBuffer, String)
   * @param content
   */
  public ByteArrayContainer( StringBuffer content, String encoding )
  {
    super();
    this.copy( content, encoding );
  }
  
  /**
   * @return true: content is empty, false: container has content with size > 0
   * @see #isNotEmpty()
   */
  public boolean isEmpty()
  {
    return ( this.content == null || this.content.length == 0 );
  }
  
  /**
   * @see #isEmpty()
   * @return false: content is empty, true: container has content with size > 0
   */
  public boolean isNotEmpty()
  {
    return !this.isEmpty();
  }
  
  public void download( URI uri )
  {
    this.download( URIHelper.getURLfromURI( uri ) );
  }
  
  /**
   * Downloads the content from the given url resource.
   * 
   * @see #download(URI)
   * @see #download(String))
   * @param url
   */
  public void download( URL url )
  {
    if ( url != null )
    {
      DownloadConnection downloadConnection = new DownloadConnection();
      downloadConnection.download( url );
      this.copy( downloadConnection.getContentAsBytes() );
    }
    else
    {
      this.clear();
    }
  }
  
  /**
   * Downloads the content from the given url resource. Ensure the urlStr is encoded correctly and is valid at all.
   * 
   * @see #download(URI)
   * @see #download(URL)
   * @param urlStr
   */
  public void download( String urlStr )
  {
    this.download( URLHelper.createURL( urlStr ) );
  }
  
  /**
   * Loads the content of the given file into the container.
   * 
   * @param file
   * @throws IOException
   */
  public void load( File file ) throws IOException
  {
    FileInputStream fis = new FileInputStream( file );
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    StreamConnector.connect( fis, baos );
    fis.close();
    baos.close();
    this.content = baos.toByteArray();
  }
  
  public void save( File file ) throws IOException
  {
    FileOutputStream fos = new FileOutputStream( file );
    fos.write( this.content );
    fos.close();
  }
  
  /**
   * Copies the content from another container into this one.
   * 
   * @param source
   */
  public void copy( ByteArrayContainer source )
  {
    this.copy( source.getInputStream() );
  }
  
  /**
   * Copies the content from an InputStream into the container.
   * 
   * @param sourceInputStream
   */
  public void copy( InputStream sourceInputStream )
  {
    try
    {
      StreamConnector.connect( sourceInputStream, this.getOutputStream() );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Copies the content of a String into the container.
   * 
   * @param string
   */
  public ByteArrayContainer copy( String string, String encoding )
  {
    try
    {
      OutputStreamWriter osw = new OutputStreamWriter( this.getOutputStream(), encoding );
      osw.write( string );
      osw.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    
    //
    return this;
  }
  
  /**
   * Copies the content of a String into the container.
   * 
   * @param string
   */
  public ByteArrayContainer copy( String string )
  {
    return this.copy( string, DEFAULTENCODING );
  }
  
  /**
   * Copies the content of a StringBuffer into the container.
   * 
   * @param stringBuffer
   */
  public ByteArrayContainer copy( StringBuffer stringBuffer, String encoding )
  {
    return this.copy( stringBuffer.toString(), DEFAULTENCODING );
  }
  
  /**
   * Copies the content from a byte array into the container. The data is copied and not the given byte array is used.
   * 
   * @param source
   */
  public void copy( byte[] source )
  {
    ByteArrayInputStream bis = new ByteArrayInputStream( source );
    this.copy( bis );
  }
  
  /**
   * Transforms the content into a String. As default utf-8 is used.
   * 
   * @see #toString(String)
   */
  public String toString()
  {
    return this.toString( DEFAULTENCODING );
  }
  
  /**
   * Transforms the content into a String.
   * 
   * @see #toString()
   * @param encoding
   *          specifies the encoding of the binary data. Example: "utf-8"
   * @return String, null if error happens
   */
  public String toString( String encoding )
  {
    //
    String retval = null;
    
    //
    try
    {
      StringBuffer sb = new StringBuffer();
      StreamConnector.connect( this.getInputStream(), sb, encoding );
      retval = sb.toString();
    }
    catch ( IOException e )
    {
      retval = null;
    }
    
    //
    return retval;
  }
  
  /**
   * Returns the content as a list of strings, separated by line feed and/or carriage return. The default encoding "utf-8" is
   * used.
   * 
   * @return
   */
  public List<String> toStringList()
  {
    return this.toStringList( DEFAULTENCODING );
  }
  
  /**
   * Returns the content as a list of strings, separated by line feed and/or carriage return.
   * 
   * @param encoding
   *          : for example = "utf-8"
   * @return
   */
  public List<String> toStringList( String encoding )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    String content = this.toString( encoding );
    
    //
    content = content.replaceAll( "[\r\n]+", "\n" );
    String[] lines = StringUtils.splitPreserveAllTokens( content, "\n" );
    CollectionUtils.addAll( retlist, lines );
    
    //
    return retlist;
  }
  
  /**
   * Clears the content.
   */
  public void clear()
  {
    this.setContent( null );
  }
  
  public byte[] getContent()
  {
    return content;
  }
  
  public ByteArrayContainer setContent( byte[] content )
  {
    this.content = content;
    return this;
  }
  
  public InputStream getInputStream()
  {
    InputStream is = null;
    if ( this.content != null )
    {
      is = new ByteArrayInputStream( this.content );
    }
    return is;
  }
  
  /**
   * Returns an outputstream, that allows to write the byte array content directly.<br>
   * Be aware of the fact, that the content is written only if the outputstream is closed correctly!
   * 
   * @return
   */
  public OutputStream getOutputStream()
  {
    return new ByteArrayOutputStream( 0 )
    {
      @Override
      public void close()
      {
        try
        {
          super.close();
        }
        catch ( IOException e )
        {
        }
        ByteArrayContainer.this.content = this.toByteArray();
      }
      
      @Override
      public void flush() throws IOException
      {
        super.flush();
        ByteArrayContainer.this.content = this.toByteArray();
      }
    };
  }
  
  /**
   * Converts the content into a zip file content. For example, load a file content, call this method, and save the content back
   * to the same file, to make the file zipped.
   */
  public void zip( String zipFileName )
  {
    if ( StringUtils.isNotBlank( zipFileName ) )
    {
      Map<String, ByteArrayContainer> byteArrayContainerMap = new HashMap<String, ByteArrayContainer>();
      byteArrayContainerMap.put( zipFileName, this );
      ByteArrayContainer tempByteArrayContainer = ByteArrayContainer.zipFilenameByteArrayContainerMap( byteArrayContainerMap );
      if ( tempByteArrayContainer != null )
      {
        this.content = tempByteArrayContainer.content;
      }
    }
  }
  
  /**
   * @see #zip(String)
   */
  public void zip()
  {
    this.zip( this.generateRandomDefaultFileName() );
  }
  
  /**
   * Zipps all ByteArrayContainers of a given map, which contains filenames with corresponding unzipped ByteArrayContainer
   * objects.
   * 
   * @param byteArrayContainerMap
   */
  public static ByteArrayContainer zipFilenameByteArrayContainerMap( Map<String, ByteArrayContainer> byteArrayContainerMap )
  {
    //
    ByteArrayContainer zippedBac = new ByteArrayContainer();
    try
    {
      //
      ZipOutputStream zos = new ZipOutputStream( zippedBac.getOutputStream() );
      
      //
      for ( String iFilename : byteArrayContainerMap.keySet() )
      {
        //
        ByteArrayContainer sourceByteArrayContainer = byteArrayContainerMap.get( iFilename );
        
        //
        if ( StringUtils.isNotBlank( iFilename ) && sourceByteArrayContainer != null && sourceByteArrayContainer.isNotEmpty() )
        {
          ZipEntry zipEntry = new ZipEntry( iFilename );
          zos.putNextEntry( zipEntry );
          StreamConnector.connect( sourceByteArrayContainer.getInputStream(), zos );
        }
      }
      
      zos.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    
    //
    return zippedBac;
  }
  
  /**
   * Generates a random filename basing on the zip default filename and a random number.
   * 
   * @return
   */
  private String generateRandomDefaultFileName()
  {
    return this.DEFAULTZIPFILENAME + String.valueOf( System.currentTimeMillis() )
           + String.valueOf( Math.round( Math.random() * 1000000000l ) );
  }
  
  /**
   * Converts a zip file content into unzipped content. For example load a zip file, and call this method, to get the unzipped
   * content of the file. If an error occurs, the content will be set to null, and null will be returned. <br>
   * <br>
   * Ensure the zip file contains only one file, if you wanna use the current ByteArrayContainer obejct. Only the first file is
   * read for the current container, all others are only saved within the returned map.
   * 
   * @see #unzipIntoFilenameByteArrayContainerMap(ByteArrayContainer)
   * @return Map of the zip file names with unzipped ByteArrayContainer objects, or null, if unzipping was unsuccesful.
   */
  public Map<String, ByteArrayContainer> unzip()
  {
    //
    Map<String, ByteArrayContainer> retmap = ByteArrayContainer.unzipIntoFilenameByteArrayContainerMap( this );
    
    //
    if ( retmap != null && retmap.size() > 0 )
    {
      this.content = null;
      this.copy( retmap.values().iterator().next() );
    }
    else
    {
      this.content = null;
    }
    
    //
    return retmap;
  }
  
  /**
   * Unzipps the given ByteArrayContainer object into a map containing the filenames and unzipped ByteArrayContainer objects for
   * each file.
   * 
   * @param byteArrayContainer
   * @return
   */
  public static Map<String, ByteArrayContainer> unzipIntoFilenameByteArrayContainerMap( ByteArrayContainer byteArrayContainer )
  {
    //
    Map<String, ByteArrayContainer> retmap = new LinkedHashMap<String, ByteArrayContainer>();
    
    //
    try
    {
      //
      ZipInputStream zis = new ZipInputStream( byteArrayContainer.getInputStream() );
      
      //
      for ( ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry() )
      {
        ByteArrayContainer unzippedBac = new ByteArrayContainer();
        StreamConnector.connect( zis, unzippedBac.getOutputStream() );
        retmap.put( zipEntry.getName(), unzippedBac );
      }
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    
    //
    return retmap;
  }
  
  /* ********************************************** STATIC FACTORY METHOD PART ********************************************** */

  /**
   * Used by the factory method.
   * 
   * @see #createNewInstance
   */
  public static Class<? extends ByteArrayContainer> implementationForByteArrayContainerClass = ByteArrayContainer.class;
  
  /**
   * Creates a new instance of this class.
   * 
   * @see #implementationForByteArrayContainerClass
   */
  public static ByteArrayContainer createNewInstance()
  {
    ByteArrayContainer result = null;
    
    try
    {
      result = ByteArrayContainer.implementationForByteArrayContainerClass.newInstance();
    }
    catch ( Exception e )
    {
    }
    
    return result;
  }
  
}
