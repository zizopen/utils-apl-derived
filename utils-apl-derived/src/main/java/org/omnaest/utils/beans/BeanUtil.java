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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.omnaest.utils.structure.array.ArrayUtil;
import org.omnaest.utils.structure.collection.CollectionUtil.ElementConverter;

/**
 * Simple utility class, which extends the Apache Common BeanUtils class.
 * 
 * @author Omnaest
 */
public class BeanUtil extends org.apache.commons.beanutils.BeanUtils
{
  /* ********************************************** Constants ********************************************** */
  private final static String[] PROPERTY_PREFIXES = { "get", "is", "set" };
  
  /* ********************************************** Methods ********************************************** */

  /**
   * Returns a map with the values of the given bean object. If property names are specified, only those values are extracted,
   * whose property names are specified. If no property name is specified all properties are read.
   * 
   * @param beanObject
   * @param propertyNames
   */
  public static <Value, Element> Map<String, Value> determineBeanPropertyNameToValueMap( Element beanObject,
                                                                                         String... propertyNames )
  {
    //
    Map<String, Value> retmap = new LinkedHashMap<String, Value>();
    
    //
    if ( propertyNames != null && propertyNames.length > 0 )
    {
      /*
       * only specified properties
       */

      //
      Map<String, BeanProperty> propertyToBeanPropertyMap = new HashMap<String, BeanProperty>( 0 );
      
      //
      BeanProperty[] beanObjectProperties = BeanUtil.determineBeanProperties( beanObject );
      for ( BeanProperty iBeanProperty : beanObjectProperties )
      {
        propertyToBeanPropertyMap.put( iBeanProperty.getPropertyName(), iBeanProperty );
      }
      
      //
      for ( String propertyName : propertyNames )
      {
        //
        BeanProperty iBeanProperty = propertyToBeanPropertyMap.get( propertyName );
        
        if ( iBeanProperty != null && iBeanProperty.isReadable() )
        {
          //
          Value value = BeanUtil.invokeJavaBeanPropertyMethod( beanObject, iBeanProperty.getGetterMethodName(), null );
          
          //
          retmap.put( propertyName, value );
        }
      }
    }
    else
    {
      /*
       * all properties
       */

      //
      BeanProperty[] beanObjectProperties = BeanUtil.determineBeanProperties( beanObject );
      
      //
      for ( BeanProperty iBeanProperty : beanObjectProperties )
      {
        if ( iBeanProperty != null && iBeanProperty.isReadable() )
        {
          //
          Value value = BeanUtil.invokeJavaBeanPropertyMethod( beanObject, iBeanProperty.getGetterMethodName(), null );
          
          //
          retmap.put( iBeanProperty.getPropertyName(), value );
        }
      }
    }
    
    //
    return retmap;
  }
  
  /**
   * The value must not be null, otherwise null is returned.
   * 
   * @see #injectValueOfPropertyIntoBean(Object, String, Object, Class)
   */
  public static <Value, Element> Element injectValueOfPropertyIntoBean( Element beanObject, String propertyName, Value value )
  {
    return BeanUtil.injectValueOfPropertyIntoBean( beanObject, propertyName, value, value == null ? null : value.getClass() );
  }
  
  /**
   * Injects the values for the given property name keys of the map into the bean.
   * 
   * @param beanObject
   * @param propertyToValueMap
   * @return
   */
  public static <Value, Element> Element injectPropertyToValueMapIntoBean( Element beanObject,
                                                                           Map<String, Value> propertyToValueMap )
  {
    //
    if ( propertyToValueMap != null )
    {
      for ( String key : propertyToValueMap.keySet() )
      {
        BeanUtil.injectValueOfPropertyIntoBean( beanObject, key, propertyToValueMap.get( key ) );
      }
    }
    
    //
    return beanObject;
  }
  
