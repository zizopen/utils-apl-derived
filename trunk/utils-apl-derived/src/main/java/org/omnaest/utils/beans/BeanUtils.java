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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
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
   * Returns a new {@link Map} instance which contains the property names as keys and the values of the properties as map values.
   * Modifications to the returned map will have no impact on the original Java Bean object.
   * 
   * @see MapToTypeAdapter
   * @param <B>
   * @param bean
   * @return
   */
  public static <B> Map<String, Object> transformBeanIntoMap( B bean )
  {
    //
    Map<String, Object> retmap = new HashMap<String, Object>();
    
    //
    if ( bean != null )
    {
      @SuppressWarnings("unchecked")
      Class<? extends B> clazz = (Class<? extends B>) bean.getClass();
      B beanAdapter = MapToTypeAdapter.<B> newInstance( retmap, clazz );
      
      //
      BeanUtils.copyPropertyValues( bean, beanAdapter );
    }
    
    //
    return retmap;
  }
  
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
   * @see #determinePropertyNameToBeanPropertyAccessorMap(Class)
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
      Map<String, BeanPropertyAccessor<B>> fieldnameToBeanPropertyAccessorMap = BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass );
      
      //
      retset.addAll( fieldnameToBeanPropertyAccessorMap.values() );
    }
    
    //
    return retset;
  }
  
  /**
   * Returns the {@link BeanPropertyAccessors} for a given Java Bean type
   * 
   * @param <B>
   * @param beanClass
   * @return
   */
  public static <B> BeanPropertyAccessors<B> determineBeanPropertyAccessors( Class<B> beanClass )
  {
    //
    return new BeanPropertyAccessors<B>( BeanUtils.determineBeanPropertyAccessorSet( beanClass ) );
  }
  
  /**
   * Returns a map of property names and their current values for the Java Bean properties determined by the given property names
   * for the given Java Bean object.
   * 
   * @param <B>
   *          : type of the bean
   * @param <V>
   *          : type of the values
   * @param bean
   * @param propertyNames
   * @return
   */
  public static <B, V> Map<String, V> determinePropertyNameToBeanPropertyValueMap( B bean, String... propertyNames )
  {
    return BeanUtils.determinePropertyNameToBeanPropertyValueMap( bean, Arrays.asList( propertyNames ) );
  }
  
  /**
   * Returns a map of property names and their current values for the Java Bean properties determined by the given property names
   * for the given Java Bean object.
   * 
   * @param <B>
   *          : type of the bean
   * @param <V>
   *          : type of the values
   * @param bean
   * @param propertyNameCollection
   * @return
   */
  public static <B, V> Map<String, V> determinePropertyNameToBeanPropertyValueMap( B bean,
                                                                                   Collection<String> propertyNameCollection )
  {
    //
    Map<String, V> retmap = new HashMap<String, V>();
    
    //
    if ( bean != null && propertyNameCollection != null )
    {
      Map<String, V> propertyNameToBeanPropertyValueMap = BeanUtils.determinePropertyNameToBeanPropertyValueMap( bean );
      for ( String propertyName : propertyNameCollection )
      {
        if ( propertyNameToBeanPropertyValueMap.containsKey( propertyName ) )
        {
          retmap.put( propertyName, propertyNameToBeanPropertyValueMap.get( propertyName ) );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a map of property names and their current values for all Java Bean properties or the given Java Bean object.
   * 
   * @param <B>
   *          : type of the bean
   * @param <V>
   *          : type of the values
   * @param bean
   * @return
   */
  public static <B, V> Map<String, V> determinePropertyNameToBeanPropertyValueMap( B bean )
  {
    //
    Map<String, V> retmap = new HashMap<String, V>();
    
    //
    if ( bean != null )
    {
      //
      @SuppressWarnings("unchecked")
      Class<B> beanClass = (Class<B>) bean.getClass();
      
      //      
      Map<String, BeanPropertyAccessor<B>> propertynameToBeanPropertyAccessorMap = BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass );
      if ( propertynameToBeanPropertyAccessorMap != null )
      {
        for ( String propertyName : propertynameToBeanPropertyAccessorMap.keySet() )
        {
          //
          BeanPropertyAccessor<B> beanPropertyAccessor = propertynameToBeanPropertyAccessorMap.get( propertyName );
          if ( beanPropertyAccessor != null && beanPropertyAccessor.hasGetter() )
          {
            try
            {
              //
              @SuppressWarnings("unchecked")
              V propertyValue = (V) beanPropertyAccessor.getPropertyValue( bean );
              
              //
              retmap.put( propertyName, propertyValue );
            }
            catch ( Exception e )
            {
            }
          }
        }
      }
    }
    
    //
    return retmap;
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
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertyNameToBeanPropertyAccessorMap( Class<B> beanClass,
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
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertyNameToBeanPropertyAccessorMap( Class<B> beanClass,
                                                                                                         Method... methods )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null && methods != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.determinePropertyNameToBeanMethodInformationMap( methods );
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
   * Returns a map of property types for all Java Bean properties of the given {@link Class} and a {@link Set} of
   * {@link BeanPropertyAccessor} instances for each type.
   * 
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determinePropertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @return
   */
  public static <B> Map<Class<?>, Set<BeanPropertyAccessor<B>>> determinePropertyTypeToBeanPropertyAccessorSetMap( Class<B> beanClass )
  {
    //    
    Map<Class<?>, Set<BeanPropertyAccessor<B>>> retmap = new HashMap<Class<?>, Set<BeanPropertyAccessor<B>>>();
    
    //
    if ( beanClass != null )
    {
      //
      Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet = BeanUtils.determineBeanPropertyAccessorSet( beanClass );
      
      //
      if ( beanPropertyAccessorSet != null )
      {
        for ( BeanPropertyAccessor<B> beanPropertyAccessor : beanPropertyAccessorSet )
        {
          if ( beanPropertyAccessor != null )
          {
            Class<?> propertyType = beanPropertyAccessor.determinePropertyType();
            if ( propertyType != null )
            {
              //
              if ( !retmap.containsKey( propertyType ) )
              {
                retmap.put( propertyType, new HashSet<BeanPropertyAccessor<B>>() );
              }
              
              //
              retmap.get( propertyType ).add( beanPropertyAccessor );
            }
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
  public static <B> Map<String, BeanPropertyAccessor<B>> determinePropertyNameToBeanPropertyAccessorMap( Class<B> beanClass )
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
                                    BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass,
                                                                                              beanClass.getDeclaredFields() ),
                                    BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass,
                                                                                              beanClass.getDeclaredMethods() ),
                                    BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getFields() ),
                                    BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getMethods() ) );
        
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given property name
   * 
   * @see #determineBeanPropertyAccessor(Class, Field)
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determinePropertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param propertyName
   * @return
   */
  public static <B> BeanPropertyAccessor<B> determineBeanPropertyAccessor( Class<B> beanClass, String propertyName )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanClass != null && propertyName != null )
    {
      //
      try
      {
        //
        Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.determinePropertyNameToBeanMethodInformationMap( beanClass )
                                                                       .get( propertyName );
        
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
        Field field = null;
        try
        {
          //
          field = beanClass.getDeclaredField( propertyName );
        }
        catch ( Exception e )
        {
        }
        
        //
        if ( field == null )
        {
          try
          {
            //
            field = beanClass.getField( propertyName );
          }
          catch ( Exception e )
          {
          }
        }
        
        //
        retval = new BeanPropertyAccessor<B>( field, methodGetter, methodSetter, propertyName, beanClass );
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given {@link Field}.
   * 
   * @see #determineBeanPropertyAccessor(Class, Method)
   * @see #determineBeanPropertyAccessor(Class, String)
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determinePropertyNameToBeanPropertyAccessorMap(Class)
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
      String propertyName = field.getName();
      retval = BeanUtils.determineBeanPropertyAccessor( beanClass, propertyName );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given {@link Method}.
   * 
   * @see #determineBeanPropertyAccessor(Class, Field)
   * @see #determineBeanPropertyAccessorSet(Class)
   * @see #determinePropertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param method
   * @return
   */
  public static <B> BeanPropertyAccessor<B> determineBeanPropertyAccessor( Class<B> beanClass, Method method )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanClass != null && method != null )
    {
      //
      BeanMethodInformation beanMethodInformation = BeanUtils.determineBeanMethodInformation( method );
      
      //
      String propertyName = beanMethodInformation.getReferencedFieldName();
      retval = BeanUtils.determineBeanPropertyAccessor( beanClass, propertyName );
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
   * Determines a {@link Map} with the referenced field names as keys and a {@link Set} of {@link BeanMethodInformation} for every
   * field name.
   * 
   * @param clazz
   * @return
   */
  public static Map<String, Set<BeanMethodInformation>> determinePropertyNameToBeanMethodInformationMap( Class<?> clazz )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = null;
    
    //
    if ( clazz != null )
    {
      retmap = BeanUtils.determinePropertyNameToBeanMethodInformationMap( clazz.getMethods() );
    }
    else
    {
      retmap = new HashMap<String, Set<BeanMethodInformation>>();
    }
    
    //
    return retmap;
  }
  
  /**
   * Determines a {@link Map} with the referenced {@link Method#getName()}s as keys and the respective
   * {@link BeanMethodInformation} name.
   * 
   * @param clazz
   * @return
   */
  public static Map<String, BeanMethodInformation> determineMethodNameToBeanMethodInformationMap( Class<?> clazz )
  {
    //
    Map<String, BeanMethodInformation> retmap = null;
    
    //
    if ( clazz != null )
    {
      retmap = BeanUtils.determineMethodNameToBeanMethodInformationMap( clazz.getMethods() );
    }
    else
    {
      retmap = new HashMap<String, BeanMethodInformation>();
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
  public static Map<String, Set<BeanMethodInformation>> determinePropertyNameToBeanMethodInformationMap( Method... methods )
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
   * Determines a {@link Map} with all {@link Method#getName()}s and the respective {@link BeanMethodInformation} instances.
   * 
   * @param methods
   * @return
   */
  public static Map<String, BeanMethodInformation> determineMethodNameToBeanMethodInformationMap( Method... methods )
  {
    //
    Map<String, BeanMethodInformation> retmap = new HashMap<String, BeanMethodInformation>();
    
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
          String methodName = method.getName();
          
          //          
          retmap.put( methodName, beanMethodInformation );
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
   * Copies the property values from one Java Bean to another.
   * 
   * @param beanSource
   * @param beanDestination
   * @param <S>
   * @param <D>
   */
  public static <S, D> void copyPropertyValues( S beanSource, D beanDestination )
  {
    //
    Collection<String> propertyNameCollection = null;
    BeanUtils.copyPropertyValues( beanSource, beanDestination, propertyNameCollection );
  }
  
  /**
   * Copies the property values for the given property names from one Java Bean to another.
   * 
   * @param beanSource
   * @param beanDestination
   * @param propertyNames
   * @param <S>
   * @param <D>
   */
  public static <S, D> void copyPropertyValues( S beanSource, D beanDestination, String... propertyNames )
  {
    BeanUtils.copyPropertyValues( beanSource, beanDestination, Arrays.asList( propertyNames ) );
  }
  
  /**
   * Copies the property values from one Java Bean to another but only for the given property names.
   * 
   * @param beanSource
   * @param beanDestination
   * @param propertyNameCollection
   * @param <S>
   * @param <D>
   */
  @SuppressWarnings("unchecked")
  public static <S, D> void copyPropertyValues( S beanSource, D beanDestination, Collection<String> propertyNameCollection )
  {
    //
    if ( beanSource != null && beanDestination != null )
    {
      //
      Map<String, BeanPropertyAccessor<S>> fieldnameToBeanPropertyAccessorSourceMap = BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( (Class<S>) beanSource.getClass() );
      Map<String, BeanPropertyAccessor<D>> fieldnameToBeanPropertyAccessorDestinationMap = BeanUtils.determinePropertyNameToBeanPropertyAccessorMap( (Class<D>) beanDestination.getClass() );
      
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
        if ( ( propertyNameCollection == null || propertyNameCollection.contains( beanPropertySourceAccessor.getPropertyName() )
                                                 && beanPropertySourceAccessor.hasGetter()
                                                 && beanPropertyDestinationAccessor.hasSetter() ) )
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
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.determinePropertyNameToBeanMethodInformationMap( clazz );
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
