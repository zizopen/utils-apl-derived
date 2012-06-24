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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.utils.beans.replicator.adapter.helper.DTOPackage;
import org.omnaest.utils.structure.element.converter.Converter;
import org.omnaest.utils.structure.element.converter.ElementConverterNumberToString;

/**
 * @see ReflectionUtils
 * @author Omnaest
 */
public class ReflectionUtilsTest
{
  /* ********************************************** Classes/Interfaces ********************************************** */
  /**
   * @author Omnaest
   */
  private static class TestClass
  {
    private String value = null;
    
    @SuppressWarnings("unused")
    public TestClass( String value )
    {
      super();
      this.value = value;
    }
    
    public String getValue()
    {
      return this.value;
    }
    
  }
  
  private static interface TestSuperInterface
  {
  }
  
  private static interface TestSubSuperInterface extends TestSuperInterface
  {
  }
  
  private static class TestSupertype implements TestSubSuperInterface
  {
  }
  
  private static class TestSubType extends TestSupertype
  {
  }
  
  private static class TestSubSubType extends TestSubType
  {
  }
  
  private static class TestFieldAnnotation
  {
    @SuppressWarnings("unused")
    @XmlElement
    public final String fieldString = null;
  }
  
  /* ********************************************** Methods ********************************************** */
  @Test
  public void testNewInstanceOf()
  {
    assertEquals( "test", ReflectionUtils.newInstanceOf( String.class, "test" ) );
    assertEquals( "test", ReflectionUtils.newInstanceOf( TestClass.class, "test" ).getValue() );
  }
  
  @Test
  @Ignore("Performance test")
  public void testNewInstanceOfPerformance()
  {
    for ( int ii = 0; ii < 1000000; ii++ )
    {
      assertEquals( "12", ReflectionUtils.newInstanceOf( ElementConverterNumberToString.class ).convert( 12 ) );
    }
  }
  
