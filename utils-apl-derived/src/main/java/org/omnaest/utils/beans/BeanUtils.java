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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.tuple.TupleDuad;

/**
 * Helper class for Java beans.
 * 
 * @author Omnaest
 */
public class BeanUtils
{
  
  /**
   * Returns the number of available Java Bean properties for the given Java Bean type.
   * 
   * @param beanClass
   * @return
   */
  public static int determineNumberOfProperties( Class<?> beanClass )
  {
    return BeanUtils.determineBeanPropertyAccessorSet( beanClass ).size();
  }
  
  /**
   * Returns a natural ordered list of {@link BeanPropertyAccessor} instances for all Java Bean properties of the given
   * {@link Class}. Natural order means the order of declared {@link Field}, declared {@link Method}s, inherited {@link Field}s
   * and inherited {@link Method}s.
   * 
   * @see #determineFieldnameToBeanPropertyAccessorMap(Class)
   * @see #determineBeanPropertyAccessor(Class, Field)
   * @param beanClass
   * @return
   */
  public static <B> List<BeanPropertyAccessor<B>> determineBeanPropertyAccessorList( Class<B> beanClass )
  {
    //
    List<BeanPropertyAccessor<B>> retlist = new ArrayList<BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null )
    {
      //
      Map<String, BeanPropertyAccessor<B>> fieldnameToBeanPropertyAccessorMap = BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass );
      
      //
      retlist.addAll( fieldnameToBeanPropertyAccessorMap.values() );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns a set of {@link BeanPropertyAccessor} instances for all Java Bean properties of the given {@link Class}
   * 
   * @see #determineFieldnameToBeanPropertyAccessorMap(Class)
   * @see #determineBeanPropertyAccessor(Class, Field)
   * @param beanClass
   * @return
   */
  public static <B> Set<BeanPropertyAccessor<B>> determineBeanPropertyAccessorSet( Class<B> beanClass )
  {
    //
    Set<BeanPropertyAccessor<B>> retset = new HashSet<BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null )
    {
      //
      Map<String, BeanPropertyAccessor<B>> fieldnameToBeanPropertyAccessorMap = BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass );
      
