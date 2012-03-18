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

package com.pongasoft.tools.kiwidoc.index.builder.impl

import com.pongasoft.kiwidoc.builder.ContentHandlersImpl
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStoreImpl
import com.pongasoft.kiwidoc.index.api.KiwidocIndex
import com.pongasoft.kiwidoc.index.impl.KiwidocIndexImpl
import com.pongasoft.kiwidoc.index.impl.KiwidocIndexStats
import com.pongasoft.kiwidoc.index.impl.StringResourceEncoder
import com.pongasoft.kiwidoc.index.impl.keyword.api.MutableKeywordIndex
import com.pongasoft.kiwidoc.index.impl.keyword.impl.KeywordIndexImpl
import com.pongasoft.kiwidoc.index.impl.keyword.impl.ResourceExtractor
import com.pongasoft.kiwidoc.index.impl.lucene.impl.FSDirectoryFactory
import com.pongasoft.kiwidoc.index.impl.lucene.impl.LuceneDirectoryImpl
import com.pongasoft.kiwidoc.index.impl.prefix.api.MutablePrefixIndex
import com.pongasoft.kiwidoc.index.impl.prefix.impl.NoResultsPrefixIndex
import grails.spring.BeanBuilder
import org.apache.commons.vfs.FileSystemManager
import org.apache.commons.vfs.impl.StandardFileSystemManager
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.springframework.context.ApplicationContext

/**
 * @author yan@pongasoft.com  */
public class Wiring
{
  def opts

  private ApplicationContext _appContext
  private KiwidocLibraryStore _libraryStore
  private KiwidocIndex _kiwidocIndex

  public static final String DEFAULT_CACHES_DIR = '/export/content/pongasoft/kiwidoc/caches'
  public static final String DEFAULT_KIWIDOC_ROOT = '/export/content/pongasoft/kiwidoc/data'

  private ApplicationContext createApplicationContext()
  {
    def bb = new BeanBuilder()

    bb.beans {

      // fileSystem
      fileSystemBean(StandardFileSystemManager.class) { bean ->
        bean.initMethod = 'init'
        bean.destroyMethod = 'close'
      }

      // kiwidocIndex
      analyzer(StandardAnalyzer.class)

      userDataExtractor(ResourceExtractor.class)

      [
        publicOnlyDirectory: new File(cachesDir, 'keyword/publicOnly'),
        publicAndPrivateDirectory: new File(cachesDir, 'keyword/publicAndPrivate')
      ].each { name, path ->

        "${name}_dir"(FSDirectoryFactory.class) { bean ->
          bean.initMethod = 'init'
          directoryPath = path
        }

        "${name}"(LuceneDirectoryImpl.class) { bean ->
          bean.initMethod = 'open'
          bean.destroyMethod = 'close'
          analyzer = analyzer
          directoryFactory = ref("${name}_dir")
          userDataExtractor = userDataExtractor
        }
      }

      keywordIndexBean(KeywordIndexImpl.class) {
        analyzer = analyzer
        publicOnlyDirectory = publicOnlyDirectory
        publicAndPrivateDirectory = publicAndPrivateDirectory
        /* TODO HIGH YP: cannot express this in spring dsl!
        <property name="resourceEncoder">
          <bean factory-bean="userDataExtractor" factory-method="getResourceEncoder"/>
        </property>
         */
        resourceEncoder = { StringResourceEncoder sre -> }
      }
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

  KiwidocIndex getKiwidocIndex()
  {
    if(_kiwidocIndex == null)
    {
      _kiwidocIndex = new KiwidocIndexImpl()
      _kiwidocIndex.prefixIndex = prefixIndex
      _kiwidocIndex.stats = stats
      _kiwidocIndex.keywordIndex = keywordIndex
    }
    return _kiwidocIndex
  }

  MutablePrefixIndex getPrefixIndex()
  {
    new NoResultsPrefixIndex()
  }

  MutableKeywordIndex getKeywordIndex()
  {
    getBean('keywordIndexBean')
  }

  KiwidocIndex.Stats getStats()
  {
    return new KiwidocIndexStats(0, 0, 0)
  }

  String getCachesDir()
  {
    opts.'output' ?: DEFAULT_CACHES_DIR
  }

  String getKiwidocRoot()
  {
    opts.'kiwidoc-root' ?: DEFAULT_KIWIDOC_ROOT
  }

  FileSystemManager getFileSystem()
  {
    getBean('fileSystemBean')
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