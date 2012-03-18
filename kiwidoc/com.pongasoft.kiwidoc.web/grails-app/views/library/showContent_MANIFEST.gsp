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

<%@ page import="com.pongasoft.kiwidoc.web.utils.OSGiUtils" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <meta name="Description" content="Type: Manifest, Name: ${content.resource.resourceName}, Location: ${content.resource.libraryVersionResource.encodeAsHTML()}, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title><pk:renderModelResource model="${content}"/> - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'manifest.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>
<div id="content">
  <h2 class="title">Manifest: ${content.resource.parent}</h2>
  <div class="entries version">
  <h2 class="heading">Attributes</h2>
    <a name="mainAttributesRef" id="mainAttributesRef"></a>
    <h3>Main Attributes</h3>
    <dl>
      <g:each in="${ModelUtils.sortBy(content.manifest.mainAttributes.keySet(), 'name')}" var="attributeName">
        <dt>${attributeName.encodeAsHTML()}</dt>
        <dd><pk:ul var="v" in="${OSGiUtils.splitHeader(attributeName.toString(), content.manifest.mainAttributes[attributeName])}"><pk:split in="${v}" separator=";" var="x">${x.encodeAsHTML()}</pk:split></pk:ul></dd>
      </g:each>
    </dl>
    <g:each in="${content.manifest.entries}" var="entry">
      <a name="entry${entry.key.encodeAsHTML()}Ref" id="entry${entry.key.encodeAsHTML()}Ref"></a>
      <h3>${entry.key.encodeAsHTML()}</h3>
      <dl>
        <g:each in="${entry.value}" var="attribute">
          <dt>${attribute.key.encodeAsHTML()}</dt>
          <dd>${attribute.value.encodeAsHTML()}</dd>
        </g:each>
      </dl>
    </g:each>
    <a name="rawRef" id="rawRef"></a>
  <h2 class="heading">Raw manifest</h2>
<pre>${raw}</pre>
</div> %{-- entries version --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>

    <div id="sidebar-manifest" class="entry">
    <h2>Manifest</h2>
    <ul>
      <li><a href="#rawRef">Raw Manifest</a></li>
      <li><a href="#mainAttributesRef">Main Attributes</a></li>
      <g:each in="${content.manifest.entries}" var="entry">
        <li>
          <a href="#entry${RequestUtils.encodeAsURL(entry.key)}Ref">${entry.key.encodeAsHTML()}</a>
        </li>
      </g:each>
    </ul>
    </div> %{-- sidebar-manifest --}%
  </div> %{-- sidebar-content --}%
</div> %{-- sidebast --}%
</body>
</html>