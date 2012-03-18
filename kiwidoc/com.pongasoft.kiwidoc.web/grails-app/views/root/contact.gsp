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
  <title>kiwidoc - Contact</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Contact</li></ul></h1>
<div id="content">
  <div id="contact" class="entry">
  <h2 class="heading">Contact information</h2>
    <dl>
      <dt>Feedback</dt>
      <dd>Any feedback or questions about <span class="kiwidoc">kiwidoc</span> can be sent to
        <a href="mailto:feedback@kiwidoc.com">feedback@kiwidoc.com</a> or directly on <a href="http://kiwidoc.uservoice.com">the kiwidoc uservoice forum</a>.</dd>

      <dt>Blog</dt>
      <dd>You can check the blog: <a href="http://blog.pongasoft.com/">The software cookbook</a></dd>

      <dt>Twitter</dt>
      <dd>Follow <span class="kiwidoc">pongasoft</span> on <a href="http://twitter.com/pongasoft">twitter</a>.</dd>

      <dt>Bugs</dt>
      <dd>If you find any bugs or problems, please send details to <a href="mailto:bugs@kiwidoc.com">bugs@kiwidoc.com</a></dd>

      <dt>Support</dt>
      <dd>For any other kind of inquiries, please contact <a href="mailto:support@kiwidoc.com">support@kiwidoc.com</a></dd>
    </dl>
  </div> %{-- contact --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
