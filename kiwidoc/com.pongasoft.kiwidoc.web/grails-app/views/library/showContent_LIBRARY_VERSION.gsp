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
  <meta name="Description" content="Type: Versioned Library, Name: ${content.resource}, Location: ${content.resource.parent.encodeAsHTML()}, View: ${preferences.isViewModePrivate ? 'private': 'public'} javadoc API"/>
  <title><pk:renderModelResource model="${content}"/> - kiwidoc</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'lvr.css')}" />
</head>
<body>
<h1 class="heading"><pk:resourcePath resource="${content.resource}"/></h1>
<div id="content">
<h2 class="title">Library: <pk:lvr resource="${content.resource}"/></h2>
<g:if test="${content.overview?.hasDoc()}">
<h2 class="heading">Overview</h2>
<div class="library-overview">
  <pd:renderDoc doc="${content.overview}" resource="${content.resource}" groupByStyle="lvr"/>
</div>
</g:if>

 <g:each in="${['Exported Packages': ['Public', content.exportedPackages], 'Private Packages': ['Internal', content.privatePackages]]}" var="entry">
   <g:if test="${entry.value[1]}">
     <div class="entries ${entry.value[0]}">
       <a name="package${entry.value[0]}Ref" id="package${entry.value[0]}Ref"></a>
       <h2 class="heading">${entry.key}</h2>
       <ul>
         <g:each var="p" in="${ModelUtils.sortByName(entry.value[1])}">
           <li class="entry ${entry.value[0]} ${ModelUtils.apiClass(p)}">
             <pk:contentLink resource="${p.resource}">${p.name}</pk:contentLink>
             %{--<ul>--}%
               %{--<g:each var="c" in="${p.exportedClassesResources}">--}%
                 %{--<li>--}%
                   %{--<pk:contentLink resource="${c.value}"><span class="class">${c.key}</span></pk:contentLink>--}%
                 %{--</li>--}%
               %{--</g:each>--}%
             %{--</ul>--}%
           </li>
         </g:each>
       </ul>
     </div>
   </g:if>
 </g:each>
