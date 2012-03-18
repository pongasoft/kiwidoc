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

package com.pongasoft.kiwidoc.builder.doclet

import org.apache.commons.vfs.impl.StandardFileSystemManager
import org.apache.commons.vfs.FileObject
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.KiwidocLibraryStoreImpl
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource
import com.pongasoft.kiwidoc.builder.model.LibraryModelBuilder
import com.pongasoft.kiwidoc.model.resource.RepositoryResource
import com.pongasoft.kiwidoc.builder.DuplicateLibraryException
import com.pongasoft.kiwidoc.builder.NoSuchContentException
import com.pongasoft.kiwidoc.builder.ContentHandlersImpl
import com.pongasoft.kiwidoc.model.resource.OrganisationResource
import com.pongasoft.kiwidoc.model.resource.LibraryResource

class TestKiwidocLibraryStore extends GroovyTestCase
{
  StandardFileSystemManager manager

  public void setUp() throws IOException
  {
    manager = new StandardFileSystemManager()
    manager.init()
  }

  public void tearDown() throws IOException
  {
    manager.close()
  }

  public void testAddRemove() throws Exception
  {
    FileObject storage = manager.resolveFile("ram://testWithTestData/kiwidoc")

    KiwidocLibraryStore store = new KiwidocLibraryStoreImpl(new ContentHandlersImpl(storage))

    check(store) {
      // empty
    }
    
    LibraryVersionResource lib1V1_0_0 =
      new LibraryVersionResource("test.organisation", "test.name", "1.0.0")

    // adding lib1 v1.0.0
    store.storeLibrary(new LibraryModelBuilder(lib1V1_0_0))

    shouldFail(DuplicateLibraryException) {
      store.storeLibrary(new LibraryModelBuilder(lib1V1_0_0))
    }

    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "1.0.0")
      }
    }

    LibraryVersionResource lib1V2_0_0 =
      new LibraryVersionResource("test.organisation", "test.name", "2.0.0")

    shouldFail(NoSuchContentException) {
      store.loadContent(lib1V2_0_0)
    }

    // adding lib1 v2.0.0
    store.storeLibrary(new LibraryModelBuilder(lib1V2_0_0))

    shouldFail(DuplicateLibraryException) {
      store.storeLibrary(new LibraryModelBuilder(lib1V2_0_0))
    }

    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
    }

    // deleting lib1 v1.0.0
    assertTrue store.deleteLibraryVersion(lib1V1_0_0)
    assertFalse store.deleteLibraryVersion(lib1V1_0_0)

    shouldFail(NoSuchContentException) {
      store.loadContent(lib1V1_0_0)
    }

    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "2.0.0")
      }
    }

    // adding lib1 v1.0.0 back
    store.storeLibrary(new LibraryModelBuilder(lib1V1_0_0))

    shouldFail(DuplicateLibraryException) {
      store.storeLibrary(new LibraryModelBuilder(lib1V1_0_0))
    }
    
    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
    }

    // adding 2 versions of another library
    LibraryVersionResource lib2V1_0_0 =
      new LibraryVersionResource("test.organisation.2", "test.name", "1.0.0")

    LibraryVersionResource lib2V2_0_0 =
      new LibraryVersionResource("test.organisation.2", "test.name", "2.0.0")

    // adding lib2 v1.0.0 and v2.0.0
    store.storeLibrary(new LibraryModelBuilder(lib2V1_0_0))
    store.storeLibrary(new LibraryModelBuilder(lib2V2_0_0))

    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
      organisation(name: 'test.organisation.2') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
    }

    // removing lib1 v1.0.0
    assertTrue store.deleteLibraryVersion(lib1V1_0_0)
    assertFalse store.deleteLibraryVersion(lib1V1_0_0)

    check(store) {
      organisation(name: 'test.organisation') {
        library(name: 'test.name', version: "2.0.0")
      }
      organisation(name: 'test.organisation.2') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
    }

    shouldFail(NoSuchContentException) {
      store.loadContent(lib1V1_0_0)
    }

    // removing lib1 v2.0.0
    assertTrue store.deleteLibraryVersion(lib1V2_0_0)
    assertFalse store.deleteLibraryVersion(lib1V2_0_0)

    check(store) {
      organisation(name: 'test.organisation.2') {
        library(name: 'test.name', version: "1.0.0")
        library(name: 'test.name', version: "2.0.0")
      }
    }

    // the library version is gone
    shouldFail(NoSuchContentException) {
      store.loadContent(lib1V2_0_0)
    }

    // so is the entire library
    shouldFail(NoSuchContentException) {
      println store.loadContent(lib1V2_0_0.parent)
    }

    // so is the entire organisation
    shouldFail(NoSuchContentException) {
      store.loadContent(lib1V2_0_0.parent.parent)
    }

    // removing lib2
    assertTrue store.deleteLibrary(lib2V1_0_0.parent)
    assertFalse store.deleteLibrary(lib2V1_0_0.parent)

    // the library version is gone
    shouldFail(NoSuchContentException) {
      store.loadContent(lib2V1_0_0)
    }

    // so is the entire library
    shouldFail(NoSuchContentException) {
      println store.loadContent(lib2V1_0_0.parent)
    }

    // so is the entire organisation
    shouldFail(NoSuchContentException) {
      store.loadContent(lib2V1_0_0.parent.parent)
    }

    check(store) {
      // empty
    }
  }

  private void check(store, closure)
  {
    def node = new NodeBuilder().repository(closure)

    def repositoryModel = store.loadContent(RepositoryResource.INSTANCE)
    // should have same number of organisations
    assertEquals(node.organisation?.size(), repositoryModel.organisationResources?.size())

    node.organisation?.each { organisation ->
      def organisationResource = new OrganisationResource(organisation.'@name')
      assertTrue(repositoryModel.organisationResources.contains(organisationResource))

      def libraryNames = new HashSet(organisation.library.'@name')

      def organisationModel = store.loadContent(organisationResource)
      assertEquals(libraryNames.size(), organisationModel.libraryResources.size())
      assertEquals(organisationResource, organisationModel.resource)
      assertEquals(RepositoryResource.INSTANCE, organisationModel.resource.parent)

      libraryNames.each { libraryName ->
        def libraryResource = new LibraryResource(organisationResource, libraryName)
        assertTrue(organisationModel.libraryResources.contains(libraryResource))
        def libraryVersions = organisation.library.findAll { it.'@name' == libraryName}

        def libraryModel = store.loadContent(libraryResource)
        assertEquals(libraryResource, libraryModel.resource)
        assertEquals(organisationResource, libraryModel.resource.parent)
        assertEquals(libraryVersions.size(), libraryModel.versions.size())
        libraryVersions.each { library ->
          assertTrue(libraryModel.versions.contains(library.'@version'))

          def lvr = new LibraryVersionResource(libraryResource, library.'@version')
          def libraryVersionModel = store.loadContent(lvr)
          assertEquals(lvr, libraryVersionModel.resource)
          assertEquals(libraryResource, libraryVersionModel.resource.parent)
        }
      }
    }
  }
}