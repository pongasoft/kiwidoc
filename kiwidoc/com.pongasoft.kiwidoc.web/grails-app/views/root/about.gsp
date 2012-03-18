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

<%@ page import="com.pongasoft.kiwidoc.model.resource.LibraryVersionResource; com.pongasoft.kiwidoc.model.resource.ClassResource" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>kiwidoc - About</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'info.css')}" />
</head>
<body>
<h1 class="heading"><ul><li class="r0">About</li></ul></h1>
<div id="content">
  <div id="introduction" class="entry">
  <h2 class="heading">Introduction</h2>
    In a few words, <span class="kiwidoc">kiwidoc</span> can be described as javadoc on steroids.
    The main goal is to help software developers quickly find the information about java libraries
    in a single location:
    <ul>
    <li>proximity search and typeahead allow you to quickly locate what you are looking for.</li>
    <li>IDE-style display shows you the relevant information in a familiar format.</li>
    <li>immediate access to additional information such as library dependencies, manifest, OSGi 
    headers, etc.</li>
    <li>the private view can provide even more details if you need to dig deeper (in order to, for example
    extend a library or better understand its internals).</li>
    </ul>
    <p>
      Numerous additions and enhancements to <span class="kiwidoc">kiwidoc</span> are already
    being planned in order to make it an even more useful tool.  But we need your
    feedback to help us get it right! Positive, negative, or otherwise, your
    <g:link controller="root" action="info" params="[page: 'contact']">feedback</g:link> is 
    crucial to the future of this project.
    </p>
    <p>
    I started the <span class="kiwidoc">kiwidoc</span> project in early 2009. My blog
    (<a href="http://www.pongasoft.com/blog/yan">The software cookbook</a>) has more information about
    the history of the project itself.
    </p>
  </div>
  <div id="features" class="entry">
  <h2 class="heading">Features</h2>
  <dl>
    <a name="F1Ref" id="F1Ref"></a>
    <dt>Dynamically linked javadoc</dt>
    <dd>When looking at standard javadoc, classes are linked together within the library. It is
    also possible, using javadoc, to create links to classes in other libraries, but it requires you to have
    the source code for those libraries during the javadoc generation phase.
      <span class="kiwidoc">kiwidoc</span>
      automatically links all the libraries it knows about. And, it's even dynamic: if a dependent
      library gets added to <span class="kiwidoc">kiwidoc</span> later, the link will
    automatically be created. As of 1.4.0, the javadoc is pre-rendered which improves the site speed
    drastically while still preserving the dynamicity when new libraries get added.</dd>

    <a name="F2Ref" id="F2Ref"></a>
    <dt>Searchable</dt>
    <dd><span class="kiwidoc">kiwidoc</span> is completely searchable. You can search javadoc content
    as well as manifest headers.</dd>

    <a name="F3Ref" id="F3Ref"></a>
    <dt>Proximity searching</dt>
    <dd>When running a search, the current viewed resource (whether it is a class, a package, etc.) is
    always treated as the starting point for the search. The search is then expanded outward from
    that point. This provides very intuitive results that favor items 'closer' to the current resource.
    For example,
    if you are currently looking at the class <tt>java.util.HashMap (java/j2se/1.5)</tt> and
    you run a search for <tt>map</tt>, <span class="kiwidoc">kiwidoc</span> will automatically
    show you the class
    <tt>java.util.Map (java/j2se/1.5)</tt> as the first result, then the following results will
    be 'close' to this one (for example, other <tt>java.util.Map</tt> in other versions of the
    jdk, then it will expand to other libraries).</dd>

    <a name="F4Ref" id="F4Ref"></a>
    <dt>Typeahead searching</dt>
    <dd>The search box features a typeahead feature on class names, package names and library
    names for fast access. The typeahead results are sorted using the proximity searching
    algorithm described previously. This feature makes the site extremely easy to navigate.</dd>

    <a name="F5Ref" id="F5Ref"></a>
    <dt>Camel case searching</dt>
    <dd>The typeahead searching feature allows you to use only the uppercases in a class name. For
    example, if you are looking for the class <tt>BufferedInputStream</tt>, simply enter
    <tt>BIS</tt>.</dd>

    <a name="F6Ref" id="F6Ref"></a>
    <dt>Dynamically generated javadoc</dt>
    <dd>In a similar fashion to the dynamically linked feature, the content of the javadoc is
    generated dynamically, covering <tt>{@inheritdoc}</tt> tag and inherited parameter 
    documentation.</dd>

    <a name="F7Ref" id="F7Ref"></a>
    <dt>Public View vs Private view</dt>
    <dd><span class="kiwidoc">kiwidoc</span> allows you to switch between 'public' view and
    'private' view at any point in time. 'public' view shows you the public API, while
    'private' view shows you additional non-public information (private methods, non exported packages, etc.). 
    Check out the class
    <tt><pk:contentLink resource="${new ClassResource(new LibraryVersionResource('java', 'j2se', '1.6'), 'java.lang.StringBuilder')}">StringBuilder</pk:contentLink></tt> 
      for a good example of the changes (pay close attention to the
    inheritance section!).
    </dd>

    <a name="F8Ref" id="F8Ref"></a>
    <dt>Display dependencies</dt>
    <dd>When looking at a library you can see its dependencies (direct, transitive and optional being
    called out separately). Most of the information is coming from the maven repositories,
    but internally <span class="kiwidoc">kiwidoc</span> can handle dependencies as long as
    they are provided.</dd>

    <a name="F9Ref" id="F9Ref"></a>
    <dt>Manifest view</dt>
    <dd>You can view the manifest of all the libraries indexed (provided there is one). The
    manifest view is OSGi aware and displays the long headers in a very readable fashion.</dd>

    <a name="F10Ref" id="F10Ref"></a>
    <dt>OSGi aware</dt>
    <dd><span class="kiwidoc">kiwidoc</span> is aware of OSGi and will tell you whether a library
    is an OSGi bundle or not.
    Since <span class="kiwidoc">kiwidoc</span> automatically indexes the manifests, it is easy to
    find all the bundles by simply searching for
    <g:link controller="search" action="findKeywords" params="[viewMode: 'p', k: 'bundle-symbolicname', m: 'm']">Bundle-SymbolicName</g:link>.
    </dd>

    <a name="F11Ref" id="F11Ref"></a>
    <dt>Facetted search</dt>
    <dd>Facetted search automatically categorizes search results, allowing you to narrow your search
    to just one category.</dd>

    <a name="F12Ref" id="F12Ref"></a>
    <dt>Easy third party access</dt>
    <dd>All links are following a REST style pattern making it easy for third parties to access
    <span class="kiwidoc">kiwidoc</span> information.</dd>

    <a name="F13Ref" id="F13Ref"></a>
    <dt>Scalable architecture</dt>
    <dd>Scalable architecture means that <span class="kiwidoc">kiwidoc</span> can be expanded to
    include a virtually limitless number of libraries.</dd>

    <a name="F14Ref" id="F14Ref"></a>
    <dt>Bytecode parsing</dt>
    <dd>Even if the source code for a library is not available, <span class="kiwidoc">kiwidoc</span>
    can generate basic reference information directly from the bytecode (including, in most cases,
    parameter names).</dd>

    <a name="F15Ref" id="F15Ref"></a>
    <dt>IDE Style display</dt>
    <dd>Information is displayed in a familiar way.  Icons inspired by those used in
    Eclipse will also help you feel right at home with <span class="kiwidoc">kiwidoc</span>.</dd>

    <a name="F16Ref" id="F16Ref"></a>
    <dt>Not advertising heavy</dt>
    <dd><span class="kiwidoc">kiwidoc</span> is a functional reference tool,
    not a thinly-disguised advertising billboard. You won't see <span class="kiwidoc">kiwidoc</span>
    littered with ads like so many other sites.</dd>
  </dl>
