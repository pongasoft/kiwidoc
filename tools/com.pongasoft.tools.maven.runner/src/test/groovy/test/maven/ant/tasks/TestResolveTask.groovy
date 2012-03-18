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

package test.maven.ant.tasks

import com.pongasoft.maven.ant.tasks.ResolveTask

/**
 * @author yan@pongasoft.com
 */
public class TestResolveTask extends GroovyTestCase
{
  public void testResolveTask()
  {
    def ant = new AntBuilder()
    ant.taskdef(name: 'resolve', classname: ResolveTask.class.name)

    def resolveTask = ant.resolve(artifactOnly: true, sources: true, javadoc: true) {
      dependency(groupId: 'junit', artifactId: 'junit', version: '4.4')
    }

    println resolveTask.result.artifact
    println resolveTask.result.sources
    println resolveTask.result.javadoc

    resolveTask = ant.resolve(artifactOnly: false, verbose: true) {
      dependency(groupId: 'commons-vfs', artifactId: 'commons-vfs', version: '1.0')
    }

    println resolveTask.result.artifact
    println resolveTask.result.sources
    println resolveTask.result.javadoc
    println resolveTask.result.classpath

    resolveTask = ant.resolve(artifactOnly: false, verbose: true) {
      dependency(groupId: 'com.pongasoft', artifactId: 'com.pongasoft.kiwidoc.builder', version: '1.0.0-SNAPSHOT')
    }

    println resolveTask.result.artifact
    println resolveTask.result.sources
    println resolveTask.result.javadoc
    println resolveTask.result.classpath*.getFile()

    resolveTask = ant.resolve(artifactOnly: false, sources: true, javadoc: true) {
      dependency(groupId: 'org.apache.activemq', artifactId: 'activemq-core', version: '5.1.0')
    }

    resolveTask.result.each { k, v ->
      if(v instanceof Collection)
      {
        v = v.collect { "${it.groupId}/${it.artifactId}/${it.version} | ${it.file?.name}" }.sort().join('\n')
        v = "[\n${v}\n]"
      }
      println "${k} => ${v}"
    }
  }
}