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
import com.pongasoft.kiwidoc.builder.NoSuchContentException
import com.pongasoft.kiwidoc.model.DependenciesModel
import com.pongasoft.kiwidoc.model.LibraryVersionModel
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.maven.runner.MavenDependency
import com.pongasoft.maven.runner.MavenDependencyResolver
import com.pongasoft.util.io.IOUtils
import groovy.xml.NamespaceBuilder
import org.apache.commons.vfs.FileSystemManager
import org.linkedin.util.clock.Chronos

/**
 * @author yan@pongasoft.com
 */
class KiwidocProcessor
{
  public static final String DEFAULT_KIWIDOC_ROOT = '/export/content/pongasoft/kiwidoc/data'

  static def log =
    org.apache.commons.logging.LogFactory.getLog(KiwidocProcessor.class);

  def options
  KiwidocWiring kiwidocWiring
  KiwidocLibraryStore libraryStore
  FileSystemManager fileSystem
  KiwidocBuilder kiwidocBuilder
  def settings

  KiwidocProcessor(options)
  {
    this.options = options
    this.settings = [settingsFile: options.s, verbose: options.v]
    kiwidocWiring = new KiwidocWiring()
    fileSystem = kiwidocWiring.getBean('fileSystem')
    libraryStore =
      new KiwidocLibraryStoreImpl(new ContentHandlersImpl(fileSystem.resolveFile(new File(kiwidocRoot).toURL().toString())))

    kiwidocBuilder = kiwidocWiring.getBean('kiwidocBuilder')
  }

  LibraryVersionModel processKiwidoc(uri)
  {
    Chronos c = new Chronos()

    def args
    if(uri.scheme == 'mvn')
    {
      args = computeArgsFromMaven(uri.toString())
    }
    else
    {
      args = computeArgsFromLocal(uri)
    }

    log.info "Downloaded dependencies in ${c.elapsedTimeAsHMS}"

    def lib = args.lib

    if(!options.f)
    {
      try
      {
        LibraryVersionModel model = (LibraryVersionModel) libraryStore.loadContent(args.lib)
        log.info "Done processing (already indexed) ${lib} in ${c.elapsedTimeAsHMS}"
        return model
      }
      catch (NoSuchContentException e)
      {
        // ok it will be reprocessed
      }
    }

    def modelBuilder = generateKiwidoc(args)

    def model = modelBuilder.buildModel()

    log.info "Generated model (detected JDK ${modelBuilder.jdkVersion}) in ${c.elapsedTimeAsHMS}"

    if(!options.n)
    {
      def updated = libraryStore.deleteLibraryVersion(modelBuilder.libraryVersionResource)

      libraryStore.storeLibrary(modelBuilder)

      log.info "${updated ? 'Updated' : 'Added'} kiwidoc in ${c.elapsedTimeAsHMS}"

      // for gc...
      modelBuilder = null
    }
    else
    {
      log.info "No indexing: ${args}"
    }

    log.info "Done processing ${lib} in ${c.elapsedTimeAsHMS}"

    return model
  }

  def generateKiwidoc(args)
  {
    def dependencies =
      args.directDependencies ? new DependenciesModel(args.directDependencies,
                                                      args.transitiveDependencies,
                                                      args.optionalDependencies) : DependenciesModel.NO_DEPENDENCIES

    def library = new KiwidocBuilder.Library(libraryVersionResource: args.lib,
                                             dependencies: dependencies,
                                             classes: args.classes,
                                             javadoc: args.javadoc,
                                             sources: args.sources,
                                             overviewFilename: args.overview ?: "overview.html",
                                             classpath: args.classpath,
                                             publicOnly: args.publicOnly ?: false,
                                             jdkVersion: args.jdk ?: 0)

    def modelBuilder = kiwidocBuilder.buildKiwidoc(library)

    return modelBuilder
  }

