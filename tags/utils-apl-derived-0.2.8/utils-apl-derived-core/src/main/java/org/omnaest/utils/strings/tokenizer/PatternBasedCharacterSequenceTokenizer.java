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
package org.omnaest.utils.strings.tokenizer;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**

 * 
 * @author Omnaest
 */
public class PatternBasedCharacterSequenceTokenizer implements  CharacterSequenceTokenizer
{
  /* ********************************************** Variables ********************************************** */
  private final CharSequence charSequence;
  private final String       regexDelimiter;
  
  /* ********************************************** Methods ********************************************** */
  
  /**
   * @see PatternBasedCharacterSequenceTokenizer
   * @param charSequence
   * @param regexDelimiter
   */
  public PatternBasedCharacterSequenceTokenizer( CharSequence charSequence, String regexDelimiter )
  {
    super();
    this.charSequence = charSequence;
    this.regexDelimiter = regexDelimiter;
  }
  
  @Override
  public Iterator<CharSequence> iterator()
  {
    //
    final Pattern pattern = Pattern.compile( this.regexDelimiter );
    final Matcher matcher = pattern.matcher( this.charSequence );
    
    //    
    return new Iterator<CharSequence>()
    {
      /* ********************************************** Variables ********************************************** */
      private boolean      hasReadTail                   = false;
      private boolean      hasToReadNewCharacterSequence = true;
      private CharSequence nextCharacterSequence         = null;
      
      /* ********************************************** Methods ********************************************** */
      
      /**
       * @return
       */
      private CharSequence readNextCharacterSequence()
      {
        //
        CharSequence retval = null;
        
        //
        if ( !this.hasToReadNewCharacterSequence )
        {
          retval = this.nextCharacterSequence;
        }
        else if ( !this.hasReadTail )
        {
          //
          final StringBuffer stringBuffer = new StringBuffer();
          {
            //
            final boolean find = matcher.find();
            
            //
            if ( find )
            {
              //
              matcher.appendReplacement( stringBuffer, "" );
            }
            else
            {
              //
              matcher.appendTail( stringBuffer );
              this.hasReadTail = true;
            }
          }
          
          //
          retval = stringBuffer;
          
          //
          this.nextCharacterSequence = retval;
          this.hasToReadNewCharacterSequence = false;
        }
        
        //
        return retval;
      }
      
      @Override
      public boolean hasNext()
      {
        //
        final CharSequence nextCharacterSequence = this.readNextCharacterSequence();
        
        // 
        return nextCharacterSequence != null;
      }
      
      @Override
      public CharSequence next()
      {
        //
        final CharSequence nextCharacterSequence = this.readNextCharacterSequence();
        
        //
        this.hasToReadNewCharacterSequence = true;
        
        //
        return nextCharacterSequence;
      }
      
      @Override
      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    };
  }
}
