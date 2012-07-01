package org.omnaest.utils.table2.impl.serializer;

import java.io.InputStream;

import org.apache.commons.io.input.CharSequenceReader;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.container.ByteArrayContainer;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer.Unmarshaller;

/**
 * Abstract implementation for an {@link Unmarshaller}
 * 
 * @author Omnaest
 * @param <E>
 */
abstract class UnmarshallerAbstract<E> implements Unmarshaller<E>
{
  /* ***************************** Beans / Services / References / Delegates (external) ***************************** */
  protected final Table<E>         table;
  protected final ExceptionHandler exceptionHandler;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see UnmarshallerAbstract
   * @param table
   * @param exceptionHandler
   */
  @SuppressWarnings("javadoc")
  public UnmarshallerAbstract( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super();
    this.table = table;
    this.exceptionHandler = exceptionHandler;
  }
  
  @Override
  public Table<E> from( InputStream inputStream )
  {
    //
    if ( inputStream != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      byteArrayContainer.copyFrom( inputStream );
      
      this.from( byteArrayContainer.getReader( this.getEncoding() ) );
    }
    
    // 
    return this.table;
  }
  
  protected abstract String getEncoding();
  
  @Override
  public Table<E> from( CharSequence charSequence )
  {
    return this.from( new CharSequenceReader( charSequence ) );
  }
  
}
