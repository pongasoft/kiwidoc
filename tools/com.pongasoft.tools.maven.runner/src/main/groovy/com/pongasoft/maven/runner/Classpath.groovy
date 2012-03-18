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
public class Classpath
{
  def File[] compute(args)
  {
    def out = []
    if(args.classpathEntries)
    {
      def dependencies = []
      def resolver = new MavenClasspathResolver()
      args.classpathEntries.each { dependency ->
        def mvnDependency = MavenDependency.create(dependency)
        if(mvnDependency)
        {
          dependencies << mvnDependency
        }
        else
        {
          if(dependencies)
          {
            resolver.resolveClasspath(gavs: dependencies,
                                      settingsFile: args.fileSettings,
                                      verbose: args.verbose).each {
              out << it
            }
            dependencies = []
          }
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
                  out << it
                }
              }
            }

            out << file
          }
          else
          {
            throw new IllegalArgumentException('only directories, jar files or mvn:// urls allowed')
          }
        }
      }

      if(dependencies)
      {
        resolver.resolveClasspath(gavs: dependencies,
                                  settingsFile: args.settingsFile,
                                  verbose: args.verbose).each {
          out << it
        }
        dependencies = []
      }
    }

    return out as File[]
  }

  public static void main(String[] args)
  {
    println args
    println new Classpath().compute(args)
  }
}