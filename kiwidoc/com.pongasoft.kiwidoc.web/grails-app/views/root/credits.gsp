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
  <title>kiwidoc - Credits</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Credits</li></ul></h1>
<div id="content">
  <div id="people" class="entry">
    <h2 class="heading">People</h2>
    <dl>
      <dt>Design</dt>
      <dd><a href="mailto:yan@pongasoft.com">yan@pongasoft.com</a></dd>

      <dt>Architecture</dt>
      <dd><a href="mailto:yan@pongasoft.com">yan@pongasoft.com</a></dd>

      <dt>Implementation</dt>
      <dd><a href="mailto:yan@pongasoft.com">yan@pongasoft.com</a></dd>

      <dt>Web Dev</dt>
      <dd><a href="mailto:yan@pongasoft.com">yan@pongasoft.com</a></dd>

      <dt>Testing</dt>
      <dd><a href="mailto:yan@pongasoft.com">yan@pongasoft.com</a></dd>

      <dt>Logos and icons</dt>
      <dd><a href="mailto:markus@pongasoft.com">Markus Tuberville</a></dd>

      <dt>Content editing</dt>
      <dd><a href="mailto:markus@pongasoft.com">Markus Tuberville</a></dd>
    </dl>
  </div> %{-- people --}%
  <div id="thanks" class="entry">
    <h2 class="heading">Thanks</h2>
  <p>
  Special thanks go to Markus Tuberville for the really cool logos and icons (both for
  <span class="kiwidoc">pongasoft</span> and <span class="kiwidoc">kiwidoc</span>): they really
  make the site look good.
  </p>
  <p>
    Thanks go to Jean-Luc Vaillant for some ideas and advice as well as allowing me the luxury to
    work part-time in order to fulfill my dream.
  </p>
  </div> %{-- thanks --}%
  <div id="software" class="entry">
    <h2 class="heading">Software</h2>
  <dl>
    <dt><a href="http://groovy.codehaus.org/">groovy</a> and <a href="http://java.sun.com/javase/">java</a></dt>
    <dd>java has essentially been used for the backend. groovy for the frontend (through Grails)
    and testing.</dd>

    <dt><a href="http://commons.apache.org/vfs/">commons-vfs</a></dt>
    <dd>This library has been used to abstract the location of the files generated during the
      <span class="kiwidoc">kiwidoc</span> processing. It makes it easy to write tests with no
    file access at all.</dd>

    <dt><a href="http://www.json.org/java/">org.json</a></dt>
    <dd>The internal storage format is json and this library has been used to read and write json
    serialized objects.</dd>

    <dt><a href="http://asm.ow2.org/">asm</a></dt>
    <dd>For parsing the bytecode.</dd>

    <dt><a href="http://java.sun.com/j2se/javadoc/">javadoc</a></dt>
    <dd>For parsing the source code.</dd>

    <dt><a href="http://www.junit.org">junit</a></dt>
    <dd>For testing.</dd>

    <dt><a href="http://www.aqute.biz/Code/Bnd">bnd</a></dt>
    <dd>For parsing OSGi headers.</dd>

    <dt><a href="http://lucene.apache.org/java/docs/">lucene</a></dt>
    <dd>For the text search engine.</dd>

    <dt><a href="http://jericho.htmlparser.net/docs/index.html">jericho-html</a></dt>
    <dd>For parsing html.</dd>

    <dt><a href="http://www.grails.org/">Grails</a></dt>
    <dd>For the frontend.</dd>

    <dt><a href="http://www.w3.org/Style/CSS/">css</a></dt>
    <dd>For the final rendering.</dd>

    <dt><a href="http://home.ccil.org/~cowan/XML/tagsoup/">tagsoup</a></dt>
    <dd>For filtering improperly formatted javadoc.</dd>

    <dt><a href="http://www.gradle.org/">gradle</a></dt>
    <dd>For the build.</dd>

    <dt><a href="http://maven.apache.org/">maven</a></dt>
    <dd>To fetch and process the source code and dependencies during
    the <span class="kiwidoc">kiwidoc</span> processing.</dd>
  </dl>
  </div>
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
