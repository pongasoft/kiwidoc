
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

package com.pongasoft.kiwidoc.web.utils

import com.pongasoft.kiwidoc.model.Model
import com.pongasoft.util.core.enums.EnumCodec
import com.pongasoft.kiwidoc.index.api.KeywordQuery
import com.pongasoft.kiwidoc.index.api.Visibility
import com.pongasoft.kiwidoc.model.resource.RepositoryResource

/**
 * @author yan@pongasoft.com
*/
class SearchParams
{
  final def _params = [:]
  final def _pathManager

  SearchParams(pathManager, params)
  {
    _pathManager = pathManager
    init(params)
  }

  SearchParams init(params)
  {
    ['k', 'r', 'l', 'm', 'viewMode'].each {
      def v = params[it]
      if(v != null)
        _params[it] = v
    }

    _params.k = _params.k?.trim()

    return this
  }

  Visibility getVisibility()
  {
    return EnumCodec.INSTANCE.decode(Visibility, params.viewMode)
  }

  SearchParams setKeyword(keyword)
  {
    return newSearchParams().init(k: keyword)
  }

  def getKeyword()
  {
    return params.k
  }

  SearchParams setBaseResource(baseResource)
  {
    return newSearchParams().init(r: _pathManager.computePath(baseResource))
  }

  def getBaseResource()
  {
    return extractBaseResource(params.r)
  }

  private def extractBaseResource(String uri)
  {
    try
    {
      return _pathManager.computeResource(new URI(uri))
    }
    catch(Exception e)
    {
      return RepositoryResource.INSTANCE;
    }
  }

  SearchParams setFilterSearch()
  {
    return newSearchParams().init(l: 1)
  }

  def isFilteredSearch()
  {
    return (params.l ?: 0).toString() == '1'
  }

  SearchParams setFilterSearch(baseResource)
  {
    return setBaseResource(baseResource).setFilterSearch()
  }

  SearchParams setResourceKind(resourceKind)
  {
    return newSearchParams().init(m: EnumCodec.INSTANCE.encode(resourceKind))
  }

  SearchParams setResourceKinds(resourceKinds)
  {
    return newSearchParams().init(m: resourceKinds.collect { EnumCodec.INSTANCE.encode(it)}.join(','))
  }

  Set<Model.Kind> getResourceKinds()
  {
    if(params.m)
    {
      def resourceKinds = EnumSet.noneOf(Model.Kind)
      params.m.split(',').each {
        resourceKinds << EnumCodec.INSTANCE.decode(Model.Kind, it)
      }
      return resourceKinds
    }
    return null
  }

  private def newSearchParams()
  {
    return new SearchParams(_pathManager, [*:_params])
  }

  private def newSearchParams(excludedParams)
  {
    def params = [*:_params]
    excludedParams.each { params.remove(it) }
    return new SearchParams(_pathManager, params)
  }

  def getParams()
  {
    return _params
  }

  def toKeywordQuery(maxResults)
  {
    def query = new KeywordQuery(keyword, maxResults)

    query.visibility = visibility
    query.baseResource = baseResource
    query.filter = isFilteredSearch() ? baseResource : null
    query.resourceKinds = resourceKinds

    return query
  }

  SearchParams clearFacets()
  {
    return newSearchParams(['l', 'm'])
  }

  boolean hasFacets()
  {
    return isFilteredSearch() || resourceKinds
  }
}