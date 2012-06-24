/*******************************************************************************
 * Copyright 2011 Danny Kunz
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
package org.omnaest.utils.structure.element.converter;

import java.io.Serializable;

import org.omnaest.utils.assertion.Assert;

/**
 * Extension of {@link ElementConverter} which allows to retrieve the source and target types using
 * {@link #getSourceAndTargetType()}.
 * 
 * @author Omnaest
 * @param <FROM>
 * @param <TO>
 */
public interface ElementConverterTypeAware<FROM, TO> extends ElementConverter<FROM, TO>
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @see ElementConverterTypeAware
   * @author Omnaest
   */
  public static class SourceAndTargetType<FROM, TO> implements Serializable
  {
    /* ************************************************** Constants *************************************************** */
    private static final long           serialVersionUID = 7691608527507412541L;
    /* ********************************************** Variables ********************************************** */
    private final Class<? extends FROM> sourceType;
    private final Class<? extends TO>   targetType;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see SourceAndTargetType
     * @param sourceType
     * @param targetType
     * @throws IllegalArgumentException
     *           if any parameter is null
     */
    public SourceAndTargetType( Class<? extends FROM> sourceType, Class<? extends TO> targetType )
    {
      super();
      this.sourceType = sourceType;
      this.targetType = targetType;
      
      Assert.isNotNull( "source and target type must be not null", sourceType, targetType );
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "SourceAndTargetType [sourceType=" );
      builder.append( this.sourceType );
      builder.append( ", targetType=" );
      builder.append( this.targetType );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @return the sourceType
     */
    @SuppressWarnings("unchecked")
    public Class<FROM> getSourceType()
    {
      return (Class<FROM>) this.sourceType;
    }
    
    /**
     * @return the targetType
     */
    @SuppressWarnings("unchecked")
    public Class<TO> getTargetType()
    {
      return (Class<TO>) this.targetType;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  /**
   * Returns the {@link SourceAndTargetType} of the {@link ElementConverter}
   * 
   * @return
   */
  public SourceAndTargetType<FROM, TO> getSourceAndTargetType();
}
