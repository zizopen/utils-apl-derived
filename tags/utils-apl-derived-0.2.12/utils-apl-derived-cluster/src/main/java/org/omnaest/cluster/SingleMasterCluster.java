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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ObjectUtils;
import org.omnaest.cluster.ClusterCommunicatorAdapter.ClusterStateHandler;
import org.omnaest.cluster.ClusterCommunicatorAdapter.ClusterStoreData;
import org.omnaest.cluster.ClusterCommunicatorAdapter.ClusterStoreDatas;
import org.omnaest.cluster.ClusterCommunicatorAdapter.ClusterStoreHandler;
import org.omnaest.cluster.ClusterState.ClusterNodeState;
import org.omnaest.cluster.communicator.ClusterCommunicator;
import org.omnaest.cluster.store.ClusterStore;
import org.omnaest.cluster.store.ClusterStoreProvider;
import org.omnaest.cluster.store.ClusterStoreProvider.ClusterStoreIdentifier;
import org.omnaest.utils.assertion.Assert;
import org.omnaest.utils.assertion.AssertLogger;

/**
 * {@link Cluster} implementation having a single master server and multiple slaves
 * 
 * @author Omnaest
 */
public class SingleMasterCluster implements Cluster
{
  private static final long          serialVersionUID                 = 775257056854060821L;
  
  private Server                     localServer                      = null;
  private ClusterConfiguration       clusterConfiguration;
  private ClusterCommunicatorAdapter clusterCommunicatorAdapter;
  private ClusterStoreProvider       clusterStoreProvider;
  private int                        scanInterval                     = 500;
  
  private volatile ClusterState      clusterState                     = null;
  
  private final ClusterWatchRunnable clusterWatchRunnable             = new ClusterWatchRunnable();
  private transient Thread           clusterWatchThread               = new Thread( this.clusterWatchRunnable );       ;
  private final Lock                 clusterNotAvailableLock          = new ReentrantLock();
  private final Condition            clusterNotAvailableLockCondition = this.clusterNotAvailableLock.newCondition();
  private volatile boolean           clusterAvailable                 = false;
  private boolean                    connected                        = false;
  
  private final AssertLogger         assertLogger                     = new AssertLogger( SingleMasterCluster.class );
  
  private final class ClusterStoreClientImpl<T> implements ClusterStore<T>
  {
    private final ClusterStore<T> clusterStore;
    private final String          fullQualifier;
    private final String[]        qualifiers;
    private final Class<T>        type;
    
    private ClusterStoreClientImpl( ClusterStore<T> clusterStore, String fullQualifier, String[] qualifiers, Class<T> type )
    {
      this.clusterStore = clusterStore;
      this.fullQualifier = fullQualifier;
      this.qualifiers = qualifiers;
      this.type = type;
    }
    
    @Override
    public T get()
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        return this.clusterStore.get();
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
    }
    
