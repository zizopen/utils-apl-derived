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
package org.omnaest.utils.structure.element.converter;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.omnaest.utils.reflection.ReflectionUtils;
import org.omnaest.utils.structure.collection.ComparatorUtils;
import org.omnaest.utils.structure.element.ObjectUtils;
import org.omnaest.utils.structure.element.converter.ElementConverterTypeAware.SourceAndTargetType;

/**
 * Static registration of {@link ElementConverter} and {@link ElementConverterTypeAware} instances. <br>
 * <br>
 * New instances of the following {@link ElementConverter}s will be added automatically at {@link Class} creation time of the
 * {@link ElementConverterRegistration}:<br>
 * <ul>
 * <li>{@link ElementConverterNumberToString}</li>
 * <li>{@link ElementConverterObjectToString}</li>
 * <li>{@link ElementConverterBooleanToString}</li>
 * </ul>
 * 
 * @see ElementConverterRegistered
 * @author Omnaest
 */
public class ElementConverterRegistration implements Serializable
{
  private static final long serialVersionUID = 4667088490394555133L;
  /* ********************************************** Constants ********************************************** */
  private static final Comparator<SourceAndTargetType<?, ?>>                       comparator                               = ComparatorUtils.comparatorDecoratorUsingWeakHashMapCache( new Comparator<SourceAndTargetType<?, ?>>()
                                                                                                                            {
                                                                                                                              @Override
                                                                                                                              public int compare( SourceAndTargetType<?, ?> sourceAndTargetType,
                                                                                                                                                  SourceAndTargetType<?, ?> sourceAndTargetType2 )
                                                                                                                              {
                                                                                                                                //
                                                                                                                                int retval = 0;
                                                                                                                                
                                                                                                                                //
                                                                                                                                final Class<?> sourceType = sourceAndTargetType.getSourceType();
                                                                                                                                final Class<?> sourceType2 = sourceAndTargetType2.getSourceType();
                                                                                                                                
                                                                                                                                //
                                                                                                                                retval = this.determineAssignmentBasedComparisonValueFor( sourceType,
                                                                                                                                                                                          sourceType2 );
                                                                                                                                
                                                                                                                                //
                                                                                                                                if ( retval == 0 )
                                                                                                                                {
                                                                                                                                  //
                                                                                                                                  final Class<?> targetType = sourceAndTargetType.getTargetType();
                                                                                                                                  final Class<?> targetType2 = sourceAndTargetType2.getTargetType();
                                                                                                                                  
                                                                                                                                  //
                                                                                                                                  retval = this.determineAssignmentBasedComparisonValueFor( targetType,
                                                                                                                                                                                            targetType2 );
                                                                                                                                }
                                                                                                                                
                                                                                                                                //
                                                                                                                                if ( retval == 0 )
                                                                                                                                {
                                                                                                                                  retval = String.valueOf( sourceAndTargetType )
                                                                                                                                                 .compareTo( String.valueOf( sourceAndTargetType2 ) );
                                                                                                                                }
                                                                                                                                
                                                                                                                                //
                                                                                                                                return retval;
                                                                                                                              }
                                                                                                                              
                                                                                                                              private int determineAssignmentBasedComparisonValueFor( Class<?> type1,
                                                                                                                                                                                      Class<?> type2 )
                                                                                                                              {
                                                                                                                                //
                                                                                                                                int retval = 0;
                                                                                                                                
                                                                                                                                //
                                                                                                                                if ( !type1.equals( type2 ) )
                                                                                                                                {
                                                                                                                                  if ( type1.isAssignableFrom( type2 ) )
                                                                                                                                  {
                                                                                                                                    retval = 1;
                                                                                                                                  }
                                                                                                                                  else if ( type2.isAssignableFrom( type1 ) )
                                                                                                                                  {
                                                                                                                                    retval = -1;
                                                                                                                                  }
                                                                                                                                }
                                                                                                                                
                                                                                                                                //
                                                                                                                                return retval;
                                                                                                                              }
                                                                                                                            } );
  public static final SortedMap<SourceAndTargetType<?, ?>, ElementConverter<?, ?>> sourceAndTargetTypeToElementConverterMap = new ConcurrentSkipListMap<SourceAndTargetType<?, ?>, ElementConverter<?, ?>>(
                                                                                                                                                                                                            comparator );
  static
  {
    registerElementConverterTypeAware( ElementConverterNumberToString.class );
    registerElementConverterTypeAware( ElementConverterObjectToString.class );
    registerElementConverterTypeAware( ElementConverterBooleanToString.class );
    registerElementConverterTypeAware( ElementConverterStringToInteger.class );
    registerElementConverterTypeAware( ElementConverterStringToLong.class );
    registerElementConverterTypeAware( ElementConverterStringToByte.class );
    registerElementConverterTypeAware( ElementConverterStringToShort.class );
    registerElementConverterTypeAware( ElementConverterStringToBigDecimal.class );
    registerElementConverterTypeAware( ElementConverterStringToBigInteger.class );
    registerElementConverterTypeAware( ElementConverterStringToDouble.class );
    registerElementConverterTypeAware( ElementConverterStringToFloat.class );
  }
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * Determines the most fitting registered {@link ElementConverter} instance within the {@link ElementConverterRegistration}.<br>
   * <br>
   * Most fitting means e.g. if a {@link Double} instance is given as source type and {@link String} as target type the
   * {@link ElementConverterObjectToString} would fit, but the {@link ElementConverterNumberToString} is a closer fit. So the
   * {@link ElementConverterNumberToString} is returned instead of the {@link ElementConverterObjectToString}.
   * 
   * @param sourceType
   * @param targetType
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <FROM, TO> ElementConverter<FROM, TO> determineElementConverterFor( Class<FROM> sourceType, Class<TO> targetType )
  {
    //
    ElementConverter<FROM, TO> elementConverter = null;
    
    //
    final Class<?> sourceTypeNormalized = ObjectUtils.objectTypeFor( sourceType );
    final Class<?> targetTypeNormalized = ObjectUtils.objectTypeFor( targetType );
    
    //
    if ( sourceTypeNormalized.isAssignableFrom( targetTypeNormalized ) )
    {
      elementConverter = new ElementConverterIdentitiyCast<FROM, TO>();
    }
    else
    {
      //
      if ( sourceType != null && targetType != null )
      {
        for ( SourceAndTargetType<?, ?> sourceAndTargetType : sourceAndTargetTypeToElementConverterMap.keySet() )
        {
          //
          final Class<?> iSourceType = ObjectUtils.objectTypeFor( sourceAndTargetType.getSourceType() );
          final Class<?> iTargetType = ObjectUtils.objectTypeFor( sourceAndTargetType.getTargetType() );
          
          //
          boolean isAssignableFromSourceType = iSourceType.isAssignableFrom( sourceTypeNormalized );
          boolean isAssignableToTargetType = targetTypeNormalized.isAssignableFrom( iTargetType );
          if ( isAssignableFromSourceType && isAssignableToTargetType )
          {
            //
            elementConverter = (ElementConverter<FROM, TO>) sourceAndTargetTypeToElementConverterMap.get( sourceAndTargetType );
            
            //
            break;
          }
        }
      }
    }
    
    //
    return elementConverter;
  }
  