  def computeArgsFromMaven(uri)
  {
    def args = [:]

    def gav = MavenDependency.create(uri)

    def lib = toLVR(gav)
    args.lib = lib

    log.info "Processing ${lib}..."

    def resolution =
      new MavenDependencyResolver().resolveArtifact(sources: true,
                                                    javadoc: true,
                                                    optional:true,
                                                    *:settings,
                                                    *:gav)

    args.directDependencies = (resolution.directDependencies ?: []).collect {toLVR(it) }
    args.transitiveDependencies = (resolution.transitiveDependencies ?: []).collect {toLVR(it) }
    args.optionalDependencies = (resolution.optionalDependencies ?: []).collect {toLVR(it) }

    args.classpath = resolution.classpath.collect { artifactToFileObject(it) }

    args.javadoc = artifactToJarFileObject(resolution.javadoc)
    if(!args.javadoc)
    {
      log.warn "no javadoc found for ${lib}"
    }
    args.sources = artifactToJarFileObject(resolution.sources)
    if(!args.sources)
    {
      log.warn "no sources found for ${lib}"
    }
    args.classes = artifactToJarFileObject(resolution.artifact)

    return args
  }

  def computeArgsFromLocal(map)
  {
    def args =[:]
    args.lib = toLVR(map.path)

    args.classpath = []
    args.directDependencies = []
    args.transitiveDependencies = []
    args.optionalDependencies = []

    log.info "Processing ${args.lib}..."

    if(map.mvn)
    {
      args.putAll(computeArgsFromMaven(map.mvn))
    }

    args.javadoc = resolveJar(map.javadoc) ?: args.javadoc
    args.sources = resolveJar(map.sources) ?: args.sources
    args.classpath.addAll(map.classpath?.collect {resolve(it)} ?: [])
    if(map.classes)
    {
      args.classes = resolveJar(map.classes)
      args.classpath << resolve(map.classes)
    }

    if(map.ivy)
    {
      fetchIvyClasspath(map.ivy)?.each {
        args.classpath << resolve(it)
      }
    }

    args.publicOnly = map.publicOnly
    if(map.jdk)
      args.jdk = map.jdk

    args.directDependencies.addAll((map.dependencies?.directDependencies ?: []).collect {toLVR(it) })
    args.transitiveDependencies.addAll((map.dependencies?.transitiveDependencies ?: []).collect {toLVR(it) })
    args.optionalDependencies.addAll((map.dependencies?.optionalDependencies ?: []).collect {toLVR(it) })

    args.overview = map.overview

    return args
  }

  def fetchIvyClasspath(ivyFile)
  {
    def ant = new AntBuilder()
    def ivy = NamespaceBuilder.newInstance(ant, 'antlib:org.apache.ivy.ant')

    ivy.cachefileset(setid: 'ivy', file: ivyFile, log: 'download-only')

    def files = ant.project.getReference('ivy').collect { it.file.canonicalPath }

    return files
  }

  def storeKiwidoc(modelBuilder)
  {
    libraryStore.deleteLibraryVersion(modelBuilder.libraryVersionResource)
  }

  def deleteLibrary(String path)
  {
    Chronos c = new Chronos()
    log.info("Deleting library ${path}")
    def lvr = toLVR(path)
    def deleted = libraryStore.deleteLibraryVersion(lvr)
    if(deleted)
      log.info("Library deleted in ${c.elapsedTimeAsHMS}")
    else
      log.info("Library does not exist")
    return deleted
  }

  private def toLVR(gav)
  {
    return new LibraryVersionResource(gav.groupId, gav.artifactId, gav.version)
  }

  private def toLVR(String path)
  {
    if(path == null)
      return null

    def a = path.split('/')
    return new LibraryVersionResource(a[0], a[1], a[2])
  }

  private def resolveJar(location)
  {
    if(location)
    {
      def res = resolve(location)
      if(location.endsWith('.jar'))
        res = IOUtils.createJarFileObject(res)
      return res
    }
    else
      return null
  }

  private def resolve(location)
  {
    if(location)
      return fileSystem.resolveFile(location)
    else
      return null
  }

  private def fileToFileObject(File file)
  {
    return resolve(file?.path)
  }

  private def fileToFileObject(String file)
  {
    if(file != null)
      return fileToFileObject(new File(file))
    else
      return null
  }

  private def artifactToFileObject(artifact)
  {
    return fileToFileObject(artifact?.file)
  }

  private def artifactToJarFileObject(artifact)
  {
    return IOUtils.createJarFileObject(artifactToFileObject(artifact))
  }

  def destroy()
  {
    if(kiwidocWiring)
    {
      kiwidocWiring.destroy()
      kiwidocWiring = null
    }
  }

  String getKiwidocRoot()
  {
    options.'kiwidoc-root' ?: DEFAULT_KIWIDOC_ROOT
  }
}