</div>
</div> %{-- content --}%
<div id="sidebar">
  <div id="sidebar-content">
    <g:render template="sidebarCommon" model="[pages: pages]"/>
    <div id="sidebar-features" class="entry">
      <h2>Features</h2>
      <ul>
        <li><a href="#F1Ref">Dynamically linked javadoc</a></li>
        <li><a href="#F2Ref">Searchable</a></li>
        <li><a href="#F3Ref">Proximity searching</a></li>
        <li><a href="#F4Ref">Typeahead searching</a></li>
        <li><a href="#F5Ref">Camel case searching</a></li>
        <li><a href="#F6Ref">Dynamically generated javadoc</a></li>
        <li><a href="#F7Ref">Public View vs Private view</a></li>
        <li><a href="#F8Ref">Display dependencies</a></li>
        <li><a href="#F9Ref">Manifest view</a></li>
        <li><a href="#F10Ref">OSGi aware</a></li>
        <li><a href="#F11Ref">Facetted search</a></li>
        <li><a href="#F12Ref">Easy third party access</a></li>
        <li><a href="#F13Ref">Scalable architecture</a></li>
        <li><a href="#F14Ref">Bytecode parsing</a></li>
        <li><a href="#F15Ref">IDE Style display</a></li>
        <li><a href="#F16Ref">Not advertising heavy</a></li>
      </ul>
    </div> %{-- sidebar-features --}%
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
