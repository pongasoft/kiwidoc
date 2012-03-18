  %{--
  - Copyright (c) 2012 Yan Pujante
  -
  - Licensed under the Apache License, Version 2.0 (the "License"); you may not
  - use this file except in compliance with the License. You may obtain a copy of
  - the License at
  -
  - http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  - WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  - License for the specific language governing permissions and limitations under
  - the License.
  --}%
<div id="sidebar-info" class="entry">
  <h2>Info</h2>
  <pk:ul in="${pages}" var="v">
    <g:link controller="root" action="info" params="[page: v.key]">${v.value}</g:link>
  </pk:ul>
</div> %{-- sidebar-info --}%
<div id="sidebar-recommend" class="entry">
  <h2>Recommended</h2>
  <dl>
    <div class="recommendation">
      <dd><a href="http://www.amazon.com/gp/product/0201633612?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=0201633612"><img class="amazon" border="0" src="${resource(dir: 'images/amazon', file: 'design_patterns.jpg')}" alt="Design Pattenrs" /></a><img src="http://www.assoc-amazon.com/e/ir?t=pongasoft-20&l=as2&o=1&a=0201633612" width="1" height="1" border="0" alt="1x1" style="border:none !important; margin:0px !important;" /></dd>
      <dt><a href="http://www.amazon.com/gp/product/0201633612?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=0201633612">Design Patterns</a></dt>
    </div>

    <div class="recommendation">
      <dd><a href="http://www.amazon.com/gp/product/1932394842?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=1932394842"><img class="amazon" border="0" src="${resource(dir: 'images/amazon', file: 'groovy_in_action.jpg')}" alt="Groovy in Action"/></a><img src="http://www.assoc-amazon.com/e/ir?t=pongasoft-20&l=as2&o=1&a=1932394842" width="1" height="1" border="0" alt="1x1" style="border:none !important; margin:0px !important;" /></dd>
      <dt><a href="http://www.amazon.com/gp/product/1932394842?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=1932394842">Groovy in Action</a></dt>
    </div>

    <div class="recommendation">
      <dd><a href="http://www.amazon.com/gp/product/0321509021?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=0321509021"><img class="amazon" border="0" src="${resource(dir: 'images/amazon', file: 'bulletproof_web_design.jpg')}" alt="Bulletproof Web Design"/></a><img src="http://www.assoc-amazon.com/e/ir?t=pongasoft-20&l=as2&o=1&a=0321509021" width="1" height="1" border="0" alt="1x1" style="border:none !important; margin:0px !important;" /></dd>
      <dt><a href="http://www.amazon.com/gp/product/0321509021?ie=UTF8&tag=pongasoft-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=0321509021">Bulletproof Web Design</a></dt>
    </div>
  </dl>
</div>
<div class="end-float"/>
