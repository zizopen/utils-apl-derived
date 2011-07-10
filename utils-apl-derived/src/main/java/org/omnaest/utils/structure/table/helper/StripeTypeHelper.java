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
package org.omnaest.utils.structure.table.helper;

import org.omnaest.utils.structure.table.Table.Stripe.StripeType;

/**
 * Helper for {@link StripeType}
 * 
 * @author Omnaest
 */
public class StripeTypeHelper
{
  
  /**
   * Returns {@link StripeType#ROW} for {@link StripeType#COLUMN} and vice versa
   * 
   * @param stripeType
   * @return
   */
  public static StripeType determineInvertedStripeType( StripeType stripeType )
  {
    //
    StripeType retval = null;
    
    //
    if ( StripeType.ROW.equals( stripeType ) )
    {
      retval = StripeType.COLUMN;
    }
    else if ( StripeType.COLUMN.equals( stripeType ) )
    {
      retval = StripeType.ROW;
    }
    
    //
    return retval;
    
  }
}
