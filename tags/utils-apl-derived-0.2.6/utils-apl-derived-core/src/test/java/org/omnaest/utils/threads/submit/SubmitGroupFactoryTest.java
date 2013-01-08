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
package org.omnaest.utils.threads.submit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.omnaest.utils.structure.collection.list.ListUtils;
import org.omnaest.utils.structure.collection.set.SetUtils;
import org.omnaest.utils.structure.iterator.IterableUtils;
import org.omnaest.utils.threads.submit.SubmitGroup;
import org.omnaest.utils.threads.submit.SubmitGroupFactory;
import org.omnaest.utils.threads.submit.Reducer.BooleansHandler;

/**
 * @see SubmitGroupFactory
 * @author Omnaest
 */
public class SubmitGroupFactoryTest
{
  
  @Test
  public void testNewSubmitGroup() throws Exception
  {
    final ExecutorService executorService = Executors.newFixedThreadPool( 10 );
    SubmitGroupFactory submitGroupFactory = new SubmitGroupFactory( executorService );
    {
      SubmitGroup<Boolean> submitGroup = submitGroupFactory.newSubmitGroup( boolean.class );
      submitGroup.submit( new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          return true;
        }
      } ).submit( new Callable<Boolean>()
      {
        @Override
        public Boolean call() throws Exception
        {
          return false;
        }
      } );
      
      final BooleansHandler<Boolean> valueHandler = new BooleansHandler<Boolean>()
      {
        @Override
        public Boolean reduce( Iterable<Boolean> values )
        {
          return IterableUtils.contains( values, true ) && IterableUtils.contains( values, false );
        }
      };
      boolean result = submitGroup.doWait().untilAllTasksAreDone().reduceToBooleanValue( valueHandler );
      assertTrue( result );
    }
    {
      SubmitGroup<String> submitGroup = submitGroupFactory.newSubmitGroup( String.class );
      submitGroup.submit( new Callable<String>()
      {
        @Override
        public String call() throws Exception
        {
          return "" + true;
        }
      }, 2 );
      submitGroup.submit( new Callable<String>()
      {
        @Override
        public String call() throws Exception
        {
          return "" + false;
        }
      } );
      
      final BooleansHandler<String> valueHandler = new BooleansHandler<String>()
      {
        @Override
        public Boolean reduce( Iterable<String> values )
        {
          return IterableUtils.contains( values, "true" ) && IterableUtils.contains( values, "false" );
        }
      };
      final boolean result = submitGroup.doWait().untilAllTasksAreDone().reduceToBooleanValue( valueHandler );
      assertTrue( result );
      
      final Set<String> valueSet = submitGroup.doWait().untilAllTasksAreDone().reduceToSet();
      assertEquals( SetUtils.valueOf( "true", "false" ), valueSet );
      
      final List<String> valueList = submitGroup.doWait().untilAllTasksAreDone().reduceToList();
      assertEquals( ListUtils.valueOf( "true", "true", "false" ), valueList );
    }
  }
  
}
