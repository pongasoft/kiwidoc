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
  <meta name="Description" content="Type: Library, Name: ${content.resource}, Location: ${content.resource.parent.encodeAsHTML()}, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title>Library: <pk:lib resource="${content.resource}"/> - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'library.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>
<div id="content">
<h2 class="title">Library: <pk:lib resource="${content.resource}"/></h2>
<div class="entries version">
  <h2 class="heading">Versions</h2>
  <ul>
  <g:each var="v" in="${ModelUtils.sortBy(content.versionsResources, 'version').reverse()}">
    <li class="entry">
    <h3 class="entryName"><pk:contentLink resource="${v}">${v.version}</pk:contentLink></h3>
    <g:if test="${lvrs[v]}">
    <ul>
      <g:each in="${['Exported Packages': lvrs[v].stats.exportedPackagesCount,
                     'Exported Classes': lvrs[v].stats.exportedClassesCount,
                     'Private Packages': lvrs[v].stats.privatePackagesCount,
                     'Private Classes': lvrs[v].stats.privateClassesCount]}" var="entry">
        <g:if test="${entry.value > 0}">
          <li>${entry.key}: ${entry.value}</li>
        </g:if>
      </g:each>
    </ul>
    </g:if>
    </li>
  </g:each>
</ul>
</div> %{-- entries version --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
