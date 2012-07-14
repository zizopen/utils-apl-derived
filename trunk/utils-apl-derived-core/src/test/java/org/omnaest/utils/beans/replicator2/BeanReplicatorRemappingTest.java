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
package org.omnaest.utils.beans.replicator2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.omnaest.utils.beans.replicator2.BeanReplicator.Declaration;
import org.omnaest.utils.beans.replicator2.BeanReplicator.DeclarationSupport;
import org.omnaest.utils.events.exception.ExceptionHandler;

/**
 * @see BeanReplicator
 * @author Omnaest
 */
public class BeanReplicatorRemappingTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  @SuppressWarnings("unused")
  private static class Bean0From
  {
    private Bean1From bean1From;
    
    public Bean1From getBean1From()
    {
      return this.bean1From;
    }
    
    public void setBean1From( Bean1From bean1From )
    {
      this.bean1From = bean1From;
    }
  }
  
  @SuppressWarnings("unused")
  private static class Bean0To
  {
    private Bean1To bean1To;
    
    public Bean1To getBean1To()
    {
      return this.bean1To;
    }
    
    public void setBean1To( Bean1To bean1To )
    {
      this.bean1To = bean1To;
    }
  }
  
  @SuppressWarnings("unused")
  private static class Bean1From
  {
    private Bean2From bean2From;
    
    public Bean2From getBean2From()
    {
      return this.bean2From;
    }
    
    public void setBean2From( Bean2From bean2From )
    {
      this.bean2From = bean2From;
    }
  }
  
  @SuppressWarnings("unused")
  private static class Bean1To
  {
    private Bean2To bean2To;
    
    public Bean2To getBean2To()
    {
      return this.bean2To;
    }
    
    public void setBean2To( Bean2To bean2To )
    {
      this.bean2To = bean2To;
    }
  }
  
  @SuppressWarnings("unused")
  private static class Bean2From
  {
    private String fieldString1;
    private String fieldString2;
    private String fieldForLong;
    
    public String getFieldString1()
    {
      return this.fieldString1;
    }
    
    public void setFieldString1( String fieldString1 )
    {
      this.fieldString1 = fieldString1;
    }
    
    public String getFieldString2()
    {
      return this.fieldString2;
    }
    
    public void setFieldString2( String fieldString2 )
    {
      this.fieldString2 = fieldString2;
    }
    
    public String getFieldForLong()
    {
      return this.fieldForLong;
    }
    
    public void setFieldForLong( String fieldForLong )
    {
      this.fieldForLong = fieldForLong;
    }
  }
  
  private static class Bean2To implements IBean2To
  {
    private String fieldString1;
    private String fieldString2;
    private long   fieldLong;
    
    @Override
    public String getFieldString1()
    {
      return this.fieldString1;
    }
    
    @Override
    public void setFieldString1( String fieldString1 )
    {
      this.fieldString1 = fieldString1;
    }
    
    @Override
    public String getFieldString2()
    {
      return this.fieldString2;
    }
    
    @Override
    public void setFieldString2( String fieldString2 )
    {
      this.fieldString2 = fieldString2;
    }
    
    @Override
    public long getFieldLong()
    {
      return this.fieldLong;
    }
    
    @Override
    public void setFieldLong( long fieldLong )
    {
      this.fieldLong = fieldLong;
    }
    
  }
  
  private static interface IBean2To
  {
    
    public abstract void setFieldLong( long fieldLong );
    
    public abstract long getFieldLong();
    
    public abstract void setFieldString2( String fieldString2 );
    
    public abstract String getFieldString2();
    
    public abstract void setFieldString1( String fieldString1 );
    
    public abstract String getFieldString1();
    
  }
  
  /* *************************************************** Methods **************************************************** */
  @Test
  public void testBasicReplication()
  {
    BeanReplicator<Bean0From, Bean0To> beanReplicator = new BeanReplicator<Bean0From, Bean0To>( Bean0From.class, Bean0To.class ).setExceptionHandler( newFailingExceptionHandler() );
    beanReplicator.declare( new Declaration()
    {
      @Override
      public void declare( DeclarationSupport support )
      {
        support.addTypeMapping( Bean1From.class, Bean1To.class );
        support.addPropertyNameMapping( "bean1From", "bean1To" );
        
        support.addTypeMappingForPath( "bean1From", Bean2From.class, Bean2To.class );
        support.addPropertyNameMapping( "bean1From", "bean2From", "bean2To" );
        
        support.addTypeAndPropertyNameMapping( "bean1From.bean2From", String.class, "fieldForLong", Long.class, "fieldLong" );
      }
    } );
    
    Bean0From bean0From = new Bean0From();
    Bean1From bean1From = new Bean1From();
    Bean2From bean2From = new Bean2From();
    bean2From.setFieldString1( "field1" );
    bean2From.setFieldString2( "field2" );
    bean2From.setFieldForLong( "123" );
    bean1From.setBean2From( bean2From );
    bean0From.setBean1From( bean1From );
    
    Bean0To bean0To = beanReplicator.clone( bean0From );
    
    assertNotNull( bean0To );
    assertNotNull( bean0To.getBean1To() );
    assertNotNull( bean0To.getBean1To().getBean2To() );
    assertEquals( "field1", bean0To.getBean1To().getBean2To().getFieldString1() );
    assertEquals( "field2", bean0To.getBean1To().getBean2To().getFieldString2() );
    assertEquals( 123l, bean0To.getBean1To().getBean2To().getFieldLong() );
    
    //System.out.println( ObjectUtils.toStringAsSimpleNestedHierarchy( bean0From ) );
    //System.out.println( ObjectUtils.toStringAsSimpleNestedHierarchy( bean0To ) );
  }
  
  @Test
  public void testInterfaceReplication()
  {
    BeanReplicator<Bean2From, IBean2To> beanReplicator = new BeanReplicator<Bean2From, IBean2To>( Bean2From.class, IBean2To.class ).setExceptionHandler( newFailingExceptionHandler() );
    beanReplicator.declare( new Declaration()
    {
      @Override
      public void declare( DeclarationSupport support )
      {
        support.addTypeAndPropertyNameMapping( String.class, "fieldForLong", Long.class, "fieldLong" );
      }
    } );
    
    Bean2From bean2From = new Bean2From();
    bean2From.setFieldString1( "field1" );
    bean2From.setFieldString2( "field2" );
    bean2From.setFieldForLong( "123" );
    
    IBean2To clone = beanReplicator.clone( bean2From );
    assertEquals( 123l, clone.getFieldLong() );
    assertNotSame( bean2From, clone );
    assertFalse( clone instanceof Bean2To );
  }
  
  private static ExceptionHandler newFailingExceptionHandler()
  {
    return new ExceptionHandler()
    {
      @Override
      public void handleException( Exception e )
      {
        e.printStackTrace();
        fail();
      }
    };
  }
  
}