  /**
   * Puts the given value into the property of the given bean object.
   * 
   * @param beanObject
   * @param propertyName
   * @param value
   *          : != null
   * @return
   */
  public static <Value, Element> Element injectValueOfPropertyIntoBean( Element beanObject,
                                                                        String propertyName,
                                                                        Value value,
                                                                        Class<? extends Value> valueClass )
  {
    //
    Element retval = beanObject;
    
    //
    if ( beanObject != null && propertyName != null )
    {
      //
      String methodname = "set" + propertyName.substring( 0, 1 ).toUpperCase() + propertyName.substring( 1 );
      
      //
      Class<?> beanClass = beanObject.getClass();
      try
      {
        if ( valueClass != null )
        {
          Method method = beanClass.getDeclaredMethod( methodname, valueClass );
          if ( method != null )
          {
            method.invoke( beanObject, value );
          }
        }
        else
        {
          Method[] methods = beanClass.getDeclaredMethods();
          for ( Method method : methods )
          {
            if ( StringUtils.equals( method.getName(), methodname ) )
            {
              if ( method.getParameterTypes().length == 1 )
              {
                method.invoke( beanObject, value );
              }
            }
          }
        }
        
      }
      catch ( Exception e )
      {
        retval = null;
      }
    }
    
    //
    return retval;
  }
  
