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

package com.pongasoft.maven.runner

import org.apache.maven.artifact.ant.DependenciesTask

/**
 * @author yan@pongasoft.com
 */
class MavenClasspathResolver
{
  def resolveClasspath(args)
  {
    def ant = new AntBuilder()
    ant.taskdef(name: 'dependencies', classname: DependenciesTask.class.name)

    def resolveArgs = args.subMap(['verbose', 'settingsFile']).findAll { it.value }

    def out = System.out

    try
    {
      System.out = System.err

      ant.dependencies(filesetId: "dependency.fileset", *: resolveArgs) {
        args.gavs.each { gav ->
          dependency(*: gav)
        }
      }

      def scanner = ant.fileScanner {
        fileset(refid: 'dependency.fileset')
      }

      return scanner.collect { it }
    }
    finally
    {
      System.out = out
    }
  }

  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy ${MavenClasspathResolver.class.name}.groovy [-s settingsFile] [-c classpathEntry]* [-x <fileName>]* [-ev] [<script>] [scriptArg]*")

    cli.h(longOpt: 'help', 'usage information')
    cli.c(longOpt: 'classpath',
          args: 1,
          'a dependency (ex: lib or lib/foo.jar or file:///tmp/foo.jar or mvn://grouId/artifactId/version)')
    cli.x(longOpt: 'exclude', args: 1, 'exclude a dependency (format filename)')
    cli.s(longOpt: 'settings', args: 1, 'settings file')
    cli.e(longOpt: 'execute', args: 0, 'execute the script (add to classloader and execute)')
    cli.v(longOpt: 'verbose', args: 0, 'verbose output')


    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
    }

    def script = ""
    def scriptArguments = opts.arguments()

    if(scriptArguments)
    {
      script = scriptArguments[0]

      if(scriptArguments.size() > 1)
      {
        scriptArguments = scriptArguments[1..-1]
      }
      else
      {
        scriptArguments = []
      }
    }


    def classpath = []

    if(opts.cs)
    {
      classpath =
        new Classpath().compute(classpathEntries: opts.cs, settingsFile: opts.s, verbose: opts.v)
    }

    if(opts.xs)
    {
      def excluded = new HashSet(opts.xs)
      classpath = classpath.findAll { !excluded.contains(it.name) }
    }

    if(opts.e && script)
    {
      def binding = new Binding(scriptArguments.toArray(new String[scriptArguments.size()]))

      def GroovyShell shell = new GroovyShell(binding)

      classpath.each {
        shell.classLoader.rootLoader.addURL(it.toURL())
      }

      shell.evaluate(new File(script as String))
    }
    else
    {
      if(classpath)
        classpath = "-classpath ${classpath*.absolutePath.join(File.pathSeparator)}"
      print "${classpath} ${script} ${scriptArguments.join(' ')}"
    }
  }
}
