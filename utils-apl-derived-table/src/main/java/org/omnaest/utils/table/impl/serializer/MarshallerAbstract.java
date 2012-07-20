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
package org.omnaest.utils.table.impl.serializer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.table.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table.Table;

abstract class MarshallerAbstract<E> implements Marshaller<E>
{
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  protected final Table<E>         table;
  protected final ExceptionHandler exceptionHandler;
  
  /* *************************************************** Methods **************************************************** */
  
  public MarshallerAbstract( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super();
    this.table = table;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public Table<E> to( Writer writer )
  {
    if ( writer != null )
    {
      //
      StringBuffer stringBuffer = new StringBuffer();
      this.to( stringBuffer );
      
      try
      {
        writer.append( stringBuffer );
      }
      catch ( IOException e )
      {
        this.exceptionHandler.handleException( e );
      }
      
    }
    
    return this.table;
  }
  
  @Override
  public Table<E> to( OutputStream outputStream )
  {
    final boolean closeStream = false;
    return this.to( outputStream, closeStream );
  }
  
  public Table<E> to( OutputStream outputStream, boolean closeStream )
  {
    if ( outputStream != null )
    {
      try
      {
        //
        StringBuffer stringBuffer = new StringBuffer();
        this.to( stringBuffer );
        
        //
        ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
        byteArrayContainer.copyFrom( stringBuffer, this.getEncoding() );
        byteArrayContainer.writeTo( outputStream );
      }
      finally
      {
        if ( closeStream )
        {
          try
          {
            outputStream.close();
          }
          catch ( IOException e )
          {
            this.exceptionHandler.handleException( e );
          }
        }
      }
    }
    
    // 
    return this.table;
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    this.to( builder );
    return builder.toString();
  }
  
  protected abstract String getEncoding();
  
  @Override
  public Table<E> to( File file )
  {
    OutputStream outputStream = null;
    try
    {
      outputStream = new BufferedOutputStream( new FileOutputStream( file ) );
    }
    catch ( FileNotFoundException e )
    {
      this.exceptionHandler.handleException( e );
    }
    final boolean closeStream = true;
    return this.to( outputStream, closeStream );
  }
  
}
