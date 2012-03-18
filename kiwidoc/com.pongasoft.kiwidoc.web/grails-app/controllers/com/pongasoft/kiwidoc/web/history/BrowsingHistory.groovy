
 /*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.kiwidoc.web.history

import com.pongasoft.kiwidoc.model.resource.Resource

/**
 * @author yan@pongasoft.com
*/
class BrowsingHistory
{
  def request
  def response

  def addEntry(Resource resource)
  {
    def h = history
    h.remove(resource)
    h << resource
    if(h.size() > 20)
    {
      h = h[1..-1]
    }
    history = h
    return h
  }

  def getHistory()
  {
    request.session['history'] ?: []
  }

  def setHistory(history)
  {
    request.session['history'] = history
  }
}