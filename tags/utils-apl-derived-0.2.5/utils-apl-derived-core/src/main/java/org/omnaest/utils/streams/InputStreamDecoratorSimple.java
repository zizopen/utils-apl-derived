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
package org.omnaest.utils.streams;

import java.io.IOException;
import java.io.InputStream;

import org.omnaest.utils.assertion.Assert;

/**
 * Simple decorator of an {@link InputStream} which only relies on the {@link #read()} method of the given {@link InputStream} for
 * any of the available read operations like {@link #read(byte[])} and {@link #read(byte[], int, int)}.<br>
 * <br>
 * Other operations like {@link #available()},{@link #close()},{@link #mark(int)},{@link #markSupported()},{@link #skip(long)},
 * {@link #reset()} are delegated as well.
 * 
 * @see InputStream
 * @see InputStreamDecorator
 * @author Omnaest
 */
public abstract class InputStreamDecoratorSimple extends InputStream
{
  /* ******************************** Variables / State (internal/hiding) ******************************** */
  protected final InputStream inputStream;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see InputStreamDecoratorSimple
   * @param inputStream
   *          {@link InputStream}
   */
  public InputStreamDecoratorSimple( InputStream inputStream )
  {
    super();
    this.inputStream = inputStream;
    
    Assert.isNotNull( inputStream, "inputStream must not be null" );
  }
  
  @Override
  public int read() throws IOException
  {
    return this.inputStream.read();
  }
  
  @Override
  public int hashCode()
  {
    return this.inputStream.hashCode();
  }
  
  @Override
  public boolean equals( Object obj )
  {
    return this.inputStream.equals( obj );
  }
  
  @Override
  public long skip( long n ) throws IOException
  {
    return this.inputStream.skip( n );
  }
  
  @Override
  public int available() throws IOException
  {
    return this.inputStream.available();
  }
  
  @Override
  public String toString()
  {
    return this.inputStream.toString();
  }
  
  @Override
  public void close() throws IOException
  {
    this.inputStream.close();
  }
  
  @Override
  public void mark( int readlimit )
  {
    this.inputStream.mark( readlimit );
  }
  
  @Override
  public void reset() throws IOException
  {
    this.inputStream.reset();
  }
  
  @Override
  public boolean markSupported()
  {
    return this.inputStream.markSupported();
  }
  
}
