/*******************************************************************************
 * Copyright 2013 Danny Kunz
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
package org.omnaest.cluster.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;
import org.omnaest.cluster.store.ClusterStoreProvider.ClusterStoreIdentifier;
import org.omnaest.utils.events.exception.basic.ExceptionHandlerRethrowingAsRuntimeException;

public class ClusterStoreProviderFileBasedTest
{
  private final ClusterStoreProvider clusterStoreProvider = new ClusterStoreProviderFileBased( new File( "target/test" ) ).setExceptionHandler( new ExceptionHandlerRethrowingAsRuntimeException() )
                                                                                                                          .setMarshallingStrategy( new MarshallingStrategyXMLJAXB() );
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  static class TestDomain implements Serializable
  {
    private static final long serialVersionUID = 440277899275401199L;
    private String            field;
    
    private TestDomain()
    {
      super();
    }
    
    public TestDomain( String field )
    {
      this();
      this.field = field;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TestDomain [field=" );
      builder.append( this.field );
      builder.append( "]" );
      return builder.toString();
    }
    
    public String getField()
    {
      return this.field;
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.field == null ) ? 0 : this.field.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof TestDomain ) )
      {
        return false;
      }
      TestDomain other = (TestDomain) obj;
      if ( this.field == null )
      {
        if ( other.field != null )
        {
          return false;
        }
      }
      else if ( !this.field.equals( other.field ) )
      {
        return false;
      }
      return true;
    }
    
  }
  
  @Before
  public void setUp()
  {
    this.clusterStoreProvider.clear();
  }
  
  @Test
  public void testClusterStoreProviderFileBased() throws Exception
  {
    ClusterStore<TestDomain> store = this.clusterStoreProvider.getClusterStore( TestDomain.class, "a", "b", "c" );
    
    final TestDomain instance = new TestDomain( "test" );
    store.set( instance );
    
    assertEquals( instance, store.get() );
    
    ClusterStoreIdentifier<?>[] clusterStoreIdentifiers = this.clusterStoreProvider.getClusterStoreIdentifiers();
    assertNotNull( clusterStoreIdentifiers );
    assertEquals( 1, clusterStoreIdentifiers.length );
    
    store.remove();
    assertNull( store.get() );
    
  }
}