</div> <% /* end content */ %>
<div id="sidebar">
  <div id="sidebar-content">
    <% /* history */ %>
    <g:render template="/block/block" model="[action: 'h', args: [r: pk.resourceURI(resource: content.resource), viewMode: params.viewMode]]"/>

    <div id="sidebar-info" class="entry">
      <h2>Info</h2>
      <ul>
        <li>Processed from: <pk:split separator=", " in="${content.builtFrom}">${var.toString().toLowerCase()}</pk:split></li>
        <g:if test="${!content.isJDK()}">
        <li>
          <g:if test="${content.jdkResource}">
            <pk:contentLink resource="${content.jdkResource}">JDK: 1.${content.jdkVersion}</pk:contentLink>
          </g:if>
          <g:else>
            Compiled with jdk: 1.${content.jdkVersion}
          </g:else>
        </li>
        </g:if>
        <g:each in="${['Exported Packages': ['Public', content.exportedPackages], 'Private Packages': ['Internal', content.privatePackages]]}" var="entry">
          <g:if test="${entry.value[1]}">
          <li>
            <a href="#package${entry.value[0]}Ref" title="Shortcut to ${entry.key}">${entry.key}: ${entry.value[1].size()}</a>
          </li>
        </g:if>
        </g:each>
        <g:if test="${content.hasManifest}">
          <li>
            <pk:contentLink resource="${content.manifestResource}">Manifest</pk:contentLink>
          </li>
        </g:if>
      </ul>
    </div> <% /* end sidebar-info */ %>
    <g:if test="${content.OSGiBundle}">
    <div id="sidebar-OSGi" class="entry">
    <h2>OSGi</h2>
      <ul>
        <g:each in="${['Bundle-SymbolicName', 'Fragment-Host'].collect {content.OSGiModel.findHeader(it)}}" var="header">
          <g:if test="${header}">
            <li>${header.name}: ${header.valueAsString}</li>
          </g:if>
        </g:each>
        <g:each in="${['Export-Package', 'Import-Package'].collect {content.OSGiModel.findHeader(it)}}" var="header">
          <g:if test="${header}">
            <li>${header.name}: ${header.value.size()}</li>
          </g:if>
        </g:each>
      </ul>
    </div> %{-- sidebar-OSGi --}%
    %{--<div id="sidebar-dependencies-OSGi" class="entry">--}%
      %{--<h2>Dependencies (OSGi)</h2>--}%
      %{--<g:each in="${['Import-Package', 'Require-Bundle', 'DynamicImport-Package'].collect {content.OSGiModel.findHeader(it)}}" var="header">--}%
        %{--<g:if test="${header}">--}%
          %{--<h3>${header.name}</h3>--}%
          %{--<pk:ul in="${OSGiUtils.toSplittedValues(header.value)}"><pk:split in="${var}" separator=";" var="x">${x.encodeAsHTML()}</pk:split></pk:ul>--}%
        %{--</g:if>--}%
      %{--</g:each>--}%
    %{--</div> --}%%{-- sidebar-dependencies-OSGi --}%
    %{--<g:if test="${content.OSGiModel.findHeader('Export-Package')}">--}%
    %{--<div id="sidebar-exported-packages-OSGi" class="entry">--}%
      %{--<h2>Exported Packages (OSGi)</h2>--}%
      %{--<pk:ul in="${OSGiUtils.toSplittedValues(content.OSGiModel.findHeader('Export-Package').value)}"><pk:split in="${var}" separator=";" var="x">${x.encodeAsHTML()}</pk:split></pk:ul>--}%
    %{--</div> --}%%{-- sidebar-exported-packages-OSGi --}%
    %{--</g:if>--}%
    </g:if>
    <div id="sidebar-dependencies" class="entry">
    <h2>Dependencies</h2>
      <g:if test="${content.dependencies.hasDependencies}">
        <g:each in="['Direct': content.dependencies.getDirectDependencies(false),
                     'Direct (optional)': content.dependencies.getDirectDependencies(true),
                     'Transitive': content.dependencies.getTransitiveDependencies(false),
                     'Transitive (optional)': content.dependencies.getTransitiveDependencies(true)]" var="entry">
          <g:if test="${entry.value}">
          <h3>${entry.key}</h3>
          <ul>
            <g:each in="${ModelUtils.sortByName(entry.value)}" var="dependency">
              <li class="${content.dependencies.isOptionalDependency(dependency) ? 'optional' : ''}">
                <g:if test="${resolvedDependencies.contains(dependency)}">
                  <pk:contentLink resource="${dependency}">${dependency.encodeAsHTML()}</pk:contentLink>
                </g:if>
                <g:else>
                  <span class="unresolved">${dependency.encodeAsHTML()}</span>
                </g:else>
              </li>
            </g:each>
          </ul>
          </g:if>
        </g:each>
      </g:if>
      <g:else>
        <ul>
          <li>None</li>
        </ul>
      </g:else>
    </div> <% /* end sidebar-depedencies */ %>
    <g:each in="${['Exported Packages': ['Public', content.exportedPackages], 'Private Packages': ['Internal', content.privatePackages]]}" var="entry">
      <g:if test="${entry.value[1]}">
        <div class="sidebar-${entry.value[0]} entry">
          <h2>${entry.key}</h2>
          <ul>
            <g:each var="p" in="${ModelUtils.sortByName(entry.value[1])}">
              <li>
                <pk:contentLink resource="${p.resource}">${p.name}</pk:contentLink>
              </li>
            </g:each>
          </ul>
        </div>
      </g:if>
    </g:each>
  </div> <% /* end sidebar-content */ %>
</div> <% /* end sidebar */ %>
</body>
</html>