  /**
   * Copies the properties of the given list from the source to the target bean using their getter and setter methods.
   * 
   * @param targetBean
   * @param sourceBean
   * @param propertyNames
   * @return null: no exception occurred; list of exceptions: at least one exception has occurred
   */
  public static List<Exception> copyProperties( Object targetBean, Object sourceBean, String... propertyNames )
  {
    //
    List<Exception> retlist = null;
    
    //
    for ( String iPropertyName : propertyNames )
    {
      try
      {
        BeanUtil.copyProperty( targetBean, iPropertyName, BeanUtil.getProperty( sourceBean, iPropertyName ) );
      }
      catch ( Exception e )
      {
        if ( retlist == null )
        {
          retlist = new ArrayList<Exception>( 0 );
        }
        retlist.add( e );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Copies all properties with the same signature.
   * 
   * @see org.apache.commons.beanutils.BeanUtils#copyProperties(Object, Object)
   * @param targetBean
   * @param sourceBean
   * @return true: operation was successful
   */
  public static boolean copyPropertiesWherePossible( Object targetBean, Object sourceBean )
  {
    //
    boolean retval = true;
    
    //
    try
    {
      org.apache.commons.beanutils.BeanUtils.copyProperties( targetBean, sourceBean );
    }
    catch ( IllegalAccessException e )
    {
      retval = false;
    }
    catch ( InvocationTargetException e )
    {
      retval = false;
    }
    
    //
    return retval;
  }
  
  /**
   * Copies the properties of a collection of JavaBeans to another collection of JavaBeans. The copy process goes in order of the
   * collections. JavaBean 1 from Collection 1 is copied to JavaBean 1 in Collection 2. JavaBean 2 from Collection 1 is copied to
   * JavaBean 2 from Collection 2. And so far... .The collections should have the same size. The copy process goes as long as
   * there are JavaBeans on each collection left.
   * 
   * @param targetBeanCollection
   * @param sourceBeanCollection
   * @param propertyNames
   *          indicates all names of properties, which are to be copied (other properties will be ignored)
   * @return null: no exception has occurred; list of exceptions: at least one exception has occurred
   */
  public static List<Exception> copyProperties( Collection<Object> targetBeanCollection,
                                                Collection<Object> sourceBeanCollection,
                                                String... propertyNames )
  {
    //
    List<Exception> retlist = null;
    
    //
    Iterator<Object> targetBeanCollectionIterator = targetBeanCollection.iterator();
    Iterator<Object> sourceBeanCollectionIterator = sourceBeanCollection.iterator();
    while ( targetBeanCollectionIterator.hasNext() && sourceBeanCollectionIterator.hasNext() )
    {
      //
      Object targetBean = targetBeanCollectionIterator.next();
      Object sourceBean = sourceBeanCollectionIterator.next();
      
      //
      List<Exception> exceptionList = BeanUtil.copyProperties( targetBean, sourceBean, propertyNames );
      
      //
      if ( exceptionList != null )
      {
        if ( retlist == null )
        {
          retlist = new ArrayList<Exception>( 0 );
        }
        retlist.addAll( exceptionList );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Copies the properties from a collection of JavaBeans to another collection of JavaBeans. The copy process goes in order of
   * the collections. JavaBean 1 from Collection 1 is copied to JavaBean 1 in Collection 2. JavaBean 2 from Collection 1 is copied
   * to JavaBean 2 from Collection 2. And so far... All properties the match will be copied. The collections should have the same
   * size. The copy process goes as long as there are JavaBeans on each collection left.
   * 
   * @param targetBeanCollection
   * @param sourceBeanCollection
   * @return null: no exception has occurred; list of exceptions: at least one exception has occurred
   */
  public static List<Exception> copyProperties( Collection<Object> targetBeanCollection, Collection<Object> sourceBeanCollection )
  {
    //
    List<Exception> retlist = null;
    
    //
    Iterator<Object> targetBeanCollectionIterator = targetBeanCollection.iterator();
    Iterator<Object> sourceBeanCollectionIterator = sourceBeanCollection.iterator();
    while ( targetBeanCollectionIterator.hasNext() && sourceBeanCollectionIterator.hasNext() )
    {
      //
      Object targetBean = targetBeanCollectionIterator.next();
      Object sourceBean = sourceBeanCollectionIterator.next();
      
      try
      {
        BeanUtil.copyProperties( targetBean, sourceBean );
      }
      catch ( Exception e )
      {
        if ( retlist == null )
        {
          retlist = new ArrayList<Exception>( 0 );
        }
        retlist.add( e );
      }
    }
    
    //
    return retlist;
  }
  
  /**
   * Holds information about a beans property
   * 
   * @author Omnaest
   */
  public static class BeanProperty
  {
    private String propertyName     = null;
    private String getterMethodName = null;
    private String setterMethodName = null;
    
    /**
     * Returns true, if bean has only a getter method, but no setter method
     * 
     * @see #isWriteOnly()
     * @see #isReadAndWritable()
     * @return
     */
    public boolean isReadOnly()
    {
      return ( this.getterMethodName != null && this.setterMethodName == null );
    }
    
    /**
     * Returns true, if the property can be read.
     * 
     * @return
     */
    public boolean isReadable()
    {
      return this.getterMethodName != null;
    }
    
    /**
     * Returns true, if bean has only a setter method, and no getter method
     * 
     * @see #isReadOnly()
     * @see #isReadAndWritable()
     * @return
     */
    public boolean isWriteOnly()
    {
      return ( this.getterMethodName == null && this.setterMethodName != null );
    }
    
    /**
     * Returns true, if the property can be written.
     * 
     * @return
     */
    public boolean isWritable()
    {
      return this.setterMethodName != null;
    }
    
    /**
     * Returns true, if bean has getter and setter access
     * 
     * @see #isReadOnly()
     * @see #isWriteOnly()
     * @return
     */
    public boolean isReadAndWritable()
    {
      return ( this.getterMethodName != null && this.setterMethodName != null );
    }
    
    /**
     * Copy all information of a foreign beanProperty object, that are not null. If informations are copied the own are
     * overwritten.
     * 
     * @param beanProperty
     */
    public void assimilate( BeanProperty beanProperty )
    {
      if ( beanProperty.propertyName != null )
      {
        this.propertyName = beanProperty.propertyName;
      }
      if ( beanProperty.getterMethodName != null )
      {
        this.getterMethodName = beanProperty.getterMethodName;
      }
      if ( beanProperty.setterMethodName != null )
      {
        this.setterMethodName = beanProperty.setterMethodName;
      }
    }
    
    public String getPropertyName()
    {
      return propertyName;
    }
    
    public void setPropertyName( String name )
    {
      this.propertyName = name;
    }
    
    public String getGetterMethodName()
    {
      return getterMethodName;
    }
    
    public void setGetterMethodName( String getterMethodName )
    {
      this.getterMethodName = getterMethodName;
    }
    
    public String getSetterMethodName()
    {
      return setterMethodName;
    }
    
    public void setSetterMethodName( String setterMethodName )
    {
      this.setterMethodName = setterMethodName;
    }
    
  }
  
  /**
   * Determines the property names of all properties which are declared with the given annotation.
   * 
   * @see BeanUtil#determineBeanPropertiesForAnnotations(Class, String...)
   * @param beanClass
   * @param annotationClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static String[] determineBeanPropertiesForAnnotations( Class<?> beanClass,
                                                                Class<? extends Annotation>... annotationClasses )
  {
    String[] annotationNames = (String[]) ArrayUtil.convertArrayExcludingNullElements( (Class<Annotation>[]) annotationClasses,
                                                                                       new String[0],
                                                                                       new ElementConverterClassAnnotationToString() );
    return BeanUtil.determineBeanPropertiesForAnnotations( beanClass, annotationNames );
  }
  
  private static class ElementConverterClassAnnotationToString implements ElementConverter<Class<Annotation>, String>
  {
    @Override
    public String convert( Class<Annotation> annotationClass )
    {
      return annotationClass == null ? null : Pattern.quote( annotationClass.getName() );
    }
  }
  
  /**
   * @see #determineBeanPropertiesForAnnotations(Class, Class)
   * @param beanClass
   * @param annotationName
   * @return
   */
  public static String[] determineBeanPropertiesForAnnotations( Class<?> beanClass, String... annotationNames )
  {
    return beanClass == null || annotationNames == null ? null
                                                       : BeanUtil.determineBeanProperties( beanClass,
                                                                                           PropertyCriterion.annotationname( annotationNames ),
                                                                                           PropertyCriterion.hasGetterOrSetter() );
  }
  
  /**
   * @see BeanUtil#determineBeanProperties(Class, PropertyCriterion...)
   * @param beanObject
   * @param propertyCriteria
   * @return
   */
  public static String[] determineBeanProperties( Object beanObject, PropertyCriterion... propertyCriteria )
  {
    return beanObject == null ? null : BeanUtil.determineBeanProperties( beanObject.getClass(), propertyCriteria );
  }
  
  /**
   * Determines the property names of a java bean object class. To precise the search criteria can be used. The criteria will be
   * combined by an AND conjunction. Only properties which defines public methods will be included.
   * 
   * @see PropertyCriterion
   */
  public static String[] determineBeanProperties( Class<?> beanClass, PropertyCriterion... propertyCriteria )
  {
    //
    List<String> retlist = new ArrayList<String>();
    
    //
    if ( beanClass != null )
    {
      //
      PropertyCriterion propertyCriterion = PropertyCriterion.and( propertyCriteria );
      
      //
      propertyCriterion.prepareInspection( beanClass );
      
      //
      Method[] declaredMethods = beanClass.getDeclaredMethods();
      for ( Method iMethod : declaredMethods )
      {
        if ( Modifier.isPublic( iMethod.getModifiers() ) && propertyCriterion.inspectMethod( iMethod ) )
        {
          String propertyName = BeanUtil.determinePropertyNameForMethodname( iMethod.getName() );
          if ( propertyName != null )
          {
            if ( !retlist.contains( propertyName ) )
            {
              retlist.add( propertyName );
            }
          }
        }
      }
    }
    
    //
    return retlist.toArray( new String[retlist.size()] );
  }
  
  /**
   * Determines the property name of a getter, setter or isGetter method name.
   * 
   * @param methodName
   * @return
   */
  protected static String determinePropertyNameForMethodname( String methodName )
  {
    //
    String retval = null;
    
    //
    if ( methodName != null )
    {
      
      for ( String prefix : BeanUtil.PROPERTY_PREFIXES )
      {
        if ( methodName.startsWith( prefix ) )
        {
          retval = StringUtils.removeStart( methodName, prefix );
          if ( retval.length() > 0 )
          {
            retval = retval.substring( 0, 1 ).toLowerCase() + retval.substring( 1 );
          }
          break;
        }
      }
    }
    
    //
    return retval;
  }
  
  public static BeanProperty[] determineBeanProperties( Object beanObject )
  {
    return BeanUtil.determineBeanProperties( beanObject.getClass() );
  }
  
  public static BeanProperty[] determineBeanProperties( Class<?> beanClass )
  {
    //
    BeanProperty[] retval = null;
    
    //names of methods
    List<String> declaredMethodNameList = new ArrayList<String>( 0 );
    Method[] declaredMethods = beanClass.getDeclaredMethods();
    for ( Method iMethod : declaredMethods )
    {
      if ( Modifier.isPublic( iMethod.getModifiers() ) ) //public ?
      {
        declaredMethodNameList.add( iMethod.getName() );
      }
    }
    
    //filter method names not having getters and setters
    Map<String, BeanProperty> beanPropertyMap = new LinkedHashMap<String, BeanProperty>( 0 );
    List<BeanProperty> beanPropertyList = new ArrayList<BeanProperty>();
    for ( String iMethodName : declaredMethodNameList )
    {
      BeanProperty beanProperty = BeanUtil.determineEstimatedBeanPropertyForMethod( iMethodName );
      if ( beanProperty != null )
      {
        String beanPropertyName = beanProperty.getPropertyName();
        if ( beanPropertyMap.containsKey( beanPropertyName ) )
        {
          beanPropertyMap.get( beanPropertyName ).assimilate( beanProperty );
        }
        else
        {
          beanPropertyMap.put( beanProperty.getPropertyName(), beanProperty );
          beanPropertyList.add( beanProperty );
        }
      }
    }
    retval = beanPropertyList.toArray( new BeanProperty[0] );
    
    //
    return retval;
  }
  
  /**
   * Returns the estimated property name for methods beginning with get, set or is. If the methodName is not beginning with one of
   * these prefixes, null is returned.
   * 
   * @param methodName
   * @return
   */
  private static BeanProperty determineEstimatedBeanPropertyForMethod( String methodName )
  {
    BeanProperty retval = null;
    if ( methodName != null )
    {
      if ( methodName.startsWith( "is" ) )
      {
        retval = new BeanProperty();
        retval.setGetterMethodName( methodName );
        retval.setPropertyName( methodName.substring( 2, 3 ).toLowerCase() + methodName.substring( 3 ) );
      }
      else if ( methodName.startsWith( "get" ) )
      {
        retval = new BeanProperty();
        retval.setGetterMethodName( methodName );
        retval.setPropertyName( methodName.substring( 3, 4 ).toLowerCase() + methodName.substring( 4 ) );
      }
      else if ( methodName.startsWith( "set" ) )
      {
        retval = new BeanProperty();
        retval.setSetterMethodName( methodName );
        retval.setPropertyName( methodName.substring( 3, 4 ).toLowerCase() + methodName.substring( 4 ) );
      }
    }
    return retval;
  }
  
  /**
   * Executes the method of the given javaBean object that has the given name.
   * 
   * @param <P>
   * @param javaBeanObject
   * @param methodName
   * @param parameter
   *          null: no parameter is assumed for the method, not null: parameter like for setter method
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <P extends Object> P invokeJavaBeanPropertyMethod( Object javaBeanObject, String methodName, P parameter )
  {
    //
    P retval = null;
    
    //
    if ( javaBeanObject != null )
    {
      try
      {
        
        if ( parameter != null )
        {
          Method method = javaBeanObject.getClass().getMethod( methodName, parameter.getClass() );
          retval = (P) method.invoke( javaBeanObject, parameter );
        }
        else
        {
          Method method = javaBeanObject.getClass().getMethod( methodName, (Class<?>[]) null );
          retval = (P) method.invoke( javaBeanObject );
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
   * Defines a interface for a criterion to retrieve property names of an object. This root class defines static methods which
   * return criterias for the most common cases.
   * 
   * @see BeanUtil#determineBeanProperties(Class, PropertyCriterion...)
   * @see #prepareInspection(Object)
   * @see #inspectMethod(Method)
   * @author Omnaest
   */
  public static abstract class PropertyCriterion
  {
    /**
     * This method will be called once before the inspection process runs. This can be used to collect needed information.
     * 
     * @param object
     */
    public abstract void prepareInspection( Class<?> clazz );
    
    /**
     * This method is called for every declared method. The method should return true if the method matches the criteria.
     * 
     * @param method
     * @return
     */
    public abstract boolean inspectMethod( Method method );
    
    /**
     * @see PropertyCriterionConjunctionAND
     * @param propertyCriteria
     * @return
     */
    public static PropertyCriterion and( PropertyCriterion... propertyCriteria )
    {
      return new PropertyCriterionConjunctionAND().addPropertyCriteria( propertyCriteria );
    }
    
    /**
     * @see PropertyCriterionConjunctionOR
     * @param propertyCriteria
     * @return
     */
    public static PropertyCriterion or( PropertyCriterion... propertyCriteria )
    {
      return new PropertyCriterionConjunctionOR().addPropertyCriteria( propertyCriteria );
    }
    
    /**
     * @see PropertyCriterionMethodname
     * @param methodName
     * @return
     */
    public static PropertyCriterion methodname( String methodName )
    {
      return new PropertyCriterionMethodname().setMethodName( methodName );
    }
    
    /**
     * @see PropertyCriterionAnnotationname
     * @param annotationName
     * @return
     */
    public static PropertyCriterion annotationname( String... annotationNames )
    {
      return new PropertyCriterionAnnotationname().addAnnotationNames( annotationNames );
    }
    
    /**
     * @see PropertyCriterionHasGetterSetter
     * @return
     */
    public static PropertyCriterion hasGetter()
    {
      return new PropertyCriterionHasGetterSetter().setTestForGetter( true ).setTestForSetter( false );
    }
    
    /**
     * @see PropertyCriterionHasGetterSetter
     * @return
     */
    public static PropertyCriterion hasSetter()
    {
      return new PropertyCriterionHasGetterSetter().setTestForGetter( false ).setTestForSetter( true );
    }
    
    /**
     * @see PropertyCriterionHasGetterSetter
     * @return
     */
    public static PropertyCriterion hasGetterAndSetter()
    {
      return new PropertyCriterionHasGetterSetter().setTestForGetter( true ).setTestForSetter( true );
    }
    
    /**
     * @see PropertyCriterionHasGetterSetter
     * @return
     */
    public static PropertyCriterion hasGetterOrSetter()
    {
      return PropertyCriterion.or( PropertyCriterion.hasGetter(), PropertyCriterion.hasSetter() );
    }
    
  }
  
  /**
   * Matches the name of a method using regular expression.
   * 
   * @author Omnaest
   */
  public static class PropertyCriterionMethodname extends PropertyCriterion
  {
    protected String methodName = null;
    
    public PropertyCriterion setMethodName( String methodName )
    {
      this.methodName = methodName;
      return this;
    }
    
    @Override
    public boolean inspectMethod( Method method )
    {
      //
      boolean retval = false;
      
      //
      if ( method != null && method.getName() != null && this.methodName != null )
      {
        String propertyName = BeanUtil.determinePropertyNameForMethodname( method.getName() );
        retval = propertyName != null && propertyName.matches( this.methodName );
      }
      
      //
      return retval;
    }
    
    @Override
    public void prepareInspection( Class<?> clazz )
    {
    }
    
    public String getMethodName()
    {
      return this.methodName;
    }
    
  }
  
  /**
   * Matches the name of an annotation of a property method, or the property with the same name as its property method, using
   * regular expression. It is important that the given annotation is available on runtime. Not all annotations are.
   * 
   * @see Retention
   * @author Omnaest
   */
  public static class PropertyCriterionAnnotationname extends PropertyCriterion
  {
    protected List<String> annotationNameList = new ArrayList<String>();
    
    private Class<?>       methodClass        = null;
    
    @Override
    public boolean inspectMethod( Method method )
    {
      //
      boolean retval = false;
      
      //
      if ( method != null && this.annotationNameList.size() > 0 )
      {
        Annotation[] annotations = method.getDeclaredAnnotations();
        for ( Annotation iAnnotation : annotations )
        {
          String annotationName = iAnnotation.annotationType().getName();
          for ( String iTestedAnnotationName : this.annotationNameList )
          {
            //
            retval |= annotationName.matches( iTestedAnnotationName );
            
            //
            if ( retval )
            {
              break;
            }
          }
          
          //
          if ( retval )
          {
            break;
          }
        }
      }
      
      if ( !retval && this.methodClass != null )
      {
        String propertyName = BeanUtil.determinePropertyNameForMethodname( method.getName() );
        try
        {
          Field field = this.methodClass.getDeclaredField( propertyName );
          for ( Annotation iAnnotation : field.getDeclaredAnnotations() )
          {
            //
            String annotationName = iAnnotation.annotationType().getName();
            for ( String iTestedAnnotationName : this.annotationNameList )
            {
              //
              retval |= annotationName.matches( iTestedAnnotationName );
              
              //
              if ( retval )
              {
                break;
              }
            }
            
            //
            if ( retval )
            {
              break;
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
    
    @SuppressWarnings("unchecked")
    public PropertyCriterionAnnotationname addAnnotationNames( String[] annotationNames )
    {
      //
      if ( annotationNames != null )
      {
        this.annotationNameList.addAll( Arrays.asList( annotationNames ) );
      }
      
      //
      return this;
    }
    
    @Override
    public void prepareInspection( Class<?> clazz )
    {
      this.methodClass = clazz;
    }
    
  }
  
  /**
   * Base class for other cunjunction criteria.
   * 
   * @author Omnaest
   */
  public static abstract class PropertyCriterionConjunction extends PropertyCriterion
  {
    protected List<PropertyCriterion> propertyCriterionList = new ArrayList<PropertyCriterion>();
    
    @SuppressWarnings("unchecked")
    public PropertyCriterionConjunction addPropertyCriteria( PropertyCriterion... propertyCriteria )
    {
      //
      if ( propertyCriteria != null )
      {
        this.propertyCriterionList.addAll( Arrays.asList( propertyCriteria ) );
      }
      
      //
      return this;
    }
    
    @Override
    public void prepareInspection( Class<?> clazz )
    {
      for ( PropertyCriterion iPropertyCriterion : this.propertyCriterionList )
      {
        iPropertyCriterion.prepareInspection( clazz );
      }
    }
  }
  
  /**
   * Conjunction of sub criteria. The results of the criteria have to match all.
   * 
   * @author Omnaest
   */
  public static class PropertyCriterionConjunctionAND extends PropertyCriterionConjunction
  {
    @Override
    public boolean inspectMethod( Method method )
    {
      //
      boolean retval = true;
      
      //
      for ( PropertyCriterion iPropertyCriterion : this.propertyCriterionList )
      {
        retval &= iPropertyCriterion.inspectMethod( method );
      }
      
      //
      return retval;
    }
    
  }
  
  /**
   * Conjunction of sub criteria. From the results of the criteria only one have to match.
   * 
   * @author Omnaest
   */
  public static class PropertyCriterionConjunctionOR extends PropertyCriterionConjunction
  {
    @Override
    public boolean inspectMethod( Method method )
    {
      //
      boolean retval = false;
      
      //
      for ( PropertyCriterion iPropertyCriterion : this.propertyCriterionList )
      {
        retval |= iPropertyCriterion.inspectMethod( method );
      }
      
      //
      return retval;
    }
  }
  
  /**
   * Test a method if it is a getter or / and a setter of a property. Getter and setter methods have to be public.
   * 
   * @author Omnaest
   */
  public static class PropertyCriterionHasGetterSetter extends PropertyCriterion
  {
    private boolean                   testForGetter           = false;
    private boolean                   testForSetter           = false;
    
    private Map<String, List<String>> propertyTypeNameListMap = new HashMap<String, List<String>>();
    
    @Override
    public boolean inspectMethod( Method method )
    {
      boolean retval = method != null;
      if ( method != null )
      {
        String propertyName = StringUtils.defaultString( BeanUtil.determinePropertyNameForMethodname( method.getName() ) );
        boolean hasGetter = this.propertyTypeNameListMap.get( "get" ).contains( propertyName );
        boolean hasIsGetter = this.propertyTypeNameListMap.get( "is" ).contains( propertyName );
        boolean hasSetter = this.propertyTypeNameListMap.get( "set" ).contains( propertyName );
        
        retval &= !this.testForGetter || hasGetter || hasIsGetter;
        retval &= !this.testForSetter || hasSetter;
        retval &= Modifier.isPublic( method.getModifiers() );
      }
      return retval;
    }
    
    @Override
    public void prepareInspection( Class<?> clazz )
    {
      for ( String prefix : BeanUtil.PROPERTY_PREFIXES )
      {
        this.propertyTypeNameListMap.put( prefix, new ArrayList<String>() );
      }
      
      if ( clazz != null )
      {
        //
        for ( Method iMethod : clazz.getDeclaredMethods() )
        {
          if ( Modifier.isPublic( iMethod.getModifiers() ) )
          {
            //
            String methodname = iMethod.getName();
            String propertyName = StringUtils.defaultString( BeanUtil.determinePropertyNameForMethodname( iMethod.getName() ) );
            
            //
            if ( StringUtils.isNotBlank( propertyName ) )
            {
              for ( String prefix : BeanUtil.PROPERTY_PREFIXES )
              {
                if ( methodname.startsWith( prefix ) )
                {
                  this.propertyTypeNameListMap.get( prefix ).add( propertyName );
                }
              }
            }
          }
        }
      }
    }
    
    public boolean isTestForGetter()
    {
      return this.testForGetter;
    }
    
    public PropertyCriterionHasGetterSetter setTestForGetter( boolean testForGetter )
    {
      this.testForGetter = testForGetter;
      return this;
    }
    
    public boolean isTestForSetter()
    {
      return this.testForSetter;
    }
    
    public PropertyCriterionHasGetterSetter setTestForSetter( boolean testForSetter )
    {
      this.testForSetter = testForSetter;
      return this;
    }
    
  }
  
  /**
   * Copies all properties of a target java bean object, which are marked with the CopyableProperty annotation in the source bean
   * class.
   * 
   * @see CopyableProperty
   * @param targetBeanObject
   * @param sourceBeanObject
   */
  @SuppressWarnings("unchecked")
  public static void copyPropertiesCopyable( Object targetBeanObject, Object sourceBeanObject )
  {
    if ( sourceBeanObject != null && targetBeanObject != null )
    {
      Class<?> sourceBeanClass = sourceBeanObject.getClass();
      String[] copyablePropertyNames = BeanUtil.determineBeanPropertiesForAnnotations( sourceBeanClass, CopyableProperty.class );
      BeanUtil.copyProperties( targetBeanObject, sourceBeanObject, copyablePropertyNames );
    }
  }
  
  /**
   * Marker annotation. All java bean property elements which have this annotation declared, can be copied by
   * {@link BeanUtil#copyPropertiesCopyable(Object, Object)}
   * 
   * @see BeanUtil
   * @see BeanUtil#copyPropertiesCopyable(Object, Object)
   * @author Omnaest
   */
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface CopyableProperty
  {
    
  }
}