      //
      retset.addAll( fieldnameToBeanPropertyAccessorMap.values() );
    }
    
    //
    return retset;
  }
  
  /**
   * Returns a map of field names and {@link BeanPropertyAccessor} instances for all Java Bean properties related to at least one
   * of the given {@link Field}s.
   * 
   * @param <B>
   * @param beanClass
   * @param fields
   * @return
   */
  public static <B> Map<String, BeanPropertyAccessor<B>> determineFieldnameToBeanPropertyAccessorMap( Class<B> beanClass,
                                                                                                      Field... fields )
  {
    //    
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null && fields != null )
    {
      //
      for ( Field field : fields )
      {
        //
        String fieldname = field.getName();
        
        //
        Method methodGetter = null;
        Method methodSetter = null;
        
        //
        retmap.put( fieldname, new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname ) );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a map of field names and {@link BeanPropertyAccessor} instances for all Java Bean properties related to at least one
   * of the given {@link Method}s.
   * 
   * @param <B>
   * @param beanClass
   * @param methods
   * @return
   */
  public static <B> Map<String, BeanPropertyAccessor<B>> determineFieldnameToBeanPropertyAccessorMap( Class<B> beanClass,
                                                                                                      Method... methods )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null && methods != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.determineFieldnameToBeanMethodInformationMap( methods );
      for ( String fieldname : fieldnameToBeanMethodInformationMap.keySet() )
      {
        //
        if ( !StringUtils.isBlank( fieldname ) && !"class".equals( fieldname ) )
        {
          //     
          Method methodGetter = null;
          Method methodSetter = null;
          for ( BeanMethodInformation beanMethodInformation : fieldnameToBeanMethodInformationMap.get( fieldname ) )
          {
            //
            if ( beanMethodInformation.isGetter() )
            {
              methodGetter = beanMethodInformation.getMethod();
            }
            else if ( beanMethodInformation.isSetter() )
            {
              methodSetter = beanMethodInformation.getMethod();
            }
          }
          
          //
          Field field = null;
          try
          {
            field = beanClass.getField( fieldname );
          }
          catch ( Exception e )
          {
          }
          
          //
          if ( field != null || methodGetter != null || methodSetter != null )
          {
            retmap.put( fieldname, new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname ) );
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a map of property names and {@link BeanPropertyAccessor} instances for all Java Bean properties of the given
   * {@link Class}. The properties are in their natural order, which means to be ordered like declared fields, declared methods,
   * inherited fields, inherited methods.
   * 
   * @see #determineBeanPropertyAccessorSet(Class)
   * @param beanClass
   * @return
   */
  public static <B> Map<String, BeanPropertyAccessor<B>> determineFieldnameToBeanPropertyAccessorMap( Class<B> beanClass )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null )
    {
      //
      try
      {
        /*
         * Order is determined by:
         * 
         * - declared fields
         * - declared methods
         * - fields
         * - methods
         * 
         */

        //
        retmap.putAll( BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass, beanClass.getDeclaredFields() ) );
        retmap.putAll( BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass, beanClass.getDeclaredMethods() ) );
        retmap.putAll( BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass, beanClass.getFields() ) );
        retmap.putAll( BeanUtils.determineFieldnameToBeanPropertyAccessorMap( beanClass, beanClass.getMethods() ) );
      }
      catch ( Exception e )
      {
      }
      
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given {@link Field}.
   * 
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determineFieldnameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param field
   * @return
   */
  public static <B> BeanPropertyAccessor<B> determineBeanPropertyAccessor( Class<B> beanClass, Field field )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanClass != null && field != null )
    {
      //
      try
      {
        //
        String fieldname = field.getName();
        
        //
        Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.determineFieldnameToBeanMethodInformationMap( beanClass )
                                                                       .get( fieldname );
        
        //     
        Method methodGetter = null;
        Method methodSetter = null;
        for ( BeanMethodInformation beanMethodInformation : beanMethodInformationSet )
        {
          if ( beanMethodInformation.isGetter() )
          {
            methodGetter = beanMethodInformation.getMethod();
          }
          else if ( beanMethodInformation.isSetter() )
          {
            methodSetter = beanMethodInformation.getMethod();
          }
        }
        
        //
        retval = new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a set of {@link BeanMethodInformation} instances for all methods of a given {@link Class}.
   * 
   * @param clazz
   * @return
   */
  public static Set<BeanMethodInformation> determineBeanMethodInformationSet( Class<?> clazz )
  {
    //
    Set<BeanMethodInformation> retset = new HashSet<BeanMethodInformation>();
    
    //
    if ( clazz != null )
    {
      for ( Method method : clazz.getMethods() )
      {
        retset.add( BeanUtils.determineBeanMethodInformation( method ) );
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Determines a map with the referenced field names as keys and a {@link Set} of {@link BeanMethodInformation} for every field
   * name.
   * 
   * @param clazz
   * @return
   */
  public static Map<String, Set<BeanMethodInformation>> determineFieldnameToBeanMethodInformationMap( Class<?> clazz )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = null;
    
    //
    if ( clazz != null )
    {
      retmap = BeanUtils.determineFieldnameToBeanMethodInformationMap( clazz.getMethods() );
    }
    else
    {
      retmap = new LinkedHashMap<String, Set<BeanMethodInformation>>();
    }
    
    //
    return retmap;
  }
  
  /**
   * Determines a map with the referenced field names as keys and a {@link Set} of {@link BeanMethodInformation} for every field
   * name. The map keys keep their natural order.
   * 
   * @param methods
   * @return
   */
  public static Map<String, Set<BeanMethodInformation>> determineFieldnameToBeanMethodInformationMap( Method... methods )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = new LinkedHashMap<String, Set<BeanMethodInformation>>();
    
    //
    if ( methods != null )
    {
      for ( Method method : methods )
      {
        //
        BeanMethodInformation beanMethodInformation = BeanUtils.determineBeanMethodInformation( method );
        if ( beanMethodInformation != null )
        {
          //
          String referencedFieldName = beanMethodInformation.getReferencedFieldName();
          
          //
          if ( !retmap.containsKey( referencedFieldName ) )
          {
            retmap.put( referencedFieldName, new HashSet<BeanMethodInformation>() );
          }
          
          //
          retmap.get( referencedFieldName ).add( beanMethodInformation );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link BeanMethodInformation} object determined for the given {@link Method}.
   */
  public static BeanMethodInformation determineBeanMethodInformation( Method method )
  {
    //
    BeanMethodInformation retval = null;
    
    //
    if ( method != null )
    {
      //
      try
      {
        //
        Class<?>[] parameterTypes = method.getParameterTypes();
        String methodName = method.getName();
        Class<?> returnType = method.getReturnType();
        
        //
        boolean isGetter = parameterTypes != null && parameterTypes.length == 0 && returnType != null && methodName != null
                           && ( methodName.startsWith( "is" ) || methodName.startsWith( "get" ) );
        boolean isSetter = parameterTypes != null && parameterTypes.length == 1 && methodName != null
                           && ( methodName.startsWith( "set" ) );
        
        //
        String referencedFieldName = null;
        if ( isGetter || isSetter )
        {
          //
          referencedFieldName = methodName.replaceFirst( "^(is|get|set)", "" );
          if ( referencedFieldName.length() > 0 )
          {
            referencedFieldName = referencedFieldName.replaceFirst( "^.", referencedFieldName.substring( 0, 1 ).toLowerCase() );
          }
        }
        
        //
        retval = new BeanMethodInformation( isGetter, isSetter, referencedFieldName, method );
        
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Copies the property values of one Java Bean to another.
   * 
   * @param beanSource
   * @param beanDestination
   * @param <S>
   * @param <D>
   */
  @SuppressWarnings("unchecked")
  public static <S, D> void copyPropertyValues( S beanSource, D beanDestination )
  {
    //
    if ( beanSource != null && beanDestination != null )
    {
      //
      Map<String, BeanPropertyAccessor<S>> fieldnameToBeanPropertyAccessorSourceMap = BeanUtils.determineFieldnameToBeanPropertyAccessorMap( (Class<S>) beanSource.getClass() );
      Map<String, BeanPropertyAccessor<D>> fieldnameToBeanPropertyAccessorDestinationMap = BeanUtils.determineFieldnameToBeanPropertyAccessorMap( (Class<D>) beanSource.getClass() );
      
      //
      List<TupleDuad<BeanPropertyAccessor<S>, BeanPropertyAccessor<D>>> joinTupleList = MapUtils.innerJoinMapByKey( fieldnameToBeanPropertyAccessorSourceMap,
                                                                                                                    fieldnameToBeanPropertyAccessorDestinationMap );
      
      //
      for ( TupleDuad<BeanPropertyAccessor<S>, BeanPropertyAccessor<D>> tupleDuad : joinTupleList )
      {
        //
        BeanPropertyAccessor<S> beanPropertySourceAccessor = tupleDuad.getValueFirst();
        BeanPropertyAccessor<D> beanPropertyDestinationAccessor = tupleDuad.getValueSecond();
        
        //
        if ( beanPropertySourceAccessor.hasGetter() && beanPropertyDestinationAccessor.hasSetter() )
        {
          beanPropertyDestinationAccessor.setPropertyValue( beanDestination,
                                                            beanPropertySourceAccessor.getPropertyValue( beanSource ) );
        }
      }
    }
    
  }
  
  /**
   * Determines the property names of a given bean class.
   * 
   * @param clazz
   * @return
   */
  public static String[] determinePropertyNames( Class<?> clazz )
  {
    //
    String[] retvals = null;
    
    //
    Set<String> propertyNameSet = new HashSet<String>();
    if ( clazz != null )
    {
      //
      Method[] methods = clazz.getMethods();
      if ( methods != null )
      {
        for ( Method method : methods )
        {
          if ( method != null )
          {
            //
            BeanMethodInformation beanMethodInformation = BeanUtils.determineBeanMethodInformation( method );
            
            //
            boolean isFieldAccessMethod = beanMethodInformation.isFieldAccessMethod();
            if ( isFieldAccessMethod )
            {
              //
              propertyNameSet.add( beanMethodInformation.getReferencedFieldName() );
            }
          }
        }
      }
      
      //
      propertyNameSet.remove( "class" );
      
      //
      retvals = propertyNameSet.toArray( new String[0] );
    }
    
    //
    return retvals;
  }
  
}
