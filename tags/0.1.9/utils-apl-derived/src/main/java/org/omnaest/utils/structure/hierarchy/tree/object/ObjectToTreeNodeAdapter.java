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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.omnaest.utils.beans.BeanUtils;
import org.omnaest.utils.beans.result.BeanPropertyAccessor;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverter;
import org.omnaest.utils.structure.hierarchy.tree.TreeNode;

/**
 * Adapter to make any {@link Object} available as {@link ObjectTreeNode}. Implements additionally the {@link ObjectTree}
 * interface.
 * 
 * @see ObjectTreeNode
 * @see ObjectTree
 * @author Omnaest
 */
public class ObjectToTreeNodeAdapter implements ObjectTreeNode, ObjectTree
{
  /* ********************************************** Variables ********************************************** */
  protected final ObjectModel objectModel;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see ObjectToTreeNodeAdapter
   * @param objectModel
   */
  public ObjectToTreeNodeAdapter( ObjectModel objectModel )
  {
    super();
    this.objectModel = objectModel;
  }
  
  @Override
  public ObjectModel getModel()
  {
    return this.objectModel;
  }
  
  @Override
  public List<TreeNode<ObjectModel>> getChildrenList()
  {
    //
    final List<TreeNode<ObjectModel>> retlist;
    
    //
    final Object object = this.objectModel.getObject();
    @SuppressWarnings("unchecked")
    final Class<Object> type = (Class<Object>) ( object != null ? object.getClass() : null );
    
    boolean isPrimitiveType = type != null && type.isPrimitive();
    boolean isStringType = type != null && String.class.equals( type );
    boolean isPrimitiveWrapperType = ObjectUtils.isWrapperTypeOfPrimitiveType( type );
    
    final Set<BeanPropertyAccessor<Object>> beanPropertyAccessorSet = !isPrimitiveType && !isStringType
                                                                      && !isPrimitiveWrapperType ? BeanUtils.beanPropertyAccessorSet( type )
                                                                                                : null;
    ElementConverter<BeanPropertyAccessor<Object>, TreeNode<ObjectModel>> elementConverter = new ElementConverter<BeanPropertyAccessor<Object>, TreeNode<ObjectModel>>()
    {
      @Override
      public TreeNode<ObjectModel> convert( BeanPropertyAccessor<Object> beanPropertyAccessor )
      {
        //
        Method getterMethod = beanPropertyAccessor.getMethodGetter();
        Method setterMethod = beanPropertyAccessor.getMethodSetter();
        Field field = beanPropertyAccessor.getField();
        String propertyName = beanPropertyAccessor.getPropertyName();
        Object value = beanPropertyAccessor.getPropertyValue( object );
        return new ObjectToTreeNodeAdapter( new ObjectModel( value, getterMethod, setterMethod, field, propertyName ) );
      }
    };
    retlist = ListUtils.convert( beanPropertyAccessorSet, elementConverter );
    Comparator<TreeNode<ObjectModel>> comparator = new Comparator<TreeNode<ObjectModel>>()
    {
      
      @Override
      public int compare( TreeNode<ObjectModel> o1, TreeNode<ObjectModel> o2 )
      {
        return o1 == null || o2 == null || o1.getModel() == null || o2.getModel() == null ? 0
                                                                                         : o1.getModel()
                                                                                             .getPropertyName()
                                                                                             .compareTo( o2.getModel()
                                                                                                           .getPropertyName() );
      }
    };
    Collections.sort( retlist, comparator );
    
    // 
    return retlist;
  }
  
  @Override
  public ObjectTreeNode getTreeRootNode()
  {
    return this;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append( "ObjectToTreeNodeAdapter [objectModel=" );
    builder.append( this.objectModel );
    builder.append( "]" );
    return builder.toString();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ( ( this.objectModel == null ) ? 0 : this.objectModel.hashCode() );
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
    if ( !( obj instanceof ObjectToTreeNodeAdapter ) )
    {
      return false;
    }
    ObjectToTreeNodeAdapter other = (ObjectToTreeNodeAdapter) obj;
    if ( this.objectModel == null )
    {
      if ( other.objectModel != null )
      {
        return false;
      }
    }
    else if ( !this.objectModel.equals( other.objectModel ) )
    {
      return false;
    }
    return true;
  }
}
