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
package org.omnaest.utils.spring.extension;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.omnaest.utils.operation.special.OperationVoid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @see ExtensionPointTemplate
 * @author Omnaest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "ExtensionPointTestAC.xml" })
public class ExtensionPointTemplateTest
{
  /* ********************************************** Beans / Services / References ********************************************** */
  @Autowired
  @ExtensionPoint
  protected SampleService      sampleService      = null;
  
  @Autowired
  protected SampleServiceImpl1 sampleServiceImpl1 = null;
  
  @Autowired
  protected SampleServiceImpl2 sampleServiceImpl2 = null;
  
  /* ********************************************** Classes/Interfaces ********************************************** */
  
  protected static interface SampleService
  {
    public void doSomething();
  }
  
  @Service
  protected static class SampleServiceImpl1 implements SampleService
  {
    private boolean hasBeenCalled = false;
    
    @Override
    public void doSomething()
    {
      this.hasBeenCalled = true;
    }
    
    public boolean hasBeenCalled()
    {
      return this.hasBeenCalled;
    }
    
  }
  
  @Service
  protected static class SampleServiceImpl2 implements SampleService
  {
    private boolean hasBeenCalled = false;
    
    @Override
    public void doSomething()
    {
      this.hasBeenCalled = true;
    }
    
    public boolean hasBeenCalled()
    {
      return this.hasBeenCalled;
    }
    
  }
  
  @Service
  protected static class SampleServiceExtensionPoint extends ExtensionPointTemplate<SampleService> implements SampleService
  {
    @Autowired
    public SampleServiceExtensionPoint( List<SampleService> beanList )
    {
      super( beanList );
    }
    
    @Override
    public void doSomething()
    {
      this.executeOnAll( new OperationVoid<SampleService>()
      {
        @Override
        public void execute( SampleService sampleService )
        {
          // 
          sampleService.doSomething();
        }
      } );
    }
  }
  
  @Test
  public void testExecuteOnAll()
  {
    //
    assertNotNull( this.sampleService );
    assertNotNull( this.sampleServiceImpl1 );
    assertNotNull( this.sampleServiceImpl2 );
    
    //
    this.sampleService.doSomething();
    
    //
    assertTrue( this.sampleServiceImpl1.hasBeenCalled() );
    assertTrue( this.sampleServiceImpl2.hasBeenCalled() );
  }
}
