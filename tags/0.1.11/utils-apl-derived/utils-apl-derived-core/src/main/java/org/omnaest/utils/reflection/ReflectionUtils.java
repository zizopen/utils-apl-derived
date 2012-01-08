/*****************************************************import org.omnaest.utils.beans.replicator.BeanReplicator.DTOPackage;
nsed under the Apache License, Version 2.0 (the "License");
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.element.converter.ElementConverterObjectToClassOfObject;
import org.omnaest.utils.structure.element.factory.concrete.LinkedHashSetFactory;
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
   * Returns true if the given {@link Class} type has a {@link Constructor} for the given arguments
   * 
   * @param type
   * @param arguments
   * @return
   */
  public static <B> boolean hasConstructorFor( Class<? extends B> type, Object... arguments )
  {
    return constructorFor( type, arguments ) != null;
  }
  
  /**
   * Returns true if the given {@link Class} type has a {@link Constructor} for the given parameter types
   * 
   * @param type
   * @param parameterTypes
   * @return
   */
  public static <B> boolean hasConstructorFor( Class<? extends B> type, Class<?>... parameterTypes )
  {
    return constructorFor( type, parameterTypes ) != null;
  }
  
  /**
   * Returns true if the given {@link Class} type has a default {@link Constructor}
   * 
   * @param type
   * @return
   */
  public static <B> boolean hasDefaultConstructorFor( Class<? extends B> type )
  {
    return hasConstructorFor( type );
  }
  
  /**
   * Returns the {@link Constructor} for the given {@link Class} type and arguments
   * 
   * @param type
   * @param arguments
   * @return
   */
  public static <B> Constructor<B> constructorFor( Class<? extends B> type, Object... arguments )
  {
    //
    Class<?>[] parameterTypes = ListUtils.convert( Arrays.asList( arguments ), new ElementConverterObjectToClassOfObject() )
                                         .toArray( new Class<?>[arguments.length] );
    return constructorFor( type, parameterTypes );
  }
  
  /**
   * Returns the {@link Constructor} for the given {@link Class} type and parameter types
   * 
   * @param type
   * @param parameterTypes
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <B> Constructor<B> constructorFor( Class<? extends B> type, Class<?>... parameterTypes )
  {
    //
    Constructor<B> constructor = null;
    
    //
    try
    {
      //
      constructor = (Constructor<B>) type.getDeclaredConstructor( parameterTypes );
    }
    catch ( Exception e )
    {
      try
      {
        constructor = (Constructor<B>) type.getConstructor( parameterTypes );
      }
      catch ( Exception e2 )
      {
        try
        {
          constructor = (Constructor<B>) resolveConstructorFor( type, parameterTypes );
        }
        catch ( Exception e3 )
        {
        }
      }
    }
    
    //
    return constructor;
  }
  
  /**
   * Resolves a matching constructor for the given type and the given parameter types
   * 
   * @param type
   * @param parameterTypes
   * @return
   */
  public static <C> Constructor<C> resolveConstructorFor( Class<C> type, Class<?>... parameterTypes )
  {
    //
    Constructor<C> retval = null;
    
    //
    if ( type != null )
    {
      //
      @SuppressWarnings("unchecked")
      Constructor<C>[] constructors = (Constructor<C>[]) type.getConstructors();
      if ( constructors != null )
      {
        for ( Constructor<C> constructor : constructors )
        {
          Class<?>[] parameterTypesOfContstructor = constructor.getParameterTypes();
          boolean areConstructorTypesAssignableFromParameterTypes = areAssignableFrom( parameterTypesOfContstructor,
                                                                                       parameterTypes );
          if ( areConstructorTypesAssignableFromParameterTypes )
          {
            retval = constructor;
            break;
          }
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true if all types of the assignable types are {@link Class#isAssignableFrom(Class)} to their source type counterpart.
   * Both arrays have to have the same size. Every element of one of the arrays will map to its counterpart with the same index
   * position.
   * 
   * @param assignableTypes
   * @param sourceTypes
   * @return
   */
  public static boolean areAssignableFrom( Class<?>[] assignableTypes, Class<?>[] sourceTypes )
  {
    //    
    boolean retval = assignableTypes != null && sourceTypes != null && assignableTypes.length == sourceTypes.length;
    if ( retval )
    {
      for ( int ii = 0; ii < assignableTypes.length; ii++ )
      {
        //
        Class<?> assignableType = assignableTypes[ii];
        Class<?> sourceType = sourceTypes[ii];
        
        //
        retval &= assignableType.isAssignableFrom( sourceType );
        if ( !retval )
        {
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Creates a new instance of a given {@link Class} using a constructor which has the same parameter signature as the provided
   * arguments.
   * 
   * @param type
   * @param arguments
   */
  public static <B> B createInstanceOf( Class<? extends B> type, Object... arguments )
  {
    //
    B retval = null;
    
    //
    if ( type != null )
    {
      //
      try
      {
        //
        Constructor<? extends B> constructor = constructorFor( type, arguments );
        
        //
        boolean accessible = constructor.isAccessible();
        
        //
        try
        {
          constructor.setAccessible( true );
          retval = constructor.newInstance( arguments );
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
   * @param arguments
   */
  @SuppressWarnings("unchecked")
  public static <B> B createInstanceUsingValueOfMethod( Class<? extends B> type, Object... arguments )
  {
    //
    B retval = null;
    
    //
    if ( type != null )
    {
      //
      Class<?>[] parameterTypes = ListUtils.convert( Arrays.asList( arguments ), new ElementConverterObjectToClassOfObject() )
                                           .toArray( new Class<?>[arguments.length] );
      
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
          retval = (B) valueOfMethod.invoke( null, arguments );
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
   * Returns a {@link Map} with the declared {@link Method}s of the given type and the return type of the {@link Method}s
   * 
   * @param type
   * @return
   */
  public static Map<Method, Class<?>> declaredMethodToReturnTypeMap( Class<?> type )
  {
    List<Method> methodList = declaredMethodList( type );
    return methodToReturnTypeMap( methodList );
  }
  
  /**
   * Returns a {@link Map} with {@link Method}s of the given type and the return type of the {@link Method}s
   * 
   * @param type
   * @return
   */
  public static Map<Method, Class<?>> methodToReturnTypeMap( Class<?> type )
  {
    List<Method> methodList = methodList( type );
    return methodToReturnTypeMap( methodList );
  }
  
  /**
   * Returns a {@link Map} with all the given {@link Method}s and their return type
   * 
   * @param methodList
   * @return
   */
  protected static Map<Method, Class<?>> methodToReturnTypeMap( List<Method> methodList )
  {
    //
    Map<Method, Class<?>> retmap = new LinkedHashMap<Method, Class<?>>();
    
    //
    if ( methodList != null )
    {
      //
      for ( Method method : methodList )
      {
        Class<?> returnType = method.getReturnType();
        retmap.put( method, returnType );
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
   * Returns all {@link Class#getAnnotations()} as {@link List}
   * 
   * @param type
   * @return
   */
  public static List<Annotation> annotationList( Class<?> type )
  {
    //    
    List<Annotation> retlist = new ArrayList<Annotation>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getAnnotations() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns all {@link Field#getAnnotations()} as {@link List}
   * 
   * @param field
   * @return
   */
  public static List<Annotation> annotationList( Field field )
  {
    //    
    List<Annotation> retlist = new ArrayList<Annotation>();
    
    //
    if ( field != null )
    {
      //
      retlist.addAll( Arrays.asList( field.getAnnotations() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns true if the given {@link Class} type declares or inherits the given {@link Annotation} class from any supertype, but
   * not from interfaces
   * 
   * @see #hasDeclaredAnnotation(Class, Class)
   * @see #hasAnnotationIncludingInterfaces(Class, Class)
   * @param type
   * @param annotationType
   * @return
   */
  public static boolean hasAnnotation( Class<?> type, Class<? extends Annotation> annotationType )
  {
    //
    boolean retval = false;
    
    //
    List<Annotation> annotationList = annotationList( type );
    for ( Annotation annotation : annotationList )
    {
      if ( annotation.annotationType().equals( annotationType ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true if the given {@link Field} declares the given {@link Annotation} class
   * 
   * @param field
   * @param annotationType
   * @return
   */
  public static boolean hasAnnotation( Field field, Class<? extends Annotation> annotationType )
  {
    //
    boolean retval = false;
    
    //
    final List<Annotation> annotationList = annotationList( field );
    for ( Annotation annotation : annotationList )
    {
      if ( annotation.annotationType().equals( annotationType ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Returns true if the given {@link Class} type declares or inherits the given {@link Annotation} class from any supertype, but
   * not from interfaces
   * 
   * @param type
   * @param annotationType
   * @return
   */
  public static boolean hasAnnotationIncludingInterfaces( Class<?> type, Class<? extends Annotation> annotationType )
  {
    //
    boolean retval = false;
    
    //
    final List<Annotation> annotationList = annotationList( type );
    for ( Annotation annotation : annotationList )
    {
      if ( annotation.annotationType().equals( annotationType ) )
      {
        retval = true;
        break;
      }
    }
    
    //
    if ( !retval )
    {
      //
      final boolean inherited = true;
      final Set<Class<?>> interfaceSet = interfaceSet( type, inherited );
      for ( Class<?> interfaceType : interfaceSet )
      {
        if ( hasDeclaredAnnotation( interfaceType, annotationType ) )
        {
          retval = true;
          break;
        }
      }
    }
    
    //
    return retval;
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
    Set<Annotation> declaredAnnotationSet = declaredAnnotationSet( method );
    for ( Annotation declaredAnnotation : declaredAnnotationSet )
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
  public static Set<Annotation> declaredAnnotationSet( Method method )
  {
    //    
    Set<Annotation> retlist = new LinkedHashSet<Annotation>();
    
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
   * Returns a {@link Map} of all {@link Method}s of the given {@link Class} type and a {@link List} of declared
   * {@link Annotation}s related to the {@link Method}s
   * 
   * @see #declaredMethodList(Class)
   * @see #declaredAnnotationSet(Method)
   * @param type
   * @return
   */
  public static Map<Method, Set<Annotation>> methodToAnnotationSetMap( Class<?> type )
  {
    List<Method> methodList = methodList( type );
    return methodToAnnotationSetMap( methodList );
  }
  
  /**
   * Returns a {@link Map} of all declared {@link Method}s of the given {@link Class} type and a {@link List} of declared
   * {@link Annotation}s related to the {@link Method}s
   * 
   * @see #declaredMethodList(Class)
   * @see #declaredAnnotationSet(Method)
   * @param type
   * @return
   */
  public static Map<Method, Set<Annotation>> declaredMethodToAnnotationSetMap( Class<?> type )
  {
    List<Method> methodList = declaredMethodList( type );
    return methodToAnnotationSetMap( methodList );
  }
  
  /**
   * Returns a {@link Map} of all the given {@link Method}s and a {@link List} of declared {@link Annotation}s related to the
   * {@link Method}s
   * 
   * @see #declaredMethodList(Class)
   * @see #declaredAnnotationSet(Method)
   * @param methodList
   * @return
   */
  protected static Map<Method, Set<Annotation>> methodToAnnotationSetMap( List<Method> methodList )
  {
    //
    Map<Method, Set<Annotation>> retmap = new LinkedHashMap<Method, Set<Annotation>>();
    
    //
    if ( methodList != null )
    {
      for ( Method declaredMethod : methodList )
      {
        //
        Set<Annotation> declaredAnnotationSet = declaredAnnotationSet( declaredMethod );
        if ( declaredAnnotationSet != null )
        {
          //
          retmap.put( declaredMethod, declaredAnnotationSet );
        }
      }
    }
    
    //
    return retmap;
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
    final List<Method> retlist = new ArrayList<Method>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getDeclaredMethods() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the {@link Class#getDeclaredFields()} for a given {@link Class}. Returns always a {@link List} instance.
   * 
   * @param type
   * @return
   */
  public static List<Field> declaredFieldList( Class<?> type )
  {
    //    
    final List<Field> retlist = new ArrayList<Field>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getDeclaredFields() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the {@link Class#getFields()} for a given {@link Class}. Returns always a {@link List} instance.
   * 
   * @param type
   * @return
   */
  public static List<Field> fieldList( Class<?> type )
  {
    //    
    final List<Field> retlist = new ArrayList<Field>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getFields() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns the {@link Class#getMethods()} for a given {@link Class}. Returns always a {@link List} instance.
   * 
   * @param type
   * @return
   */
  public static List<Method> methodList( Class<?> type )
  {
    //    
    final List<Method> retlist = new ArrayList<Method>();
    
    //
    if ( type != null )
    {
      //
      retlist.addAll( Arrays.asList( type.getMethods() ) );
    }
    
    //
    return retlist;
  }
  
  /**
   * Returns as {@link Set} of interfaces which are implemented by the given type. This includes inherited interfaces if the
   * respective parameter is set to true. If the given type is an interface it is included in the result.
   * 
   * @param type
   * @return
   */
  public static Set<Class<?>> interfaceSet( Class<?> type, boolean inherited )
  {
    boolean interfaceOnly = true;
    return assignableTypeSet( type, inherited, interfaceOnly );
  }
  
  /**
   * Returns as {@link Set} of assignable types which are implemented by the given type. This includes inherited types if the
   * respective parameter is set to true. The given type itself is included in the returned {@link Set}.
   * 
   * @param type
   * @param inherited
   * @return
   */
  public static Set<Class<?>> assignableTypeSet( Class<?> type, boolean inherited )
  {
    boolean interfaceOnly = false;
    return assignableTypeSet( type, inherited, interfaceOnly );
  }
  
  /**
   * Returns as {@link Set} of assignable types which are implemented by the given type. This includes inherited types if the
   * respective parameter is set to true. The given type is returned within the result {@link Set} if it is compliant to the
   * onlyReturnInterfaces flag.
   * 
   * @param type
   * @param inherited
   * @param onlyReturnInterfaces
   * @return
   */
  public static Set<Class<?>> assignableTypeSet( Class<?> type, boolean inherited, boolean onlyReturnInterfaces )
  {
    //    
    final Set<Class<?>> retset = new LinkedHashSet<Class<?>>();
    
    //
    if ( type != null )
    {
      //
      final Set<Class<?>> remainingTypeSet = new LinkedHashSet<Class<?>>();
      
      //
      class Helper
      {
        public void addNewInterfaceTypes( Class<?> type )
        {
          Class<?>[] interfaces = type.getInterfaces();
          if ( interfaces != null )
          {
            for ( Class<?> interfaceType : interfaces )
            {
              if ( !retset.contains( interfaceType ) )
              {
                remainingTypeSet.add( interfaceType );
              }
            }
          }
        }
      }
      
      Helper helper = new Helper();
      helper.addNewInterfaceTypes( type );
      
      //
      if ( !onlyReturnInterfaces || type.isInterface() )
      {
        retset.add( type );
      }
      
      //
      if ( inherited )
      {
        remainingTypeSet.addAll( supertypeSet( type ) );
      }
      
      //
      while ( !remainingTypeSet.isEmpty() )
      {
        //
        Iterator<Class<?>> iterator = remainingTypeSet.iterator();
        Class<?> remainingType = iterator.next();
        iterator.remove();
        
        //
        if ( !onlyReturnInterfaces || remainingType.isInterface() )
        {
          retset.add( remainingType );
        }
        
        //
        if ( inherited )
        {
          //
          helper.addNewInterfaceTypes( remainingType );
        }
        
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns a {@link Set} of all supertypes the given type extends. The given type is not within the result {@link Set}.
   * 
   * @param type
   * @return
   */
  public static Set<Class<?>> supertypeSet( Class<?> type )
  {
    //    
    Set<Class<?>> retset = new LinkedHashSet<Class<?>>();
    
    //
    if ( type != null )
    {
      Class<?> supertype = type.getSuperclass();
      while ( supertype != null )
      {
        retset.add( supertype );
        supertype = supertype.getSuperclass();
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns as {@link Set} of assignable types which are implemented by the given type. This includes inherited types if the
   * respective parameter is set to true. The given types are included within the result {@link Set} if they are compliant to the
   * onlyReturnInterfaces flag.
   * 
   * @param inherited
   * @param intersection
   *          : false -> types are merged, true -> intersection of type sets for each type
   * @param onlyReturnInterfaces
   * @param types
   * @return
   */
  public static Set<Class<?>> assignableTypeSet( boolean inherited,
                                                 boolean onlyReturnInterfaces,
                                                 boolean intersection,
                                                 Class<?>... types )
  {
    //    
    Set<Class<?>> retset = new HashSet<Class<?>>();
    
    //
    if ( types.length > 0 )
    {
      //
      Set<Set<Class<?>>> assignableTypeSetSet = new LinkedHashSet<Set<Class<?>>>();
      for ( Class<?> type : types )
      {
        Set<Class<?>> assignableTypeSet = assignableTypeSet( type, inherited, onlyReturnInterfaces );
        assignableTypeSetSet.add( assignableTypeSet );
      }
      
      //
      if ( !intersection )
      {
        retset = SetUtils.mergeAll( assignableTypeSetSet );
      }
      else
      {
        retset = SetUtils.intersection( assignableTypeSetSet );
      }
    }
    
    //
    return retset;
  }
  
  /**
   * Returns true if the given target {@link Class} is {@link Class#isAssignableFrom(Class)} for the given {@link Class} type.
   * 
   * @param targetType
   * @param type
   * @return
   */
  public static boolean isAssignableFrom( Class<?> targetType, Class<?> type )
  {
    return type != null && targetType != null && targetType.isAssignableFrom( type );
  }
  
  /**
   * Returns a {@link Set} of all {@link Package} instances known to the {@link ClassLoader} which are annotated with the given
   * package {@link Annotation} type
   * 
   * @see #annotatedPackageToAnnotationSetMap(Class...)
   * @param packageAnnotationTypes
   * @return
   */
  public static <A extends Annotation> Set<Package> annotatedPackageSet( Class<? extends A>... packageAnnotationTypes )
  {
    return annotatedPackageToAnnotationSetMap( packageAnnotationTypes ).keySet();
  }
  
  /**
   * Returns a {@link Set} of all {@link Package} instances out of the given {@link Set} of {@link Package}s which are annotated
   * with at least one of the given package {@link Annotation} types
   * 
   * @see #annotatedPackageToAnnotationSetMap(Class...)
   * @param scannedPackageSet
   * @param packageAnnotationTypes
   * @return
   */
  public static <A extends Annotation> Set<Package> annotatedPackageSet( Set<Package> scannedPackageSet,
                                                                         Class<? extends A>... packageAnnotationTypes )
  {
    return annotatedPackageToAnnotationSetMap( scannedPackageSet, packageAnnotationTypes ).keySet();
  }
  
  /**
   * Returns a {@link Map} of all {@link Package}s annotated with at least one of the given package level {@link Annotation}s
   * including the {@link Annotation} instances related to each {@link Package}. <br>
   * <br>
   * If no {@link Annotation} is specified an empty {@link Map} is returned.
   * 
   * @see #annotatedPackageSet(Class...)
   * @param packageAnnotationTypes
   * @return
   */
  public static <A extends Annotation> Map<Package, Set<A>> annotatedPackageToAnnotationSetMap( Class<? extends A>... packageAnnotationTypes )
  {
    final Set<Package> scannedPackageSet = SetUtils.valueOf( Package.getPackages() );
    return annotatedPackageToAnnotationSetMap( scannedPackageSet, packageAnnotationTypes );
  }
  
  /**
   * Returns a {@link Map} of all {@link Package}s out of the given {@link Package}s {@link Set} annotated with at least one of
   * the given package level {@link Annotation}s including the {@link Annotation} instances related to each {@link Package}. <br>
   * <br>
   * If no {@link Annotation} or {@link Package} is specified an empty {@link Map} is returned.
   * 
   * @see #annotatedPackageSet(Class...)
   * @param scannedPackageSet
   * @param packageAnnotationTypes
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <A extends Annotation> Map<Package, Set<A>> annotatedPackageToAnnotationSetMap( Set<Package> scannedPackageSet,
                                                                                                Class<? extends A>... packageAnnotationTypes )
  {
    //
    final Map<Package, Set<A>> retmap = MapUtils.initializedMap( new LinkedHashSetFactory<A>() );
    
    //
    if ( scannedPackageSet != null && packageAnnotationTypes.length > 0 )
    {
      //
      for ( Package package_ : scannedPackageSet )
      {
        for ( Class<? extends Annotation> packageAnnotationType : packageAnnotationTypes )
        {
          //
          final Annotation annotation = package_.getAnnotation( packageAnnotationType );
          if ( annotation != null )
          {
            retmap.get( package_ ).add( (A) annotation );
          }
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * Invokes {@link Class#forName(String)} for the given type name but does not throw any {@link Exception}. Instead it returns
   * null, if the resolvation failed.
   * 
   * @param className
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> classForName( String className )
  {
    //
    Class<T> retval = null;
    
    //
    try
    {
      Class<?> forName = Class.forName( className );
      retval = (Class<T>) forName;
    }
    catch ( Exception e )
    {
    }
    
    //
    return retval;
  }
}
