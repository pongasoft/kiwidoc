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
public class MavenRunner
{
  def runScript(args)
  {
    def scriptArgs = args.scriptArgs ?: []

    def binding = args.binding ?: new Binding(scriptArgs.toArray(new String[scriptArgs.size()]))

    GroovyShell shell = args.shell ?: new GroovyShell(binding)

    def classLoader = args.classLoader ?: shell.classLoader

    run(args.dependencies, shell.classLoader) {
      shell.evaluate(new File(args.script))
    }
  }

  def run(dependencies, closure)
  {
    run(dependencies, this.class.classLoader.rootLoader)
  }

  def run(dependencies, classLoader, closure)
  {
    def dep = new MavenDependencyResolver()

    if(dependencies)
    {
      def mavenDependencies = []
      dependencies.each { dependency ->
        def mavenDependency = MavenDependency.create(dependency)
        if(mavenDependency)
          mavenDependencies << mavenDependency
        else
        {
          def uri = new URI(dependency.toString())
          if(!uri.scheme)
          {
            uri = new File(dependency).absoluteFile.toURI()
          }

          if(uri.scheme == "file")
          {
            def file = new File(uri.path)
            if(file.isDirectory())
            {
              file.eachFile() {
                if(it.name.endsWith('.jar'))
                {
                  classLoader.addURL(it.toURL())
                }
              }
            }
          }
          classLoader.addURL(uri.toURL())
        }
      }

      if(mavenDependencies)
      {
        new MavenDependencyResolver().addToClassLoader(classLoader, mavenDependencies)
      }
    }

    closure()
  }
}