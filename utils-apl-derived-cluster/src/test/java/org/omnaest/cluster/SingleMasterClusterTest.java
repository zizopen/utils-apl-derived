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
package org.omnaest.cluster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.cluster.Cluster.ClusterDisconnectedException;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/SingleMasterClusterTestAC.xml")
public class SingleMasterClusterTest
{
  @Autowired
  @Qualifier("singleMasterCluster1")
  Cluster cluster1;
  
  @Autowired
  @Qualifier("singleMasterCluster2")
  Cluster cluster2;
  
  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  static class TestDomain implements Serializable
  {
    private static final long serialVersionUID = -1530290982315559134L;
    private String            field;
    
    public String getField()
    {
      return this.field;
    }
    
    public TestDomain setField( String field )
    {
      this.field = field;
      return this;
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
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "TestDomain [field=" );
      builder.append( this.field );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  @Test
  public void test() throws InterruptedException,
                    ClusterDisconnectedException
  {
    //
    this.cluster1.connect();
    this.cluster2.connect();
    
    this.cluster1.awaitUntilClusterIsAvailable();
    this.cluster2.awaitUntilClusterIsAvailable();
    
    //
    final TestDomain testDomain1 = new TestDomain().setField( "value1" );
    final TestDomain testDomain2 = new TestDomain().setField( "value2" );
    
    //testDomain1 set by Master, both clusters connected
    this.cluster1.getClusterStore( TestDomain.class, "abc", "def" ).set( testDomain1 );
    {
      TestDomain testDomainRetrieved = this.cluster1.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain1, testDomainRetrieved );
    }
    {
      TestDomain testDomainRetrieved = this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain1, testDomainRetrieved );
    }
    
    //testDomain2 set by client, both clusters connected
    this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).set( testDomain2 );
    {
      TestDomain testDomainRetrieved = this.cluster1.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain2, testDomainRetrieved );
    }
    {
      TestDomain testDomainRetrieved = this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain2, testDomainRetrieved );
    }
    
    //
    System.out.println( "cluster1 disconnect" );
    this.cluster1.disconnect();
    this.cluster2.awaitUntilClusterIsAvailable();
    {
      TestDomain testDomainRetrieved = this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain2, testDomainRetrieved );
    }
    
    //testDomain1 set by client, master disconnected
    System.out.println( "set test domain 1 to cluster2" );
    this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).set( testDomain1 );
    {
      TestDomain testDomainRetrieved = this.cluster1.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain2, testDomainRetrieved );
    }
    System.out.println( "retrieving test domain from cluster2" );
    {
      TestDomain testDomainRetrieved = this.cluster2.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain1, testDomainRetrieved );
    }
    
    this.cluster1.connect();
    this.cluster1.awaitUntilClusterIsAvailable();
    {
      TestDomain testDomainRetrieved = this.cluster1.getClusterStore( TestDomain.class, "abc", "def" ).get();
      assertEquals( testDomain1, testDomainRetrieved );
    }
    
    //
    {
      Map<String, String> clusterStoreMap = this.cluster1.getClusterStoreMap( "xyz", "123" );
      clusterStoreMap.put( "key1", "value1" );
    }
    {
      Map<String, String> clusterStoreMap = this.cluster2.getClusterStoreMap( "xyz", "123" );
      assertEquals( "value1", clusterStoreMap.get( "key1" ) );
      assertEquals( SetUtils.valueOf( "key1" ), clusterStoreMap.keySet() );
      assertEquals( SetUtils.valueOf( "value1" ), SetUtils.valueOf( clusterStoreMap.values() ) );
      
      clusterStoreMap.put( "key2", "value2" );
      assertEquals( SetUtils.valueOf( "key1", "key2" ), clusterStoreMap.keySet() );
      clusterStoreMap.remove( "key1" );
    }
    {
      Map<String, String> clusterStoreMap = this.cluster1.getClusterStoreMap( "xyz", "123" );
      assertEquals( "value2", clusterStoreMap.get( "key2" ) );
      assertEquals( SetUtils.valueOf( "key2" ), clusterStoreMap.keySet() );
      assertEquals( SetUtils.valueOf( "value2" ), SetUtils.valueOf( clusterStoreMap.values() ) );
    }
    
    //
    this.cluster2.disconnect();
    this.cluster1.disconnect();
    
  }
  
  @Test
  public void testSerializable() throws InterruptedException,
                                ClusterDisconnectedException
  {
    System.out.println( "test serializable" );
    this.cluster1.connect();
    this.cluster1.awaitUntilClusterIsAvailable();
    Cluster clone = SerializationUtils.clone( this.cluster1 );
    assertNotNull( clone );
    assertTrue( clone.isAvailable() );
    clone.disconnect();
  }
}
