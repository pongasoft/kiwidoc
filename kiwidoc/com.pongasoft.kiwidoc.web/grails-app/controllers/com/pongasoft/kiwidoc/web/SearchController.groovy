/*
 * Copyright (c) 2012 Yan Pujante
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pongasoft.kiwidoc.web

import com.pongasoft.kiwidoc.index.api.KiwidocIndex
import com.pongasoft.kiwidoc.index.api.Visibility
import com.pongasoft.kiwidoc.index.api.KeywordQuery
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.index.api.SearchResults
import com.pongasoft.kiwidoc.model.resource.PathManager
import com.pongasoft.kiwidoc.model.resource.RepositoryResource
import com.pongasoft.kiwidoc.web.utils.PaginationHelper
import grails.converters.JSON
import com.pongasoft.kiwidoc.model.resource.ClassResource
import com.pongasoft.kiwidoc.model.resource.PackageResource
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.kiwidoc.web.utils.ModelUtils
import com.pongasoft.kiwidoc.index.api.MalformedQueryException
import com.pongasoft.kiwidoc.index.api.SearchQuery
import com.pongasoft.kiwidoc.web.utils.SearchParams
import com.pongasoft.util.core.enums.EnumCodec
import com.pongasoft.kiwidoc.model.resource.ManifestResource
import org.linkedin.util.clock.Chronos

class SearchController extends BaseController
{

  public static final int MAX_KEYWORD_RESULTS = 500
  public static final int MAX_PREFIX_RESULTS = 10

  public static PaginationHelper PH =
    new PaginationHelper(defaultMax: 20, maxMax: 50)

  KiwidocIndex kiwidocIndex
  KiwidocLibraryStore libraryStore
  PathManager pathManager

  /**
   * Find by prefix
   */
  def findByPrefix = {
    def model = initModel()

    def prefix = params.query?.trim()
    def baseResource = extractBaseResource(params.r)

    def json = []

    if(prefix && prefix.size() > 1)
    {
      def query = new KeywordQuery(prefix, MAX_PREFIX_RESULTS)
      query.baseResource = baseResource
      adjustQuery(query, model.preferences)
      Chronos c = new Chronos()
      def results = kiwidocIndex.combinedSearch(query)
      log.info("p:'${prefix}',r:${baseResource},s:${results.topResults.size()},a:${results.totalResultsCount},t:${c.elapsedTimeAsHMS}")
      def allUppercase = isAllUppercase(prefix)
      results.topResults.each { result ->
        def resource = result.resource
        json << [id: resource.toString(),
                 name: renderPrefixResult(resource, result.queryType, prefix, allUppercase),
                 href: pk.createContentLink(resource: resource, viewMode: EnumCodec.INSTANCE.encode(results.visibility))]
      }
    }

    json = [result: json]

    render json as JSON
  }

  /**
   * Open search (implemented by Firefox)
   * http://www.opensearch.org/Specifications/OpenSearch/Extensions/Suggestions
   */
  def openSearch = {
    def model = initModel()

    println params

    def prefix = params.k?.trim()
    def baseResource = params.r ?: 'java/j2se/1.6'
    baseResource = extractBaseResource(baseResource)

    def json = []
    def completions = []
    def descriptions = []
    def urls = []

    if(prefix && prefix.size() > 1)
    {
      def query = new KeywordQuery(prefix, MAX_PREFIX_RESULTS)
      query.baseResource = baseResource
      adjustQuery(query, model.preferences)
      Chronos c = new Chronos()
      def results = kiwidocIndex.combinedSearch(query)
      log.info("o:'${prefix}',r:${baseResource},s:${results.topResults.size()},a:${results.totalResultsCount},t:${c.elapsedTimeAsHMS}")
      results.topResults.each { result ->
        def resource = result.resource
        completions << ModelUtils.toString(resource)
        descriptions << "${ModelUtils.toString(resource)} [${resource.modelKind.toString().toLowerCase().capitalize()}]"
        urls << pk.createContentLink(absolute: true, resource: resource, viewMode: EnumCodec.INSTANCE.encode(results.visibility))
      }

      json = [prefix, completions, descriptions, urls]
    }

    render json as JSON
  }

  def firefoxSearchPlugin = {
    // excluding search completion for now
    /*
  <Url type="application/x-suggestions+json" template="${g.createLink(absolute: true, controller: 'search', action: 'openSearch', params: [viewMode: params.viewMode, k: '{searchTerms}', r: 'java/j2se/1.6'])}"/>
  <Url type="text/html" method="GET" template="${g.createLink(absolute: true, controller: 'search', action: 'findByPrefix', params: [viewMode: params.viewMode, query: '{searchTerms}', r: 'java/j2se/1.6'])}"/>
     */
    response.contentType = 'application/opensearchdescription+xml'
    render """<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/"
                       xmlns:moz="http://www.mozilla.org/2006/browser/search/">
  <ShortName>kiwidoc</ShortName>
  <Description>A fresh way to browse and search javadoc</Description>
  <InputEncoding>UTF-8</InputEncoding>
  <Image width="16" height="16" type="image/x-icon">http://www.kiwidoc.com/favicon.ico</Image>
  <Url type="text/html" method="GET" template="${g.createLink(absolute: true, controller: 'search', action: 'findKeywords', params: [viewMode: params.viewMode])}">
    <Param name="k" value="{searchTerms}"/>
    <Param name="r" value="java/j2se/1.6"/>
  </Url>
  <moz:SearchForm>${g.createLink(absolute: true, controller: 'root', action: 'index')}</moz:SearchForm>
</OpenSearchDescription>"""
  }


  def openSearchPlugin = {
    response.contentType = 'application/opensearchdescription+xml'
    render """<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/"
                       xmlns:moz="http://www.mozilla.org/2006/browser/search/">
  <ShortName>kiwidoc</ShortName>
  <Description>A fresh way to browse and search javadoc</Description>
  <InputEncoding>UTF-8</InputEncoding>
  <Image width="16" height="16" type="image/x-icon">http://www.kiwidoc.com/favicon.ico</Image>
  <Url type="text/html" method="GET" template="${g.createLink(absolute: true, controller: 'search', action: 'findKeywords', params: [viewMode: params.viewMode])}">
    <Param name="k" value="{searchTerms}"/>
    <Param name="r" value="java/j2se/1.6"/>
  </Url>
  <Url type="application/x-suggestions+json" template="${g.createLink(absolute: true, controller: 'search', action: 'openSearch', params: [viewMode: params.viewMode])}">
    <Param name="k" value="{searchTerms}"/>
    <Param name="r" value="java/j2se/1.6"/>
  </Url>
</OpenSearchDescription>"""
  }

  /**
   * Find keywords
   */
  def findKeywords = {
    def model = initModel()

    def searchParams = new SearchParams(pathManager, params)

    if(request.method == 'POST')
    {
      def link = createLink(action: 'findKeywords', params: searchParams.params)
      redirect(url: link)
      return
    }

    def query = searchParams.toKeywordQuery(MAX_KEYWORD_RESULTS)

    def ksrm = [
      searchParams: searchParams,
      baseResource: query.baseResource,
      query: query,
      max: PH.extractMax(params),
      offset: PH.extractOffset(params),
      pageResults: [:],
      paginate: false,
      groupByLVRCount: new LinkedHashMap(),
      groupByLibrariesCount: new LinkedHashMap(),
      groupByOrganisationsCount: new LinkedHashMap(),
      searchResults: SearchResults.NO_RESULTS
    ]

    if(query.keyword)
    {
      Chronos c = new Chronos()
      def results
      try
      {
        results = kiwidocIndex.combinedSearch(query)
      }
      catch (MalformedQueryException e)
      {
        log.warn("malformed query: ${query.keyword}")
        results = SearchResults.NO_RESULTS
      }
      ksrm.searchResults = results
      def info = []
      info << "k:'${query.keyword}'" << "r:${query.baseResource}"
      if(query.resourceKinds)
        info << "m:${query.resourceKinds.collect {EnumCodec.INSTANCE.encode(it)}}"
      if(query.filter)
        info << 'l: true'
      info << "s:${results.topResults.size()}"
      info << "a:${results.totalResultsCount}"
      info << "t:${c.elapsedTimeAsHMS}"
      log.info(info.join(','))
      if(results.totalResultsCount > 0)
      {
        ksrm.range = PH.computeRange(ksrm.offset, ksrm.max, results.topResults.size())

        def pageResults = results.topResults[ksrm.range]
        ksrm.paginate = pageResults.size() < results.totalResultsCount

        def models = libraryStore.loadContent(pageResults.resource)
        def modelsMap = [:]
        models.each { m ->
          modelsMap[m.resource] = m
        }

        def keywordResults = pageResults.findAll { it.queryType == SearchQuery.Type.keyword }.resource

        def highlightedResults = [:]

        if(keywordResults)
        {
          highlightedResults = 
            kiwidocIndex.highlightResults(query, modelsMap.subMap(keywordResults).values().findAll { it })
        }

        pageResults.resource.each { result ->
          def m = modelsMap[result]
          if(m)
          {
            ksrm.pageResults[result] = [m, highlightedResults[result]]
          }
          else
          {
            log.warn("${result} appears to be in the keyword index")
          }
        }

        ksrm.searchResults.groupByLibrariesCount.each { lvr, count ->
          if(lvr && !ksrm.groupByLVRCount[lvr])
          {
            ksrm.groupByLVRCount[lvr] = count
            def parent = lvr.parent
            ksrm.groupByLibrariesCount[parent] = (ksrm.groupByLibrariesCount[parent] ?: 0) + count
            parent = parent.parent
            ksrm.groupByOrganisationsCount[parent] = (ksrm.groupByOrganisationsCount[parent] ?: 0) + count
          }
        }
      }
    }

    model.results = ksrm
    model.content = [resource: query.baseResource]

    render (view: 'keywordResults', model: model)
  }

  private def extractBaseResource(String uri)
  {
    try
    {
      return pathManager.computeResource(new URI(uri))
    }
    catch(Exception e)
    {
      return RepositoryResource.INSTANCE;
    }
  }

  private def adjustQuery(query, preferences)
  {
    if(preferences.isViewModePrivate)
      query.visibility = Visibility.publicAndPrivate
    else
      query.visibility = Visibility.publicOnly
  }

  private def renderPrefixResult(resource, resultType, prefix, allUppercase)
  {
    def out = new StringBuilder()

    if(resource instanceof ClassResource)
    {
      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">${highlightPrefix(resource, resultType, prefix, allUppercase)} (${resource.packageName.encodeAsHTML()}) <span class=\"l-lvr\">[${pk.lvr(resource: resource.libraryVersionResource)}]</span></span>"
      return out.toString()
    }

    if(resource instanceof PackageResource)
    {
      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">${highlightPrefix(resource.packageName, resultType, prefix)} [${pk.lvr(resource: resource.libraryVersionResource)}]</span>"
      return out.toString()
    }

    if(resource instanceof LibraryVersionResource)
    {
      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">"
      out << highlightPrefix(resource.toString(), resultType, prefix)
      out << '</span>'
      return out.toString()
    }

    if(resource instanceof ManifestResource)
    {
      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">"
      out << highlightPrefix(resource.toString(), resultType, prefix)
      out << '</span>'
      return out.toString()
    }

    // TODO HIGH YP: add methods and fields  
//    if(resource instanceof MethodResource)
//    {
//      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">${resource.name}</span>"
//      return out.toString()
//    }
//
//    if(resource instanceof FieldResource)
//    {
//      out << "<span class=\"${ModelUtils.resourceClass(resource)}\">${resource.name}</span>"
//      return out.toString()
//    }

    return out.toString()
  }

  private def highlightPrefix(ClassResource resource, resultType, prefix, allUppercase)
  {
    def out = new StringBuilder()
    def className = resource.simpleClassNameWithDot
    if(resource.isInnerClass())
    {
      out << resource.outerClassResource.simpleClassNameWithDot << '.'
      className = resource.innerClassName
    }

    if(allUppercase && resultType == SearchQuery.Type.prefix)
    {
      out << highlightUppercasePrefix(className, prefix)
    }
    else
    {
      out << highlightPrefix(className, resultType, prefix)
    }

    return out.toString()
  }

  private def highlightPrefix(String resource, resultType, prefix)
  {
    if(resultType == SearchQuery.Type.keyword)
    {
      return resource.encodeAsHTML()
    }

    def locations = findPrefixLocations(resource, prefix)

    def out = new StringBuilder()

    locations.eachWithIndex { range, idx ->
      def text = resource[range].encodeAsHTML()
      if(idx % 2 == 0)
        out << text
      else
      {
        out << '<span class="highlight">'
        out << text
        out << '</span>'
      }
    }

    return out.toString()
  }

  private def findPrefixLocations(String resource, prefix)
  {
    resource = resource.toLowerCase()
    prefix = prefix.toLowerCase()

    def locations = []

    def idx = 0
    while(idx != -1 && idx < resource.size())
    {
      def newIdx = resource.indexOf(prefix, idx)
      if(newIdx != -1)
      {
        locations << (idx..<newIdx)
        idx = newIdx + prefix.size()
        locations << (newIdx..<idx)
      }
      else
      {
        locations << (idx..-1)
        idx = -1
      }
    }

    return locations
  }

  private def highlightUppercasePrefix(String resource, String prefix)
  {
    def out = new StringBuilder()

    def pc = prefix.chars
    def pi = 0
    def rc = resource.chars
    def ri = 0

    while(pi < pc.length)
    {
      def r = rc[ri]

      if(rc[ri] == pc[pi])
      {
        out << '<span class="highlight">'
        out << "${r}".encodeAsHTML()
        out << '</span>'

        pi++
      }
      else
      {
        out << "${r}".encodeAsHTML()
      }
      ri++
    }

    if(ri < rc.length)
    {
      out << resource[ri..rc.length - 1].encodeAsHTML()
    }

    return out.toString()
  }

  private def isAllUppercase(String prefix)
  {
    boolean allUppercase = true;

    int len = prefix.length();
    for(int i = 0; i < len; i++)
    {
      if(!Character.isUpperCase(prefix.charAt(i)))
      {
        allUppercase = false;
        break;
      }
    }

    return allUppercase
  }
}
