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

/*
 * Toggle show/hide by adding/removing the class 'hidden'
 * @param containerId where to start looking for children
 * @param className only select the children that have the provided className
*/
function toggleShowHide(containerId, className) {
  var container = document.getElementById(containerId);
  var children = container.getElementsByTagName('*');
  for(var i = 0; i < children.length; i++)
  {
    var child = children.item(i);
    if(YAHOO.util.Dom.hasClass(child, className))
    {
      if(YAHOO.util.Dom.hasClass(child, 'hidden'))
      {
        YAHOO.util.Dom.removeClass(child, 'hidden');
      }
      else
      {
        YAHOO.util.Dom.addClass(child, 'hidden');
      }
    }
  }
}

var blocks = new Array();

function renderBlocks() {
  for(var i = 0; i < blocks.length; i++)
  {
    var fn = blocks[i];
    fn();
  }
}