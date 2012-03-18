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

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="Description" content="Type: Repository, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title>Repository - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'repository.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>

<div id="content">
<g:if test="${content.libraries.size() > libraries.size()}">
  <div class="pagination">
    <g:paginate total="${content.libraries.size()}" controller="library" action="showContent" params="[viewMode: preferences.viewMode]"/>
  </div> %{-- pagination --}%
</g:if>
<table id="libraries">
  <pk:split var="lib" in="${libraries}">
    <tr class="${ModelUtils.oddOrEven(idx)}">
    <td class="library"><pk:renderResource resource="${lib}"/></td><td class="version"><pk:ul in="${ModelUtils.sortBy(content.getLibraryVersions(lib), 'version').reverse()}" var="version"><pk:contentLink resource="${version}">${version.version.encodeAsHTML()}</pk:contentLink></pk:ul></td>
    </tr>
  </pk:split>
</table>
<g:if test="${content.libraries.size() > libraries.size()}">
  <div class="pagination">
    <g:paginate total="${content.libraries.size()}" controller="library" action="showContent" params="[viewMode: preferences.viewMode]"/>
  </div> %{-- pagination --}%
</g:if>
</div> %{-- content --}%

<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>

    <div id="sidebar-jdks" class="entry">
      <h2>JDKs</h2>
      <ul>
        <g:each in="${ModelUtils.sortBy(jdks, 'version').reverse()}" var="jdk">
          <pk:renderResource resource="${jdk}"/>
        </g:each>
      </ul>
    </div> %{-- sidebar-jdks --}%

    <div id="sidebar-stats" class="entry">
    <h2>Stats</h2>
      <pk:ul separator=", " in="['bundles', 'libraries', 'packages', 'classes']" var="entry"> ${entry}=${stats[entry]}</pk:ul>
    </div> %{-- sidebar-stats --}%

  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%

</body>
</html>