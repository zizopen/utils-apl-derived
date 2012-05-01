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
package org.omnaest.utils.store.persistence.marshaller;

/**
 * @see #marshal(Object)
 * @see #unmarshal(byte[])
 * @see MarshallingException
 * @see UnMarshallingException
 * @author Omnaest
 */
public interface MarshallerAndUnmarshaller
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @see MarshallerAndUnmarshaller
   * @author Omnaest
   */
  @SuppressWarnings("serial")
  public static class MarshallingException extends RuntimeException
  {
  }
  
  /**
   * @see MarshallerAndUnmarshaller
   * @author Omnaest
   */
  @SuppressWarnings("serial")
  public static class UnMarshallingException extends RuntimeException
  {
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Marshalls a given {@link Object} into a byte buffer
   * 
   * @param object
   * @return
   */
  public byte[] marshal( Object object );
  
  /**
   * Unmarshalls a given byte array
   * 
   * @param buffer
   * @return
   */
  public <E> E unmarshal( byte[] buffer );
}
