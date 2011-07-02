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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.MapUtils.MapElementMergeOperation;
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
   * Returns a set of {@link BeanPropertyAccessor} instances for all Java Bean properties of the given {@link Class}
   * 
   * @see #determinePropertynameToBeanPropertyAccessorMap(Class)
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
      Map<String, BeanPropertyAccessor<B>> fieldnameToBeanPropertyAccessorMap = BeanUtils.determinePropertynameToBeanPropertyAccessorMap( beanClass );
      
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
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertynameToBeanPropertyAccessorMap( Class<B> beanClass,
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
        retmap.put( fieldname, new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname, beanClass ) );
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
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertynameToBeanPropertyAccessorMap( Class<B> beanClass,
                                                                                                         Method... methods )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null && methods != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.determinePropertynameToBeanMethodInformationMap( methods );
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
          if ( methodGetter != null || methodSetter != null )
          {
            Field field = null;
            retmap.put( fieldname, new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname, beanClass ) );
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a map of property names and {@link BeanPropertyAccessor} instances for all Java Bean properties of the given
   * {@link Class}. The properties are in no order.
   * 
   * @see #determineBeanPropertyAccessorSet(Class)
   * @param beanClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertynameToBeanPropertyAccessorMap( Class<B> beanClass )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new HashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null )
    {
      //
      try
      {
        //
        MapElementMergeOperation<String, BeanPropertyAccessor<B>> mapElementMergeOperation = new MapElementMergeOperation<String, BeanPropertyAccessor<B>>()
        {
          @Override
          public void merge( String key, BeanPropertyAccessor<B> value, Map<String, BeanPropertyAccessor<B>> mergedMap )
          {
            //
            if ( mergedMap.containsKey( key ) )
            {
              BeanPropertyAccessor<B> beanPropertyAccessor = mergedMap.get( key );
              BeanPropertyAccessor<B> beanPropertyAccessorMerged = BeanPropertyAccessor.merge( beanPropertyAccessor, value );
              mergedMap.put( key, beanPropertyAccessorMerged );
            }
            else
            {
              mergedMap.put( key, value );
            }
          }
        };
        
        retmap = MapUtils.mergeAll( mapElementMergeOperation,
                                    BeanUtils.determinePropertynameToBeanPropertyAccessorMap( beanClass,
                                                                                              beanClass.getDeclaredFields() ),
                                    BeanUtils.determinePropertynameToBeanPropertyAccessorMap( beanClass,
                                                                                              beanClass.getDeclaredMethods() ),
                                    BeanUtils.determinePropertynameToBeanPropertyAccessorMap( beanClass, beanClass.getFields() ),
                                    BeanUtils.determinePropertynameToBeanPropertyAccessorMap( beanClass, beanClass.getMethods() ) );
        
      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given {@link Field}.
   * 
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determinePropertynameToBeanPropertyAccessorMap(Class)
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
        Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.determinePropertynameToBeanMethodInformationMap( beanClass )
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
        retval = new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, fieldname, beanClass );
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
  public static Map<String, Set<BeanMethodInformation>> determinePropertynameToBeanMethodInformationMap( Class<?> clazz )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = null;
    
    //
    if ( clazz != null )
    {
      retmap = BeanUtils.determinePropertynameToBeanMethodInformationMap( clazz.getMethods() );
    }
    else
    {
      retmap = new HashMap<String, Set<BeanMethodInformation>>();
    }
    
    //
    return retmap;
  }
  
  /**
   * Determines a map with the referenced field names as keys and a {@link Set} of {@link BeanMethodInformation} for every field
   * name. The map keys are in no order.
   * 
   * @param methods
   * @return
   */
  public static Map<String, Set<BeanMethodInformation>> determinePropertynameToBeanMethodInformationMap( Method... methods )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = new HashMap<String, Set<BeanMethodInformation>>();
    
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
      Map<String, BeanPropertyAccessor<S>> fieldnameToBeanPropertyAccessorSourceMap = BeanUtils.determinePropertynameToBeanPropertyAccessorMap( (Class<S>) beanSource.getClass() );
      Map<String, BeanPropertyAccessor<D>> fieldnameToBeanPropertyAccessorDestinationMap = BeanUtils.determinePropertynameToBeanPropertyAccessorMap( (Class<D>) beanDestination.getClass() );
      
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
          //
          Object propertyValue = beanPropertySourceAccessor.getPropertyValue( beanSource );
          beanPropertyDestinationAccessor.setPropertyValue( beanDestination, propertyValue );
        }
      }
    }
    
  }
  
  /**
   * Determines the property names of a given bean class which are addressed by getter or setter.
   * 
   * @param clazz
   * @return
   */
  public static String[] determinePropertyNamesForMethodAccess( Class<?> clazz )
  {
    //
    String[] retvals = null;
    
    //
    Set<String> propertyNameSet = new HashSet<String>();
    if ( clazz != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.determinePropertynameToBeanMethodInformationMap( clazz );
      propertyNameSet.addAll( fieldnameToBeanMethodInformationMap.keySet() );
      propertyNameSet.remove( null );
      propertyNameSet.remove( "class" );
      
      //
      retvals = propertyNameSet.toArray( new String[0] );
    }
    
    //
    return retvals;
  }
  
}
