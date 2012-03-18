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

package com.pongasoft.tools.kiwidoc.index.builder.impl

import com.pongasoft.kiwidoc.model.LVROverviewModel
import com.pongasoft.kiwidoc.model.RepositoryModel
import com.pongasoft.kiwidoc.model.resource.LVROverviewResource
import com.pongasoft.kiwidoc.model.resource.RepositoryResource
import com.pongasoft.util.io.IOUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.linkedin.util.clock.Chronos
import org.linkedin.util.clock.Timespan
import com.pongasoft.kiwidoc.model.LibraryVersionModel

/**
 * @author yan@pongasoft.com  */
public class IndexBuilder
{
  public static final Log log = LogFactory.getLog(IndexBuilder.class);

  def opts

  private Wiring _wiring
  private volatile def _stats = Collections.synchronizedMap([l: 0, c: 0, p: 0])

  public synchronized void destroy()
  {
    _wiring?.destroy()
    _wiring = null
  }

  public void run()
  {
    _wiring = new Wiring(opts: opts)

    try
    {
      addShutdownHook {
        destroy()
      }

      Chronos c = new Chronos()

      RepositoryModel repository = _wiring.libraryStore.loadContent(RepositoryResource.INSTANCE)

      log.info "Processing ${repository.libraryVersions.size()} libraries"

      Thread.startDaemon {
        while(true)
        {
          // sleep 5s
          Thread.sleep(1000 * 5)
          log.info "t=${new Timespan(c.totalTime)};l=${_stats.l};p=${_stats.p};c=${_stats.c}"
        }
      }

      repository.libraryVersions.each { libraryVersion ->
        LVROverviewModel lovm = _wiring.libraryStore.loadContent(new LVROverviewResource(libraryVersion))

        //////////////////////////
        // handle prefix
        _wiring.kiwidocIndex.indexResources(lovm)

        def lvr = lovm.resource.libraryVersionResource

        //////////////////////////
        // handle keyword

        // first we unindex
        try
        {
          _wiring.keywordIndex.unindexLibraryVersion(lvr)
        }
        catch (Exception e)
        {
          log.warn("Failed to unindex ${lvr}: ${e.message}")
        }

        // then we index
        LibraryVersionModel lvm = _wiring.libraryStore.loadContent(lvr)
        lvm.allPackages.each { p ->
          try
          {
            def models = []
            p.allClassesResources.each { classResource ->
              loadContent(classResource) {
                models << it
                _stats.c = _stats.c + 1
              }
            }
            loadContent(p.resource) {
              models << it
              _stats.p = _stats.p + 1
            }
            _wiring.keywordIndex.batchIndexModels(models)
          }
          catch (Exception e)
          {
            log.warn("Failed to index classes in package ${p.name}: ${e.message}")
          }
        }

        try
        {
          def models = []
          if(lvm.hasManifest)
          {
            loadContent(lvm.manifestResource) {
              models << it
            }
          }
          _wiring.keywordIndex.batchIndexModels(models)
        }
        catch (Exception e)
        {
          log.warn("Failed to index library model ${lvm.resource}: ${e.message}")
        }

        _stats.l = _stats.l + 1
      }

      log.info("Saving stats")
      IOUtils.serializeToFile(_wiring.kiwidocIndex.stats, new File("${_wiring.cachesDir}/stats.ser"))

      Chronos c1 = new Chronos()
      log.info("Optimizing keyword index")
      _wiring.keywordIndex.optimize()
      log.info("Keyword index optimized in ${c1.elapsedTimeAsHMS}")

      log.info "${new Timespan(c.totalTime)} ${_wiring.kiwidocIndex.stats}"
    }
    finally
    {
      destroy()
    }
  }

  private def loadContent(resource, closure)
  {
    try
    {
      closure(_wiring.libraryStore.loadContent(resource))
    }
    catch(Exception e)
    {
      log.warn("Failed to load class ${resource}: ${e.message}")
    }
  }

  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy IndexBuilder.groovy")

    cli.h(longOpt: 'help', 'usage information')
    cli._(longOpt: 'spring-config', 'spring configuration file path', argName: 'path', args: 1, required: false)
    cli._(longOpt: 'kiwidoc-root', "kiwidoc root folder (default to ${Wiring.DEFAULT_KIWIDOC_ROOT})", argName: 'folder', args: 1, required: false)
    cli.o(longOpt: 'output', "base path for output (default to ${Wiring.DEFAULT_CACHES_DIR})", argName: 'path', args: 1, required: false)

    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
      System.exit(0)
    }

    new IndexBuilder(opts: opts).run()

    System.exit(0)
  }
}