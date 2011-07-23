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
package org.omnaest.utils.streams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.omnaest.utils.streams.StreamConnector.TransferResult;
import org.omnaest.utils.structure.container.ByteArrayContainer;

public class StreamConnectorTest
{
  
  @Test
  public void testConnectInputStreamStringBufferString()
  {
    //
    String testString = "testßßßßßßüäöäöäöä##+#+~~~";
    for ( int ii = 1; ii <= 10; ii++ )
    {
      testString += testString;
    }
    
    //
    StringBuffer sb = new StringBuffer();
    ByteArrayContainer firstBac = new ByteArrayContainer();
    ByteArrayContainer secondBac = new ByteArrayContainer();
    
    final String encoding = "utf-8";
    
    try
    {
      StreamConnector.connect( new StringBuffer( testString ), firstBac.getOutputStream(), encoding );
      StreamConnector.connect( firstBac.getInputStream(), secondBac.getOutputStream() );
      StreamConnector.connect( secondBac.getInputStream(), sb, encoding );
    }
    catch ( IOException e )
    {
      fail();
    }
    
    //
    assertEquals( testString, sb.toString() );
    assertEquals( testString, secondBac.toString( encoding ) );
    
    //
    ByteArrayContainer directBac = new ByteArrayContainer();
    directBac.copyFrom( testString );
    assertEquals( testString, directBac.toString( encoding ) );
    
    directBac.copyFrom( secondBac );
    assertEquals( testString, directBac.toString( encoding ) );
    
    try
    {
      File file = new File( "ByteArrayContainerTest.bin" );
      directBac.save( file );
      directBac.clear();
      assertNull( directBac.getContent() );
      
      directBac.load( file );
      file.delete();
      assertEquals( testString, directBac.toString( encoding ) );
    }
    catch ( IOException e )
    {
      fail( e.getMessage() );
    }
    //
  }
  
  @Test
  public void testZippedBac()
  {
    String test = "asdfsfjlkjflksjspoo9384584385094385943543=)?=)(=)098ß}]}]}[]}[{]{6[";
    ByteArrayContainer testBac = new ByteArrayContainer();
    testBac.copyFrom( test );
    
    assertNotNull( testBac );
    assertEquals( test, testBac.toString() );
    
    testBac.zip();
    assertTrue( !StringUtils.equals( test, testBac.toString() ) );
    
    testBac.unzip();
    assertEquals( test, testBac.toString() );
  }
  
  @Test
  public void testTransfer()
  {
    //
    String sourceContent = "asdfsfjlßßß+ö#äö#äöäö#äö@@@@µµµµµ";
    
    //
    InputStream inputStream = new ByteArrayInputStream( sourceContent.getBytes() );
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    
    //
    TransferResult transferResult = StreamConnector.transfer( inputStream, outputStream );
    
    //
    assertTrue( transferResult.isSuccessful() );
    assertEquals( null, transferResult.getException() );
    assertEquals( sourceContent.getBytes().length, transferResult.getTransferSizeInBytes() );
    
    //    
    String destinationContent = outputStream.toString();
    assertEquals( sourceContent, destinationContent );
    
  }
}