    @Override
    public void set( T instance )
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        checkIfMasterIsAvailableAndWaitIfNot();
        SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Client) Sending new instance for "
                                                             + this.fullQualifier );
        SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreSetElement( this.type, this.qualifiers, instance,
                                                                                    getMasterFromClusterState() );
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
    }
    
    @Override
    public void remove()
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        checkIfMasterIsAvailableAndWaitIfNot();
        SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Client) Sending remove request for "
                                                             + this.fullQualifier );
        SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreRemoveElement( this.type, this.qualifiers,
                                                                                       getMasterFromClusterState() );
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
      
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreClientImpl [clusterStore=" );
      builder.append( this.clusterStore );
      builder.append( ", fullQualifier=" );
      builder.append( this.fullQualifier );
      builder.append( ", qualifiers=" );
      builder.append( Arrays.toString( this.qualifiers ) );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  private final class ClusterStoreMasterImpl<T> implements ClusterStore<T>
  {
    private final ClusterStore<T> clusterStore;
    private final String          fullQualifier;
    private final String[]        qualifiers;
    private final Class<T>        type;
    
    private ClusterStoreMasterImpl( ClusterStore<T> clusterStore, String fullQualifier, String[] qualifiers, Class<T> type )
    {
      this.clusterStore = clusterStore;
      this.fullQualifier = fullQualifier;
      this.qualifiers = qualifiers;
      this.type = type;
    }
    
    @Override
    public T get()
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        return this.clusterStore.get();
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
    }
    
    @Override
    public void set( T instance )
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        //
        SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) Storing new instance for "
                                                             + this.fullQualifier );
        this.clusterStore.set( instance );
        
        //
        ClusterState clusterState = getClusterState();
        if ( clusterState != null )
        {
          Set<Server> serverSet = clusterState.getServerSet();
          if ( serverSet != null )
          {
            for ( Server server : serverSet )
            {
              if ( !ObjectUtils.equals( getLocalServer(), server ) )
              {
                try
                {
                  SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) Sending new instance for "
                                                                       + this.fullQualifier + " to client: " + server );
                  
                  SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreSetElement( this.type, this.qualifiers,
                                                                                              instance, server );
                }
                catch ( Exception e )
                {
                  SingleMasterCluster.this.assertLogger.error.message( e );
                }
              }
            }
          }
        }
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
    }
    
    @Override
    public void remove()
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        //
        SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) Removing instance for "
                                                             + this.fullQualifier );
        this.clusterStore.remove();
        
        //
        ClusterState clusterState = getClusterState();
        if ( clusterState != null )
        {
          Set<Server> serverSet = clusterState.getServerSet();
          if ( serverSet != null )
          {
            for ( Server server : serverSet )
            {
              if ( !ObjectUtils.equals( getLocalServer(), server ) )
              {
                try
                {
                  SingleMasterCluster.this.assertLogger.trace.message( getLocalServer()
                                                                       + " (Master) Sending remove instance request for "
                                                                       + this.fullQualifier + " to client: " + server );
                  
                  SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreRemoveElement( this.type, this.qualifiers,
                                                                                                 server );
                }
                catch ( Exception e )
                {
                  SingleMasterCluster.this.assertLogger.error.message( e );
                }
              }
            }
          }
        }
      }
      finally
      {
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
      
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ClusterStoreMasterImpl [clusterStore=" );
      builder.append( this.clusterStore );
      builder.append( ", fullQualifier=" );
      builder.append( this.fullQualifier );
      builder.append( ", qualifiers=" );
      builder.append( Arrays.toString( this.qualifiers ) );
      builder.append( ", type=" );
      builder.append( this.type );
      builder.append( "]" );
      return builder.toString();
    }
  }
  
  private final class ClusterWatchRunnable implements Runnable, Serializable
  {
    private static final long serialVersionUID = -7186309327530057737L;
    
    @Override
    public void run()
    {
      SingleMasterCluster.this.clusterNotAvailableLock.lock();
      try
      {
        SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Cluster watch thread started" );
        while ( SingleMasterCluster.this.connected )
        {
          if ( isMaster() )
          {
            final ClusterState clusterState = getClusterState();
            final int counter = checkSlaveServersAndUpdateState();
            if ( counter <= 1 && clusterState.size() >= 2 )
            {
              setClusterNotAvailable();
            }
            else
            {
              setClusterAvailable();
            }
            checkForHigherMasterServerIsAvailableAndSetItAsMasterIfAvailable();
          }
          else
          {
            try
            {
              boolean masterAvailable = SingleMasterCluster.this.clusterCommunicatorAdapter.isAvailable( getMasterFromClusterState() );
              if ( masterAvailable )
              {
                setClusterAvailable();
              }
              else
              {
                setClusterNotAvailable();
                findMaster();
                setClusterAvailable();
              }
            }
            catch ( RuntimeException e )
            {
            }
          }
          
          SingleMasterCluster.this.clusterNotAvailableLock.lock();
          try
          {
            SingleMasterCluster.this.clusterNotAvailableLockCondition.await( SingleMasterCluster.this.scanInterval,
                                                                             TimeUnit.MILLISECONDS );
          }
          finally
          {
            SingleMasterCluster.this.clusterNotAvailableLock.unlock();
          }
        }
      }
      catch ( InterruptedException e )
      {
      }
      finally
      {
        try
        {
          SingleMasterCluster.this.clusterNotAvailableLockCondition.signalAll();
        }
        catch ( Exception e )
        {
        }
        SingleMasterCluster.this.clusterNotAvailableLock.unlock();
      }
      
      //
      SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Cluster watch thread stopped" );
    }
    
    private void checkForHigherMasterServerIsAvailableAndSetItAsMasterIfAvailable()
    {
      ClusterState clusterState = SingleMasterCluster.this.clusterState;
      if ( clusterState != null )
      {
        for ( Server server : clusterState.getServerSet() )
        {
          if ( ObjectUtils.equals( getLocalServer(), server ) )
          {
            break;
          }
          
          ClusterNodeState clusterNodeState = clusterState.getClusterNodeState( server );
          if ( clusterNodeState.isAvailable() )
          {
            findMaster();
          }
        }
      }
    }
    
  }
  
  public SingleMasterCluster()
  {
    super();
    
  }
  
  @Override
  public boolean isAvailable()
  {
    return this.clusterAvailable;
  }
  
  @Override
  public void awaitUntilClusterIsAvailable() throws InterruptedException,
                                            ClusterDisconnectedException
  {
    if ( !this.clusterAvailable )
    {
      this.clusterNotAvailableLock.lock();
      try
      {
        while ( !this.clusterAvailable )
        {
          if ( !this.connected )
          {
            throw new ClusterDisconnectedException( getLocalServer() );
          }
          this.clusterNotAvailableLockCondition.await();
        }
      }
      finally
      {
        this.clusterNotAvailableLock.unlock();
      }
    }
  }
  
  private void checkIfMasterIsAvailableAndWaitIfNot()
  {
    boolean masterIsAvailable = false;
    while ( !masterIsAvailable )
    {
      //
      Server master = this.getMasterFromClusterState();
      if ( master == null )
      {
        this.assertLogger.info.message( getLocalServer() + " Master is not known, waiting for it to be known again." );
        setClusterNotAvailableAndWaitForGettingAvailableAgain();
      }
      if ( master != null )
      {
        masterIsAvailable = this.clusterCommunicatorAdapter.isAvailable( master );
        if ( !masterIsAvailable )
        {
          this.assertLogger.info.message( getLocalServer() + " Master is not available, waiting for it to be available again." );
          this.setClusterNotAvailableAndWaitForGettingAvailableAgain();
        }
      }
    }
  }
  
  private void setClusterNotAvailableAndWaitForGettingAvailableAgain()
  {
    try
    {
      this.setClusterNotAvailable();
      this.awaitUntilClusterIsAvailable();
    }
    catch ( Exception e )
    {
      this.assertLogger.error.message( e );
    }
  }
  
  @Override
  public ClusterState getClusterState()
  {
    return this.clusterState;
  }
  
  private void init()
  {
    SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Initializing cluster" );
    
    this.initClusterCommunication();
    this.initClusterWatchThread();
    
    this.findMaster();
    if ( this.isMaster() )
    {
      this.checkSlaveServersAndUpdateState();
    }
    
  }
  
  private void synchronizeClusterStoreFromMasterToSingleClient( Server client )
  {
    if ( client != null )
    {
      SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) sending store data to client: " + client );
      sendingStoreDataToDestinationServer( client );
    }
  }
  
  private void synchronizeClusterStoreFromOldMasterToNewMaster( Server newMaster )
  {
    if ( newMaster != null )
    {
      SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) sending store data to new master: "
                                                           + newMaster );
      sendingStoreDataToDestinationServer( newMaster );
    }
  }
  
  private void sendingStoreDataToDestinationServer( final Server destination )
  {
    this.clusterStoreProvider.executeReadAtomical( new Runnable()
    {
      @Override
      public void run()
      {
        List<ClusterStoreData> clusterStoreDataList = new ArrayList<ClusterCommunicatorAdapter.ClusterStoreData>();
        ClusterStoreIdentifier<?>[] clusterStoreIdentifiers = SingleMasterCluster.this.clusterStoreProvider.getClusterStoreIdentifiers();
        for ( ClusterStoreIdentifier<?> clusterStoreIdentifier : clusterStoreIdentifiers )
        {
          Object data = SingleMasterCluster.this.clusterStoreProvider.getClusterStore( clusterStoreIdentifier ).get();
          clusterStoreDataList.add( new ClusterStoreData( clusterStoreIdentifier, data ) );
        }
        ClusterStoreDatas clusterStoreDatas = new ClusterStoreDatas( clusterStoreDataList );
        SingleMasterCluster.this.clusterCommunicatorAdapter.sendClusterStoreData( clusterStoreDatas, destination );
      }
    } );
  }
  
  private void initClusterWatchThread()
  {
    SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Starting cluster watch thread" );
    if ( this.clusterWatchThread == null )
    {
      this.clusterWatchThread = new Thread( this.clusterWatchRunnable );
    }
    this.clusterWatchThread.start();
  }
  
  private void setClusterNotAvailable()
  {
    if ( this.clusterAvailable )
    {
      this.clusterNotAvailableLock.lock();
      try
      {
        this.clusterAvailable = false;
        this.assertLogger.info.message( getLocalServer() + " Cluster is not available anymore" );
      }
      finally
      {
        this.clusterNotAvailableLock.unlock();
      }
    }
  }
  
  private int checkSlaveServersAndUpdateState()
  {
    int counter = 0;
    final ClusterState clusterState = this.clusterState;
    if ( clusterState != null )
    {
      ClusterStateBuilder clusterStateBuilder = new ClusterStateBuilder( clusterState );
      for ( Server server : clusterState.getServerSet() )
      {
        if ( !ObjectUtils.equals( server, getLocalServer() ) )
        {
          int ping = checkSingleSlaveServerAndUpdateState( clusterStateBuilder, server );
          if ( ping >= 0 )
          {
            counter++;
          }
        }
        else
        {
          counter++;
        }
      }
      this.clusterState = clusterStateBuilder.build();
    }
    return counter;
  }
  
  private int checkSingleSlaveServerAndUpdateState( ClusterStateBuilder clusterStateBuilder, Server server )
  {
    ClusterNodeState clusterNodeState = this.getClusterState().getClusterNodeState( server );
    boolean availableBefore = clusterNodeState != null && clusterNodeState.isAvailable();
    
    int ping = SingleMasterCluster.this.clusterCommunicatorAdapter.ping( server );
    clusterStateBuilder.updateServerState( server, ping );
    
    final boolean available = ping >= 0;
    if ( available && !availableBefore )
    {
      this.synchronizeClusterStoreFromMasterToSingleClient( server );
    }
    
    return ping;
  }
  
  private void setClusterAvailable()
  {
    if ( !this.clusterAvailable )
    {
      this.clusterNotAvailableLock.lock();
      try
      {
        this.clusterAvailable = true;
      }
      finally
      {
        this.clusterNotAvailableLock.unlock();
        try
        {
          this.clusterNotAvailableLockCondition.signalAll();
        }
        catch ( Exception exception )
        {
        }
      }
      this.assertLogger.info.message( getLocalServer() + " Cluster is available now" );
    }
  }
  
  @Override
  public SingleMasterCluster connect()
  {
    this.assertLogger.info.message( this.getLocalServer() + " Cluster connecting..." );
    this.clusterNotAvailableLock.lock();
    try
    {
      this.connected = true;
      this.init();
    }
    finally
    {
      this.clusterNotAvailableLock.unlock();
    }
    this.assertLogger.info.message( this.getLocalServer() + " Cluster connected" );
    return this;
  }
  
  @Override
  public SingleMasterCluster disconnect()
  {
    this.assertLogger.info.message( this.getLocalServer() + " Cluster disconnecting..." );
    this.clusterNotAvailableLock.lock();
    try
    {
      this.connected = false;
      try
      {
        this.clusterWatchThread.interrupt();
        this.clusterNotAvailableLockCondition.awaitUninterruptibly();
        this.clusterWatchThread.join();
        this.clusterWatchThread = null;
      }
      catch ( InterruptedException e )
      {
      }
      this.setClusterNotAvailable();
      this.deinitClusterCommunication();
      this.notifySlavesOfMasterDisconnect();
      this.clusterNotAvailableLockCondition.signalAll();
    }
    finally
    {
      this.clusterNotAvailableLock.unlock();
    }
    this.assertLogger.info.message( this.getLocalServer() + " Cluster disconnected" );
    return this;
  }
  
  private void notifySlavesOfMasterDisconnect()
  {
    if ( this.isMaster() )
    {
      final ClusterState clusterState = new ClusterStateBuilder( this.clusterState ).setMaster( null ).build();
      final boolean publish = true;
      this.changeClusterStateWithoutBeingUnavailable( clusterState, publish );
    }
  }
  
  private void findMaster()
  {
    this.clusterNotAvailableLock.lock();
    try
    {
      SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Searching master..." );
      boolean foundMaster = false;
      while ( !foundMaster && this.connected )
      {
        Server master = null;
        {
          if ( this.clusterConfiguration != null )
          {
            List<Server> serverList = this.clusterConfiguration.getServerList();
            if ( serverList != null )
            {
              for ( Server server : serverList )
              {
                //
                if ( ObjectUtils.equals( getLocalServer(), server ) )
                {
                  this.electLocalServerAsMaster();
                  master = this.getLocalServer();
                  break;
                }
                
                //
                boolean available = this.clusterCommunicatorAdapter.isAvailable( server );
                if ( available )
                {
                  master = server;
                  
                  final ClusterState clusterState = new ClusterStateBuilder( this.clusterState ).setMaster( master ).build();
                  final boolean publish = true;
                  this.changeClusterState( clusterState, publish );
                  break;
                }
              }
            }
          }
        }
        
        foundMaster = master != null;
        if ( !foundMaster )
        {
          SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Master not found yet..." );
          try
          {
            this.clusterNotAvailableLockCondition.await( this.scanInterval, TimeUnit.MILLISECONDS );
          }
          catch ( InterruptedException e )
          {
          }
        }
        else
        {
          SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Master is " + master );
        }
      }
    }
    finally
    {
      this.clusterNotAvailableLock.unlock();
    }
  }
  
  private void electLocalServerAsMaster()
  {
    if ( this.clusterConfiguration != null )
    {
      //      
      final Server localServer = getLocalServer();
      this.changeClusterState( new ClusterStateBuilder( this.clusterState ).setMaster( localServer ).build(), true );
    }
  }
  
  private Server getLocalServer()
  {
    return this.localServer;
  }
  
  private void initClusterCommunication()
  {
    SingleMasterCluster.this.assertLogger.info.message( getLocalServer() + " Initializing cluster communication" );
    if ( this.clusterCommunicatorAdapter != null && this.clusterConfiguration != null )
    {
      final Server localServer = getLocalServer();
      ClusterStateHandler clusterStateHandler = new ClusterStateHandler()
      {
        private static final long serialVersionUID = 869338421252749839L;
        
        @Override
        public void setClusterState( ClusterState clusterState )
        {
          final boolean isMaster = isMaster();
          
          final boolean publish = !isMaster;
          changeClusterState( clusterState, publish );
          
          if ( getMasterFromClusterState() == null )
          {
            findMaster();
          }
          
          if ( isMaster )
          {
            Server newMaster = getMasterFromClusterState();
            if ( !ObjectUtils.equals( localServer, newMaster ) )
            {
              synchronizeClusterStoreFromOldMasterToNewMaster( newMaster );
            }
          }
          
        }
      };
      ClusterStoreHandler clusterStoreHandler = new ClusterStoreHandler()
      {
        private static final long serialVersionUID = -7234662873875057275L;
        
        @SuppressWarnings("unchecked")
        @Override
        public void set( Class<?> type, String[] qualifiers, Object instance )
        {
          SingleMasterCluster.this.clusterNotAvailableLock.lock();
          try
          {
            final String fullQualifier = type + " " + Arrays.deepToString( qualifiers );
            try
            {
              awaitUntilClusterIsAvailable();
            }
            catch ( InterruptedException e )
            {
            }
            
            //
            if ( isMaster() )
            {
              //              
              SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) persisting instance for "
                                                                   + fullQualifier );
              SingleMasterCluster.this.clusterStoreProvider.getClusterStore( (Class<Object>) type, qualifiers ).set( instance );
              
              //
              checkSlaveServersAndUpdateState();
              
              //
              ClusterState clusterState = getClusterState();
              for ( Server server : clusterState.getServerSet() )
              {
                ClusterNodeState clusterNodeState = clusterState.getClusterNodeState( server );
                boolean master = clusterNodeState.isMaster();
                boolean available = clusterNodeState.isAvailable();
                if ( !master )
                {
                  if ( !available )
                  {
                    ClusterStateBuilder clusterStateBuilder = new ClusterStateBuilder( clusterState );
                    checkSingleSlaveServerAndUpdateState( clusterStateBuilder, localServer );
                    clusterState = clusterStateBuilder.build();
                    available = clusterState.getClusterNodeState( server ).isAvailable();
                  }
                  
                  if ( available )
                  {
                    SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) Sending set instance for "
                                                                         + fullQualifier + " to " + server );
                    SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreSetElement( (Class<Object>) type, qualifiers,
                                                                                                instance, server );
                  }
                  else
                  {
                    SingleMasterCluster.this.assertLogger.trace.message( getLocalServer()
                                                                         + " (Master) Could not send set instance for "
                                                                         + fullQualifier + " to " + server
                                                                         + " since it was not available" );
                  }
                }
              }
            }
            else
            {
              //
              SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Client) persisting instance for "
                                                                   + fullQualifier );
              SingleMasterCluster.this.clusterStoreProvider.getClusterStore( (Class<Object>) type, qualifiers ).set( instance );
            }
          }
          catch ( ClusterDisconnectedException e )
          {
          }
          finally
          {
            SingleMasterCluster.this.clusterNotAvailableLock.unlock();
          }
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void remove( Class<?> type, String[] qualifiers )
        {
          SingleMasterCluster.this.clusterNotAvailableLock.lock();
          try
          {
            final String fullQualifier = type + " " + Arrays.deepToString( qualifiers );
            try
            {
              awaitUntilClusterIsAvailable();
            }
            catch ( InterruptedException e )
            {
            }
            
            //
            
            //
            if ( isMaster() )
            {
              //
              SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) removing store for "
                                                                   + fullQualifier );
              SingleMasterCluster.this.clusterStoreProvider.getClusterStore( (Class<Object>) type, qualifiers ).remove();
              
              //
              ClusterState clusterState = getClusterState();
              for ( Server server : clusterState.getServerSet() )
              {
                ClusterNodeState clusterNodeState = clusterState.getClusterNodeState( server );
                boolean master = clusterNodeState.isMaster();
                boolean available = clusterNodeState.isAvailable();
                if ( !master )
                {
                  if ( !available )
                  {
                    ClusterStateBuilder clusterStateBuilder = new ClusterStateBuilder( clusterState );
                    checkSingleSlaveServerAndUpdateState( clusterStateBuilder, localServer );
                    clusterState = clusterStateBuilder.build();
                    available = clusterState.getClusterNodeState( server ).isAvailable();
                  }
                  
                  if ( available )
                  {
                    SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Master) sent remove request for "
                                                                         + fullQualifier + " to " + server );
                    SingleMasterCluster.this.clusterCommunicatorAdapter.clusterStoreRemoveElement( (Class<Object>) type,
                                                                                                   qualifiers, server );
                  }
                  else
                  {
                    SingleMasterCluster.this.assertLogger.trace.message( getLocalServer()
                                                                         + " (Master) Could not send remove request for "
                                                                         + fullQualifier + " to " + server
                                                                         + " since it was not available" );
                  }
                }
              }
            }
            else
            {
              //
              SingleMasterCluster.this.assertLogger.trace.message( getLocalServer() + " (Client) removing store for "
                                                                   + fullQualifier );
              SingleMasterCluster.this.clusterStoreProvider.getClusterStore( (Class<Object>) type, qualifiers ).remove();
            }
          }
          catch ( ClusterDisconnectedException e )
          {
          }
          finally
          {
            SingleMasterCluster.this.clusterNotAvailableLock.unlock();
          }
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void setStoreData( final ClusterStoreDatas clusterStoreDatas )
        {
          SingleMasterCluster.this.assertLogger.trace.message( getLocalServer()
                                                               + " (Client) updating store data by data received from master" );
          SingleMasterCluster.this.clusterStoreProvider.executeWriteAtomical( new Runnable()
          {
            @Override
            public void run()
            {
              SingleMasterCluster.this.clusterStoreProvider.clear();
              for ( ClusterStoreData clusterStoreData : clusterStoreDatas.getClusterStoreDataList() )
              {
                ClusterStoreIdentifier clusterStoreIdentifier = clusterStoreData.getClusterStoreIdentifier();
                Object data = clusterStoreData.getData();
                SingleMasterCluster.this.clusterStoreProvider.getClusterStore( clusterStoreIdentifier ).set( data );
              }
              
            }
          } );
          
        }
      };
      this.clusterCommunicatorAdapter.enableReceiver( localServer, clusterStateHandler, clusterStoreHandler );
    }
  }
  
  private void deinitClusterCommunication()
  {
    if ( this.clusterCommunicatorAdapter != null )
    {
      this.clusterCommunicatorAdapter.disbaleReceiver( this.localServer );
    }
  }
  
  private void changeClusterState( ClusterState clusterState, boolean publish )
  {
    this.clusterNotAvailableLock.lock();
    try
    {
      this.setClusterNotAvailable();
      this.changeClusterStateWithoutBeingUnavailable( clusterState, publish );
      this.setClusterAvailable();
    }
    finally
    {
      this.clusterNotAvailableLock.unlock();
    }
  }
  
  /**
   * Master:set new state and send new state to all children Slave: send state to master and set new state
   * 
   * @param clusterState
   * @param publish
   *          if true slave/master is notified
   */
  private void changeClusterStateWithoutBeingUnavailable( ClusterState clusterState, boolean publish )
  {
    //    
    if ( !publish || !ObjectUtils.equals( this.clusterState, clusterState ) )
    {
      if ( this.isMaster() )
      {
        this.clusterState = clusterState;
        this.assertLogger.info.message( getLocalServer() + " (Master) Changed cluster state to " + clusterState );
        if ( publish )
        {
          final List<Server> serverList = this.clusterConfiguration.getServerList();
          if ( serverList != null )
          {
            for ( Server server : serverList )
            {
              if ( !ObjectUtils.equals( this.getLocalServer(), server ) )
              {
                this.assertLogger.info.message( getLocalServer() + " (Master) Sending cluster state to " + server );
                this.clusterCommunicatorAdapter.putClusterState( server, this.clusterState );
              }
            }
          }
        }
      }
      else
      {
        if ( publish )
        {
          Server master = this.getMasterFromClusterState();
          if ( master == null )
          {
            master = findMasterByAvailability();
          }
          if ( master != null )
          {
            this.assertLogger.info.message( getLocalServer() + " (Client) Sending cluster state to " + master );
            this.clusterCommunicatorAdapter.putClusterState( master, clusterState );
          }
        }
        
        this.clusterState = clusterState;
        this.assertLogger.info.message( getLocalServer() + " (Client) Changed cluster state to " + clusterState );
      }
    }
    
  }
  
  private Server findMasterByAvailability()
  {
    Server retval = null;
    
    ClusterState clusterState = getClusterState();
    if ( clusterState != null )
    {
      Set<Server> serverSet = clusterState.getServerSet();
      if ( serverSet != null )
      {
        final Server localServer = getLocalServer();
        for ( Server server : serverSet )
        {
          if ( !ObjectUtils.equals( localServer, server ) )
          {
            this.clusterCommunicatorAdapter.isAvailable( server );
            retval = server;
            break;
          }
        }
      }
    }
    
    return retval;
  }
  
  private Server getMasterFromClusterState()
  {
    final ClusterNodeState masterServerNodeState = this.clusterState == null ? null
                                                                            : this.clusterState.getMasterServerNodeState();
    return masterServerNodeState == null ? null : masterServerNodeState.getServer();
  }
  
  @Override
  public boolean isMaster()
  {
    final ClusterNodeState clusterNodeState = this.clusterState == null ? null
                                                                       : this.clusterState.getClusterNodeState( getLocalServer() );
    return clusterNodeState != null && clusterNodeState.isMaster();
  }
  
  @Override
  public <T> ClusterStore<T> getClusterStore( final Class<T> type, final String... qualifiers )
  {
    Assert.isNotNull( this.clusterStoreProvider, "No ClusterStoreProvider has been configured" );
    
    ClusterStore<T> retval = null;
    {
      final ClusterStore<T> clusterStore = this.clusterStoreProvider.getClusterStore( type, qualifiers );
      final String fullQualifier = type + " " + Arrays.deepToString( qualifiers );
      if ( this.isMaster() )
      {
        retval = new ClusterStoreMasterImpl<T>( clusterStore, fullQualifier, qualifiers, type );
      }
      else
      {
        retval = new ClusterStoreClientImpl<T>( clusterStore, fullQualifier, qualifiers, type );
      }
    }
    return retval;
  }
  
  public SingleMasterCluster setClusterConfiguration( ClusterConfiguration clusterConfiguration )
  {
    this.initClusterState( clusterConfiguration );
    this.clusterConfiguration = clusterConfiguration;
    return this;
  }
  
  private void initClusterState( ClusterConfiguration clusterConfiguration )
  {
    this.clusterState = new ClusterStateBuilder( clusterConfiguration ).build();
  }
  
  public SingleMasterCluster setClusterStoreProvider( ClusterStoreProvider clusterStoreProvider )
  {
    this.clusterStoreProvider = clusterStoreProvider;
    return this;
  }
  
  public SingleMasterCluster setClusterCommunicator( ClusterCommunicator clusterCommunicator )
  {
    this.clusterCommunicatorAdapter = new ClusterCommunicatorAdapter( clusterCommunicator );
    return this;
  }
  
  /**
   * Sets the scan interval in milliseconds
   * 
   * @param scanInterval
   * @return
   */
  public SingleMasterCluster setScanInterval( int scanInterval )
  {
    this.scanInterval = scanInterval;
    return this;
  }
  
  public SingleMasterCluster setLocalServer( Server localServer )
  {
    this.localServer = localServer;
    return this;
  }
  
  private void readObject( java.io.ObjectInputStream in ) throws IOException,
                                                         ClassNotFoundException,
                                                         InterruptedException,
                                                         ClusterDisconnectedException
  {
    in.defaultReadObject();
    if ( this.connected )
    {
      this.connect();
      this.awaitUntilClusterIsAvailable();
    }
  }
  
}
