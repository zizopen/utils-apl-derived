package org.omnaest.utils.table2.impl.serializer;

import java.io.Reader;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.table2.ImmutableTableSerializer.Marshaller.MarshallingConfiguration;
import org.omnaest.utils.table2.Table;
import org.omnaest.utils.table2.TableSerializer.UnmarshallerPlainText;

/**
 * {@link UnmarshallerPlainText} implementation
 * 
 * @see PlainTextMarshaller
 * @author Omnaest
 * @param <E>
 */
@SuppressWarnings("javadoc")
class PlainTextUnmarshaller<E> extends UnmarshallerAbstract<E> implements UnmarshallerPlainText<E>
{
  /* ************************************************** Constants *************************************************** */
  static final String              delimiterRow         = "-";
  static final String              delimiterColumn      = "|";
  static final String              delimiterTitleColumn = "!";
  static final String              delimiterTableTitle  = "=";
  
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private MarshallingConfiguration configuration;
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see PlainTextUnmarshaller
   * @param table
   * @param exceptionHandler
   */
  public PlainTextUnmarshaller( Table<E> table, ExceptionHandler exceptionHandler )
  {
    super( table, exceptionHandler );
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Table<E> from( Reader reader )
  {
    /*
    ===Table1===
    !  !c0 !c1 !
    !r0!0:0|0:1|
    !r1!1:0|1:1|
    !r2!2:0|2:1|
    ------------
    */
    
    //
    if ( reader != null )
    {
      //
      this.table.clear();
      
      //
      final Scanner scanner = new Scanner( reader );
      
      //
      {
        //
        String firstLine = scanner.hasNextLine() ? scanner.nextLine() : null;
        if ( firstLine != null && firstLine.startsWith( delimiterTableTitle ) )
        {
          //
          String tableName = firstLine.replaceAll( delimiterTableTitle, "" );
          this.table.setTableName( tableName );
        }
      }
      
      //
      boolean hasRowTitles = false;
      String line = scanner.hasNextLine() ? scanner.nextLine() : null;
      if ( line != null && line.startsWith( delimiterTitleColumn ) && line.endsWith( delimiterTitleColumn ) )
      {
        //
        String[] columnTokens = StringUtils.splitPreserveAllTokens( line, delimiterTitleColumn );
        if ( columnTokens.length > 1 )
        {
          //
          columnTokens = ArrayUtils.remove( columnTokens, columnTokens.length - 1 );
          columnTokens = ArrayUtils.remove( columnTokens, 0 );
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
        
        //
        if ( line != null && line.startsWith( delimiterTitleColumn ) )
        {
          //
          if ( columnTokens.length > 0 )
          {
            //
            columnTokens = ArrayUtils.remove( columnTokens, 0 );
          }
        }
        
        //
        columnTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( columnTokens );
        
        //
        this.table.setColumnTitles( Arrays.asList( columnTokens ) );
      }
      
      //
      hasRowTitles = line != null && line.startsWith( delimiterTitleColumn );
      
      //
      int rowIndexPosition = 0;
      while ( line != null )
      {
        //
        if ( !line.startsWith( delimiterRow ) )
        {
          //
          String[] cellTokens = StringUtils.splitPreserveAllTokens( line, delimiterColumn );
          if ( cellTokens.length > 0 )
          {
            //
            cellTokens = ArrayUtils.remove( cellTokens, cellTokens.length - 1 );
          }
          
          //
          if ( cellTokens.length > 0 && hasRowTitles )
          {
            //
            String[] firstCellTokens = StringUtils.splitPreserveAllTokens( cellTokens[0], delimiterTitleColumn );
            
            //
            firstCellTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( firstCellTokens );
            
            //
            if ( firstCellTokens.length >= 2 )
            {
              //
              final String titleValue = firstCellTokens[1];
              this.table.setRowTitle( rowIndexPosition, titleValue );
            }
            
            //
            cellTokens[0] = "";
            if ( firstCellTokens.length >= 3 )
            {
              cellTokens[0] = firstCellTokens[2];
            }
          }
          else if ( cellTokens.length > 0 )
          {
            //
            cellTokens = ArrayUtils.remove( cellTokens, 0 );
          }
          
          //
          cellTokens = org.omnaest.utils.structure.array.ArrayUtils.trimStringArrayTokens( cellTokens );
          
          //
          final Class<E> elementType = this.table.elementType();
          E[] elements = (E[]) Array.newInstance( elementType, cellTokens.length );
          for ( int ii = 0; ii < elements.length; ii++ )
          {
            elements[ii] = ObjectUtils.castTo( elementType, cellTokens[ii] );
          }
          this.table.setRowElements( rowIndexPosition++, elements );
        }
        
        //
        line = scanner.hasNextLine() ? scanner.nextLine() : null;
      }
      
    }
    
    // 
    return this.table;
  }
  
  @Override
  public UnmarshallerPlainText<E> using( MarshallingConfiguration configuration )
  {
    this.configuration = ObjectUtils.defaultIfNull( configuration, new MarshallingConfiguration() );
    return this;
  }
  
  @Override
  protected String getEncoding()
  {
    return this.configuration.getEncoding();
  }
  
}
