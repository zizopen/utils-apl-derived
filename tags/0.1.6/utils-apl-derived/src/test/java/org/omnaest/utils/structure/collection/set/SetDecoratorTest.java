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
package org.omnaest.utils.structure.collection.set;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;
import org.omnaest.utils.xml.XMLHelper;

/**
 * @see SetDecorator
 * @author Omnaest
 */
public class SetDecoratorTest
{
  
  @Test
  public void testJAXBCompliance()
  {
    //
    Set<String> stringSet = new LinkedHashSet<String>( Arrays.asList( "a", "b", "c", "d" ) );
    
    //
    Set<String> decoratedSet = new SetDecorator<String>( stringSet );
    assertEquals( stringSet, decoratedSet );
    
    //
    //System.out.println( XMLHelper.storeObjectAsXML( decoratedSet ) );
    
    //
    @SuppressWarnings("unchecked")
    Set<String> fromXML = XMLHelper.loadObjectFromXML( XMLHelper.storeObjectAsXML( decoratedSet ), SetDecorator.class );
    assertEquals( stringSet, fromXML );
  }
  
}
