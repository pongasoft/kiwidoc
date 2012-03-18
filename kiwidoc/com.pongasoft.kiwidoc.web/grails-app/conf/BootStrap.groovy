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

import com.pongasoft.kiwidoc.index.api.KeywordQuery
import com.pongasoft.kiwidoc.model.Model
import com.pongasoft.kiwidoc.index.api.KiwidocIndex
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.NoSuchContentException
import com.pongasoft.kiwidoc.model.resource.LibraryResource
import org.linkedin.util.clock.Chronos

class BootStrap
{
  KiwidocIndex kiwidocIndex
  KiwidocLibraryStore libraryStore

  def android = new LibraryResource('android', 'android')

  def androidLibraries = [
    [resource: new LibraryVersionResource(android, '9'),
     platform: '2.3',
     codename: 'gingerbread'],
    [resource: new LibraryVersionResource(android, '8'),
     platform: '2.2',
     codename: 'froyo'],
    [resource: new LibraryVersionResource(android, '5'),
     platform: '2.0',
     codename: 'donut']
  ]

  def init = {servletContext ->
    Chronos c = new Chronos()
    log.info("Initializing index...")

    KiwidocIndex.Stats kstats = kiwidocIndex.stats

    def stats =
    [
      classes: kstats.classCount,
      packages: kstats.packageCount,
      libraries: kstats.libraryCount,
      bundles: 0
    ]

    // we run a search query to find out how many bundles
    KeywordQuery query = new KeywordQuery('bundle-symbolicname', 10)
    query.setResourceKinds(EnumSet.of(Model.Kind.MANIFEST))
    stats.bundles = kiwidocIndex.findByKeyword(query).totalResultsCount

    androidLibraries = androidLibraries.findAll { m ->
      try
      {
        libraryStore.loadContent(m.resource)
        return true
      }
      catch(NoSuchContentException e)
      {
        println "could not find ${m.resource}"
        return false
      }
    }

    log.info("Index initialized in ${c.elapsedTimeAsHMS} (c=${stats.classes}, p=${stats.packages}, l=${stats.libraries}, b=${stats.bundles})")
    servletContext.setAttribute('libraryStoreStats', stats)
    servletContext.setAttribute('androidLibraries', androidLibraries)
    System.gc()
  }

  def destroy = {
  }
} 