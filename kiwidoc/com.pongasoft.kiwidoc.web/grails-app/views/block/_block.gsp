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

<g:javascript>
  function block${action}() {
  var p = "${pk.mapToQueryString(args)}";
  <g:remoteFunction controller="block" action="${action}" params="p" update='[success: "__block_${action}_success", failure: "__block_${action}_failure"]'/>
  }
  blocks.push(block${action});
</g:javascript><div id="__block_${action}_success"></div><div id="__block_${action}_failure" class="hidden"></div>