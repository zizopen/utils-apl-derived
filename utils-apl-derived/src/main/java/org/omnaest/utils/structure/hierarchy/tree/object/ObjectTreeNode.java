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
package org.omnaest.utils.structure.hierarchy.tree.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.omnaest.utils.structure.hierarchy.tree.TreeNode;
import org.omnaest.utils.structure.hierarchy.tree.object.ObjectTreeNode.ObjectModel;

/**
 * {@link TreeNode} for {@link Object} graphs
 * 
 * @see ObjectTree
 * @see ObjectModel
 * @author Omnaest
 */
public interface ObjectTreeNode extends TreeNode<ObjectModel>
{
  /**
   * Model of the {@link ObjectTreeNode}
   * 
   * @author Omnaest
   */
  public static class ObjectModel
  {
    /* ********************************************** Variables ********************************************** */
    private final Object object;
    private final Method setterMethod;
    private final Method getterMethod;
    private final Field  field;
    private final String propertyName;
    
    /* ********************************************** Methods ********************************************** */
    
    /**
     * @see ObjectModel
     * @param object
     * @param getterMethod
     * @param setterMethod
     * @param field
     * @param propertyName
     */
    public ObjectModel( Object object, Method getterMethod, Method setterMethod, Field field, String propertyName )
    {
      super();
      this.object = object;
      this.getterMethod = getterMethod;
      this.setterMethod = setterMethod;
      this.field = field;
      this.propertyName = propertyName;
    }
    
    /**
     * @see ObjectModel
     * @param object
     */
    public ObjectModel( Object object )
    {
      super();
      this.object = object;
      this.setterMethod = null;
      this.getterMethod = null;
      this.field = null;
      this.propertyName = null;
    }
    
    /**
     * @return the object
     */
    public Object getObject()
    {
      return this.object;
    }
    
    /**
     * @return the field
     */
    public Field getField()
    {
      return this.field;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append( "ObjectModel [object=" );
      builder.append( this.object );
      builder.append( ", setterMethod=" );
      builder.append( this.setterMethod );
      builder.append( ", getterMethod=" );
      builder.append( this.getterMethod );
      builder.append( ", field=" );
      builder.append( this.field );
      builder.append( ", propertyName=" );
      builder.append( this.propertyName );
      builder.append( "]" );
      return builder.toString();
    }
    
    /**
     * @return the setterMethod
     */
    public Method getSetterMethod()
    {
      return this.setterMethod;
    }
    
    /**
     * @return the getterMethod
     */
    public Method getGetterMethod()
    {
      return this.getterMethod;
    }
    
    /**
     * @return the propertyName
     */
    public String getPropertyName()
    {
      return this.propertyName;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + ( ( this.object == null ) ? 0 : this.object.hashCode() );
      return result;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
      if ( !( obj instanceof ObjectModel ) )
      {
        return false;
      }
      ObjectModel other = (ObjectModel) obj;
      if ( this.object == null )
      {
        if ( other.object != null )
        {
          return false;
        }
      }
      else if ( !this.object.equals( other.object ) )
      {
        return false;
      }
      return true;
    }
    
  }
}
