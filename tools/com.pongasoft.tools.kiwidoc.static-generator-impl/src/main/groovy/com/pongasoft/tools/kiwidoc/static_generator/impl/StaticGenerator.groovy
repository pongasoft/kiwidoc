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

package com.pongasoft.tools.kiwidoc.static_generator.impl

import com.pongasoft.kiwidoc.builder.NoSuchContentException
import com.pongasoft.kiwidoc.model.Model
import com.pongasoft.kiwidoc.model.resource.Resource
import com.pongasoft.util.io.DevNullOutputStream
import java.util.zip.Adler32
import java.util.zip.CheckedInputStream
import java.util.zip.Checksum
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.linkedin.util.clock.Chronos
import org.linkedin.util.clock.Timespan
import org.linkedin.util.io.IOUtils
import org.linkedin.util.lang.MemorySize

/**
 * @author yan@pongasoft.com  */
public class StaticGenerator
{
  public static final Log log = LogFactory.getLog(StaticGenerator.class);

  def opts

  private Wiring _wiring
  private int _maxDepth
  private Set _queue = new LinkedHashSet()
  private Set _pending = new HashSet()
  private Map _stats = [resources: 0L, size: 0L, ctime: 0L, pages: 0L, skPages: 0L, skSize: 0L]
  private Map<Object, Throwable> _errors = [:]
  private Collection<Thread> _threads = []

  private org.linkedin.util.io.resource.Resource _inputResource
  private org.linkedin.util.io.resource.Resource _outputResource

  private Chronos _chronos

  public void run()
  {
    _wiring = new Wiring(opts: opts)

    try
    {
      addShutdownHook {
        destroy()
      }

      _inputResource = _wiring.inputResource
      _outputResource = _wiring.outputResource


      if(opts.'static')
      {
        // we add main index page!
        putResourceInQueue('')

        _wiring.staticFile.eachLine { String resource ->
          if(resource && !resource.startsWith('#'))
            putResourceInQueue(resource)
        }
      }

      def resourceURI = opts.'resource-root'
      if(resourceURI)
      {
        Resource rootResource = _wiring.pathManager.computeResource(new URI(resourceURI))

        int depth = (opts.depth ?: 0) as int
        _maxDepth = Math.min(rootResource.depth + depth, 5)

        try
        {
          // we make sure that the root resource exists in the first place
          _wiring.getLibraryStore().loadContent(rootResource)
        }
        catch (NoSuchContentException e)
        {
          log.error "No such content: ${rootResource}"
          return
        }

        // we seed the queue
        Chronos c = new Chronos()
        seedResourceQueue(rootResource)
        log.info "Resource seeded in ${new Timespan(c.tick())}"
      }

      int threadCount = (opts.'thread-count' ?: 1) as int

      _chronos = new Chronos()

      log.info "Processing ${_queue.size()} resources"

      (1..threadCount).each {
        _threads << Thread.start(process)
      }

      Thread.startDaemon {
        while(true)
        {
          // sleep 10s
          Thread.sleep(1000 * 10)
          log.info stats ?: 'no stats yet'
        }
      }

      _threads.each { it.join() }

      if(opts.'static')
      {
        // we copy index.html at the root as well
        fetchResource(_outputResource.createRelative("${_wiring.contextPath}/index.html"),
                      _outputResource.createRelative("index.html"),
                      [:])
      }

      def computedStats = stats
      if(computedStats)
        println computedStats
    }
    finally
    {
      destroy()
    }
  }

  Closure process = {
    while(true)
    {
      def resource = getResourceFromQueue()
      if(resource == null)
        return

      try
      {
        def stats = [:]
        Chronos c = new Chronos()
        try
        {
          processResource(resource, stats)
        }
        catch(Throwable th)
        {
          stats.error = th
          log.warn("Unknown exception detected for resource ${resource}")
          if(log.isDebugEnabled())
            log.debug("Unknown exception detected for resource ${resource}", th)
        }

        stats.time = c.tick()
        recordStats(resource, stats)
      }
      finally
      {
        completedResource(resource)
      }
    }
  }

  void processResource(String resource, Map stats)
  {
    String path = "${_wiring.contextPath}/${resource}"
    if(new File(resource).name.contains('.'))
    {
      fetchResource(path, path, stats)
    }
    else
    {
      fetchResourceAsIndexDotHtml(path, stats)
    }
    stats.resources = (stats.resources ?: 0) + 1
  }

  void processResource(Resource resource, Map stats)
  {
    URI uri = _wiring.pathManager.computePath(resource)

    ["x", "p"].each { prefix ->
      String path = "${_wiring.contextPath}/l/${prefix}/${uri}"
      fetchResourceAsIndexDotHtml(path, stats)
      stats.resources = (stats.resources ?: 0) + 1
    }
  }

  void fetchResource(String inputPath, String outputPath, Map stats)
  {
    fetchResource(_inputResource.createRelative(inputPath),
                  _outputResource.createRelative(outputPath),
                  stats)
  }

