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

public abstract class ClusterStoreProviderAbstract implements ClusterStoreProvider
{
  
  private static final long serialVersionUID = 6782062885839337616L;
  
  public ClusterStoreProviderAbstract()
  {
    super();
  }
  
  @Override
  public <T> ClusterStore<T> getClusterStore( Class<T> type, String... qualifiers )
  {
    return this.getClusterStore( new ClusterStoreIdentifier<T>( type, qualifiers ) );
  }
  
}
