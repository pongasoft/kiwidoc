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
  <title>kiwidoc - Legal</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Legal</li></ul></h1>
<div id="content">
  <div id="privacy" class="entry">
  <h2 class="heading">Privacy policy</h2>
    <dl>
      <dd>By using the <span class="kiwidoc">kiwidoc</span> website, you are accepting this privacy
      policy. This website does not currently collect any personal information. This policy
      will be updated when and if it does. Email addresses used to send a message to any of the email
      addresses mentionned on this website will only be used for the purpose of responding and will
      never be made public or be sold. This website uses cookies to store your browsing history which
      gets displayed on the 'history' section on each page. This website uses
      <a href="http://kiwidoc.uservoice.com">uservoice</a> for feedback and you can see their
      privacy policy by <a href="http://uservoice.com/privacy">clicking here</a>. This website uses
      google analytics for tracking and you can see their privacy policy by
      <a href="http://www.google.com/intl/en/analytics/privacyoverview.html">clicking here</a>.</dd>
    </dl>
  </div> %{-- privacy --}%
  <div id="legal" class="entry">
  <h2 class="heading">Legal</h2>
  <dl>
    <dd>While every effort has been made to ensure accuracy, the accuracy of the information provided
    by <span class="kiwidoc">kiwidoc</span> can not be guaranteed and is to be used at your own risk.
      <span class="kiwidoc">pongasoft</span> and its developers cannot be held responsible for any harm
      caused by the use of such information.
    </dd>
  </dl>
  </div> %{-- legal --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