  /**
   * Similar to {@link #registerElementConverterTypeAware(ElementConverterTypeAware)} using reflection to create a new instance.
   * 
   * @param elementConverterTypeAwareType
   */
  public static void registerElementConverterTypeAware( Class<? extends ElementConverterTypeAware<?, ?>> elementConverterTypeAwareType )
  {
    registerElementConverterTypeAware( ReflectionUtils.newInstanceOf( elementConverterTypeAwareType ) );
  }
  
  /**
   * Registers an {@link ElementConverterTypeAware} instance at the {@link ElementConverterRegistration}
   * 
   * @param elementConverterTypeAware
   */
  public static void registerElementConverterTypeAware( ElementConverterTypeAware<?, ?> elementConverterTypeAware )
  {
    //
    if ( elementConverterTypeAware != null )
    {
      ElementConverterRegistration.sourceAndTargetTypeToElementConverterMap.put( elementConverterTypeAware.getSourceAndTargetType(),
                                                                                 elementConverterTypeAware );
    }
  }
  
  /**
   * Similar to {@link #registerElementConverter(ElementConverter, Class, Class)} but using reflection to create a new instance of
   * the {@link ElementConverter} type.
   * 
   * @param elementConverterType
   * @param sourceType
   * @param targetType
   */
  public static void registerElementConverter( final Class<? extends ElementConverter<?, ?>> elementConverterType,
                                               final Class<?> sourceType,
                                               final Class<?> targetType )
  {
    final ElementConverter<?, ?> elementConverter = ReflectionUtils.newInstanceOf( elementConverterType );
    registerElementConverter( elementConverter, sourceType, targetType );
  }
  
  /**
   * Similar to {@link #registerElementConverterTypeAware(ElementConverterTypeAware)} but for non type aware
   * {@link ElementConverter} instances
   * 
   * @param elementConverter
   * @param sourceType
   * @param targetType
   */
  public static void registerElementConverter( final ElementConverter<?, ?> elementConverter,
                                               final Class<?> sourceType,
                                               final Class<?> targetType )
  {
    if ( elementConverter != null && sourceType != null && targetType != null )
    {
      ElementConverterRegistration.sourceAndTargetTypeToElementConverterMap.put( new SourceAndTargetType<Object, Object>(
                                                                                                                          sourceType,
                                                                                                                          targetType ),
                                                                                 elementConverter );
    }
  }
  
}
