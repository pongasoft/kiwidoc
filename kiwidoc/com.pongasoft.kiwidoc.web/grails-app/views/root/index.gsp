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

<%@ page import="com.pongasoft.kiwidoc.model.resource.RepositoryResource" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <g:set var="stats"><pk:split separator=", " in="['libraries', 'bundles', 'packages', 'classes']" var="entry">${entry}: ${stats[entry]}</pk:split></g:set>
  <title>kiwidoc</title>
  <meta name="Description" content="A fresh way to browse and search javadoc. ${stats}"/>
  <link rel="search" type="application/opensearchdescription+xml" title="kiwidoc search [Firefox]" href="${createLink(controller: 'search', action: 'firefoxSearchPlugin', params: [viewMode: params.viewMode])}">
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
  <link rel='stylesheet' type='text/css' href='/java/js/yui/2.7.0/container/assets/skins/sam/container.css'/>
  <link rel='stylesheet' type='text/css' href='/java/js/yui/2.7.0/autocomplete/assets/skins/sam/autocomplete.css'/>
  <link rel='stylesheet' type='text/css' href='/java/plugins/grails-ui-1.2-SNAPSHOT/js/grailsui/../../css/grailsui/grails-ui.css'/>
  <script type="text/javascript" src="/java/js/yui/2.7.0/yahoo-dom-event/yahoo-dom-event.js" ></script>
  <script type="text/javascript" src="/java/plugins/grails-ui-1.2-SNAPSHOT/js/grailsui/grailsui.js" ></script>
  <script type="text/javascript" src="/java/js/yui/2.7.0/datasource/datasource-min.js" ></script>
  <script type="text/javascript" src="/java/js/yui/2.7.0/connection/connection-min.js" ></script>
  <script type="text/javascript" src="/java/js/yui/2.7.0/autocomplete/autocomplete-min.js" ></script>
  <style type="text/css">
  body {
    background-color: #ffffff;
    color: #36702e;
    font-family: trebuchet ms, arial, sans-serif;
    font-size: 0.9em;
    text-align:center;
  }
  .tagline {
    font-style: italic;
    font-size: 0.9em;
    margin: -2em 0 1em 0;
    padding: 0;
  }
  .logo {
    margin-bottom: 0;
    padding-bottom: 0;
  }

  #submit {
    padding-top: 1em;
  }
  table   {
    padding-top: 5em;
    font-size: 0.8em;
  }
  table th {
    text-align: right;
  }
  #footer {
    padding-top: 5em;
    text-align: right;
  }
  #footer a:link {
    color: #36702e;
    text-decoration: none;
  }

  #footer a:hover {
    color: #36702e;
  }

  #footer a:visited {
    color: #36702e;
    text-decoration: none;
  }
  form {
    display: inline-block;
  }
  input#k.yui-ac-input {
    width: inherit;
    position: static;
  }
  .yui-skin-sam div.yui-ac-content {
    overflow: auto;
    width: auto;
  }
  div.yui-ac-container ul {
    width: inherit;
    color: #000000;
    font-family: courier, monospace;
    text-align: left;
    font-size: 0.9em;
  }
  .yui-skin-sam .yui-ac-content li .highlight {
    background: #faf585;
  }
  .yui-skin-sam .yui-ac-content li.yui-ac-highlight .highlight {
    background-color: inherit;
  }
  /* highlighted results */
  .highlight {
    font-weight: bold;
  }
  </style>
  <link rel="stylesheet" href="${resource(dir:'css',file:'icons.css')}" />
</head>
<body>
<div class="content">
<div class="logo"><img src="${resource(dir: 'images', file: 'kiwidoc_home_page.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/></div>
<p class="tagline">A fresh way to browse and search javadoc</p>  
<g:form class="yui-skin-sam" name="findKeywords" controller="search" action="findKeywords" params="[viewMode: 'p']">
  <pa:autoComplete id="k" minQueryLength="1"
    size="55" maxlength="500"
    value="${params.k ?: ''}"
    url="[controller: 'search', action: 'findByPrefix', params: [viewMode: 'p', r: 'java/j2se/1.6']]"
    allowBrowserAutocomplete="false"/>
  <g:hiddenField name="r" value="java/j2se"/>
  <div id="submit">
<g:submitButton name="Search" value="Kiwidoc Search"/>
  </div>
</g:form>
<table align="center">
  <tr id="jdks">
    <th>JDKs:</th>
    <td><pk:split in="${ModelUtils.sortBy(jdks, 'version').reverse()}" var="jdk" separator=", "><pk:renderResource resource="${jdk}"/></pk:split></td>
  </tr>
  <g:if test="${android}">
    <tr id="android">
      <th>Android:</th>
      <td><pk:split in="${android}" var="android" separator=", "><pk:contentLink resource="${android.resource}">API Level ${android.resource.version} (platform ${android.platform} &quot;${android.codename}&quot;)</pk:contentLink></pk:split></td>
    </tr>
  </g:if>
  <tr id="links">
    <th>Sitemap:</th>
    <td><pk:contentLink title="Browse the repository" resource="${RepositoryResource.INSTANCE}">Browse</pk:contentLink>, <g:link controller="search" action="findKeywords" params="[viewMode: 'p', k: 'bundle-symbolicname', m: 'm']">Bundles</g:link>,
    <pk:split in="${pages}" var="v" separator=", "><g:link controller="root" action="info" params="[page: v.key]">${v.value}</g:link></pk:split>
    </td>
  </tr>
  <tr id="stats">
    <th>Stats:</th>
    <td>${stats}</td>
  </tr>
  <tr id="firefox">
    <th>NEW:</th>
    <td>Using Firefox? <a href="#" onclick="javascript:window.external.AddSearchProvider('http://www.kiwidoc.com${createLink(controller: 'search', action: 'firefoxSearchPlugin', params: [viewMode: params.viewMode])}');">Add kiwidoc as a Firefox search plugin</a></td>
  </tr>
</table>
</div>
<div id="footer"><a href="http://www.pongasoft.com/">&copy;pongasoft 2009-2012</a></div>
</body>
</html>