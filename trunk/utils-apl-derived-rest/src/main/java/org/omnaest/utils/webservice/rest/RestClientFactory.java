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
package org.omnaest.utils.webservice.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.omnaest.utils.cache.Cache;
import org.omnaest.utils.cache.CacheUtils;
import org.omnaest.utils.download.URIHelper;
import org.omnaest.utils.proxy.StubCreator;
import org.omnaest.utils.proxy.handler.MethodCallCapture;
import org.omnaest.utils.proxy.handler.MethodInvocationHandler;
import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.reflection.ReflectionUtils.MethodParameterMetaInformation;
import org.omnaest.utils.structure.element.factory.Factory;

/**
 * The {@link RestClientFactory} allows to produce proxies for given class or interface types which are based on the <a
 * href="http://jsr311.java.net/">JSR-311 API (REST)</a><br>
 * 
 * @see #newRestClient(Class)
 * @author Omnaest
 */
public abstract class RestClientFactory
{
  /* ********************************************** Variables ********************************************** */
  private URI                                  baseAddress                          = null;
  private RestInterfaceMethodInvocationHandler restInterfaceMethodInvocationHandler = null;
  private Cache<Types, StubCreator<Object>>    stubCreatorCache                     = CacheUtils.adapter( new ConcurrentHashMap<Types, StubCreator<Object>>() );
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  private static class Types
  {
    private List<Class<?>> typeList = new ArrayList<Class<?>>();
    
    public Types( Class<?> type, Class<?>... types )
    {
      super();
      this.typeList.add( type );
      for ( Class<?> itype : types )
      {
        this.typeList.add( itype );
      }
    }
    
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.typeList == null ) ? 0 : this.typeList.hashCode() );
      return result;
    }
    
    @Override
    public boolean equals( Object obj )
    {
      if ( this == obj )
      {
        return true;
      }
      if ( obj == null )
      {
        return false;
      }
      if ( !( obj instanceof Types ) )
      {
        return false;
      }
      Types other = (Types) obj;
      if ( this.typeList == null )
      {
        if ( other.typeList != null )
        {
          return false;
        }
      }
      else if ( !this.typeList.equals( other.typeList ) )
      {
        return false;
      }
      return true;
    }
    
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "Types [typeList=" );
      builder.append( this.typeList );
      builder.append( "]" );
      return builder.toString();
    }
    
  }
  
  /**
   * Handler for {@link Method} invocations on the generated proxy for a JSR-311 type
   * 
   * @author Omnaest
   */
  public static interface RestInterfaceMethodInvocationHandler
  {
    /**
     * @see HttpMethod
     * @see Parameter
     * @param baseAddress
     * @param pathRelative
     * @param httpMethod
     * @param parameterList
     * @param returnType
     * @param consumesMediaTypes
     *          media types in order method declaration, type declaration
     * @param producesMediaTypes
     *          media types in order method declaration, type declaration
     * @return
     */
    public <T> T handleMethodInvocation( URI baseAddress,
                                         String pathRelative,
                                         HttpMethod httpMethod,
                                         List<Parameter> parameterList,
                                         Class<T> returnType,
                                         String[] consumesMediaTypes,
                                         String[] producesMediaTypes );
  }
  
  /**
   * Representation of a method parameter. See the derived types.
   * 
   * @see BodyParameter
   * @see QueryParameter
   * @see MatrixParameter
   * @author Omnaest
   */
  public static interface Parameter
  {
  }
  
  /**
   * Representation of a {@link Parameter} without any annotation
   * 
   * @author Omnaest
   */
  public static class BodyParameter implements Parameter
  {
    /* ********************************************** Variables ********************************************** */
    private Object value = null;
    
    /* ********************************************** Methods ********************************************** */
    public BodyParameter( Object value )
    {
      super();
      this.value = value;
    }
    
    public Object getValue()
    {
      return this.value;
    }
    
  }
  
  /**
   * Representation of a {@link QueryParam} annotated method parameter
   * 
   * @author Omnaest
   */
  public static class QueryParameter implements Parameter
  {
    /* ********************************************** Variables ********************************************** */
    private String key   = null;
    private String value = null;
    
    /* ********************************************** Methods ********************************************** */
    public String getKey()
    {
      return this.key;
    }
    
    public String getValue()
    {
      return this.value;
    }
    
    public QueryParameter( String key, String value )
    {
      super();
      this.key = key;
      this.value = value;
    }
    
  }
  
  /**
   * Representation of a {@link MatrixParam} annotated method parameter
   * 
   * @author Omnaest
   */
  public static class MatrixParameter implements Parameter
  {
    /* ********************************************** Variables ********************************************** */
    private String             key             = null;
    private Collection<Object> valueCollection = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public MatrixParameter( String key, Collection<Object> valueCollection )
    {
      super();
      this.key = key;
      this.valueCollection = valueCollection;
    }
    
    public String getKey()
    {
      return this.key;
    }
    
    public Collection<Object> getValueCollection()
    {
      return this.valueCollection;
    }
    
  }
  
  /**
   * The {@link RestInterfaceMetaInformation} will hold meta information related to the annotations used by the JSR-311 API
   * 
   * @author Omnaest
   */
  protected static class RestInterfaceMetaInformation
  {
    /* ********************************************** Variables ********************************************** */
    protected RestInterfaceMetaInformationForClass               restInterfaceMetaInformationForClass             = null;
    protected Map<Method, RestInterfaceMetaInformationForMethod> methodToRestInterfaceMetaInformationForMethodMap = new HashMap<Method, RestInterfaceMetaInformationForMethod>();
    
    /* ********************************************** Methods ********************************************** */
    
    public RestInterfaceMetaInformationForClass getRestInterfaceMetaInformationForClass()
    {
      return this.restInterfaceMetaInformationForClass;
    }
    
    public void setRestInterfaceMetaInformationForClass( RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass )
    {
      this.restInterfaceMetaInformationForClass = restInterfaceMetaInformationForClass;
    }
    
    public Map<Method, RestInterfaceMetaInformationForMethod> getMethodToRestInterfaceMetaInformationForMethodMap()
    {
      return this.methodToRestInterfaceMetaInformationForMethodMap;
    }
    
  }
  
  protected static class RestInterfaceMetaInformationForClass
  {
    /* ********************************************** Variables ********************************************** */
    protected Class<?>     type                  = null;
    protected String       path                  = null;
    protected List<String> mediaTypeProducesList = new ArrayList<String>();
    protected List<String> mediaTypeConsumesList = new ArrayList<String>();
    
    /* ********************************************** Methods ********************************************** */
    
    public String getPath()
    {
      return this.path;
    }
    
    public void setPath( String path )
    {
      this.path = path;
    }
    
    public List<String> getMediaTypeProducesList()
    {
      return this.mediaTypeProducesList;
    }
    
    public List<String> getMediaTypeConsumesList()
    {
      return this.mediaTypeConsumesList;
    }
    
    public void setType( Class<?> type )
    {
      this.type = type;
    }
    
    public Class<?> getType()
    {
      return this.type;
    }
    
  }
  
  /**
   * @author Omnaest
   */
  public static enum HttpMethod
  {
    GET,
    PUT,
    POST,
    DELETE
  }
  
  protected static class RestInterfaceMetaInformationForMethod extends RestInterfaceMetaInformationForClass
  {
    /* ********************************************** Variables ********************************************** */
    protected Method                     method                                        = null;
    protected HttpMethod                 httpMethod                                    = null;
    protected SortedMap<Integer, String> parameterIndexPositionToQueryParameterTagMap  = new TreeMap<Integer, String>();
    protected SortedMap<Integer, String> parameterIndexPositionToPathParameterTagMap   = new TreeMap<Integer, String>();
    protected SortedMap<Integer, String> parameterIndexPositionToMatrixParameterTagMap = new TreeMap<Integer, String>();
    
    /* ********************************************** Methods ********************************************** */
    
    public HttpMethod getHttpMethod()
    {
      return this.httpMethod;
    }
    
    public void setHttpMethod( HttpMethod httpMethod )
    {
      this.httpMethod = httpMethod;
    }
    
    public SortedMap<Integer, String> getParameterIndexPositionToQueryParameterTagMap()
    {
      return this.parameterIndexPositionToQueryParameterTagMap;
    }
    
    public SortedMap<Integer, String> getParameterIndexPositionToPathParameterTagMap()
    {
      return this.parameterIndexPositionToPathParameterTagMap;
    }
    
    public SortedMap<Integer, String> getParameterIndexPositionToMatrixParameterTagMap()
    {
      return this.parameterIndexPositionToMatrixParameterTagMap;
    }
    
    public Method getMethod()
    {
      return this.method;
    }
    
    public void setMethod( Method method )
    {
      this.method = method;
    }
    
  }
  
  protected class RestClientMethodInvocationHandler implements MethodInvocationHandler
  {
    /* ********************************************** Variables ********************************************** */
    protected RestInterfaceMetaInformation restInterfaceMetaInformation = null;
    @SuppressWarnings("hiding")
    protected URI                          baseAddress                  = null;
    
    /* ********************************************** Methods ********************************************** */
    
    public RestClientMethodInvocationHandler( RestInterfaceMetaInformation restInterfaceMetaInformation, URI baseAddress )
    {
      super();
      this.restInterfaceMetaInformation = restInterfaceMetaInformation;
      this.baseAddress = baseAddress;
    }
    
    @Override
    public Object handle( MethodCallCapture methodCallCapture ) throws Throwable
    {
      //
      Object retval = null;
      
      //
      Class<?> returnType = methodCallCapture.getMethod().getReturnType();
      boolean hasSubResourceReturnType = ReflectionUtils.hasDeclaredAnnotation( returnType, Path.class );
      
      //
      final URI baseAddress = this.baseAddress;
      final Method method = methodCallCapture.getMethod();
      final RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass = this.restInterfaceMetaInformation.getRestInterfaceMetaInformationForClass();
      final RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod = this.restInterfaceMetaInformation.getMethodToRestInterfaceMetaInformationForMethodMap()
                                                                                                                           .get( method );
      final Object[] arguments = methodCallCapture.getArguments();
      final String relativePath = this.buildRelativePath( restInterfaceMetaInformationForClass,
                                                          restInterfaceMetaInformationForMethod, arguments );
      
      //
      if ( hasSubResourceReturnType )
      {
        //
        URI newBaseAddress = URIHelper.createUri( baseAddress, relativePath );
        retval = newRestClient( returnType, newBaseAddress );
      }
      else
      {
        //
        final RestInterfaceMethodInvocationHandler restInterfaceMethodInvocationHandler = RestClientFactory.this.restInterfaceMethodInvocationHandler;
        if ( restInterfaceMethodInvocationHandler != null )
        {
          
          //          
          final List<Parameter> parameterList = this.buildParamterList( restInterfaceMetaInformationForClass,
                                                                        restInterfaceMetaInformationForMethod, arguments );
          final HttpMethod httpMethod = restInterfaceMetaInformationForMethod.getHttpMethod();
          final String[] consumesMediaTypes = this.determineConsumesMediaTypes( restInterfaceMetaInformationForClass,
                                                                                restInterfaceMetaInformationForMethod );
          final String[] producesMediaTypes = this.determineProducesMediaTypes( restInterfaceMetaInformationForClass,
                                                                                restInterfaceMetaInformationForMethod );
          if ( relativePath != null && httpMethod != null && parameterList != null )
          {
            retval = restInterfaceMethodInvocationHandler.handleMethodInvocation( baseAddress, relativePath, httpMethod,
                                                                                  parameterList, returnType, consumesMediaTypes,
                                                                                  producesMediaTypes );
          }
        }
      }
      
      //
      return retval;
    }
    
    protected String[] determineConsumesMediaTypes( RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass,
                                                    RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod )
    {
      //
      List<String> mediaTypeConsumesList = new ArrayList<String>();
      mediaTypeConsumesList.addAll( restInterfaceMetaInformationForMethod.getMediaTypeConsumesList() );
      mediaTypeConsumesList.addAll( restInterfaceMetaInformationForClass.getMediaTypeConsumesList() );
      
      //
      return new LinkedHashSet<String>( mediaTypeConsumesList ).toArray( new String[0] );
    }
    
    protected String[] determineProducesMediaTypes( RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass,
                                                    RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod )
    {
      //
      List<String> mediaTypeProducesList = new ArrayList<String>();
      mediaTypeProducesList.addAll( restInterfaceMetaInformationForMethod.getMediaTypeProducesList() );
      mediaTypeProducesList.addAll( restInterfaceMetaInformationForClass.getMediaTypeProducesList() );
      
      //
      return new LinkedHashSet<String>( mediaTypeProducesList ).toArray( new String[0] );
    }
    
    protected List<Parameter> buildParamterList( RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass,
                                                 RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod,
                                                 Object[] arguments )
    {
      //      
      List<Parameter> retlist = new ArrayList<Parameter>();
      
      //
      {
        //
        SortedMap<Integer, String> parameterIndexPositionToQueryParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToQueryParameterTagMap();
        for ( Integer parameterIndexPosition : parameterIndexPositionToQueryParameterTagMap.keySet() )
        {
          if ( parameterIndexPosition != null && parameterIndexPosition < arguments.length )
          {
            //
            String key = parameterIndexPositionToQueryParameterTagMap.get( parameterIndexPosition );
            Object value = arguments[parameterIndexPosition];
            
            //
            try
            {
              retlist.add( new QueryParameter( key, String.valueOf( value ) ) );
            }
            catch ( Exception e )
            {
            }
          }
        }
      }
      
      //
      {
        //
        SortedMap<Integer, String> parameterIndexPositionToMatrixParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToMatrixParameterTagMap();
        for ( Integer parameterIndexPosition : parameterIndexPositionToMatrixParameterTagMap.keySet() )
        {
          if ( parameterIndexPosition != null && parameterIndexPosition < arguments.length )
          {
            //
            String key = parameterIndexPositionToMatrixParameterTagMap.get( parameterIndexPosition );
            Object value = arguments[parameterIndexPosition];
            
            //
            if ( value instanceof Collection )
            {
              //
              @SuppressWarnings("unchecked")
              Collection<Object> valueCollection = (Collection<Object>) value;
              
              //
              retlist.add( new MatrixParameter( key, valueCollection ) );
            }
          }
        }
      }
      
      //
      {
        //
        SortedMap<Integer, String> parameterIndexPositionToMatrixParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToMatrixParameterTagMap();
        SortedMap<Integer, String> parameterIndexPositionToPathParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToPathParameterTagMap();
        SortedMap<Integer, String> parameterIndexPositionToQueryParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToQueryParameterTagMap();
        for ( int indexPosition = 0; indexPosition < arguments.length; indexPosition++ )
        {
          //
          boolean isMatrixParameter = parameterIndexPositionToMatrixParameterTagMap.containsKey( indexPosition );
          boolean isPathParameter = parameterIndexPositionToPathParameterTagMap.containsKey( indexPosition );
          boolean isQueryParameter = parameterIndexPositionToQueryParameterTagMap.containsKey( indexPosition );
          if ( !isMatrixParameter && !isPathParameter && !isQueryParameter )
          {
            //
            Object value = arguments[indexPosition];
            retlist.add( new BodyParameter( value ) );
          }
        }
      }
      
      //
      return retlist;
    }
    
    protected String buildRelativePath( RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass,
                                        RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod,
                                        Object[] arguments )
    {
      //
      String retval = null;
      
      //
      UriBuilder uriBuilder = UriBuilder.fromResource( restInterfaceMetaInformationForClass.getType() );
      
      //
      Method method = restInterfaceMetaInformationForMethod.getMethod();
      boolean hasDeclaredPathAnnotation = ReflectionUtils.hasDeclaredAnnotation( method, Path.class );
      if ( hasDeclaredPathAnnotation )
      {
        uriBuilder.path( restInterfaceMetaInformationForMethod.getMethod() );
      }
      
      //
      Map<String, Object> pathKeyToValueMap = new LinkedHashMap<String, Object>();
      {
        //
        SortedMap<Integer, String> parameterIndexPositionToPathParameterTagMap = restInterfaceMetaInformationForMethod.getParameterIndexPositionToPathParameterTagMap();
        for ( Integer indexPosition : parameterIndexPositionToPathParameterTagMap.keySet() )
        {
          if ( indexPosition != null && indexPosition < arguments.length )
          {
            //
            String key = parameterIndexPositionToPathParameterTagMap.get( indexPosition );
            Object value = arguments[indexPosition];
            
            //
            pathKeyToValueMap.put( key, value );
          }
        }
      }
      
      //
      URI uri = uriBuilder.buildFromMap( pathKeyToValueMap );
      retval = uri.toString();
      
      //
      return retval;
    }
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @param baseAddress
   * @param restInterfaceMethodInvocationHandler
   */
  public RestClientFactory( String baseAddress, RestInterfaceMethodInvocationHandler restInterfaceMethodInvocationHandler )
  
  {
    super();
    
    //
    try
    {
      this.baseAddress = baseAddress == null ? null : new URI( baseAddress );
    }
    catch ( URISyntaxException e )
    {
    }
    this.restInterfaceMethodInvocationHandler = restInterfaceMethodInvocationHandler;
  }
  
  /**
   * Factory method for new REST client proxy instances.
   * 
   * @param type
   * @return
   */
  public <T> T newRestClient( Class<T> type )
  {
    return newRestClient( type, this.baseAddress );
  }
  
  /**
   * Factory method for new REST client proxy instances.
   * 
   * @param type
   * @param baseAddress
   * @return
   */
  @SuppressWarnings({ "unchecked" })
  public <T> T newRestClient( final Class<T> type, URI baseAddress )
  {
    //
    T retval = null;
    
    //
    if ( type != null )
    {
      //
      final RestInterfaceMetaInformation restInterfaceMetaInformation = analyzeTheRestInterfaceMetaInformation( type );
      final MethodInvocationHandler methodInvocationHandler = new RestClientMethodInvocationHandler(
                                                                                                     restInterfaceMetaInformation,
                                                                                                     baseAddress );
      
      //
      final Class<?>[] interfaces = type.getInterfaces();
      
      //
      retval = (T) this.stubCreatorCache.getOrCreate( new Types( type, interfaces ), new Factory<StubCreator<Object>>()
      {
        @Override
        public StubCreator<Object> newInstance()
        {
          return new StubCreator<Object>( type, interfaces );
        }
      } ).build( methodInvocationHandler );
    }
    
    //
    return retval;
  }
  
  protected static <T> RestInterfaceMetaInformation analyzeTheRestInterfaceMetaInformation( Class<T> type )
  {
    //
    RestInterfaceMetaInformation restInterfaceMetaInformation = new RestInterfaceMetaInformation();
    {
      //
      RestInterfaceMetaInformationForClass restInterfaceMetaInformationForClass = new RestInterfaceMetaInformationForClass();
      {
        //
        restInterfaceMetaInformationForClass.setType( type );
        
        //
        List<Annotation> declaredAnnotationList = ReflectionUtils.declaredAnnotationList( type );
        for ( Annotation annotation : declaredAnnotationList )
        {
          if ( annotation instanceof Path )
          {
            //
            Path path = (Path) annotation;
            String pathValue = path.value();
            restInterfaceMetaInformationForClass.setPath( pathValue );
          }
          else if ( annotation instanceof Produces )
          {
            //
            Produces produces = (Produces) annotation;
            String[] values = produces.value();
            restInterfaceMetaInformationForClass.getMediaTypeProducesList().addAll( Arrays.asList( values ) );
          }
          else if ( annotation instanceof Consumes )
          {
            //
            Consumes consumes = (Consumes) annotation;
            String[] values = consumes.value();
            restInterfaceMetaInformationForClass.getMediaTypeConsumesList().addAll( Arrays.asList( values ) );
          }
        }
      }
      restInterfaceMetaInformation.setRestInterfaceMetaInformationForClass( restInterfaceMetaInformationForClass );
      
      //
      List<Method> declaredMethodList = ReflectionUtils.declaredMethodList( type );
      for ( Method method : declaredMethodList )
      {
        //
        RestInterfaceMetaInformationForMethod restInterfaceMetaInformationForMethod = new RestInterfaceMetaInformationForMethod();
        {
          //
          restInterfaceMetaInformationForMethod.setMethod( method );
          
          //
          {
            //
            Set<Annotation> declaredAnnotationSet = ReflectionUtils.declaredAnnotationSet( method );
            for ( Annotation annotation : declaredAnnotationSet )
            {
              if ( annotation instanceof Path )
              {
                //
                Path path = (Path) annotation;
                String pathValue = path.value();
                restInterfaceMetaInformationForMethod.setPath( pathValue );
              }
              else if ( annotation instanceof GET )
              {
                HttpMethod httpMethod = HttpMethod.GET;
                restInterfaceMetaInformationForMethod.setHttpMethod( httpMethod );
              }
              else if ( annotation instanceof PUT )
              {
                HttpMethod httpMethod = HttpMethod.PUT;
                restInterfaceMetaInformationForMethod.setHttpMethod( httpMethod );
              }
              else if ( annotation instanceof POST )
              {
                HttpMethod httpMethod = HttpMethod.POST;
                restInterfaceMetaInformationForMethod.setHttpMethod( httpMethod );
              }
              else if ( annotation instanceof DELETE )
              {
                HttpMethod httpMethod = HttpMethod.DELETE;
                restInterfaceMetaInformationForMethod.setHttpMethod( httpMethod );
              }
              else if ( annotation instanceof Produces )
              {
                //
                Produces produces = (Produces) annotation;
                String[] values = produces.value();
                restInterfaceMetaInformationForMethod.getMediaTypeProducesList().addAll( Arrays.asList( values ) );
              }
              else if ( annotation instanceof Consumes )
              {
                //
                Consumes consumes = (Consumes) annotation;
                String[] values = consumes.value();
                restInterfaceMetaInformationForMethod.getMediaTypeConsumesList().addAll( Arrays.asList( values ) );
              }
            }
          }
          
          //          
          {
            //
            List<MethodParameterMetaInformation> declaredMethodParameterMetaInformationList = ReflectionUtils.declaredMethodParameterMetaInformationList( method );
            int indexPosition = 0;
            for ( MethodParameterMetaInformation methodParameterMetaInformation : declaredMethodParameterMetaInformationList )
            {
              //
              List<Annotation> declaredAnnotationList = methodParameterMetaInformation.getDeclaredAnnotationList();
              for ( Annotation annotation : declaredAnnotationList )
              {
                if ( annotation instanceof QueryParam )
                {
                  //
                  QueryParam queryParam = (QueryParam) annotation;
                  String value = queryParam.value();
                  restInterfaceMetaInformationForMethod.getParameterIndexPositionToQueryParameterTagMap().put( indexPosition,
                                                                                                               value );
                }
                else if ( annotation instanceof PathParam )
                {
                  //
                  PathParam pathParam = (PathParam) annotation;
                  String value = pathParam.value();
                  restInterfaceMetaInformationForMethod.getParameterIndexPositionToPathParameterTagMap().put( indexPosition,
                                                                                                              value );
                }
                else if ( annotation instanceof MatrixParam )
                {
                  //
                  MatrixParam matrixParam = (MatrixParam) annotation;
                  String value = matrixParam.value();
                  restInterfaceMetaInformationForMethod.getParameterIndexPositionToMatrixParameterTagMap().put( indexPosition,
                                                                                                                value );
                }
              }
              
              //
              indexPosition++;
            }
          }
        }
        
        //
        restInterfaceMetaInformation.getMethodToRestInterfaceMetaInformationForMethodMap()
                                    .put( method, restInterfaceMetaInformationForMethod );
      }
    }
    
    //
    return restInterfaceMetaInformation;
  }
}
