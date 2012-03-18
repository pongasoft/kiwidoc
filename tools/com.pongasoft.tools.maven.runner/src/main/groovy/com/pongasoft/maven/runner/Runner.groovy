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

/**
 * @author yan@pongasoft.com
 */
public class Runner
{
  public static void main(String[] args)
  {
    def cli = new CliBuilder(usage: "groovy ${Runner.getClass().name}.groovy [-c classpathEntry]* <script> [scriptArg]*")

    cli.h(longOpt: 'help', 'usage information')
    cli.c(longOpt: '--classpath',
          args: 1,
          'a dependency (ex: lib or lib/foo.jar or file:///tmp/foo.jar or mvn://grouId/artifactId/version)')

    def opts = cli.parse(args)
    if(!opts) return
    if(opts.h)
    {
      cli.usage()
    }

    def scriptArguments = opts.arguments()

    if(!scriptArguments)
    {
      println "Missing script name"
      cli.usage()
      System.exit(1)
    }

    def script = scriptArguments[0]
    if(scriptArguments.size() > 1)
    {
      scriptArguments = scriptArguments[1..-1]
    }
    else
    {
      scriptArguments = []
    }

    def classpath = new Classpath().compute(opts.cs)

    if(classpath)
      classpath = "-classpath ${classpath}"

    print "${classpath} ${script} ${scriptArguments.join(' ')}"
  }
}