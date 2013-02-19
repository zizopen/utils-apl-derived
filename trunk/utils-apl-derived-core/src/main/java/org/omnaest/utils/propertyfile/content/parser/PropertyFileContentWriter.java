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
package org.omnaest.utils.propertyfile.content.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.propertyfile.content.Element;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.element.BlankLineElement;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;
import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * Offers functionality to write a given {@link PropertyFileContent} to disc.
 * 
 * @see PropertyFileContent
 * @author Omnaest
 */
public class PropertyFileContentWriter
{
  /* ********************************************** Constants ********************************************** */
  protected static final String LINE_SEPARATOR_OS_DEFAULT = System.getProperty( "line.separator" );
  
  /* ********************************************** Methods ********************************************** */
  
  public static void writePropertyFileContentToFile( PropertyFileContent propertyFileContent, File file, String fileEncoding )
  {
    boolean useJavaStyleUnicodeEscaping = false;
    writePropertyFileContentToFile( propertyFileContent, file, fileEncoding, useJavaStyleUnicodeEscaping );
  }
  
  /**
   * @see PropertyFileContentParser
   * @param propertyFileContent
   * @param file
   */
  public static void writePropertyFileContentToFile( PropertyFileContent propertyFileContent,
                                                     File file,
                                                     String fileEncoding,
                                                     boolean useJavaStyleUnicodeEscaping )
  {
    //
    if ( propertyFileContent != null && file != null && fileEncoding != null )
    {
      //
      try
      {
        //ensure the file is created
        FileUtils.writeStringToFile( file, "", fileEncoding );
        
        //
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter( new FileOutputStream( file ), fileEncoding );
        
        //
        writePropertyFileContentToOutputStreamWriter( propertyFileContent, outputStreamWriter, useJavaStyleUnicodeEscaping );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Returns the {@link PropertyFileContent} as {@link String}
   * 
   * @see PropertyFileContentParser
   * @param propertyFileContent
   * @return
   */
  public static String writePropertyFileContentToString( PropertyFileContent propertyFileContent )
  {
    boolean useJavaStyleUnicodeEscaping = false;
    return writePropertyFileContentToString( propertyFileContent, useJavaStyleUnicodeEscaping );
  }
  
  /**
   * Returns the {@link PropertyFileContent} as {@link String}
   * 
   * @see PropertyFileContentParser
   * @param propertyFileContent
   * @return
   */
  public static String writePropertyFileContentToString( PropertyFileContent propertyFileContent,
                                                         boolean useJavaStyleUnicodeEscaping )
  {
    //
    String retval = null;
    
    //
    if ( propertyFileContent != null )
    {
      //
      ByteArrayContainer byteArrayContainer = new ByteArrayContainer();
      OutputStreamWriter outputStreamWriter = byteArrayContainer.getOutputStreamWriter();
      
      //
      writePropertyFileContentToOutputStreamWriter( propertyFileContent, outputStreamWriter, useJavaStyleUnicodeEscaping );
      
      //
      retval = byteArrayContainer.toString();
    }
    
    //
    return retval;
  }
  
  public static void writePropertyFileContentToOutputStreamWriter( PropertyFileContent propertyFileContent,
                                                                   OutputStreamWriter outputStreamWriter )
  {
    boolean useJavaStyleUnicodeEscaping = false;
    writePropertyFileContentToOutputStreamWriter( propertyFileContent, outputStreamWriter, useJavaStyleUnicodeEscaping );
  }
  
  /**
   * @see PropertyFileContentParser
   * @param propertyFileContent
   * @param outputStreamWriter
   * @param useJavaStyleUnicodeEscaping
   */
  public static void writePropertyFileContentToOutputStreamWriter( PropertyFileContent propertyFileContent,
                                                                   OutputStreamWriter outputStreamWriter,
                                                                   boolean useJavaStyleUnicodeEscaping )
  {
    //
    if ( propertyFileContent != null && outputStreamWriter != null )
    {
      //
      try
      {
        //
        List<String> contentList = new ArrayList<String>();
        
        //
        List<Element> elementList = propertyFileContent.getElementListAscendingByIndexPosition();
        for ( Element element : elementList )
        {
          //
          if ( element instanceof Comment )
          {
            //
            Comment comment = (Comment) element;
            
            //
            String prefixBlanks = comment.getPrefixBlanks();
            String commentIndicator = comment.getCommentIndicator();
            String commentString = comment.getComment();
            
            //
            String text = prefixBlanks + commentIndicator + commentString;
            contentList.add( text );
          }
          else if ( element instanceof Property )
          {
            //
            Property property = (Property) element;
            
            //
            String prefixBlanks = property.getPrefixBlanks();
            String key = property.getKey();
            String delimiter = property.getDelimiter();
            List<String> valueList = property.getValueList();
            
            //            
            String value = escapeJavaStyleIfNecessary( useJavaStyleUnicodeEscaping, valueList.get( 0 ) );
            String text = prefixBlanks + key + delimiter + value;
            
            if ( valueList.size() > 1 )
            {
              text += "\\";
            }
            
            contentList.add( text );
            
            //
            for ( int ii = 1; ii < valueList.size(); ii++ )
            {
              //
              value = escapeJavaStyleIfNecessary( useJavaStyleUnicodeEscaping, valueList.get( ii ) );
              
              if ( ii < valueList.size() - 1 )
              {
                value += "\\";
              }
              
              contentList.add( value );
            }
            
          }
          else if ( element instanceof BlankLineElement )
          {
            //
            BlankLineElement blankLineElement = (BlankLineElement) element;
            
            //
            contentList.add( blankLineElement.getBlanks() );
          }
        }
        
        //
        {
          //
          BufferedWriter bufferedWriter = new BufferedWriter( outputStreamWriter );
          
          //http://code.google.com/p/i18n-binder/issues/detail?id=1#c1
          final String lineSeparator = StringUtils.defaultString( propertyFileContent.getLineSeparator(),
                                                                  LINE_SEPARATOR_OS_DEFAULT );
          boolean firstLine = true;
          for ( String content : contentList )
          {
            //
            if ( firstLine )
            {
              firstLine = false;
            }
            else
            {
              bufferedWriter.write( lineSeparator );
            }
            
            //
            bufferedWriter.write( content );
          }
          
          //
          bufferedWriter.close();
        }
        
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
  }
  
  private static String escapeJavaStyleIfNecessary( boolean useJavaStyleUnicodeEscaping, final String value )
  {
    return useJavaStyleUnicodeEscaping ? StringEscapeUtils.escapeJava( value ) : value;
  }
}
