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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.propertyfile.PropertyFile;
import org.omnaest.utils.propertyfile.content.PropertyFileContent;
import org.omnaest.utils.propertyfile.content.PropertyMap;
import org.omnaest.utils.propertyfile.content.element.Property;

public class PropertyFileContentWriterTest
{
  /* ********************************************** Variables ********************************************** */
  private File srcFile  = null;
  private File destFile = null;
  
  /* ********************************************** Methods ********************************************** */
  @Before
  public void setUp() throws Exception
  {
    //
    URL resourceUrl = this.getClass().getResource( "PropertyFile.properties" );
    
    this.srcFile = new File( resourceUrl.getFile() );
    this.destFile = new File( this.srcFile.getParentFile().getAbsolutePath() + "/" + "test.properties" );
    
    //
    if ( this.destFile.exists() )
    {
      this.destFile.delete();
    }
  }
  
  @Test
  public void testWritePropertyFileContentToOutputStreamWriter()
  {
    //
    final String fileEncoding = "UTF-8";
    boolean useJavaStyleUnicodeEscaping = true;
    PropertyFileContent propertyFileContent = PropertyFileContentParser.parsePropertyFileContent( this.srcFile, fileEncoding,
                                                                                                  useJavaStyleUnicodeEscaping );
    
    //
    PropertyMap propertyMap = propertyFileContent.getPropertyMap();
    
    //
    Property property = new Property();
    property.setKey( "new.key" );
    property.addValue( "new value" );
    
    propertyMap.put( property );
    
    //
    PropertyFileContentWriter.writePropertyFileContentToFile( propertyFileContent, this.destFile, fileEncoding );
    
    //
    assertTrue( this.destFile.exists() );
    
    //
    PropertyFile propertyFile = new PropertyFile( this.destFile );
    propertyFile.load();
    boolean hasPropertyKey = propertyFile.getPropertyFileContent().hasPropertyWithSameKeyAndValue( property );
    
    //
    assertTrue( hasPropertyKey );
  }
  
  /**
   * Fix for http://code.google.com/p/i18n-binder/issues/detail?id=1#c1()
   */
  @Test
  public void testFixForIssue1()
  {
    //
    {
      //
      String fileContent = "# This is a comment\n! And this is a comment, too\nsimpleKey Text";
      PropertyFile propertyFile = new PropertyFile( (File) null );
      propertyFile.load( fileContent );
      
      //
      String fileContentRewritten = propertyFile.toString();
      assertEquals( fileContent, fileContentRewritten );
    }
    
    //
    {
      //
      String fileContent = "# This is a comment\r\n! And this is a comment, too\r\nsimpleKey Text";
      PropertyFile propertyFile = new PropertyFile( (File) null );
      propertyFile.load( fileContent );
      
      //
      String fileContentRewritten = propertyFile.toString();
      assertEquals( fileContent, fileContentRewritten );
    }
    
    //
    {
      //
      String fileContent = "# This is a comment\r! And this is a comment, too\rsimpleKey Text";
      PropertyFile propertyFile = new PropertyFile( (File) null );
      propertyFile.load( fileContent );
      
      //
      String fileContentRewritten = propertyFile.toString();
      assertEquals( fileContent, fileContentRewritten );
    }
    
  }
  
  @Test
  public void testFixForIssue9()
  {
    //
    String fileContent = "unicodeKey Text with Üäöß characters";
    PropertyFile propertyFile = new PropertyFile( (File) null ).setUseJavaStyleUnicodeEscaping( true );
    propertyFile.load( fileContent );
    
    //
    String fileContentRewritten = propertyFile.toString();
    assertEquals( "unicodeKey Text with \\u00DC\\u00E4\\u00F6\\u00DF characters", fileContentRewritten );
  }
}
