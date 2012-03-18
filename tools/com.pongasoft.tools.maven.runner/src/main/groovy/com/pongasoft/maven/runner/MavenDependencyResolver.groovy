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

import com.pongasoft.maven.ant.tasks.ResolveTask

/**
 * @author yan@pongasoft.com
 */
class MavenDependencyResolver
{
  def resolveArtifact(args)
  {
    def ant = new AntBuilder()
    ant.taskdef(name: 'resolve', classname: ResolveTask.class.name)

    def dependencyArgs = args.subMap(['groupId', 'artifactId', 'version'])
    def resolveArgs =
    args.subMap(['artifactOnly',
                'sources',
                'javadoc',
                'optional',
                'settingsFile',
                'verbose']).findAll { it.value }

    def resolveTask = ant.resolve(resolveArgs) {
      dependency(dependencyArgs)
    }

    def res = resolveTask.result

    return [artifact: res.artifact,
      sources: res.sources,
      javadoc: res.javadoc,
      classpath: res.classpath,
      transitiveDependencies: res.transitiveDependencies,
      directDependencies: res.directDependencies,
      optionalDependencies: res.optionalDependencies
    ]
  }

  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy ${MavenDependencyResolver.getClass().name}.groovy [-vsjoa] [-i settingsFile] [mvn://a/b/c]*")

    cli.h(longOpt: 'help', 'usage information')
    cli.i(longOpt: 'settings', args: 1, 'settings file')
    cli.v(longOpt: 'verbose', args: 0, 'verbose output')
    cli.o(longOpt: 'optional', args: 0, 'optional')
    cli.s(longOpt: 'sources', args: 0, 'sources')
    cli.j(longOpt: 'javadoc', args: 0, 'optional')
    cli.a(longOpt: 'artifactOnly', args: 0, 'artifactOnly')

    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
    }

    def dependencyResolver = new MavenDependencyResolver()
    opts.arguments()?.each {
      def artifact = dependencyResolver.resolveArtifact(*: MavenDependency.create(it),
                                                        verbose: opts.v,
                                                        settingsFile: opts.i,
                                                        sources: opts.s,
                                                        javadoc: opts.j,
                                                        optional: opts.o,
                                                        artifactOnly: opts.a)

      println it
      artifact.each { k, v ->
        if(v instanceof Collection)
        {
          v = v.collect { "${it.groupId}/${it.artifactId}/${it.version} | ${it.file?.name}" }.sort().join('\n')
          v = "[\n${v}\n]"
        }
        println "${k} => ${v}"
      }
    }
  }
}