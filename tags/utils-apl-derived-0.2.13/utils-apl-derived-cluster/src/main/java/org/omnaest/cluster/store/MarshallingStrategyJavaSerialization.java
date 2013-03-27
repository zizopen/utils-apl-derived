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

import org.apache.commons.lang3.SerializationUtils;

public class MarshallingStrategyJavaSerialization implements MarshallingStrategy
{
  
  private static final long serialVersionUID = -2891922954762705564L;
  
  @Override
  public byte[] marshal( Object object ) throws MarshallingException
  {
    try
    {
      return SerializationUtils.serialize( (Serializable) object );
    }
    catch ( Exception exception )
    {
      throw new MarshallingException( exception );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T unmarshal( byte[] data, Class<T> type ) throws UnmarshallingException
  {
    try
    {
      return (T) SerializationUtils.deserialize( data );
    }
    catch ( Exception exception )
    {
      throw new UnmarshallingException( exception );
    }
  }
  
}
