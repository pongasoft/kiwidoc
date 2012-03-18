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
<g:if test="${history?.history}">
  <div id="sidebar-history" class="entry">
  <h2>History</h2>
    <g:set var="h" value="${history.history.reverse()}"/>
    <g:if test="${h.size() > 10}">
      <g:set var="h" value="${h[0..10]}"/>
    </g:if>
    <pk:ul in="${h}" var="e">
      <pk:contentLink resource="${e}"><span class="${ModelUtils.resourceClass(e)}">${ModelUtils.toShortString(e)}</span></pk:contentLink>
    </pk:ul>
  </div>
</g:if>
