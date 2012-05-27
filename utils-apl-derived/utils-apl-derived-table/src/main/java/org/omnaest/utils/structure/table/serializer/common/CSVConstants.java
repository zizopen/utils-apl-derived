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
package org.omnaest.utils.structure.table.serializer.common;

import org.omnaest.utils.structure.table.serializer.marshaller.TableMarshallerCSV;
import org.omnaest.utils.structure.table.serializer.unmarshaller.TableUnmarshallerCSV;

/**
 * Holds constants used by {@link TableMarshallerCSV} and {@link TableUnmarshallerCSV}
 * 
 * @author Omnaest
 */
public interface CSVConstants
{
  public static final String  DEFAULT_DELIMITER           = ";";
  public static final String  DEFAULT_QUOTATION_CHARACTER = "\"";
  public static final boolean DEFAULT_HAS_TABLE_NAME      = false;
  public static final boolean DEFAULT_HAS_COLUMN_TITLES   = true;
  public static final boolean DEFAULT_HAS_ROW_TITLES      = false;
}
