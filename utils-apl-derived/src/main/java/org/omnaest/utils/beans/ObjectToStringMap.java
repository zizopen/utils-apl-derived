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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObjectToStringMap extends LinkedHashMap<String, String>
{
  //
  private static final long serialVersionUID = 3839815039827467247L;
  
  /*
   * Methods
   */
  public ObjectToStringMap()
  {
    this( 0 );
  }
  
  public ObjectToStringMap( int initialSize )
  {
    super( initialSize );
  }
  
  /**
   * Creates a new Map with the property names and values of the given object parsed into the map.
   * 
   * @see #readObject(Object)
   * @param object
   * @return
   */
  public static ObjectToStringMap valueOf( Object object )
  {
    //
    ObjectToStringMap retmap = new ObjectToStringMap().readObject( object );
    
    //
    return retmap;
  }
  
  /**
   * Parses all properties from this object, and if String.valueOf is usable on a field, the field will be parsed to a string
   * within a map. Key will be the fields name.<br>
   * Important is, that the propertyfield must have a respective gettermethod.
   * 
   * @param object
   */
  public ObjectToStringMap readObject( Object object )
  {
    //get the fieldnames and methodnames
    List<String> declaredFieldNameList = new ArrayList<String>( 0 );
    Field[] declaredFields = object.getClass().getDeclaredFields();
    for ( Field iField : declaredFields )
    {
      declaredFieldNameList.add( iField.getName() );
    }
    
    List<String> declaredMethodNameList = new ArrayList<String>( 0 );
    Method[] declaredMethods = object.getClass().getDeclaredMethods();
    for ( Method iMethod : declaredMethods )
    {
      declaredMethodNameList.add( iMethod.getName() );
    }
    
    //look which fields are bean properties, which means have they getters?
    List<String> beanPropertyGetterNameList = new ArrayList<String>( 0 );
    for ( String iMethodName : declaredMethodNameList )
    {
      if ( declaredFieldNameList.contains( ObjectToStringMap.determineEstimatedPropertyNameForMethod( iMethodName ) ) )
      {
        beanPropertyGetterNameList.add( iMethodName );
      }
    }
    
    //try to get string values from the beanmethod
    for ( String iPropertyGetterName : beanPropertyGetterNameList )
    {
      try
      {
        Method getterMethod = object.getClass().getDeclaredMethod( iPropertyGetterName );
        Object getterReturnValue = getterMethod.invoke( object );
        String value = String.valueOf( getterReturnValue );
        String key = ObjectToStringMap.determineEstimatedPropertyNameForMethod( iPropertyGetterName );
        if ( value != null )
        {
          this.put( key, value );
        }
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return this;
  }
  
  /**
   * Writes the values of the given map into the object. To do this the given object have to have setters for the propertynames
   * stored within the map. And the properties must have .valueOf(String) methods.
   * 
   * @param object
   * @return
   */
  public ObjectToStringMap writeObject( Object object )
  {
    //get the declared methods names
    Map<String, Method> declaredMethodNameMap = new HashMap<String, Method>( 0 );
    Method[] declaredMethods = object.getClass().getDeclaredMethods();
    for ( Method iMethod : declaredMethods )
    {
      String methodName = iMethod.getName();
      Class<?>[] parameterTypes = iMethod.getParameterTypes();
      
      if ( methodName.startsWith( "set" ) && parameterTypes.length == 1 )
      {
        declaredMethodNameMap.put( methodName, iMethod );
      }
    }
    
    //go through all this methods and look, if there is a respective key within this map.
    for ( String iMethodName : declaredMethodNameMap.keySet() )
    {
      String key = ObjectToStringMap.determineEstimatedPropertyNameForMethod( iMethodName );
      if ( this.containsKey( key ) )
      {
        String value = this.get( key );
        Method setterMethod = null;
        Class<?>[] parameterTypes = null;
        try
        {
          setterMethod = declaredMethodNameMap.get( iMethodName );
          parameterTypes = setterMethod.getParameterTypes();
        }
        catch ( Exception e )
        {
        }
        
        if ( setterMethod != null && parameterTypes != null && parameterTypes.length == 1 )
        {
          Class<?> parameterType = parameterTypes[0];
          Method parameterValueOfMethod = null;
          
          //we need the value as object to pass it to the setter
          Object valueObject = null;
          
          //if the setter do not want to have a string value, we have to find the valueOf method of the class of the type that is to be parsed.
          if ( parameterType.getName().equals( "java.lang.String" ) )
          {
            valueObject = value;
          }
          else
          {
            
            //try to get the valueOf(String) method of the unknown parameterType
            // if the type is primitive, get the wrapper first
            try
            {
              if ( !parameterType.isPrimitive() )
              {
                parameterValueOfMethod = parameterType.getDeclaredMethod( "valueOf", String.class );
              }
              else
              {
                Class<?> estimatedWrapperClass = this.determineEstimatedWrapperClass( parameterType.getName() );
                if ( estimatedWrapperClass != null )
                {
                  parameterValueOfMethod = estimatedWrapperClass.getDeclaredMethod( "valueOf", String.class );
                }
                
              }
            }
            catch ( Exception e )
            {
            }
            
            //now convert the string value into the object with the valueof function
            if ( parameterValueOfMethod != null )
            {
              try
              {
                valueObject = (Object) parameterValueOfMethod.invoke( null, value );
              }
              catch ( Exception e )
              {
              }
            }
          }
          
          //try to invoke the setter method
          try
          {
            if ( valueObject != null )
            {
              
              setterMethod.invoke( object, valueObject );
            }
          }
          catch ( Exception e )
          {
          }
        }
      }
    }
    //
    return this;
  }
  
  private Class<?> determineEstimatedWrapperClass( String typeFullQualifiedName )
  {
    //
    Class<?> retval = null;
    
    //
    if ( typeFullQualifiedName != null )
    {
      typeFullQualifiedName = typeFullQualifiedName.replace( "int", "integer" ).replace( "char", "character" );
      String newTypeFullQualifiedName = "java.lang." + typeFullQualifiedName.substring( 0, 1 ).toUpperCase()
                                        + typeFullQualifiedName.substring( 1 );
      try
      {
        retval = Class.forName( newTypeFullQualifiedName );
      }
      catch ( ClassNotFoundException e )
      {
      }
      
    }
    
    //
    return retval;
  }
  
  private static String determineEstimatedPropertyNameForMethod( String methodName )
  {
    String retval = null;
    if ( methodName != null )
    {
      if ( methodName.startsWith( "is" ) )
      {
        retval = methodName.substring( 2, 3 ).toLowerCase() + methodName.substring( 3 );
      }
      else if ( methodName.startsWith( "get" ) || methodName.startsWith( "set" ) )
      {
        retval = methodName.substring( 3, 4 ).toLowerCase() + methodName.substring( 4 );
      }
    }
    return retval;
  }
  
}
