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

import java.io.Serializable;

public interface MarshallingStrategy extends Serializable
{
  public static class MarshallingException extends Exception
  {
    private static final long serialVersionUID = 6134517209077159036L;
    
    public MarshallingException( Throwable cause )
    {
      super( cause );
    }
  }
  
  public static class UnmarshallingException extends Exception
  {
    private static final long serialVersionUID = 6134517202372159036L;
    
    public UnmarshallingException( Throwable cause )
    {
      super( cause );
    }
  }
  
  public byte[] marshal( Object object ) throws MarshallingException;
  
  public <T> T unmarshal( byte[] data, Class<T> type ) throws UnmarshallingException;
  
}
