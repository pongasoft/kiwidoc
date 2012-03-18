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
  <meta name="Description" content="Type: Package, Name: ${content.resource.name}, Location: ${content.resource.libraryVersionResource.encodeAsHTML()}, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title><pk:renderModelResource model="${content}"/> - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'package.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>
<div id="content">
<h2 class="title">Package: ${content.name.encodeAsHTML()}</h2>
<g:if test="${content.packageInfo?.hasDoc()}">
  <div class="packageInfo">
    <h2 class="heading">Overview</h2>
    <pd:renderDoc doc="${content.packageInfo}" resource="${content.resource}" groupByStyle="package"/>
  </div>
</g:if>

<div class="entries">
<g:each in="${['Interfaces': content.interfaces, 'Classes': content.classes, 'Enums': content.enums, 'Annotations': content.annotations]}" var="entry">
  <g:if test='${entry.value}'>
    <a name="content${entry.key}Ref" id="content${entry.key}Ref"></a>
    <h2 class="heading">${entry.key}</h2>
    <ul>
      <g:each in="${ModelUtils.sortByName(entry.value)}" var="cdm">
        <li class="entry ${ModelUtils.apiClass(cdm)}">
          <pk:contentLink resource="${cdm.classResource}"><span class="type ${cdm.isInterface() ? 'interface' : ''}"><pt:renderType type="${cdm.type}" style="[simpleNameOnly: true, dontRenderLink: true]" /></span></pk:contentLink>
        </li>
      </g:each>
    </ul>
  </g:if>
</g:each>
</div> <% /* end entries*/ %>
</div> <% /* end content */ %>
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>

    <g:if test="${!content.exportedPackage}">
      <div id="sidebar-info" class="entry">
        <h2>Info</h2>
        <ul>
          <li>This package is not part of the public API.</li>
        </ul>
      </div> <% /* end sidebar-info */ %>
    </g:if>
    <div id="sidebar-summary" class="entry">
      <a name="sidebarSummaryRef" id="sidebarSummaryRef"></a>
      <h2>Summary</h2>
      <ul>
        <g:each in="${['Interfaces': content.interfaces, 'Classes': content.classes, 'Enums': content.enums, 'Annotations': content.annotations]}" var="entry">
          <g:if test='${entry.value}'>
            <li><a href="#content${entry.key}Ref" title="Shortcut to ${entry.key}">${entry.key}: ${entry.value.size()}</a></li>
          </g:if>
        </g:each>
      </ul>
    </div> <% /* end sidebar-classes */ %>
    <div id="sidebar-classes" class="entry">
    <a name="sidebarClassesRef" id="sidebarClassesRef"></a>
      <h2>Classes</h2>
      <ul>
        <g:each in="${ModelUtils.sortByName(content.allClasses)}" var="cdm">
          <li class="${ModelUtils.apiClass(cdm)}"><div class="${ModelUtils.typeClass(cdm)}"><pk:contentLink resource="${cdm.classResource}"><pt:renderType type="${cdm.type}" style="[simpleNameOnly: true, dontRenderLink: true]" /></pk:contentLink></div></li>
        </g:each>
      </ul>
    </div> <% /* end sidebar-classes */ %>
  </div> <% /* end sidebar-content */ %>
</div> <% /* end sidebar */ %>
</body>
</html>