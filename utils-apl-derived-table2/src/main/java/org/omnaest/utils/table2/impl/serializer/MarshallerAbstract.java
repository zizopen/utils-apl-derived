package org.omnaest.utils.table2.impl.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller;
import org.omnaest.utils.table2.Table;

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
    if ( outputStream != null )
    {
      //
      StringBuffer stringBuffer = new StringBuffer();
      this.to( stringBuffer );
      
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( stringBuffer, this.getEncoding() );
      byteArrayContainer.writeTo( outputStream );
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
  
}
