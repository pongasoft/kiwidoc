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

import com.pongasoft.kiwidoc.builder.ContentHandlersImpl
import com.pongasoft.kiwidoc.builder.KiwidocBuilder
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStoreImpl
import com.pongasoft.kiwidoc.model.DependenciesModel
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.util.io.IOUtils
import org.apache.commons.vfs.FileObject
import org.apache.commons.vfs.FileSystemManager
import org.apache.commons.vfs.impl.StandardFileSystemManager

class TestRepoBuilder
{
  String kiwidocRoot = "file:///export/content/pongasoft/kiwidoc/data"
  String kiwidocMavenCache = "/export/content/pongasoft/maven-cache"
  KiwidocLibraryStore libraryStore
  FileSystemManager fileSystem

  TestRepoBuilder()
  {
    fileSystem = new StandardFileSystemManager()
    fileSystem.init()
  }

  def execute(File libFile)
  {
    libraryStore = new KiwidocLibraryStoreImpl(new ContentHandlersImpl(fileSystem.resolveFile(kiwidocRoot)))

    def binding = new Binding()
    def libs = [:]
    binding.libs = libs
    GroovyShell shell = new GroovyShell(binding)
    shell.evaluate(libFile)

    println libs.size()

    libs.values().each { lib ->
      buildLibrary(lib)
    }
  }

  def destroy()
  {
    fileSystem.close()
  }

  private LibraryVersionResource toLVR(lib)
  {
    if(lib instanceof LibraryVersionResource)
      return lib
    else
    {
      String[] elements = lib.split('/')
      return new LibraryVersionResource(* elements);
    }
  }

  private LibraryVersionResource buildLibrary(args)
  {
    LibraryVersionResource lib = toLVR(args.lib);

    def time = System.currentTimeMillis()
    println "Processing ${lib}"

    if(libraryStore.deleteLibraryVersion(lib))
      println "Deleted old version of ${lib}"

    FileObject javadoc = resolve(lib, args, 'javadoc')
    FileObject sources = resolve(lib, args, 'source')
    FileObject library = IOUtils.createJarFileObject(resolve(lib, args, 'library'))

    def libraries

    if(args.containsKey("classpath"))
    {
      libraries = []
      args.classpath.collect { cp ->
        if(cp)
        {
          libraries << fileSystem.resolveFile("file://${cp}")
        }
      }
    }
    else
    {
      libraries = [resolve(lib, args, 'library')]
    }

    KiwidocBuilder builder = new KiwidocBuilder(libraryStore)

    def dependencies = []
    def deps = args.dependencies
    if(deps != null)
    {
      deps.each { dependency ->
        dependencies << toLVR(dependency)
      }
    }

    try
    {
      lib = builder.buildKiwidoc(lib,
                                 new DependenciesModel(dependencies),
                                 library?.exists() ? library : null,
                                 javadoc?.exists() ? javadoc : null,
                                 sources?.exists() ? sources : null,
                                 libraries.toArray(new FileObject[libraries.size()]))
    }
    catch (Exception e)
    {
      println "Failed processing ${lib}"
      e.printStackTrace()
    }

    println "${lib} processed in ${System.currentTimeMillis() - time}ms"

    return lib
  }

  private FileObject resolve(lib, args, name)
  {
    def location = args[name]

    if(!location && args.containsKey(name))
      return null

    if(location instanceof FileObject)
      return location
    if(location)
      return fileSystem.resolveFile(location)
    else
    {
      String prefix =
      "file://${kiwidocMavenCache}/${lib.organisation}/${lib.name}/${lib.version}/${lib.name}-${lib.version}"

      if(name == 'library')
      {
        location = "${prefix}-jar.jar"
      }
      else
      {
        location = "${prefix}-${name}.jar"
        if(fileSystem.resolveFile(location).exists())
          location = "jar:${location}!/${args[name + 'Path'] ?: ''}"
      }

      return fileSystem.resolveFile(location)
    }
  }
}

TestRepoBuilder testRepoBuilder = new TestRepoBuilder()
try
{
  testRepoBuilder.execute(libFile)
}
finally
{
  testRepoBuilder.destroy()
}