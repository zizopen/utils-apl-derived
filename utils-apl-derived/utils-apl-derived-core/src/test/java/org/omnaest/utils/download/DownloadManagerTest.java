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
package org.omnaest.utils.download;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

public class DownloadManagerTest
{
  
  public void testGetDownloadConnection() throws MalformedURLException
  {
    DownloadManager dm = DownloadManager.getInstance();
    DownloadConnection dc = dm.getDownloadConnection();
    
    URL url = new URL( "http://www.life-is-more.at/life/onlinebibel/Bibel_-_Revidierte_Elberfelder_1985_-_Altes_Testament.pdf" );
    //dc.threadedDownload(url);
    
    url = new URL( "http://www.fileformat.info/info/unicode/block/yi_syllables/utf8test.htm" );//"http://java.sun.com/j2se/1.4.2/docs/api/index.html");
    DownloadConnection dc2 = dm.getDownloadConnection();
    dc2.threadedDownload( url );
    
    int percentage1 = 0;
    int percentage2 = 0;
    boolean reached2 = false;
    while ( dc.isAlive() || dc2.isAlive() )
    {
      if ( percentage1 < dc.processStatePercentage() )
      {
        System.out.println( "Is alive 1: " + dc.processStatePercentage() + "%" );
        percentage1++;
      }
      if ( percentage2 < dc2.processStatePercentage() )
      {
        percentage2++;
        System.out.println( "Is alive 2: " + percentage2 + "%" );
      }
      if ( !dc2.isAlive() && !reached2 )
      {
        System.out.println( dc2.getContentAsString() );
        System.out.println( dc2.getContentEncoding() );
        System.out.println( dc2.getUsedStringEncoding() );
        assertEquals( dc2.getContentSize(), dc2.getContentEstimatedSize() );
        reached2 = true;
      }
    }
    
    assertEquals( true, dc.isDownloadSuccessful() );
    
    assertEquals( 8621424, dc.getContentEstimatedSize() );
    assertEquals( 8621424, dc.getContentSize() );
    System.out.println( dc.getTimeExpired() );
    System.out.println( dc.getContentType() );
    
    //System.out.println(dc.saveContentToFile(new File("M:\\bibel.pdf")));
    
  }
  
}
