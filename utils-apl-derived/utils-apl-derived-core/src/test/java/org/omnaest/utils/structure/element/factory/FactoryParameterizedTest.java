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
package org.omnaest.utils.structure.element.factory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see FactoryParameterized
 * @author Omnaest
 */
public class FactoryParameterizedTest
{
  /* ********************************************** Variables ********************************************** */
  private TestFactory<String> testFactory = new TestFactory<String>();
  
  /* ********************************************** Methods ********************************************** */
  
  protected static class TestFactory<P> extends FactoryParameterized<Boolean, P>
  {
    @Override
    public Boolean newInstance( P... arguments )
    {
      return true;
    }
  }
  
  @Test
  public void testNewInstance()
  {
    assertTrue( this.testFactory.newInstance() );
  }
  
}