  @Test
  public void testNewInstanceByValueOf()
  {
    assertEquals( Integer.valueOf( 1 ), ReflectionUtils.newInstanceByValueOf( Integer.class, "1" ) );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testSupertypeSet()
  {
    Set<Class<?>> supertypeSet = ReflectionUtils.supertypeSet( TestSubSubType.class );
    assertEquals( 3, supertypeSet.size() );
    assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSubType.class, TestSupertype.class, Object.class ) ),
                  supertypeSet );
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testInterfaceSet()
  {
    //
    {
      //
      boolean inherited = true;
      Class<?> type = TestSubSubType.class;
      Set<Class<?>> interfaceSet = ReflectionUtils.interfaceSet( type, inherited );
      assertEquals( 2, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSuperInterface.class, TestSubSuperInterface.class ) ),
                    interfaceSet );
    }
    
    //
    {
      //
      boolean inherited = false;
      Class<?> type = TestSupertype.class;
      Set<Class<?>> interfaceSet = ReflectionUtils.interfaceSet( type, inherited );
      assertEquals( 1, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSubSuperInterface.class ) ), interfaceSet );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAssignableTypeSet()
  {
    //
    {
      //
      boolean inherited = true;
      Class<?> type = TestSubSubType.class;
      Set<Class<?>> interfaceSet = ReflectionUtils.assignableTypeSet( type, inherited );
      assertEquals( 6, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSuperInterface.class, TestSubSuperInterface.class,
                                                                TestSubSubType.class, TestSubType.class, TestSupertype.class,
                                                                Object.class ) ), interfaceSet );
    }
    
    //
    {
      //
      boolean inherited = false;
      Class<?> type = TestSupertype.class;
      Set<Class<?>> interfaceSet = ReflectionUtils.assignableTypeSet( type, inherited );
      assertEquals( 2, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSupertype.class, TestSubSuperInterface.class ) ),
                    interfaceSet );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAssignableTypeSetForTypes()
  {
    //
    {
      //
      boolean inherited = true;
      Class<?>[] types = new Class<?>[] { TestSubSubType.class, TestSuperInterface.class };
      boolean onlyReturnInterfaces = false;
      boolean intersection = true;
      Set<Class<?>> interfaceSet = ReflectionUtils.assignableTypeSet( inherited, onlyReturnInterfaces, intersection, types );
      assertEquals( 1, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSuperInterface.class ) ), interfaceSet );
    }
    {
      //
      boolean inherited = true;
      Class<?>[] types = new Class<?>[] { TestSubSubType.class, TestSubSuperInterface.class };
      boolean onlyReturnInterfaces = false;
      boolean intersection = true;
      Set<Class<?>> interfaceSet = ReflectionUtils.assignableTypeSet( inherited, onlyReturnInterfaces, intersection, types );
      assertEquals( 2, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSuperInterface.class, TestSubSuperInterface.class ) ),
                    interfaceSet );
    }
    
    //
    {
      //
      boolean inherited = true;
      Class<?>[] types = new Class<?>[] { TestSubSubType.class, TestSuperInterface.class };
      boolean onlyReturnInterfaces = false;
      boolean intersection = false;
      Set<Class<?>> interfaceSet = ReflectionUtils.assignableTypeSet( inherited, onlyReturnInterfaces, intersection, types );
      assertEquals( 6, interfaceSet.size() );
      assertEquals( new LinkedHashSet<Class<?>>( Arrays.asList( TestSuperInterface.class, TestSubSuperInterface.class,
                                                                TestSubSubType.class, TestSubType.class, TestSupertype.class,
                                                                Object.class ) ), interfaceSet );
    }
  }
  
  @Test
  public void testAreAssignableFrom()
  {
    {
      //
      Class<?>[] sourceTypes = new Class[] { ArrayList.class, HashSet.class };
      Class<?>[] assignableTypes = new Class[] { List.class, Set.class };
      assertTrue( ReflectionUtils.areAssignableFrom( assignableTypes, sourceTypes ) );
    }
    {
      //
      Class<?>[] sourceTypes = new Class[] { HashMap.class, HashSet.class };
      Class<?>[] assignableTypes = new Class[] { List.class, Set.class };
      assertFalse( ReflectionUtils.areAssignableFrom( assignableTypes, sourceTypes ) );
    }
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void testAnnotatedPackageToAnnotationSetMap()
  {
    //
    final Map<Package, Set<Annotation>> annotatedPackageToAnnotationSetMap = ReflectionUtils.annotatedPackageToAnnotationSetMap( DTOPackage.class,
                                                                                                                                 Converter.class );
    assertNotNull( annotatedPackageToAnnotationSetMap );
    
    //
    assertTrue( !annotatedPackageToAnnotationSetMap.isEmpty() );
    final Set<Package> packageSet = annotatedPackageToAnnotationSetMap.keySet();
    assertTrue( packageSet.contains( ReflectionUtils.class.getPackage() ) );
  }
  
  @Test
  public void testHasAnnotation()
  {
    //
    List<Field> declaredFieldList = ReflectionUtils.declaredFieldList( TestFieldAnnotation.class );
    assertEquals( 1, declaredFieldList.size() );
    
    //
    final Field field = declaredFieldList.iterator().next();
    {
      boolean hasAnnotation = ReflectionUtils.hasAnnotation( field, XmlElement.class );
      assertTrue( hasAnnotation );
    }
    {
      boolean hasAnnotation = ReflectionUtils.hasAnnotation( field, XmlAnyElement.class );
      assertFalse( hasAnnotation );
    }
  }
  
  @Test
  public void testInvokeMethod()
  {
    assertEquals( "10", ReflectionUtils.invokeMethod( String.class, "valueOf", 10 ) );
    assertEquals( "10", ReflectionUtils.invokeMethod( "", "valueOf", "10" ) );
  }
}
