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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter;
import org.omnaest.utils.beans.adapter.PropertynameMapToTypeAdapter.Configuration;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter;
import org.omnaest.utils.beans.mapconverter.BeanToNestedMapConverter.BeanConversionFilterExcludingPrimitiveAndString;
import org.omnaest.utils.beans.result.BeanMethodInformation;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.beans.result.BeanPropertyAccessor.PropertyAccessType;
import org.omnaest.utils.beans.result.BeanPropertyAccessors;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterIdentity;
import org.omnaest.utils.structure.map.MapUtils;
import org.omnaest.utils.structure.map.MapUtils.MapElementMergeOperation;
import org.omnaest.utils.tuple.TupleTwo;

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
   * @see PropertynameMapToTypeAdapter
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
      B beanAdapter = PropertynameMapToTypeAdapter.<B> newInstance( retmap, clazz );
      
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
  public static int numberOfProperties( Class<?> beanClass )
  {
    return BeanUtils.beanPropertyAccessorSet( beanClass ).size();
  }
  
  /**
   * Returns a set of {@link BeanPropertyAccessor} instances for all Java Bean properties of the given {@link Class}
   * 
   * @see #propertyNameToBeanPropertyAccessorMap(Class)
   * @see #beanPropertyAccessor(Class, Field)
   * @param beanClass
   * @return
   */
  public static <B> Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet( Class<B> beanClass )
  {
    //
    Set<BeanPropertyAccessor<B>> retset = new HashSet<BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null )
    {
      //
      Map<String, BeanPropertyAccessor<B>> fieldnameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass );
      
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
  public static <B> BeanPropertyAccessors<B> beanPropertyAccessors( Class<B> beanClass )
  {
    //
    return new BeanPropertyAccessors<B>( BeanUtils.beanPropertyAccessorSet( beanClass ) );
  }
  
  /**
   * Returns an new ordered {@link List} of all property values for the given property names from the given Java Bean object
   * 
   * @param bean
   * @param propertyNames
   * @return
   */
  public static <B> List<B> propertyValueList( B bean, String... propertyNames )
  {
    return new ArrayList<B>( propertyValueList( bean, Arrays.asList( propertyNames ) ) );
  }
  
  /**
   * Returns an new ordered {@link List} of all property values for the given property names from the given Java Bean object
   * 
   * @param bean
   * @param propertyNameCollection
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> List<B> propertyValueList( B bean, Collection<String> propertyNameCollection )
  {
    return new ArrayList<B>(
                             (Collection<? extends B>) propertyNameToBeanPropertyValueMap( bean, propertyNameCollection ).values() );
  }
  
  /**
   * Returns a {@link Map} with all properties of a given Java Bean class and an instance of the given {@link Annotation} type if
   * the respective property does have one. Otherwise the map contains a key with a null value.
   * 
   * @see #propertyNameToBeanPropertyAnnotationSetMap(Class)
   * @param beanClass
   * @param annotationType
   * @return
   */
  public static <B, A extends Annotation> Map<String, A> propertyNameToBeanPropertyAnnotationMap( final Class<B> beanClass,
                                                                                                  final Class<A> annotationType )
  {
    //
    Map<String, A> retmap = null;
    
    //
    if ( beanClass != null && annotationType != null )
    {
      //
      Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap = BeanUtils.propertyNameToBeanPropertyAnnotationSetMap( beanClass );
      ElementConverter<String, String> keyElementConverter = new ElementConverterIdentity<String>();
      ElementConverter<Set<Annotation>, A> valueElementConverter = new ElementConverter<Set<Annotation>, A>()
      {
        @SuppressWarnings("unchecked")
        @Override
        public A convert( Set<Annotation> annotationSet )
        {
          //
          A retval = null;
          
          //
          if ( annotationSet != null )
          {
            for ( Annotation annotation : annotationSet )
            {
              Class<? extends Annotation> currentAnnotationType = annotation.annotationType();
              if ( annotationType.isAssignableFrom( currentAnnotationType ) )
              {
                retval = (A) annotation;
                break;
              }
            }
          }
          
          //
          return retval;
        }
      };
      retmap = MapUtils.convertMap( propertyNameToBeanPropertyAnnotationSetMap, keyElementConverter, valueElementConverter );
      
    }
    
    return retmap;
  }
  
  /**
   * Returns a {@link Map} with all property names of the given Java Bean and a {@link Set} of all available annotations for the
   * properties, including the field, getter and setter methods.
   * 
   * @param beanClass
   * @return
   */
  public static <B> Map<String, Set<Annotation>> propertyNameToBeanPropertyAnnotationSetMap( Class<B> beanClass )
  {
    //
    Map<String, Set<Annotation>> retmap = new HashMap<String, Set<Annotation>>();
    
    //
    Map<String, BeanPropertyAccessor<B>> propertyNameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass );
    for ( String propertyName : propertyNameToBeanPropertyAccessorMap.keySet() )
    {
      //
      BeanPropertyAccessor<B> beanPropertyAccessor = propertyNameToBeanPropertyAccessorMap.get( propertyName );
      
      //
      Set<Annotation> annotationSet = new HashSet<Annotation>();
      {
        //
        Field field = beanPropertyAccessor.getField();
        if ( field != null )
        {
          Annotation[] annotations = field.getDeclaredAnnotations();
          if ( annotations != null )
          {
            annotationSet.addAll( Arrays.asList( annotations ) );
          }
        }
      }
      {
        //
        Method methodGetter = beanPropertyAccessor.getMethodGetter();
        if ( methodGetter != null )
        {
          Annotation[] annotations = methodGetter.getDeclaredAnnotations();
          if ( annotations != null )
          {
            annotationSet.addAll( Arrays.asList( annotations ) );
          }
        }
      }
      {
        //
        Method methodSetter = beanPropertyAccessor.getMethodSetter();
        if ( methodSetter != null )
        {
          Annotation[] annotations = methodSetter.getDeclaredAnnotations();
          if ( annotations != null )
          {
            annotationSet.addAll( Arrays.asList( annotations ) );
          }
        }
      }
      
      //
      retmap.put( propertyName, annotationSet );
      
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link Map} of property names and their current values for the Java Bean properties determined by the given
   * property names for the given Java Bean object. The {@link Map} will be ordered in the same way than the given property names
   * 
   * @param <B>
   *          : type of the bean
   * @param <V>
   *          : type of the values
   * @param bean
   * @param propertyNames
   * @return
   */
  public static <B, V> Map<String, V> propertyNameToBeanPropertyValueMap( B bean, String... propertyNames )
  {
    return BeanUtils.propertyNameToBeanPropertyValueMap( bean, Arrays.asList( propertyNames ) );
  }
  
  /**
   * Returns a map of property names and their current values for the Java Bean properties determined by the given property names
   * for the given Java Bean object. The {@link Map} keys and values will have the same order as the given property names.
   * 
   * @param <B>
   *          : type of the bean
   * @param <V>
   *          : type of the values
   * @param bean
   * @param propertyNameCollection
   * @return
   */
  public static <B, V> Map<String, V> propertyNameToBeanPropertyValueMap( B bean, Collection<String> propertyNameCollection )
  {
    //
    Map<String, V> retmap = new LinkedHashMap<String, V>();
    
    //
    if ( bean != null && propertyNameCollection != null )
    {
      Map<String, V> propertyNameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( bean );
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
  public static <B, V> Map<String, V> propertyNameToBeanPropertyValueMap( B bean )
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
      Map<String, BeanPropertyAccessor<B>> propertynameToBeanPropertyAccessorMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass );
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
  public static <B> Map<String, BeanPropertyAccessor<B>> propertyNameToBeanPropertyAccessorMap( Class<B> beanClass,
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
  public static <B> Map<String, BeanPropertyAccessor<B>> propertyNameToBeanPropertyAccessorMap( Class<B> beanClass,
                                                                                                Method... methods )
  {
    //
    Map<String, BeanPropertyAccessor<B>> retmap = new LinkedHashMap<String, BeanPropertyAccessor<B>>();
    
    //
    if ( beanClass != null && methods != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.propertyNameToBeanMethodInformationMap( methods );
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
            if ( beanMethodInformation.isGetter() || beanMethodInformation.isGetterWithAdditionalArguments() )
            {
              methodGetter = beanMethodInformation.getMethod();
            }
            else if ( beanMethodInformation.isSetter() || beanMethodInformation.isSetterWithAdditionalArguments() )
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
   * @see #beanPropertyAccessorSet(Class)
   * @see #propertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @return
   */
  public static <B> Map<Class<?>, Set<BeanPropertyAccessor<B>>> propertyTypeToBeanPropertyAccessorSetMap( Class<B> beanClass )
  {
    //    
    Map<Class<?>, Set<BeanPropertyAccessor<B>>> retmap = new HashMap<Class<?>, Set<BeanPropertyAccessor<B>>>();
    
    //
    if ( beanClass != null )
    {
      //
      Set<BeanPropertyAccessor<B>> beanPropertyAccessorSet = BeanUtils.beanPropertyAccessorSet( beanClass );
      
      //
      if ( beanPropertyAccessorSet != null )
      {
        for ( BeanPropertyAccessor<B> beanPropertyAccessor : beanPropertyAccessorSet )
        {
          if ( beanPropertyAccessor != null )
          {
            Class<?> propertyType = beanPropertyAccessor.getDeclaringPropertyType();
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
   * @see #beanPropertyAccessorSet(Class)
   * @param beanClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> Map<String, BeanPropertyAccessor<B>> propertyNameToBeanPropertyAccessorMap( Class<B> beanClass )
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
                                    BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getDeclaredFields() ),
                                    BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getDeclaredMethods() ),
                                    BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getFields() ),
                                    BeanUtils.propertyNameToBeanPropertyAccessorMap( beanClass, beanClass.getMethods() ) );
        
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
   * @see #beanPropertyAccessor(Class, Field)
   * @see #beanPropertyAccessorSet(Class)
   * @see #propertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param propertyName
   * @return
   */
  public static <B> BeanPropertyAccessor<B> beanPropertyAccessor( Class<B> beanClass, String propertyName )
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
        Set<BeanMethodInformation> beanMethodInformationSet = BeanUtils.propertyNameToBeanMethodInformationMap( beanClass )
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
   * @see #beanPropertyAccessor(Class, Method)
   * @see #beanPropertyAccessor(Class, String)
   * @see #beanPropertyAccessorSet(Class)
   * @see #propertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param field
   * @return
   */
  public static <B> BeanPropertyAccessor<B> beanPropertyAccessor( Class<B> beanClass, Field field )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanClass != null && field != null )
    {
      //
      String propertyName = field.getName();
      retval = BeanUtils.beanPropertyAccessor( beanClass, propertyName );
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a {@link BeanPropertyAccessor} object determined for the given {@link Method}.
   * 
   * @see #beanPropertyAccessor(Class, Field)
   * @see #beanPropertyAccessorSet(Class)
   * @see #propertyNameToBeanPropertyAccessorMap(Class)
   * @param beanClass
   * @param method
   * @return
   */
  public static <B> BeanPropertyAccessor<B> beanPropertyAccessor( Class<B> beanClass, Method method )
  {
    //
    BeanPropertyAccessor<B> retval = null;
    
    //
    if ( beanClass != null && method != null )
    {
      //
      BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
      
      //
      String propertyName = beanMethodInformation.getReferencedFieldName();
      retval = BeanUtils.beanPropertyAccessor( beanClass, propertyName );
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
  public static Set<BeanMethodInformation> beanMethodInformationSet( Class<?> clazz )
  {
    //
    Set<BeanMethodInformation> retset = new HashSet<BeanMethodInformation>();
    
    //
    if ( clazz != null )
    {
      for ( Method method : clazz.getMethods() )
      {
        retset.add( BeanUtils.beanMethodInformation( method ) );
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
  public static Map<String, Set<BeanMethodInformation>> propertyNameToBeanMethodInformationMap( Class<?> clazz )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = null;
    
    //
    if ( clazz != null )
    {
      retmap = BeanUtils.propertyNameToBeanMethodInformationMap( clazz.getMethods() );
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
   * @param type
   * @return
   */
  public static Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap( Class<?> type )
  {
    //
    Map<String, BeanMethodInformation> retmap = null;
    
    //
    if ( type != null )
    {
      retmap = BeanUtils.methodNameToBeanMethodInformationMap( type.getMethods() );
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
  public static Map<String, Set<BeanMethodInformation>> propertyNameToBeanMethodInformationMap( Method... methods )
  {
    //
    Map<String, Set<BeanMethodInformation>> retmap = new HashMap<String, Set<BeanMethodInformation>>();
    
    //
    if ( methods != null )
    {
      for ( Method method : methods )
      {
        //
        BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
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
  public static Map<String, BeanMethodInformation> methodNameToBeanMethodInformationMap( Method... methods )
  {
    //
    Map<String, BeanMethodInformation> retmap = new HashMap<String, BeanMethodInformation>();
    
    //
    if ( methods != null )
    {
      for ( Method method : methods )
      {
        //
        BeanMethodInformation beanMethodInformation = BeanUtils.beanMethodInformation( method );
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
  public static BeanMethodInformation beanMethodInformation( Method method )
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
        
        boolean isGetterWithAdditionalArguments = parameterTypes != null && parameterTypes.length >= 1 && returnType != null
                                                  && methodName != null
                                                  && ( methodName.startsWith( "is" ) || methodName.startsWith( "get" ) );
        boolean isSetterWithAdditionalArguments = parameterTypes != null && parameterTypes.length >= 2 && methodName != null
                                                  && ( methodName.startsWith( "set" ) );
        
        //
        String referencedFieldName = null;
        if ( isGetter || isSetter || isGetterWithAdditionalArguments || isSetterWithAdditionalArguments )
        {
          //
          referencedFieldName = methodName.replaceFirst( "^(is|get|set)", "" );
          if ( referencedFieldName.length() > 0 )
          {
            referencedFieldName = referencedFieldName.replaceFirst( "^.", referencedFieldName.substring( 0, 1 ).toLowerCase() );
          }
        }
        
        //
        retval = new BeanMethodInformation( isGetter, isSetter, isGetterWithAdditionalArguments, isSetterWithAdditionalArguments,
                                            referencedFieldName, method );
        
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Converter interface which offers a {@link #convert(Object)} method which has to convert one bean type to another.
   * 
   * @author Omnaest
   * @param <FROM>
   * @param <TO>
   */
  public static interface BeanConverter<FROM, TO>
  {
    /**
     * Converts a given Java bean to another
     * 
     * @param from
     * @return converted java bean instance
     */
    public TO convert( FROM from );
    
  }
  
  /**
   * Converts a given bean into another using a {@link BeanConverter}. <br>
   * {@link Exception}s are catched and ignored by this method.
   * 
   * @param sourceBean
   * @param beanConverter
   * @return a converted Java bean instance
   */
  public static <FROM, TO> TO convert( FROM sourceBean, BeanConverter<FROM, TO> beanConverter )
  {
    //
    TO retval = null;
    
    //
    if ( sourceBean != null && beanConverter != null )
    {
      try
      {
        retval = beanConverter.convert( sourceBean );
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
      Map<String, BeanPropertyAccessor<S>> fieldnameToBeanPropertyAccessorSourceMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( (Class<S>) beanSource.getClass() );
      Map<String, BeanPropertyAccessor<D>> fieldnameToBeanPropertyAccessorDestinationMap = BeanUtils.propertyNameToBeanPropertyAccessorMap( (Class<D>) beanDestination.getClass() );
      
      //
      List<TupleTwo<BeanPropertyAccessor<S>, BeanPropertyAccessor<D>>> joinTupleList = MapUtils.innerJoinMapByKey( fieldnameToBeanPropertyAccessorSourceMap,
                                                                                                                   fieldnameToBeanPropertyAccessorDestinationMap );
      
      //
      for ( TupleTwo<BeanPropertyAccessor<S>, BeanPropertyAccessor<D>> tupleDuad : joinTupleList )
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
   * @see #propertyNamesForMethodAccess(Class)
   * @param clazz
   * @return {@link Set}
   */
  public static Set<String> propertyNameSetForMethodAccess( Class<?> clazz )
  {
    return new LinkedHashSet<String>( Arrays.asList( propertyNamesForMethodAccess( clazz ) ) );
  }
  
  /**
   * Determines the property names of a given bean class which are addressed by getter or setter.
   * 
   * @see #propertyNameSetForMethodAccess(Class)
   * @param clazz
   * @return
   */
  public static String[] propertyNamesForMethodAccess( Class<?> clazz )
  {
    //
    String[] retvals = null;
    
    //
    Set<String> propertyNameSet = new HashSet<String>();
    if ( clazz != null )
    {
      //
      Map<String, Set<BeanMethodInformation>> fieldnameToBeanMethodInformationMap = BeanUtils.propertyNameToBeanMethodInformationMap( clazz );
      propertyNameSet.addAll( fieldnameToBeanMethodInformationMap.keySet() );
      propertyNameSet.remove( null );
      propertyNameSet.remove( "class" );
      
      //
      retvals = propertyNameSet.toArray( new String[0] );
    }
    
    //
    return retvals;
  }
  
  /**
   * Clones the given JavaBean by using the standard contructor to create a new instance and then to populate all properties using
   * {@link #copyPropertyValues(Object, Object)} to the new created instance.
   * 
   * @param bean
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> B cloneBean( B bean )
  {
    //    
    B retval = null;
    
    //
    if ( bean != null )
    {
      try
      {
        //
        retval = ReflectionUtils.<B> createInstanceOf( (Class<B>) bean.getClass() );
        
        //
        if ( retval != null )
        {
          BeanUtils.copyPropertyValues( bean, retval );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Clones the given Java bean using a {@link Map} to store all properties and providing a proxy which accesses this underlying
   * {@link Map}.
   * 
   * @param bean
   * @return
   */
  public static <B> B cloneBeanUsingInstanceOfMap( B bean )
  {
    boolean underlyingMapAware = false;
    return BeanUtils.cloneBeanUsingInstanceOfMap( bean, underlyingMapAware );
  }
  
  /**
   * Clones the given Java bean using a {@link Map} to store all properties and providing a proxy which accesses this underlying
   * {@link Map}.
   * 
   * @param bean
   * @param underlyingMapAware
   * @return
   */
  public static <B> B cloneBeanUsingInstanceOfMap( B bean, boolean underlyingMapAware )
  {
    //    
    B retval = null;
    
    //
    if ( bean != null )
    {
      //
      try
      {
        //
        Map<String, Object> propertyNameToBeanPropertyValueMap = BeanUtils.propertyNameToBeanPropertyValueMap( bean );
        
        //
        if ( propertyNameToBeanPropertyValueMap != null )
        {
          @SuppressWarnings("unchecked")
          Class<? extends B> type = (Class<B>) bean.getClass();
          
          Configuration configuration = new Configuration();
          configuration.setUnderlyingMapAware( underlyingMapAware );
          
          retval = PropertynameMapToTypeAdapter.newInstance( propertyNameToBeanPropertyValueMap, type, configuration );
        }
        
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Clones the given Java bean using a {@link BeanToNestedMapConverter} for marshalling and unmarshalling.
   * 
   * @param bean
   * @param underlyingMapAware
   * @return
   */
  public static <B> B cloneBeanUsingNestedfMap( B bean )
  {
    //
    B retval = null;
    
    //
    if ( bean != null )
    {
      //
      @SuppressWarnings("unchecked")
      Class<B> beanClass = (Class<B>) bean.getClass();
      PropertyAccessType propertyAccessType = null;
      BeanToNestedMapConverter<B> beanToNestedMapConverter = new BeanToNestedMapConverter<B>(
                                                                                              new BeanConversionFilterExcludingPrimitiveAndString(),
                                                                                              beanClass, propertyAccessType );
      Map<String, Object> map = beanToNestedMapConverter.marshal( bean );
      retval = beanToNestedMapConverter.unmarshal( map );
    }
    
    //
    return retval;
  }
}
