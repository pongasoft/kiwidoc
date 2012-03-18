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
  <title>kiwidoc - Feed the kiwi</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Feed the kiwi</li></ul></h1>
<div id="content">
  <div id="help" class="entry">
    <h2 class="title">How you can help</h2>
    It took a lot of time and energy to build <span class="kiwidoc">kiwidoc</span>. Unfortunately, running it
  and maintaining it costs money. If you like this website and find it useful, please support the website.
    <dl>
      <dt>Provide feedback</dt>
      <dd>Whether it is a bug, a feature request, suggestions or any kind of feedback (even negative),
      it helps tremendously to hear it! Simply go to the
      <g:link controller="root" action="info" params="[page: 'contact']">Contact</g:link>
      page to check how to send feedback.</dd>

    <dt>Spread the word</dt>
    <dd>Blog, twitter... just spread the word about <span class="kiwidoc">kiwidoc</span>.</dd>

    <dt>Donate</dt>
    <dd>You can donate using Paypal:
      <form action="https://www.paypal.com/cgi-bin/webscr" method="post">
      <input type="hidden" name="cmd" value="_s-xclick">
      <input type="hidden" name="hosted_button_id" value="7201190">
      <input type="image" src="https://www.paypal.com/en_US/i/btn/btn_donateCC_LG.gif" border="0" name="submit" alt="PayPal - The safer, easier way to pay online!">
      <img alt="" border="0" src="https://www.paypal.com/en_US/i/scr/pixel.gif" width="1" height="1">
      </form>
    </dd>

    <dt>Purchase a book</dt>
    <dd>By following any of the book links from this site to Amazon.com and then completing a
    purchase, <span class="kiwidoc">pongasoft</span> will earn a small commission.  It won't cost
    you a single penny more, and all commissions earned will go directly towards the cost of
    running this site.</dd>
    </dl>
  </div> %{-- help --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
