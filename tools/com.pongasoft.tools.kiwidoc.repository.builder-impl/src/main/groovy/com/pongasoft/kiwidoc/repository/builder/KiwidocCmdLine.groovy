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

package com.pongasoft.kiwidoc.repository.builder

import com.pongasoft.kiwidoc.model.LibraryVersionModel
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import org.linkedin.util.clock.Chronos
import org.linkedin.util.clock.Timespan

/**
 * @author yan@pongasoft.com
 */
class KiwidocCmdLine
{
  def log = org.apache.commons.logging.LogFactory.getLog(KiwidocCmdLine.class);
  def opts

  Set<LibraryVersionResource> libraries = new HashSet()
  def failed = []

  Collection<LibraryVersionResource> processLibrary(KiwidocProcessor processor, libraryName, args)
  {
    try
    {
      LibraryVersionModel library = processor.processKiwidoc(args)
      libraries << library.resource
      return library.dependencies.directDependencies
    }
    catch (Exception e)
    {
      failed << libraryName
      log.warn "Failed to process ${libraryName}... skipped", e
      return []
    }
  }

  def run()
  {
    def timeToWait = Timespan.parse(opts.t ?: null)

    def files = opts.arguments()

    def kiwidocProcessor = new KiwidocProcessor(opts)
    try
    {
      addShutdownHook {
        kiwidocProcessor.destroy()
      }

      Map librariesToProcess = [:]

      Chronos c = new Chronos()
      files.each { String filename ->
        if(filename.endsWith('.groovy'))
        {
          def binding = new Binding()
          def libs = [:]
          binding.libs = libs
          GroovyShell shell = new GroovyShell(binding)
          shell.evaluate(new File(filename))

          libs.each { path, lib ->
            librariesToProcess[path] = [scheme: 'local', path: path, *: lib]
          }
        }
        else
        {
          new File(filename).eachLine { line ->
            line = line.trim()
            if(line && !line.startsWith('#'))
            {
              librariesToProcess[line] = new URI(line)
            }
          }
        }
      }

      while(!librariesToProcess.isEmpty())
      {
        Iterator iter = librariesToProcess.entrySet().iterator()
        def entry = iter.next()
        def libraryName = entry.key
        def args = entry.value
        iter.remove()
        try
        {
          Collection<LibraryVersionResource> directDependencies =
            processLibrary(kiwidocProcessor, libraryName, args)

          if(opts.r)
          {
            directDependencies.each { LibraryVersionResource lib ->
              if(!libraries.contains(lib))
                librariesToProcess[lib.toString()] = new URI("mvn://${lib}".toString())
            }
          }

          if(timeToWait)
          {
            log.info("Sleeping for ${timeToWait}")
            Thread.sleep(timeToWait.durationInMilliseconds)
          }
        }
        catch (Exception e)
        {
          failed << libraryName
          log.warn "Failed to process ${libraryName}... skipped", e
        }
      }

      log.info "Total processed ${libraries.size() + failed.size()} in ${c.elapsedTimeAsHMS}. Success=${libraries.size()}. Failures=${failed.size()}."
      if(failed)
      {
        log.warn "Failed libraries:"
        failed.each {
          println it
        }
      }

      if(opts.d)
      {
        kiwidocProcessor.deleteLibrary(opts.d)
      }

      if(opts.o)
      {
        kiwidocProcessor.optimize()
      }
    }
    finally
    {
      kiwidocProcessor.destroy()
    }
  }

  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy KiwidocCmdLine.groovy [-s settingsFile] [-vn] [-t time] [-d a/b/c] [file.groovy | file.txt]*")

    cli.d(longOpt: 'delete',
          args: 1, 'deletes the resource (organisation, library or library version)')
    cli.f(longOpt: 'force', args: 0, 'force update if already indexed')
    cli.h(longOpt: 'help', 'usage information')
    cli.n(longOpt: 'pretend', args: 0, 'pretend to do it but do not do it')
    cli.r(longOpt: 'recurse', args: 0, 'recurse')
    cli.s(longOpt: 'settings', args: 1, 'settings file')
    cli.t(longOpt: 'timeToWait', args: 1, 'time to wait between libraries')
    cli.v(longOpt: 'verbose', args: 0, 'verbose')
    cli._(longOpt: 'kiwidoc-root', "kiwidoc root folder (default to ${KiwidocProcessor.DEFAULT_KIWIDOC_ROOT})", argName: 'folder', args: 1, required: false)

    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
      System.exit(0)
    }

    new KiwidocCmdLine(opts: opts).run()

    System.exit(0)
  }
}

