package org.omnaest.utils.beans;
import java.lang.reflect.Method;

/**
 * Helper for method names of java beans.
 * 
 * @author Omnaest
 */
public class BeanMethodNameUtils
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * Information object for methods of bean classes.
   * 
   * @author Omnaest
   */
  public static class BeanMethodInformation
  {
    /* ********************************************** Variables ********************************************** */
    protected boolean isGetter            = false;
    protected boolean isSetter            = false;
    protected String  referencedFieldName = null;
    
    /* ********************************************** Methods ********************************************** */
    protected BeanMethodInformation( boolean isGetter, boolean isSetter, String referencedFieldName )
    {
      super();
      this.isGetter = isGetter;
      this.isSetter = isSetter;
      this.referencedFieldName = referencedFieldName;
    }
    
    /**
     * Returns true, if the method has no parameters but a return type.
     * 
     * @return
     */
    public boolean isGetter()
    {
      return this.isGetter;
    }
    
    /**
     * Is true, if a method has only one parameter and begins with "set". A return type is optional.
     * 
     * @return
     */
    public boolean isSetter()
    {
      return this.isSetter;
    }
    
    public String getReferencedFieldName()
    {
      return this.referencedFieldName;
    }
    
  }
  
  /* ********************************************** Methods ********************************************** */

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
        retval = new BeanMethodInformation( isGetter, isSetter, referencedFieldName );
        
      }
      catch ( Exception e )
      {
      }
    }
    
    //
    return retval;
  }
  
}
