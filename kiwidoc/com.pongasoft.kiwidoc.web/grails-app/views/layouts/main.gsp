<?xml version="1.0" encoding="UTF-8"?>
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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title><g:layoutTitle default="kiwidoc"/></title>
  <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>
<link rel='stylesheet' type='text/css' href='/java/js/yui/2.7.0/container/assets/skins/sam/container.css'/>
<link rel='stylesheet' type='text/css' href='/java/js/yui/2.7.0/autocomplete/assets/skins/sam/autocomplete.css'/>
<link rel='stylesheet' type='text/css' href='/java/plugins/grails-ui-1.2-SNAPSHOT/js/grailsui/../../css/grailsui/grails-ui.css'/>
<script type="text/javascript" src="/java/js/yui/2.7.0/yahoo-dom-event/yahoo-dom-event.js" ></script>
<script type="text/javascript" src="/java/plugins/grails-ui-1.2-SNAPSHOT/js/grailsui/grailsui.js" ></script>
<script type="text/javascript" src="/java/js/yui/2.7.0/datasource/datasource-min.js" ></script>
<script type="text/javascript" src="/java/js/yui/2.7.0/connection/connection-min.js" ></script>
<script type="text/javascript" src="/java/js/yui/2.7.0/autocomplete/autocomplete-min.js" ></script>
<g:javascript library="prototype" />
<script type="text/javascript" src="${resource(dir:'js',file:'application.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js',file:'kiwidoc.js')}"></script>
  %{--<gui:resources components="autoComplete"/>--}%
<link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
  <link rel="stylesheet" href="${resource(dir:'css',file:'icons.css')}" />
  <g:layoutHead/>
</head>
<body class="viewMode-${preferences.viewMode}" onload="renderBlocks();">
<a name="top" id="top"></a>
<div id="wrap">
  <div id="header">
    <dl id="logo">
      <dd><a href="${resource(file: '/')}" id="logo-link"><img src="${resource(dir: 'images', file: 'kiwidoc_logo_black_37.png')}" alt="kiwidoc"/></a></dd>
    </dl>
    <dl class="entry">
      <dd>
        <g:form class="yui-skin-sam" id="findKeywords" name="findKeywords" controller="search" action="findKeywords" params="[viewMode: preferences.viewMode]">
          <pa:autoComplete id="k" minQueryLength="1"
            size="55" maxlength="500"
            value="${params.k ?: ''}"
            url="[controller: 'search', action: 'findByPrefix', params: [viewMode: preferences.viewMode, r: (content?.resource ? pk.resourceURI(resource: content.resource) : '')]]"
            allowBrowserAutocomplete="false"/>
          <g:if test="${content?.resource}">
            <g:hiddenField name="r" value="${pk.resourceURI(resource: content.resource)}"/>
          </g:if>
          <g:submitButton name="Search" value="Search"/>
        </g:form>
        <div id="fbpr"></div>
      </dd>
    </dl>
    <dl class="entry">
      <dd>View Mode: ${preferences.isViewModePrivate ? 'All': 'Public API'} (<a href="${pk.switchViewMode()}" title="Switch between 'Public API' (only public/protected api = javadoc) and 'All' (everything, including private and internals)">switch</a>)</dd>
    </dl>
  </div> <!-- end header -->

  <div id="content-wrapper">
  <g:layoutBody />
  </div>

  <div id="footer-wrapper"><div id="footer">
  <div id="footer-sitemap"><pk:split in="${pages}" var="v" separator=", "><g:link controller="root" action="info" params="[page: v.key]">${v.value}</g:link></pk:split></div>
  <div id="footer-copyright"><a href="http://www.pongasoft.com/">&copy;pongasoft 2009-2012</a></div>
  </div></div>
</div>
</body>
</html>