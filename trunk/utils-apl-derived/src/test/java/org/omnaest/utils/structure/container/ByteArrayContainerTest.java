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
package org.omnaest.utils.structure.container;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ByteArrayContainerTest
{
  
  @Before
  public void setUp() throws Exception
  {
  }
  
  @Test
  public void testToStringList()
  {
    //
    String[] part = { "lala1", "lala2", "lala3", "" };
    String testString = part[0] + "\n" + part[1] + "\r" + part[2] + "\n\r" + part[3];
    
    //
    ByteArrayContainer bac = new ByteArrayContainer();
    assertNotNull( bac );
    
    //
    bac.copy( testString );
    assertNotNull( bac.getContent() );
    
    //
    List<String> retlist = bac.toStringList();
    assertNotNull( retlist );
    assertEquals( part.length, retlist.size() );
    for ( int ii = 0; ii < retlist.size(); ii++ )
    {
      assertEquals( part[ii], retlist.get( ii ) );
    }
    
  }
}
