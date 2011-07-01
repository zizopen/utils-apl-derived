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
package org.omnaest.utils.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.collection.ListUtils.ElementTransformer;

/**
 * This class creates a proxy implementation for a given Java Bean type which is used as a facade to an underlying List&lt;?&gt;.
 * Be aware of the fact that only the declared fields of the {@link Class} or interface type will affect the {@link List} and no
 * access on properties declared by supertypes.
 * 
 * @author Omnaest
 * @see #newInstance(Map, Class)
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
  protected boolean            hasAccessToUnderlyingList = false;
  
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
        BeanMethodInformation beanMethodInformation = BeanUtils.determineBeanMethodInformation( method );
        if ( beanMethodInformation != null )
        {
          //
          String referencedFieldName = beanMethodInformation.getReferencedFieldName();
          
          //
          boolean accessToUnderlyingMap = ListToTypeAdapter.this.hasAccessToUnderlyingList
                                          && "underlyingList".equals( referencedFieldName );
          boolean isGetter = beanMethodInformation.isGetter() && args.length == 0;
          boolean isSetter = beanMethodInformation.isSetter() && args.length == 1;
          
          boolean isListNotNull = ListToTypeAdapter.this.list != null;
          
          //
          if ( !accessToUnderlyingMap )
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
   * Factory methods to create a new {@link ListToTypeAdapter} for a given {@link List} with the given {@link Class} as facade.
   * 
   * @param list
   * @param beanClass
   */
  public static <T, L extends List<?>> T newInstance( L list, Class<T> clazz )
  {
    //    
    T retval = null;
    
    //
    if ( list != null )
    {
      //
      ListToTypeAdapter<T, L> listToInterfaceAdapter = new ListToTypeAdapter<T, L>( list, clazz );
      
      //
      retval = listToInterfaceAdapter.classAdapter;
    }
    
    //
    return retval;
  }
  
  @SuppressWarnings("unchecked")
  protected ListToTypeAdapter( L list, Class<T> clazz )
  {
    //
    super();
    
    //
    this.list = (List<Object>) list;
    this.clazz = clazz;
    
    //
    List<BeanPropertyAccessor<T>> beanPropertyAccessorList = BeanUtils.determineBeanPropertyAccessorList( clazz );
    ElementTransformer<BeanPropertyAccessor<T>, String> elementTransformer = new ElementTransformer<BeanPropertyAccessor<T>, String>()
    {
      @Override
      public String transformElement( BeanPropertyAccessor<T> beanPropertyAccessor )
      {
        return beanPropertyAccessor.getPropertyname();
      }
    };
    this.propertynameList.addAll( ListUtils.transform( beanPropertyAccessorList, elementTransformer ) );
    
    //
    this.hasAccessToUnderlyingList = ListToTypeAdapter.isAssignableFromUnderlyingListAwareInterface( clazz );
    
    //
    this.initializeClassAdapter( clazz );
    this.ensureListSize();
  }
  
  /**
   * Initializes the given {@link List} to match the size of the list of declared methods of the underlying {@link Class}.
   * 
   * @param list
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
  
  @SuppressWarnings("unchecked")
  protected void initializeClassAdapter( Class<? extends T> clazz )
  {
    //
    try
    {
      //      
      Enhancer enhancer = new Enhancer();
      enhancer.setSuperclass( clazz );
      Callback callback = new ClassAdapterMethodInterceptor();
      enhancer.setCallback( callback );
      
      //
      this.classAdapter = (T) enhancer.create();
    }
    catch ( Exception e )
    {
    }
  }
  
  protected static boolean isAssignableFromUnderlyingListAwareInterface( Class<?> clazz )
  {
    return clazz != null && UnderlyingListAware.class.isAssignableFrom( clazz );
  }
  
}