  void fetchResource(org.linkedin.util.io.resource.Resource input,
                     org.linkedin.util.io.resource.Resource output,
                     Map stats)
  {
    input.inputStream.withStream { InputStream is ->
      Checksum ckin = new Adler32()
      is = new CheckedInputStream(new BufferedInputStream(is), ckin)

      File outFile = output.file
      IOUtils.createNewDirectory(outFile.parentFile)

      File newOutFile = new File("${outFile.canonicalPath}.tmp")
      newOutFile.newOutputStream().withStream { OutputStream os ->
        os << is
      }
      long size = newOutFile.size()
      stats.'size' = (stats.'size' ?: 0L) + size

      def oldChecksum = computeChecksum(outFile)
      long newChecksum = ckin.value

      if(oldChecksum != newChecksum)
      {
        stats.'pages' = (stats.'pages' ?: 0L) + 1
        newOutFile.renameTo(outFile)
      }
      else
      {
        stats.'skPages' = (stats.'skPages' ?: 0L) + 1
        stats.'skSize' = (stats.'skSize' ?: 0L) + size
        newOutFile.delete()
      }
    }
  }

  def computeChecksum(File file)
  {
    def cksum = null

    if(file.exists())
    {
      file.withInputStream { InputStream is ->
        Checksum ckin = new Adler32()
        is = new CheckedInputStream(new BufferedInputStream(is), ckin)

        DevNullOutputStream.INSTANCE << is
        cksum = ckin.value
      }
    }

    return cksum
  }

  void fetchResourceAsIndexDotHtml(String path, Map stats)
  {
    fetchResource(path, "${path}/index.html", stats)
  }

  void seedResourceQueue(Resource resource)
  {
    putResourceInQueue(resource)

    if(resource.depth < _maxDepth)
    {
      Model model = _wiring.getLibraryStore().loadContent(resource)
      model.children.each { Resource r ->
        seedResourceQueue(r)
      }
    }
  }

  void putResourceInQueue(def resource)
  {
    synchronized(_queue)
    {
      _queue << resource
      _queue.notify()
    }
  }

  def getResourceFromQueue()
  {
    synchronized(_queue)
    {
      while(_queue.isEmpty() && !_pending.isEmpty())
      {
        _queue.wait()
      }
      if(_queue.isEmpty())
      {
        return null
      }
      else
      {
        def iterator = _queue.iterator()
        def resource = iterator.next()
        iterator.remove()
        _pending << resource
        return resource
      }
    }
  }

  void completedResource(def resource)
  {
    synchronized(_queue)
    {
      _pending.remove(resource)
      _queue.notifyAll()
    }
  }

  void recordStats(def resource, Map stats)
  {
    synchronized(_queue)
    {
      _stats.keySet().collect { it }.each { key ->
        _stats[key] = _stats[key] + (stats[key] ?: 0L)
      }

      if(stats.error)
      {
        _errors[resource] = stats.error
      }
    }
  }

  private String getStats()
  {
    synchronized(_queue)
    {
      if(!_stats.resources)
        return null

      long time = _chronos.totalTime

      long avgTime = (time / (double) _stats.resources) as long
      long avgSize = (_stats['size'] / (double) _stats.resources) as long

      long erp = _queue.size() + _pending.size()

      "t=${new Timespan(time)};r=${_stats.resources};p=${_stats.pages};skp=${_stats.skPages};ct=${new Timespan(_stats.ctime)};s=${new MemorySize(_stats['size'])};sks=${new MemorySize(_stats.skSize)};at=${new Timespan(avgTime)};as=${new MemorySize(avgSize)};ert=${new Timespan(avgTime * erp)};q=${_queue.size()};r=${_pending.size()};e=${_errors.size()}"
    }
  }

  public synchronized void destroy()
  {
    _wiring?.destroy()
    _wiring = null
  }

  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy StaticGenerator.groovy <tbd>")

    cli.h(longOpt: 'help', 'usage information')
    cli._(longOpt: 'kiwidoc-root', "kiwidoc root folder (default to ${Wiring.DEFAULT_KIWIDOC_ROOT})", argName: 'folder', args: 1, required: false)
    cli.d(longOpt: 'depth', 'depth of traversal (default to 0) (ex: 0=node only, 1=children, 2=children children...)',
          argName: '[0..N]', args: 1, required: false)
    cli.f(longOpt: 'static-file', 'the file containing list of static pages',
          argName: 'path', args: 1, required: false)
    cli.i(longOpt: 'input', 'base url for input (default http://localhost:8080)', argName: 'url', args: 1, required: false)
    cli.o(longOpt: 'output', "base path for output (default to ${Wiring.DEFAULT_STATIC_DIR})", argName: 'path', args: 1, required: false)
    cli.r(longOpt: 'resource-root', 'where to start (ex: /, java/j2se, ...)', argName: 'resource', args: 1, required: false)
    cli.s(longOpt: 'static', 'generate static pages', args: 0, required: false)
    cli.t(longOpt: 'thread-count', 'number of threads (default to 1)', argName: 'count', args: 1, required: false)

    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
      System.exit(0)
    }

    new StaticGenerator(opts: opts).run()

    System.exit(0)
  }

}