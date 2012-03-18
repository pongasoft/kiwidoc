
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

package com.pongasoft.kiwidoc.index.impl

import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.kiwidoc.index.impl.keyword.impl.KeywordIndexImpl
import org.apache.lucene.analysis.standard.StandardAnalyzer
import com.pongasoft.kiwidoc.index.impl.lucene.impl.LuceneDirectoryImpl
import com.pongasoft.kiwidoc.index.impl.lucene.impl.RAMDirectoryFactory
import grails.spring.BeanBuilder
import com.pongasoft.kiwidoc.model.ClassModel
import com.pongasoft.kiwidoc.model.resource.ClassResource
import java.lang.reflect.Modifier
import com.pongasoft.kiwidoc.model.DocModel
import com.pongasoft.kiwidoc.model.tag.Tag
import com.pongasoft.kiwidoc.model.ClassModel.ClassKind
import com.pongasoft.kiwidoc.model.type.GenericTypeVariables
import com.pongasoft.kiwidoc.index.api.KeywordQuery
import com.pongasoft.kiwidoc.model.tag.InlineTextTag
import com.pongasoft.kiwidoc.index.impl.keyword.impl.ResourceExtractor
import com.pongasoft.kiwidoc.model.tag.MainTag
import com.pongasoft.kiwidoc.model.AnnotationsModel
import com.pongasoft.kiwidoc.model.resource.PackageResource

/**
 * @author yan@pongasoft.com */
public class TestKeywordIndexImpl extends GroovyTestCase
{
  def appContext
  LibraryVersionResource lib1 = new LibraryVersionResource('a', 'b', '1.0')

  protected void setUp()
  {
    super.setUp();

    def bb = new BeanBuilder()

    bb.beans {
      analyzer(StandardAnalyzer.class)

      ["publicOnlyDirectory", "publicAndPrivateDirectory"].each { name ->
        "${name}"(LuceneDirectoryImpl.class) { bean ->
          bean.initMethod = 'open'
          bean.destroyMethod = 'close'
          analyzer = analyzer
          directoryFactory = { RAMDirectoryFactory ram -> }
          userDataExtractor = { ResourceExtractor ude -> }
        }
      }

      keywordIndex(KeywordIndexImpl.class) {
        analyzer = analyzer
        publicOnlyDirectory = publicOnlyDirectory
        publicAndPrivateDirectory = publicAndPrivateDirectory
      }
    }

    appContext = bb.createApplicationContext()
  }

  protected void tearDown()
  {
    try
    {
      appContext.close()
    }
    finally
    {
      super.tearDown();
    }
  }

  /**
   * Test basic functionality of the index
   */
  void testIndex()
  {
    KeywordIndexImpl index = appContext.getBean('keywordIndex')

    // we add 3 classes
    index.indexModel(ccm(fqcn: "com.test.Class1", classDoc: "the quick brown fox"))
    index.indexModel(ccm(fqcn: "com.test2.Class2", classDoc: "the slow brown dog"))
    index.indexModel(ccm(fqcn: "com.test3.Class3", classDoc: "the quick blue goose"))

    def resources = index.findResources(new KeywordQuery("Class1", 10))
    assertEquals([ccr(fqcn:"com.test.Class1")], resources.topResults.resource)

    resources = index.findResources(new KeywordQuery("Class4", 10))
    assertEquals([], resources.topResults.resource)

    resources = index.findResources(new KeywordQuery("quick", 10))
    assertEquals([ccr(fqcn:"com.test.Class1"), ccr(fqcn: "com.test3.Class3")], resources.topResults.resource)

    // should be only 1 result
    resources = index.findResources(new KeywordQuery("quick", 1))
    assertEquals([ccr(fqcn:"com.test.Class1")], resources.topResults.resource)

    resources = index.findResources(new KeywordQuery("brown", 10))
    assertEquals([ccr(fqcn:"com.test.Class1"), ccr(fqcn: "com.test2.Class2")], resources.topResults.resource)

    // test with filter
    def query = new KeywordQuery("quick", 10)
    query.filter =  new PackageResource(lib1, "com.test")
    resources = index.findResources(query)
    assertEquals([ccr(fqcn:"com.test.Class1")], resources.topResults.resource)

    query.filter =  new PackageResource(lib1, "com.test2")
    resources = index.findResources(query)
    assertEquals([], resources.topResults.resource)

    query.filter =  new PackageResource(lib1, "com.test3")
    resources = index.findResources(query)
    assertEquals([ccr(fqcn:"com.test3.Class3")], resources.topResults.resource)

    query.filter =  ccr(fqcn: "com.test3.Class3")
    resources = index.findResources(query)
    assertEquals([ccr(fqcn:"com.test3.Class3")], resources.topResults.resource)

    query.filter =  lib1
    resources = index.findResources(query)
    assertEquals([ccr(fqcn:"com.test.Class1"), ccr(fqcn: "com.test3.Class3")], resources.topResults.resource)

    // we unindex 1 class
    index.unindexResource(ccr(fqcn: "com.test3.Class3"))
    
    resources = index.findResources(new KeywordQuery("quick", 10))
    assertEquals([ccr(fqcn:"com.test.Class1")], resources.topResults.resource)

    resources = index.findResources(new KeywordQuery("dog", 10))
    assertEquals([ccr(fqcn:"com.test2.Class2")], resources.topResults.resource)

    // we unindex the library
    index.unindexLibraryVersion(lib1)

    resources = index.findResources(new KeywordQuery("quick", 10))
    assertEquals([], resources.topResults.resource)

    resources = index.findResources(new KeywordQuery("dog", 10))
    assertEquals([], resources.topResults.resource)
  }

  void testHighlight()
  {
    KeywordIndexImpl index = appContext.getBean('keywordIndex')

    def highlights =
      index.highlightResults(new KeywordQuery('brown', 10),
                             [ccm(fqcn: "com.test.Class1", classDoc: "the <div>quick</div> <p>brown</p> fox")])

    println highlights[ccr(fqcn:"com.test.Class1")][0]
  }

  private def ccm(args)
  {
    def resource = ccr(args)

    return new ClassModel(resource,
                          Modifier.PUBLIC,
                          new DocModel(new MainTag(resource, args.classDoc ? [new InlineTextTag(args.classDoc)]: []),
                                       Tag.NO_TAGS),
                          new AnnotationsModel([]),
                          ClassKind.CLASS,
                          GenericTypeVariables.NO_GENERIC_TYPE_VARIABLES,
                          null,
                          [],
                          [],
                          [],
                          null,
                          [],
                          [])
  }

  private def ccr(args)
  {
    return new ClassResource(args.libraryVersion ?: lib1, args.fqcn)
  }
}
