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
package org.omnaest.utils.structure.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see ObjectUtils
 * @author Omnaest
 */
public class ObjectUtilsTest
{
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  protected static class TestValueOf
  {
    /* ********************************************** Variables ********************************************** */
    protected Integer value  = null;
    protected String  string = null;
    
    /* ********************************************** Methods ********************************************** */
    
    protected TestValueOf( String string )
    {
      super();
      this.string = string;
    }
    
    protected TestValueOf( int value )
    {
      super();
      this.value = value;
    }
    
    public static TestValueOf valueOf( Integer value )
    {
      return new TestValueOf( value );
    }
    
    public Integer getValue()
    {
      return this.value;
    }
    
    public String getString()
    {
      return this.string;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  @Test
  public void testCastTo()
  {
    //  
    {
      //
      Object object = "5";
      Class<?> type = Integer.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Integer );
      assertEquals( Integer.valueOf( 5 ), objectCasted );
    }
    {
      //
      Object object = "5";
      Class<?> type = long.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Long );
      assertEquals( Long.valueOf( 5 ), objectCasted );
    }
    {
      //
      Object object = "true";
      Class<?> type = Boolean.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Boolean );
      assertEquals( Boolean.valueOf( (String) object ), objectCasted );
    }
    {
      //
      Object object = "true";
      Class<?> type = boolean.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof Boolean );
      assertEquals( Boolean.valueOf( (String) object ), objectCasted );
    }
    {
      //
      Object object = Integer.valueOf( 10 );
      Class<?> type = TestValueOf.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof TestValueOf );
      assertEquals( object, ( (TestValueOf) objectCasted ).getValue() );
    }
    {
      //
      Object object = "10";
      Class<?> type = TestValueOf.class;
      
      //
      Object objectCasted = ObjectUtils.castTo( type, object );
      
      //
      assertTrue( objectCasted instanceof TestValueOf );
      assertEquals( object, ( (TestValueOf) objectCasted ).getString() );
    }
  }
}
