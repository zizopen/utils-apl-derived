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
package org.omnaest.utils.solr.client.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.junit.Test;
import org.omnaest.utils.solr.client.manager.SolrServerManager.SolrServerAccessor;

/**
 * @see SolrServerManager
 * @author Omnaest
 */
public class SolrServerManagerTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  private SolrServer         solrServer         = newSolrServer();
  private SolrServerAccessor solrServerAccessor = new SolrServerManager.SolrServerAccessorFailover(
                                                                                                    Arrays.asList( this.solrServer ) );
  private SolrServerManager  solrServerManager  = new SolrServerManager( Arrays.asList( this.solrServerAccessor ) );
  
  /* ********************************************** Methods ********************************************** */
  
  public static SolrServer newSolrServer()
  {
    //
    SolrServer retval = null;
    
    //
    String solrServerUrl = "http://localhost:8983/solr";
    try
    {
      retval = new CommonsHttpSolrServer( solrServerUrl );
    }
    catch ( MalformedURLException e )
    {
      e.printStackTrace();
    }
    
    //
    return retval;
  }
  
  @Test
  public void testResolveActiveSolrServerList()
  {
    List<SolrServer> activeSolrServerList = this.solrServerManager.resolveActiveSolrServerList();
    assertNotNull( activeSolrServerList );
    assertEquals( 1, activeSolrServerList.size() );
  }
  
}
