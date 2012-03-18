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
  <meta name="Description" content="Type: Organisation, Name: ${content.resource}, Location: Repository, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title>Organsation: ${content.resource.organisation} - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'organisation.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>
<div id="content">
<h2 class="title">Organisation: ${content.resource.organisation}</h2>
<div class="entries version">
  <h2 class="heading">Libraries</h2>
  <ul>
  <g:each var="lib" in="${ModelUtils.sortByName(content.libraryResources)}">
    <li class="entry">
    <h3 class="entryName"><pk:contentLink resource="${lib}">${lib.name}</pk:contentLink></h3>
    <ul>
      <g:each in="${ModelUtils.sortByName(libraries[lib].versionsResources)}" var="version">
        <li><pk:contentLink resource="${version}">${version.version}</pk:contentLink></li>
      </g:each>
    </ul>
  </g:each>
</ul>
</div> %{-- entries version --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebast --}%
</body>
</html>