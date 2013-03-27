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
package org.omnaest.utils.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import org.omnaest.utils.assertion.Assert;

/**
 * Decorator for an {@link OutputStream}
 * 
 * @author Omnaest
 */
public class OutputStreamDecorator extends OutputStream implements Serializable
{
  private static final long  serialVersionUID = 3589911743774751664L;
  private final OutputStream outputStream;
  
  /**
   * @see OutputStreamDecorator
   * @param outputStream
   */
  public OutputStreamDecorator( OutputStream outputStream )
  {
    super();
    this.outputStream = outputStream;
    Assert.isNotNull( outputStream, "outputStream must not be null" );
  }
  
  public void write( int b ) throws IOException
  {
    this.outputStream.write( b );
  }
  
  public int hashCode()
  {
    return this.outputStream.hashCode();
  }
  
  public void write( byte[] b ) throws IOException
  {
    this.outputStream.write( b );
  }
  
  public void write( byte[] b, int off, int len ) throws IOException
  {
    this.outputStream.write( b, off, len );
  }
  
  public boolean equals( Object obj )
  {
    return this.outputStream.equals( obj );
  }
  
  public void flush() throws IOException
  {
    this.outputStream.flush();
  }
  
  public void close() throws IOException
  {
    this.outputStream.close();
  }
  
  public String toString()
  {
    return this.outputStream.toString();
  }
  
}
