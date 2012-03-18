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

<%@ page import="com.pongasoft.kiwidoc.model.Model" contentType="text/html;charset=UTF-8" %>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>${results.query.keyword.encodeAsHTML()} - kiwidoc Search</title>
  <link rel="stylesheet" href="${resource(dir:'css',file:'results.css')}" />
</head>
<body>
<h1 class="heading">
  <g:if test="${results.searchResults.totalResultsCount > 0}">
  <ul>
    <li class="r0">Results <span class="c">${results.range.from + 1}</span> - <span class="c">${results.range.to + 1}</span> of <span class="c">${results.searchResults.totalResultsCount}</span> for <span class="c">${results.query.keyword.encodeAsHTML()}</span></li>
  </ul>
  </g:if>
  <g:else>
    <ul>
      <li class="r0">No results for <span class="c">${results.query.keyword.encodeAsHTML()}</span></li>
    </ul>
  </g:else>
</h1>
<div id="content">
<g:if test="${results.searchResults.totalResultsCount > 0}">
  <g:if test="${results.paginate}">
    <div class="pagination">
      <g:paginate total="${results.searchResults.topResults.size()}" controller="search" action="findKeywords" params="${results.searchParams.params}"/>
    </div> %{-- pagination --}%
  </g:if>
  <dl>
  <g:each in="${results.pageResults}" var="entry">
    <dt class="${ModelUtils.apiClass(entry.value[0])}"><div class="${ModelUtils.typeClass(entry.value[0])}"><pk:contentLink resource="${entry.key}"><pk:renderSimpleModelResource model="${entry.value[0]}"/></pk:contentLink></div></dt>
    <dd class="summary"><pk:split in="${entry.value[1]}" var="highlight" separator="..." begin=".." end="..">${highlight}</pk:split></dd>
    <dd class="bc"><pk:resourcePath2 resource="${entry.key}"/></dd>
  </g:each>
  </dl>

  <g:if test="${results.paginate}">
    <div class="pagination">
      <g:paginate total="${results.searchResults.topResults.size()}" controller="search" action="findKeywords" params="${results.searchParams.params}"/>
    </div> %{-- pagination --}%
  </g:if>
  </g:if>
  <g:else>
    No results found for <span class="keywords">${results.query.keyword.encodeAsHTML()}</span>.
  </g:else>
</div> %{-- content --}%

<div id="sidebar">
  <div id="sidebar-content">
    <div id="sidebar-criteria" class="entry">
    <h2>Search Criteria</h2>
      <ul>
        <div class="criteria">
          <li>Sort By:</li>
          <li class="indent">Proximity, Relevance</li>
        </div>
        <div class="criteria">
          <li>Proximity starting point:</li>
          <li class="indent"><div class="${ModelUtils.resourceClass(content.resource)}"><pk:renderResource resource="${content.resource}"/></div></li>
        </div>
        <g:if test="${results.searchParams.hasFacets()}">
          <div class="criteria">
            <li>Limited to:</li>
            <g:if test="${results.searchParams.isFilteredSearch()}"><li class="indent"><div class="${ModelUtils.resourceClass(content.resource)}"><pk:renderResource resource="${content.resource}"/></div></li></g:if>
            <g:each in="${results.searchParams.resourceKinds}" var="kind">
              <li class="indent"><div class="${ModelUtils.resourceKind(kind)}">${ModelUtils.modelKindName(kind)}</div></li>
            </g:each>
          </div>
          <li><g:link controller="search" action="findKeywords" params="${results.searchParams.clearFacets().params}">Expand your search...</g:link></li>
        </g:if>
      </ul>
    </div> %{-- sidebar-criteria --}%
    <div id="sidebar-summary" class="entry">
      <h2>Summary</h2>
      <ul>
        <g:if test="${results.range}">
          <li>Showing: ${results.range.from + 1} - ${results.range.to + 1}</li>
        </g:if>
        <li>Results: ${results.searchResults.topResults.size()}</li>
        <li>Total: ${results.searchResults.totalResultsCount}</li>
      </ul>
    </div> %{-- sidebar-summary --}%
    <div id="sidebar-kind" class="entry">
      <h2>Types</h2>
      <pk:ul in="${results.searchResults.groupByKindCount.keySet()}" var="kind">
        <g:if test="${results.searchResults.groupByKindCount[kind]}">
          <div class="${ModelUtils.resourceKind(kind)}"><g:link title="Limit search to: ${ModelUtils.modelKindName(kind)}" controller="search" action="findKeywords" params="${results.searchParams.setResourceKind(kind).params}">${ModelUtils.modelKindName(kind)}&nbsp;(${results.searchResults.groupByKindCount[kind]})</g:link></div>
        </g:if>
      </pk:ul>
    </div>  %{-- sidebar-kind --}%
    <g:each var="entry" in="${[Versions: [results.groupByLVRCount, 'RV'], Libraries: [results.groupByLibrariesCount, 'RL'], Organizations: [results.groupByOrganisationsCount, 'RO']]}">
      <div id="sidebar-groupby-${entry.key}" class="entry">
        <h2><div class="T-icon ${entry.value[1]}">${entry.key}</div></h2>
        <ul>
          <g:each in="${ModelUtils.sortBy(entry.value[0].keySet()) {ModelUtils.toString(it)}}" var="key">
            <li><g:link title="Limit search to: ${ModelUtils.toString(key).encodeAsHTML()}" controller="search" action="findKeywords" params="${results.searchParams.setFilterSearch(key).params}">${ModelUtils.toString(key).encodeAsHTML()}&nbsp;(${entry.value[0][key]})</g:link></li>
          </g:each>
        </ul>
      </div>
    </g:each>
  </div> %{-- sidebar-content --}%
</div> %{-- sidebar --}%
</body>
</html>
