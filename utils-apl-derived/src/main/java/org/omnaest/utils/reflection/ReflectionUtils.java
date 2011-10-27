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
package org.omnaest.utils.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.structure.collection.ListUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectClass;
import org.omnaest.utils.structure.map.MapUtils;

/**
 * Helper for Java Reflection.
 * 
 * @author Omnaest
 */
public class ReflectionUtils
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  /**
   * Represents the meta information about a single {@link Method} parameter
   * 
   * @author Omnaest
   */
  public static class MethodParameterMetaInformation
  {
    /* ********************************************** Variables ********************************************** */
    private Class<?>         type                   = null;
    private List<Annotation> declaredAnnotationList = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public MethodParameterMetaInformation( Class<?> type, List<Annotation> declaredAnnotationList )
    {
      super();
      this.type = type;
      this.declaredAnnotationList = declaredAnnotationList;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
    public List<Annotation> getDeclaredAnnotationList()
    {
      return this.declaredAnnotationList;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Determines the index position of a declared {@link Method} within a given {@link Class}.
   * 
   * @param clazz
   * @param method
   * @return
   */
  public static int declaredMethodIndexPosition( Class<?> clazz, Method method )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && method != null )
    {
      //
      Method[] declaredMethods = clazz.getDeclaredMethods();
      if ( declaredMethods != null )
      {
        retval = ArrayUtils.indexOf( declaredMethods, method );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Determines the index position of a declared {@link Field} within a given {@link Class}.
   * 
   * @param clazz
   * @param field
   * @return
   */
  public static int declaredFieldIndexPosition( Class<?> clazz, Field field )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && field != null )
    {
      //
      Field[] declaredFields = clazz.getDeclaredFields();
      if ( declaredFields != null )
      {
        retval = ArrayUtils.indexOf( declaredFields, field );
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Determines the index position of a declared {@link Field} within a given {@link Class}.
   * 
   * @param clazz
   * @param field
   * @return -1 if the {@link Field} could not be determined at all.
   */
  public static int declaredFieldIndexPosition( Class<?> clazz, String fieldname )
  {
    //
    int retval = -1;
    
    //
    if ( clazz != null && fieldname != null )
    {
      //
      try
      {
        //
        Field field = clazz.getField( fieldname );
        
        //
        if ( field != null )
        {
          //
          Field[] declaredFields = clazz.getDeclaredFields();
          if ( declaredFields != null )
          {
            retval = ArrayUtils.indexOf( declaredFields, field );
          }
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
   * Returns the number of declared {@link Field}s of a {@link Class}.
   * 
   * @param clazz
   * @return number of declared {@link Field}s or 0 if clazz == null
   */
  public static int numberOfDeclaredFields( Class<?> clazz )
  {
    return clazz != null ? clazz.getDeclaredFields().length : 0;
  }
  
  /**
   * Returns the number of declared {@link Method}s of a {@link Class}.
   * 
   * @param clazz
   * @return number of declared {@link Method}s or 0 if clazz == null
   */
  public static int numberOfDeclaredMethods( Class<?> clazz )
  {
    return clazz != null ? clazz.getDeclaredMethods().length : 0;
  }
  
  /**
   * Creates a new instance of a given {@link Class} using a constructor which has the same parameter signature as the provided
   * arguments.
   * 
   * @param type
   * @param args
   */
  public static <B> B createInstanceOf( Class<? extends B> type, Object... args )
  {
    //
    B retval = null;
    
    //
    if ( type != null )
    {
      //
      Class<?>[] parameterTypes = ListUtils.transform( Arrays.asList( args ), new ElementConverterObjectClass() )
                                           .toArray( new Class<?>[args.length] );
      
      //
      try
      {
        //
        Constructor<? extends B> constructor = type.getDeclaredConstructor( parameterTypes );
        
        //
        boolean accessible = constructor.isAccessible();
        
        //
        try
        {
          constructor.setAccessible( true );
          retval = constructor.newInstance( args );
        }
        catch ( Exception e )
        {
        }
        
        //
        constructor.setAccessible( accessible );
      }
      catch ( Exception e )
      {
      }
      
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new instance of a given {@link Class} using a possibly present valueOf method which has the same parameter
   * signature as the provided arguments.
   * 
   * @param type
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static <B> B createInstanceUsingValueOfMethod( Class<? extends B> type, Object... args )
  {
    //
    B retval = null;
    
    //
    if ( type != null )
    {
      //
      Class<?>[] parameterTypes = ListUtils.transform( Arrays.asList( args ), new ElementConverterObjectClass() )
                                           .toArray( new Class<?>[args.length] );
      
      //
      try
      {
        //
        String name = "valueOf";
        Method valueOfMethod = type.getDeclaredMethod( name, parameterTypes );
        
        //
        boolean accessible = valueOfMethod.isAccessible();
        
        //
        try
        {
          valueOfMethod.setAccessible( true );
          retval = (B) valueOfMethod.invoke( null, args );
        }
        catch ( Exception e )
        {
        }
        
        //
        valueOfMethod.setAccessible( accessible );
      }
      catch ( Exception e )
      {
      }
      
    }
    
    //
    return retval;
  }
  
  /**
   * Returns a {@link Map} with {@link Method}s of the given Java Bean and a {@link Set} of all available {@link Annotation}s for
   * the {@link Method}
   * 
   * @param beanClass
   * @return
   */
  public static Map<Method, Set<Annotation>> methodToAnnotationSetMap( Class<?> type )
  {
    //
    Map<Method, Set<Annotation>> retmap = new LinkedHashMap<Method, Set<Annotation>>();
    
    //
    if ( type != null )
    {
      //
      Method[] declaredMethods = type.getDeclaredMethods();
      for ( Method method : declaredMethods )
      {
        Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
        retmap.put( method, new LinkedHashSet<Annotation>( Arrays.asList( declaredAnnotations ) ) );
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Returns a {@link Map} with all {@link Method}s of a given {@link Class} and the annotation instance for the given method.
   * Methods which will have no matching {@link Annotation}s will be excluded and will not show as keys.
   * 
   * @param type
   * @param annotation
   * @return
   */
  public static <A extends Annotation> Map<Method, A> methodToAnnotationMap( final Class<?> type, final Class<A> annotationType )
  {
    //
    Map<Method, A> retmap = null;
    
    Map<Method, Set<Annotation>> methodToAnnotationSetMap = methodToAnnotationSetMap( type );
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
            if ( annotation != null && annotationType.isAssignableFrom( annotation.getClass() ) )
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
    retmap = MapUtils.convertMapValue( methodToAnnotationSetMap, valueElementConverter );
    retmap = MapUtils.filteredMapExcludingNullValues( retmap );
    
    //
    return retmap;
  }
  
  /**
   * Returns all {@link Class#getDeclaredAnnotations()} as {@link List}
   * 
   * @param type
   * @return
   */
  public static List<Annotation> declaredAnnotationList( Class<?> type )
  {
    //    
    List<Annotation> retlist = new ArrayList<Annotation>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getDeclaredAnnotations() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns true if the given {@link Class} type declares the given {@link Annotation} class
   * 
   * @param type
   * @param annotationType
   * @return
   */
  public static boolean hasDeclaredAnnotation( Class<?> type, Class<? extends Annotation> annotationType )
  {
    //
    boolean retval = false;
    
    //
    List<Annotation> declaredAnnotationList = declaredAnnotationList( type );
    for ( Annotation declaredAnnotation : declaredAnnotationList )
    {
      if ( declaredAnnotation.annotationType().equals( annotationType ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true if the given {@link Method} declares the given {@link Annotation} class
   * 
   * @param method
   * @param annotationType
   * @return
   */
  public static boolean hasDeclaredAnnotation( Method method, Class<? extends Annotation> annotationType )
  {
    //
    boolean retval = false;
    
    //
    List<Annotation> declaredAnnotationList = declaredAnnotationList( method );
    for ( Annotation declaredAnnotation : declaredAnnotationList )
    {
      if ( declaredAnnotation.annotationType().equals( annotationType ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns all {@link Method#getDeclaredAnnotations()} as {@link List}
   * 
   * @param method
   * @return
   */
  public static List<Annotation> declaredAnnotationList( Method method )
  {
    //    
    List<Annotation> retlist = new ArrayList<Annotation>();
    
    //
    if ( method != null )
    {
      //
      retlist.addAll( Arrays.asList( method.getDeclaredAnnotations() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns an ordered {@link List} of {@link MethodParameterMetaInformation} instances for each parameter the given
   * {@link Method} has.
   * 
   * @param method
   * @return
   */
  public static List<MethodParameterMetaInformation> declaredMethodParameterMetaInformationList( Method method )
  {
    //    
    List<MethodParameterMetaInformation> retlist = new ArrayList<MethodParameterMetaInformation>();
    
    //
    if ( method != null )
    {
      //
      Class<?>[] parameterTypes = method.getParameterTypes();
      Annotation[][] parametersAnnotations = method.getParameterAnnotations();
      
      //
      for ( int ii = 0; ii < Math.max( parameterTypes.length, parametersAnnotations.length ); ii++ )
      {
        //
        Class<?> parameterType = parameterTypes[ii];
        Annotation[] parameterAnnotations = parametersAnnotations[ii];
        
        //
        MethodParameterMetaInformation methodParameterMetaInformation = new MethodParameterMetaInformation(
                                                                                                            parameterType,
                                                                                                            Arrays.asList( parameterAnnotations ) );
        retlist.add( methodParameterMetaInformation );
      }
      
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the {@link Class#getDeclaredMethods()} for a given {@link Class}. Returns always a {@link List} instance.
   * 
   * @param type
   * @return
   */
  public static List<Method> declaredMethodList( Class<?> type )
  {
    //    
    List<Method> retlist = new ArrayList<Method>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getDeclaredMethods() ) );
    }
    
    //
    return retlist;
  }
  
}
