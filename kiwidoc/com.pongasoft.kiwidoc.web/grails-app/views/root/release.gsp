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
  <title>kiwidoc - Release Notes</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">Release Notes</li></ul></h1>
<div id="content">
  <div id="latest" class="entry">
    <h2 class="heading">Latest release</h2>
    <dl>
      <dt>2.0.0 - 2012/04/11</dt>
      <dd>First open source release!</dd>
    </dl>
  </div> %{-- latest --}%
  <div id="previous" class="entry">
    <h2 class="heading">Previous releases</h2>
  <dl>
    <dl>
      <dt>1.4.3 - 2012/01/29</dt>
      <dd>Added announcement for kiwidoc shutdown :(</dd>
    </dl>
    <dl>
      <dt>1.4.2 - 2011/07/18</dt>
      <dd>Added Firefox search plugin (thanks Tyler!).</dd>
    </dl>
    <dl>
      <dt>1.4.1 - 2011/03/15</dt>
      <dd>Fixed issue with links to methods/fields in javadoc.</dd>
    </dl>
    <dl>
      <dt>1.4.0 - 2011/03/11</dt>
      <dd>Huge improvements in site speed: moved to a static model with a dynamic 'block'
          infrastructure.</dd>
    </dl>
    <dl>
      <dt>1.3.0 - 2011/02/16</dt>
      <dd>Integrated with <a href="http://kiwidoc.uservoice.com">uservoice</a> for feedback.</dd>
    </dl>
    <dl>
      <dt>1.2.0 - 2011/01/04</dt>
      <dd>Added Android API Level 9 (platform 2.3 &quot;froyo&quot;). Moved to grails 1.3.5.</dd>
    </dl>
    <dl>
      <dt>1.1.1 - 2010/11/14</dt>
      <dd>Fixed jdk6 dependency issue and extends vs implements for interfaces. Added org.linkedin
      libraries.</dd>
    </dl>
    <dl>
      <dt>1.1.0-lib.1 - 2010/10/16</dt>
      <dd>Added library: restlet.</dd>
    </dl>
    <dl>
      <dt>1.1.0 - 2010/08/22</dt>
      <dd>Moved to grails 1.3.4. Use of gradle for the build.</dd>
    </dl>
    <dl>
      <dt>1.0.11.beta - 2010/07/31</dt>
      <dd>Improved search results (include private results when no public results).</dd>
    </dl>
    <dl>
      <dt>1.0.10.beta - 2010/07/25</dt>
      <dd>Added Google Code Search and Stack Overflow related links. Tweaked proximity searching.
      new library: jcommander</dd>
    </dl>
    <dl>
      <dt>1.0.9.beta - 2010/07/03</dt>
      <dd>Added Android API Level 8 (platform 2.2 &quot;froyo&quot;). Now accessible directly from the home
      page.</dd>
    </dl>
    <dl>
      <dt>1.0.8.beta - 2010/06/20</dt>
      <dd>Enhancements in searching (sort by most current version). new library: testng.</dd>
    </dl>
    <dl>
      <dt>1.0.7.beta - 2010/06/12</dt>
      <dd>Changed home page proximity search to use jdk1.6 first. Some code enhancements.</dd>
    </dl>
    <dl>
      <dt>1.0.6.beta - 2010/03/13</dt>
      <dd>Added browsing history. Enhanced class display. Fixed a couple of minor problems.</dd>
    </dl>
    <dl>
      <dt>1.0.5.beta - 2010/02/06</dt>
      <dd>Indexed latest version for 161 libraries including google-collections, junit, activemq,
      ehcache, ivy, lucene, groovy and many more.
      </dd>
    </dl>
    <dl>
      <dt>1.0.5.beta - 2010/01/11</dt>
      <dd>Fixed inheritance issue with interface. Added [ +/- ] feature in methods list to show/hide the
        inherited members.
      </dd>
    </dl>
    <dt>1.0.4.beta - 2009/12/24</dt>
    <dd>Some minor enhancements.</dd>
    <dt>1.0.3.beta - 2009/11/29</dt>
    <dd>Fixed link issue in package overview. new library: android 2.0 (API level 5).
    <dt>1.0.2.beta - 2009/11/07</dt>
    <dd>Fixed broken pages due to javadoc returning &lt;any&gt; for some types.
    <dt>1.0.1.beta - 2009/10/17</dt>
    <dd>new library: jetty.
    </dd>
    <dt>1.0.1.beta - 2009/09/19</dt>
    <dd>new libraries: zookeeper, grails, junit.
    </dd>
    <dt>1.0.1.beta - 2009/09/05</dt>
    <dd>Fixed html rendering issue on some html pages. Split method display in the sidebar
    (constructors, static and members). Added libraries: db4o, asm, commons-vfs.
    </dd>

    <dt>1.0.0.beta - 2009/08/30</dt>
    <dd>new libraries: hadoop, gwt, gxt, cascading, servlet, mail, jax-rs, activation.</dd>

    <dt>1.0.0.beta - 2009/08/23</dt>
    <dd>This release contains the initial set of features and is released live.</dd>

    <dt>1.0.0.alpha - 2009/08/01</dt>
    <dd>This release contains the initial set of features on a small set of libraries. Internal
    release only. Demo.</dd>
  </dl>
  </div> %{-- previous --}%
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
