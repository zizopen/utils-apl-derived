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
package org.omnaest.utils.table.impl.rowdata;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.events.exception.ExceptionHandler;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.table.Row;
import org.omnaest.utils.table.RowDataAccessor;
import org.omnaest.utils.table.RowDataReader;
import org.omnaest.utils.table.Table;

/**
 * Factory for arbitrary bean proxy instances based on given {@link RowDataAccessor} instance
 * 
 * @see #build(RowDataAccessor)
 * @see #build(Row)
 * @author Omnaest
 * @param <B>
 */
public class RowDataBasedBeanFactory<B>
{
  /* ************************************** Variables / State (internal/hiding) ************************************* */
  private final StubCreator<B>      stubCreator;
  private final Map<Method, String> getterMethodToPropertyNameMap;
  private final Map<Method, String> setterMethodToPropertyNameMap;
  private final ExceptionHandler    exceptionHandler;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * @author Omnaest
   * @param <E>
   */
  private final static class RowAccessInvocationHandler<E> implements MethodInvocationHandler
  {
    private final ExceptionHandler    exceptionHandler;
    private final Map<Method, String> getterMethodToPropertyNameMap;
    private final Map<Method, String> setterMethodToPropertyNameMap;
    private final RowDataAccessor<E>  rowDataAccessor;
    
    /**
     * @see RowAccessInvocationHandler
     * @param exceptionHandler
     * @param getterMethodToPropertyNameMap
     * @param setterMethodToPropertyNameMap
     * @param rowDataAccessor
     */
    RowAccessInvocationHandler( ExceptionHandler exceptionHandler, Map<Method, String> getterMethodToPropertyNameMap,
                                Map<Method, String> setterMethodToPropertyNameMap, RowDataAccessor<E> rowDataAccessor )
    {
      this.exceptionHandler = exceptionHandler;
      this.getterMethodToPropertyNameMap = getterMethodToPropertyNameMap;
      this.setterMethodToPropertyNameMap = setterMethodToPropertyNameMap;
      this.rowDataAccessor = rowDataAccessor;
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      Object retval = null;
      
      try
      {
        final Method method = methodCallCapture.getMethod();
        final String methodName = method.getName();
        if ( this.getterMethodToPropertyNameMap.containsKey( method ) )
        {
          final String propertyName = this.getterMethodToPropertyNameMap.get( method );
          final E element = this.rowDataAccessor.getElement( propertyName );
          retval = element;
        }
        else if ( this.setterMethodToPropertyNameMap.containsKey( method ) )
        {
          final String propertyName = this.setterMethodToPropertyNameMap.get( method );
          E element = methodCallCapture.getArgumentCasted( 0 );
          this.rowDataAccessor.setElement( propertyName, element );
        }
        else if ( StringUtils.equals( "toString", methodName ) )
        {
          retval = Arrays.deepToString( this.rowDataAccessor.getElements() );
        }
      }
      catch ( Exception e )
      {
        this.exceptionHandler.handleException( e );
      }
      
      return retval;
    }
  }
  
  /* *************************************************** Methods **************************************************** */
  
  /**
   * @see RowDataBasedBeanFactory
   * @param beanType
   * @param exceptionHandler
   */
  public RowDataBasedBeanFactory( Class<B> beanType, ExceptionHandler exceptionHandler )
  {
    super();
    this.exceptionHandler = exceptionHandler;
    
    this.getterMethodToPropertyNameMap = new HashMap<Method, String>();
    this.setterMethodToPropertyNameMap = new HashMap<Method, String>();
    {
      final Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.beanMethodInformationSet( beanType );
      for ( BeanMethodInformation beanMethodInformation : beanMethodInformationSet )
      {
        final boolean isGetter = beanMethodInformation.isGetter();
        final boolean isSetter = beanMethodInformation.isSetter();
        final Method method = beanMethodInformation.getMethod();
        final String propertyName = beanMethodInformation.getPropertyName();
        if ( isGetter )
        {
          this.getterMethodToPropertyNameMap.put( method, propertyName );
        }
        else if ( isSetter )
        {
          this.setterMethodToPropertyNameMap.put( method, propertyName );
        }
      }
    }
    this.stubCreator = new StubCreator<B>( beanType, exceptionHandler );
  }
  
  public <E> B build( final RowDataAccessor<E> rowDataAccessor )
  {
    MethodInvocationHandler methodInvocationHandler = new RowDataBasedBeanFactory.RowAccessInvocationHandler<E>(
                                                                                                                 this.exceptionHandler,
                                                                                                                 this.getterMethodToPropertyNameMap,
                                                                                                                 this.setterMethodToPropertyNameMap,
                                                                                                                 rowDataAccessor );
    
    B bean = this.stubCreator.build( methodInvocationHandler );
    return bean;
  }
  
  public <E> B build( final Table<E> table, final E[] elements )
  {
    return this.build( new ElementsToRowDataReaderAdapter<E>( elements, table ) );
  }
  
  public <E> B build( final RowDataReader<E> rowDataReader )
  {
    return this.build( new RowDataAccessor<E>()
    {
      @Override
      public E getElement( String columnTitle )
      {
        return rowDataReader.getElement( columnTitle );
      }
      
      @Override
      public E getElement( int columnIndex )
      {
        return rowDataReader.getElement( columnIndex );
      }
      
      @Override
      public E[] getElements()
      {
        return rowDataReader.getElements();
      }
      
      @Override
      public void setElement( String columnTitle, E element )
      {
        throw new UnsupportedOperationException();
      }
      
      @Override
      public void setElement( int columnIndex, E element )
      {
        throw new UnsupportedOperationException();
      }
    } );
  }
  
  public <E> B build( final Row<E> row )
  {
    return this.build( new RowToRowDataAccessorAdapter<E>( row ) );
  }
}
