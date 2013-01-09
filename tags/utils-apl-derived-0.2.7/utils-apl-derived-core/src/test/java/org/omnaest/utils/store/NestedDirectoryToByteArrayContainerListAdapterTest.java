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
package org.omnaest.utils.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.omnaest.utils.structure.container.ByteArrayContainer;

/**
 * @see NestedDirectoryToByteArrayContainerListAdapter
 * @author Omnaest
 */
public class NestedDirectoryToByteArrayContainerListAdapterTest
{
  private final File               baseDirectory          = new File( "target/directoryStore" );
  private List<ByteArrayContainer> byteArrayContainerList = new NestedDirectoryToByteArrayContainerListAdapter(
                                                                                                                this.baseDirectory );
  
  @Before
  public void setUp()
  {
    this.byteArrayContainerList.clear();
    assertEquals( 0, this.byteArrayContainerList.size() );
  }
  
  @After
  public void cleanUp()
  {
    this.byteArrayContainerList.clear();
  }
  
  @Test
  public void testDirectory()
  {
    {
      boolean result = this.byteArrayContainerList.add( new ByteArrayContainer( "Test text" ) );
      assertTrue( result );
      assertEquals( 1, this.byteArrayContainerList.size() );
    }
    {
      this.byteArrayContainerList.add( 100, new ByteArrayContainer( "Test text 100" ) );
      assertEquals( 2, this.byteArrayContainerList.size() );
      assertEquals( "Test text 100", this.byteArrayContainerList.get( 100 ).toString() );
    }
    {
      this.byteArrayContainerList.remove( 0 );
      assertEquals( 1, this.byteArrayContainerList.size() );
    }
    {
      this.byteArrayContainerList.clear();
      assertEquals( 0, this.byteArrayContainerList.size() );
    }
  }
  
  @Test
  public void testManyReadWriteCycles()
  {
    for ( int ii = 0; ii < 50; ii++ )
    {
      int index = (int) ( Math.random() * 1000 );
      final String content = "" + index;
      this.byteArrayContainerList.add( index, new ByteArrayContainer( content ) );
      assertEquals( content, this.byteArrayContainerList.get( index ).toString() );
    }
  }
  
}
