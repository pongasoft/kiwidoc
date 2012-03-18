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
  <title>kiwidoc - Party with the kiwi</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Party with the kiwi</li></ul></h1>
<div id="content">
  <h2 class="heading">Party with the kiwi</h2>
  The kiwi likes to dress up for the holidays...

  <h2 class="heading">4th July 2010</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_july4th2010.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

  <h2 class="heading">New Year 2010</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_newyear2010.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

  <h2 class="heading">Christmas 2009</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_christmas2009.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

  <h2 class="heading">Thanksgiving 2009</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_thanksgiving2009.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

  <h2 class="heading">Halloween 2009</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_logo_halloween_2009.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

  <h2 class="heading">Labor Day 2009</h2>
  <img class="logo" src="${resource(dir: 'images/logos', file: 'kiwidoc_logo_beta_laborday2009.png')}" alt="kiwidoc: A fresh way to browse and search javadoc"/>

</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
