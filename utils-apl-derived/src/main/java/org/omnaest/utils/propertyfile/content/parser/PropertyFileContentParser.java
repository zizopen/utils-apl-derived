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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.propertyfile.PropertyFile;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.element.BlankLineElement;
import org.omnaest.utils.propertyfile.content.element.Comment;
import org.omnaest.utils.propertyfile.content.element.Property;

/**
 * Parses the content of a {@link PropertyFile} and creates a {@link PropertyFileContent} object.
 * 
 * @see PropertyFileContent
 * @author Omnaest
 */
public class PropertyFileContentParser
{
  
  /**
   * @see #parsePropertyFileContent(String)
   * @param file
   * @param fileEncoding
   * @return
   */
  public static PropertyFileContent parsePropertyFileContent( File file, String fileEncoding )
  {
    //
    PropertyFileContent propertyFileContent = null;
    
    //
    if ( file != null && file.exists() )
    {
      //
      try
      {
        //
        String fileContent = IOUtils.toString( new FileInputStream( file ), fileEncoding );
        propertyFileContent = parsePropertyFileContent( fileContent );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
    else
    {
      propertyFileContent = new PropertyFileContent();
    }
    
    //
    return propertyFileContent;
  }
  
  /**
   * @see #parsePropertyFileContent(File, String)
   * @param fileContent
   * @return
   */
  public static PropertyFileContent parsePropertyFileContent( final String fileContent )
  {
    //
    PropertyFileContent propertyFileContent = new PropertyFileContent();
    
    //
    if ( fileContent != null )
    {
      //
      try
      {
        //      
        List<String> lineContentList = new ArrayList<String>();
        {
          //
          Scanner scanner = new Scanner( fileContent );
          while ( scanner.hasNextLine() )
          {
            lineContentList.add( scanner.nextLine() );
          }
          if ( fileContent.endsWith( "\n" ) || fileContent.endsWith( "\r" ) )
          {
            lineContentList.add( "" );
          }
          
          //See http://code.google.com/p/i18n-binder/issues/detail?id=1#c1
          String lineSeparator = System.getProperty( "line.separator" );
          {
            //
            if ( fileContent.contains( "\r\n" ) )
            {
              lineSeparator = "\r\n";
            }
            else if ( fileContent.contains( "\n" ) )
            {
              lineSeparator = "\n";
            }
            else if ( fileContent.contains( "\r" ) )
            {
              lineSeparator = "\r";
            }
          }
          propertyFileContent.setLineSeparator( lineSeparator );
          
        }
        
        //
        Pattern patternComment = Pattern.compile( "([^\\:\\=]{0,4}[\\s]*)(\\!|\\#)(.*)" );
        Pattern patternProperty = Pattern.compile( "([^\\w]*)([^\\s\\:\\=]+)(\\s*=\\s*|\\s*\\:\\s*|\\s+)(.*[^\\\\]|[^\\\\]?)(\\\\?)" );
        Pattern patternBlankLine = Pattern.compile( "([^\\w]*)" );
        Pattern patternPropertyOngoingLine = Pattern.compile( "(.*[^\\\\]|[^\\\\]?)(\\\\?)" );
        
        //
        Property propertyMultiline = null;
        for ( String lineContent : lineContentList )
        {
          //
          if ( propertyMultiline == null )
          {
            //
            Matcher matcherComment = patternComment.matcher( lineContent );
            Matcher matcherProperty = patternProperty.matcher( lineContent );
            Matcher matcherBlankLine = patternBlankLine.matcher( lineContent );
            
            //
            if ( matcherComment.matches() )
            {
              //
              Comment comment = new Comment();
              comment.setPrefixBlanks( matcherComment.group( 1 ) );
              comment.setCommentIndicator( matcherComment.group( 2 ) );
              comment.setComment( matcherComment.group( 3 ) );
              
              //
              propertyFileContent.appendElement( comment );
            }
            else if ( matcherProperty.matches() )
            {
              //
              Property property = new Property();
              property.setPrefixBlanks( matcherProperty.group( 1 ) );
              property.setKey( matcherProperty.group( 2 ) );
              property.setDelimiter( matcherProperty.group( 3 ) );
              List<String> valueList = property.getValueList();
              valueList.add( matcherProperty.group( 4 ) );
              
              //
              if ( StringUtils.isNotBlank( matcherProperty.group( 5 ) ) )
              {
                propertyMultiline = property;
              }
              else
              {
                propertyFileContent.appendElement( property );
              }
            }
            else if ( matcherBlankLine.matches() )
            {
              propertyFileContent.appendElement( new BlankLineElement().setBlanks( matcherBlankLine.group( 1 ) ) );
            }
          }
          else
          {
            //
            Matcher matcherPropertyOngoingLine = patternPropertyOngoingLine.matcher( lineContent );
            
            //
            if ( matcherPropertyOngoingLine.matches() )
            {
              //
              List<String> valueList = propertyMultiline.getValueList();
              valueList.add( matcherPropertyOngoingLine.group( 1 ) );
              
              //
              if ( StringUtils.isBlank( matcherPropertyOngoingLine.group( 2 ) ) )
              {
                //
                propertyFileContent.appendElement( propertyMultiline );
                
                //
                propertyMultiline = null;
              }
            }
          }
        }
        
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
    
    //
    return propertyFileContent;
  }
}
