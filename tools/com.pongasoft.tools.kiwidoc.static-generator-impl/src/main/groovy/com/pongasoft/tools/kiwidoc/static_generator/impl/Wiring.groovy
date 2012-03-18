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

import com.pongasoft.kiwidoc.builder.ContentHandlersImpl
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStoreImpl
import com.pongasoft.kiwidoc.builder.path.URLPathManager
import com.pongasoft.kiwidoc.model.resource.PathManager
import grails.spring.BeanBuilder
import org.apache.commons.vfs.FileSystemManager
import org.apache.commons.vfs.impl.StandardFileSystemManager
import org.linkedin.util.io.resource.FileResource
import org.linkedin.util.io.resource.Resource
import org.linkedin.util.io.resource.URLResource
import org.springframework.context.ApplicationContext

/**
 * @author yan@pongasoft.com  */
public class Wiring
{
  public static final String DEFAULT_STATIC_DIR = '/export/content/pongasoft/kiwidoc/static'
  public static final String DEFAULT_KIWIDOC_ROOT = '/export/content/pongasoft/kiwidoc/data'

  def opts

  private ApplicationContext _appContext
  private KiwidocLibraryStore _libraryStore

  private ApplicationContext createApplicationContext()
  {
    def bb = new BeanBuilder()

    bb.beans {

      // fileSystem
      fileSystemBean(StandardFileSystemManager.class) { bean ->
        bean.initMethod = 'init'
        bean.destroyMethod = 'close'
      }

      pathManagerBean(URLPathManager.class)
    }

    return bb.createApplicationContext()
  }

  KiwidocLibraryStore getLibraryStore()
  {
    if(_libraryStore == null)
    {
      _libraryStore =
        new KiwidocLibraryStoreImpl(new ContentHandlersImpl(fileSystem.resolveFile(new File(kiwidocRoot).toURL().toString())))
    }

    return _libraryStore
  }

  String getKiwidocRoot()
  {
    opts.'kiwidoc-root' ?: DEFAULT_KIWIDOC_ROOT
  }

  File getStaticFile()
  {
    def filename = opts.'static-file'
    if(!filename)
      throw new Exception("missing static file")
    return new File(filename)
  }

  String getContextPath()
  {
    return 'java'
  }

  Resource getInputResource()
  {
    return URLResource.createFromRoot(new URL(opts.input ?: "http://localhost:8080"))
  }

  Resource getOutputResource()
  {
    return FileResource.createFromRoot(new File(opts.output ?: DEFAULT_STATIC_DIR))
  }

  FileSystemManager getFileSystem()
  {
    getBean('fileSystemBean')
  }

  PathManager getPathManager()
  {
    getBean('pathManagerBean')
  }

  ApplicationContext getApplicationContext()
  {
    if(_appContext == null)
    {
      _appContext = createApplicationContext()
    }

    return _appContext
  }

  def destroy()
  {
    _appContext?.close()
  }

  public <T> T getBean(String beanName)
  {
    return (T) applicationContext.getBean(beanName)
  }
}