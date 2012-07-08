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
package org.omnaest.utils.beans.adapter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;

/**
 * This class creates a proxy implementation for a given Java Bean type which is used as a facade to an underlying List&lt;?&gt;.
 * Be aware of the fact that only the declared fields of the {@link Class} or interface type will affect the {@link List} and no
 * access on properties declared by supertypes.
 * 
 * @author Omnaest
 * @see #newInstance(List, Class)
 * @param <T>
 * @param <L>
 */
public class ListToTypeAdapter<T, L extends List<?>>
{
  /* ********************************************** Variables ********************************************** */
  protected List<Object>       list                      = null;
  protected T                  classAdapter              = null;
  protected Class<? extends T> clazz                     = null;
  protected List<String>       propertynameList          = new ArrayList<String>();
  protected boolean            hasAccessToUnderlyingData = false;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * This interface makes a derivative type aware of an underlying map implementation. This is normally used in combination with
   * an {@link ListToTypeAdapter}.
   */
  public static interface UnderlyingListAware<L extends List<?>>
  {
    /**
     * Returns the {@link List} which underlies this class type facade.
     * 
     * @return
     */
    public L getUnderlyingList();
    
    /**
     * Sets the {@link List} which should underly this class type facade.
     * 
     * @param underlyingList
     */
    public void setUnderlyingList( L underlyingList );
    
    /**
     * Sets the underlying property name list after which the elements of the value list are ordered.
     * 
     * @param propertynameList
     */
    public void setUnderlyingPropertynameList( List<String> propertynameList );
    
    /**
     * Returns the underlying property name list after which the elements of the value list are ordered.
     * 
     * @return
     */
    public List<String> getUnderlyingPropertynameList();
  }
  
