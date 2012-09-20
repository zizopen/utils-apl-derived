/*******************************************************************************
 * Copyright 2012 Danny Kunz
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
package org.omnaest.utils.table.connector;

import java.util.Map;

import org.omnaest.utils.table.ImmutableStripe;
import org.omnaest.utils.table.Stripe;
import org.omnaest.utils.table.StripeTransformerPlugin;

import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 * {@link StripeTransformerPlugin} which creates {@link ODocument} instances based on a given {@link Stripe}
 * 
 * @author Omnaest
 * @param <E>
 */
public class StripeTransformerPluginODocument<E> implements StripeTransformerPlugin<E, ODocument>
{
  private static final long serialVersionUID = 2260118296164761594L;
  
  @Override
  public Class<ODocument> getType()
  {
    return ODocument.class;
  }
  
  @Override
  public ODocument transform( ImmutableStripe<E> stripe )
  {
    String tableName = stripe.table().getTableName();
    final ODocument document = new ODocument( tableName );
    this.copyDataToDocument( stripe, document );
    return document;
  }
  
  @Override
  public ODocument transform( ImmutableStripe<E> stripe, ODocument instance )
  {
    if ( instance != null )
    {
      copyDataToDocument( stripe, instance );
    }
    return instance;
  }
  
  private void copyDataToDocument( ImmutableStripe<E> stripe, ODocument instance )
  {
    final Map<String, E> map = stripe.to().map();
    for ( String iFieldName : map.keySet() )
    {
      E element = map.get( iFieldName );
      instance.field( iFieldName, element );
    }
  }
  
}
