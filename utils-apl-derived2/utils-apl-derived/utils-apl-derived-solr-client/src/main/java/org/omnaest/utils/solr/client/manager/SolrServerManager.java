package org.omnaest.utils.solr.client.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.SolrPingResponse;

/**
 * Manager for {@link SolrServer} instances
 * 
 * @author Omnaest
 */
public class SolrServerManager
{
  /* ********************************************** Variables ********************************************** */
  protected final List<SolrServerAccessor> solrServerAccessorList;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Operation which is executed on a give {@link SolrServer} instance
   * 
   * @author Omnaest
   */
  public static interface SolrServerOperation
  {
    /**
     * @see SolrServerOperation
     * @param solrServer
     */
    public void executeOn( SolrServer solrServer );
  }
  
  /**
   * Repository of an internal {@link List} of one or more {@link SolrServer} instances
   * 
   * @author Omnaest
   */
  public static interface SolrServerAccessor
  {
    /**
     * Returns current {@link SolrServer}
     * 
     * @return
     */
    public SolrServer getActiveSolrServer();
    
  }
  
  /**
   * @see SolrServerAccessor
   * @author Omnaest
   */
  public static abstract class SolrServerAccessorBasic implements SolrServerAccessor
  {
    /* ********************************************** Variables ********************************************** */
    protected final Iterable<SolrServer> solrServerIterable;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see SolrServerAccessorBasic
     * @param solrServerIterable
     */
    public SolrServerAccessorBasic( Iterable<SolrServer> solrServerIterable )
    {
      super();
      this.solrServerIterable = solrServerIterable;
    }
    
    /**
     * Returns the {@link SolrPingResponse#getQTime()} for a {@link SolrServer} which is alive, otherwise -1 for a dead server
     * 
     * @param solrServer
     * @return
     */
    protected static int checkSolrServerQueryTime( SolrServer solrServer )
    {
      //
      int retval = -1;
      
      //
      try
      {
        //
        SolrPingResponse solrPingResponse = solrServer.ping();
        int status = solrPingResponse.getStatus();
        
        //
        boolean isHttpStatusCodeOk = status == 0;
        if ( isHttpStatusCodeOk )
        {
          retval = solrPingResponse.getQTime();
        }
      }
      catch ( Exception e )
      {
      }
      
      //
      return retval;
    }
    
  }
  
  /**
   * This {@link SolrServerAccessor} implementation holds a ordered {@link List} of {@link SolrServer} instances which are
   * addressed from first to last entry. If a live {@link SolrServer} is found it is used as long as it keeps alive. If the
   * recently active {@link SolrServer} goes dead the given {@link List} is searched again from the beginning. <br>
   * <br>
   * For any single attemp to resolve a {@link SolrServer} instance using {@link #getActiveSolrServer()} the given {@link List} is
   * only traversed once and if there are no active {@link SolrServer}s null is returned.<br>
   * <br>
   * The numberOfAccessBeforeRetry property can be used to set the number of access to {@link #getActiveSolrServer()} before the
   * available {@link SolrServer} instances are checked newly. This allows to recover after a failover to the regular
   * {@link SolrServer} instances.
   * 
   * @see SolrServerAccessor
   * @author Omnaest
   */
  public static class SolrServerAccessorFailover extends SolrServerAccessorBasic
  {
    /* ********************************************** Variables ********************************************** */
    protected SolrServer solrServerActive          = null;
    protected int        numberOfAccessBeforeRetry = 100;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see SolrServerAccessorFailover
     * @param solrServerIterable
     */
    public SolrServerAccessorFailover( Iterable<SolrServer> solrServerIterable )
    {
      super( solrServerIterable );
    }
    
    @Override
    public SolrServer getActiveSolrServer()
    {
      //
      if ( this.solrServerActive != null )
      {
        //
        final int solrServerQueryTime = checkSolrServerQueryTime( this.solrServerActive );
        if ( solrServerQueryTime < 0 )
        {
          this.solrServerActive = null;
        }
      }
      
      //
      if ( this.solrServerActive == null )
      {
        //
        for ( SolrServer solrServer : this.solrServerIterable )
        {
          int solrServerQueryTime = checkSolrServerQueryTime( solrServer );
          if ( solrServerQueryTime >= 0 )
          {
            this.solrServerActive = solrServer;
            break;
          }
        }
      }
      
      // 
      return this.solrServerActive;
    }
    
    /**
     * @param numberOfAccessBeforeRetry
     *          the numberOfAccessBeforeRetry to set
     */
    public void setNumberOfAccessBeforeRetry( int numberOfAccessBeforeRetry )
    {
      this.numberOfAccessBeforeRetry = numberOfAccessBeforeRetry;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see SolrServerManager
   * @param solrServerAccessorList
   */
  public SolrServerManager( List<SolrServerAccessor> solrServerAccessorList )
  {
    super();
    this.solrServerAccessorList = solrServerAccessorList;
  }
  
  /**
   * Executes a given {@link SolrServerOperation} on all currently active {@link SolrServer} instances
   * 
   * @param solrServerOperation
   */
  public void executeOnAllSolrServers( SolrServerOperation solrServerOperation )
  {
    //
    if ( solrServerOperation != null )
    {
      //
      for ( SolrServer solrServer : this.resolveActiveSolrServerList() )
      {
        solrServerOperation.executeOn( solrServer );
      }
    }
  }
  
  /**
   * Returns a new {@link List} instance containing all currently active {@link SolrServer} instances
   * 
   * @return
   */
  public List<SolrServer> resolveActiveSolrServerList()
  {
    //
    final List<SolrServer> retlist = new ArrayList<SolrServer>();
    
    //
    for ( SolrServerAccessor solrServerAccessor : this.solrServerAccessorList )
    {
      //
      final SolrServer solrServer = solrServerAccessor.getActiveSolrServer();
      if ( solrServer != null )
      {
        retlist.add( solrServer );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a new {@link Iterator} instances for all {@link SolrServer} instances resolved by
   * {@link #resolveActiveSolrServerList()}. During the iteration there will be no check for live or dead {@link SolrServer}
   * instances.<br>
   * <br>
   * This method should provide a simple round robin implementation for use during indexing time.<br>
   * <br>
   * The returned {@link Iterator} instance will never return false for {@link Iterator#hasNext()} and will always return the next
   * {@link SolrServer} instance within the cycle for the {@link Iterator#next()} method.
   * 
   * @return
   */
  public Iterator<SolrServer> newRoundRobinIteratorForActiveSolrServers()
  {
    return new Iterator<SolrServer>()
    {
      /* ********************************************** Variables ********************************************** */
      private final List<SolrServer> solrServerList       = resolveActiveSolrServerList();
      private int                    currentIndexPosition = -1;
      
      /* ********************************************** Methods ********************************************** */
      
      @Override
      public boolean hasNext()
      {
        return true;
      }
      
      @Override
      public SolrServer next()
      {
        this.currentIndexPosition = ( this.currentIndexPosition + 1 ) % this.solrServerList.size();
        return this.solrServerList.get( this.currentIndexPosition );
      }
      
      @Override
      public void remove()
      {
        this.solrServerList.remove( this.currentIndexPosition );
      }
    };
  }
}