  /**
   * A {@link MethodInterceptor} implementation special for this {@link ListToTypeAdapter}
   */
  protected class ClassAdapterMethodInterceptor implements MethodInterceptor
  {
    @SuppressWarnings("unchecked")
    @Override
    public Object intercept( Object obj, Method method, Object[] args, MethodProxy proxy ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      try
      {
        //
        BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
        if ( beanMethodInformation != null )
        {
          //
          String referencedFieldName = beanMethodInformation.getPropertyName();
          
          //
          boolean accessToUnderlyingMap = ListToTypeAdapter.this.hasAccessToUnderlyingData
                                          && StringUtils.equals( "underlyingList", referencedFieldName );
          boolean accessUnderlyingPropertynameList = ListToTypeAdapter.this.hasAccessToUnderlyingData
                                                     && StringUtils.equals( "underlyingPropertynameList", referencedFieldName );
          
          //
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          
          //
          boolean isListNotNull = ListToTypeAdapter.this.list != null;
          
          //
          if ( !accessToUnderlyingMap && !accessUnderlyingPropertynameList )
          {
            //
            int methodIndexPosition = ListToTypeAdapter.this.propertynameList.indexOf( referencedFieldName );
            
            //
            if ( isListNotNull && methodIndexPosition >= 0 )
            {
              //
              ListToTypeAdapter.this.ensureListSize();
              
              //
              if ( isGetter )
              {
                //                
                retval = ListToTypeAdapter.this.list.get( methodIndexPosition );
              }
              else if ( isSetter )
              {
                //
                ListToTypeAdapter.this.list.set( methodIndexPosition, args[0] );
                
                //
                retval = Void.TYPE;
              }
            }
          }
          else
          {
            //
            if ( accessToUnderlyingMap )
            {
              //
              if ( isGetter )
              {
                //
                retval = ListToTypeAdapter.this.list;
              }
              else if ( isSetter )
              {
                //
                ListToTypeAdapter.this.list = (List<Object>) args[0];
                
                //
                retval = Void.TYPE;
              }
            }
            else if ( accessUnderlyingPropertynameList )
            {
              //
              if ( isGetter )
              {
                //
                retval = ListToTypeAdapter.this.propertynameList;
              }
              else if ( isSetter )
              {
                //
                ListToTypeAdapter.this.propertynameList = (List<String>) args[0];
                
                //
                retval = Void.TYPE;
              }
            }
          }
        }
      }
      catch ( Exception e )
      {
      }
      
      // 
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Factory method to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade. The
   * list will contain as many elements as properties are possible and for each property an immutable index position is reserved.
   * Within the list the objects will be stored in the order the property names have after invoking the given {@link Comparator}
   * on them.
   * 
   * @see #newInstance(List, Class)
   * @param list
   * @param clazz
   * @param comparatorPropertyname
   * @param underlyingListAware
   *          : true > returned stub implements {@link UnderlyingListAware}
   * @return new
   */
  public static <T> T newInstance( List<?> list,
                                   Class<T> clazz,
                                   Comparator<String> comparatorPropertyname,
                                   boolean underlyingListAware )
  {
    //
    List<String> propertynameList = null;
    return ListToTypeAdapter.newInstance( list, clazz, comparatorPropertyname, propertynameList, underlyingListAware );
  }
  
  /**
   * Factory method to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade. The
   * list will contain as many elements as properties are possible and for each property an immutable index position is reserved.
   * The objects will be stored in the order of the given {@link List} of property names.
   * 
   * @see #newInstance(List, Class)
   * @param list
   * @param clazz
   * @param underlyingListAware
   *          : true > returned stub implements {@link UnderlyingListAware}
   * @return new
   */
  public static <T> T newInstance( List<?> list, Class<T> clazz, List<String> propertynameList, boolean underlyingListAware )
  {
    //
    Comparator<String> comparatorPropertyname = null;
    return ListToTypeAdapter.newInstance( list, clazz, comparatorPropertyname, propertynameList, underlyingListAware );
  }
  
  /**
   * Factory method to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade. The
   * list will contain as many elements as properties are possible and for each property an immutable index position is reserved.
   * The objects will be stored in no particular order within the {@link List}.
   * 
   * @see #newInstance(List, Class)
   * @param list
   * @param clazz
   * @param underlyingListAware
   *          : true > returned stub implements {@link UnderlyingListAware}
   * @return new
   */
  public static <T> T newInstance( List<?> list, Class<T> clazz, boolean underlyingListAware )
  {
    //
    List<String> propertynameList = null;
    Comparator<String> comparatorPropertyname = null;
    return ListToTypeAdapter.newInstance( list, clazz, comparatorPropertyname, propertynameList, underlyingListAware );
  }
  
  /**
   * Factory method to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade. The
   * list will contain as many elements as properties are possible and for each property an immutable index position is reserved.
   * The objects will be stored in no particular order within the {@link List}.
   * 
   * @see #newInstance(List, Class, boolean)
   * @see #newInstance(List, Class, List, boolean)
   * @see #newInstance(List, Class, Comparator, boolean) *
   * @param list
   * @param clazz
   * @return new
   */
  public static <T> T newInstance( List<?> list, Class<T> clazz )
  {
    //
    List<String> propertynameList = null;
    Comparator<String> comparatorPropertyname = null;
    boolean underlyingListAware = false;
    return ListToTypeAdapter.newInstance( list, clazz, comparatorPropertyname, propertynameList, underlyingListAware );
  }
  
  /**
   * Factory method to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade.
   * 
   * @see #newInstance(List, Class)
   * @see #newInstance(List, Class, Comparator, boolean)
   * @see #newInstance(List, Class, List, boolean)
   * @param list
   * @param clazz
   * @param comparatorPropertyname
   * @param propertynameList
   * @param underlyingListAware
   * @return new
   */
  protected static <T> T newInstance( List<?> list,
                                      Class<T> clazz,
                                      Comparator<String> comparatorPropertyname,
                                      List<String> propertynameList,
                                      boolean underlyingListAware )
  {
    //    
    T retval = null;
    
    //
    if ( list != null && clazz != null )
    {
      //
      ListToTypeAdapter<T, List<?>> listToInterfaceAdapter = new ListToTypeAdapter<T, List<?>>( list, clazz,
                                                                                                comparatorPropertyname,
                                                                                                propertynameList,
                                                                                                underlyingListAware );
      
      //
      retval = listToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  /**
   * @param list
   * @param clazz
   * @param comparatorPropertyname
   *          : optional
   * @param propertynameList
   *          : optional
   * @param underlyingListAware
   */
  @SuppressWarnings("unchecked")
  protected ListToTypeAdapter( L list, Class<T> clazz, Comparator<String> comparatorPropertyname, List<String> propertynameList,
                               boolean underlyingListAware )
  {
    //
    super();
    
    //
    this.list = (List<Object>) list;
    this.clazz = clazz;
    
    //
    if ( propertynameList != null )
    {
      //
      this.propertynameList.addAll( propertynameList );
    }
    else
    {
      //
      List<String> propertynameResolvedList = new ArrayList<String>();
      {
        //
        Set<BeanPropertyAccessor<T>> beanPropertyAccessorList = BeanUtils.beanPropertyAccessorSet( clazz );
        ElementConverter<BeanPropertyAccessor<T>, String> elementTransformer = new ElementConverter<BeanPropertyAccessor<T>, String>()
        {
          @Override
          public String convert( BeanPropertyAccessor<T> beanPropertyAccessor )
          {
            return beanPropertyAccessor.getPropertyName();
          }
        };
        propertynameResolvedList.addAll( ListUtils.convert( beanPropertyAccessorList, elementTransformer ) );
      }
      
      //
      this.propertynameList = propertynameResolvedList;
    }
    
    //
    if ( comparatorPropertyname != null )
    {
      Collections.sort( this.propertynameList, comparatorPropertyname );
    }
    
    //
    this.hasAccessToUnderlyingData = underlyingListAware;
    
    //
    this.initializeClassAdapter( clazz, underlyingListAware );
    this.ensureListSize();
  }
  
  /**
   * Initializes the given {@link List} to match the size of the list of declared methods of the underlying {@link Class}.
   */
  protected void ensureListSize()
  {
    //
    int numberOfDeclaredFields = this.propertynameList.size();
    while ( this.list.size() < numberOfDeclaredFields )
    {
      this.list.add( null );
    }
  }
  
  /**
   * Creates the stub
   * 
   * @param clazz
   * @param underlyingListAware
   */
  protected void initializeClassAdapter( Class<? extends T> clazz, boolean underlyingListAware )
  {
    //
    try
    {
      //      
      Class<?>[] interfaces = underlyingListAware ? new Class[] { UnderlyingListAware.class } : null;
      MethodInterceptor methodInterceptor = new ClassAdapterMethodInterceptor();
      this.classAdapter = StubCreator.newStubInstance( clazz, interfaces, methodInterceptor );
    }
    catch ( Exception e )
    {
    }
  }
  
}
