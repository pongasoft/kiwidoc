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

package com.pongasoft.kiwidoc.web

import com.pongasoft.kiwidoc.builder.KiwidocLibraryStore
import com.pongasoft.kiwidoc.builder.NoSuchContentException
import com.pongasoft.kiwidoc.model.Model
import com.pongasoft.kiwidoc.model.DependenciesModel
import com.pongasoft.kiwidoc.model.ClassModel
import com.pongasoft.kiwidoc.model.resource.PathManager
import com.pongasoft.kiwidoc.model.resource.Resource
import com.pongasoft.kiwidoc.model.ResolvableModel
import com.pongasoft.kiwidoc.web.utils.PaginationHelper
import com.pongasoft.kiwidoc.model.MethodModel

class LibraryController extends BaseController
{
  KiwidocLibraryStore libraryStore
  PathManager pathManager

  public static PaginationHelper LPH =
    new PaginationHelper(defaultMax: 50, maxMax: 50)

  def index = {
    render "From Library controller!"
  }

  def showContent = {
    def uri = extractURI(request, params.viewMode)
    if(!uri)
    {
      response.sendError 404
      return
    }
    Model content
    def model = initModel()
    try
    {
      Resource resource = pathManager.computeResource(uri)
      if(resource == null)
      {
        response.sendError 404
        return
      }
      content = libraryStore.loadContent(resource)
      switch(content.kind)
      {
        case Model.Kind.CLASS:
          def inheritance = libraryStore.resolveWithInheritance(DependenciesModel.NO_DEPENDENCIES, (ClassModel) content)
          if(inheritance.isPublicAPI() && model.preferences.isViewModePublic)
            inheritance = inheritance.toPublicAPI()
          if(inheritance.superClass || inheritance.interfaces)
          {
            inheritance = inheritance.inheritDoc()
            model.inheritance = [
              model: inheritance,
              superClassHierarchy: inheritance.flattenSuperClasses(),
              interfaceHierarchy: inheritance.flattenInterfaces(),
            ]
          }
          content = inheritance.baseClass
          model.methods = splitMethodsByType(content.resource, inheritance?.allMethods ?: content.allMethods)
          break;

        case Model.Kind.PACKAGE:
        content =
          libraryStore.resolve(DependenciesModel.NO_DEPENDENCIES, (ResolvableModel) content)

          if(content.isExportedPackage() && model.preferences.isViewModePublic)
            content = content.toPublicAPI()
          break;

        case Model.Kind.LIBRARY_VERSION:
          content = libraryStore.resolve(DependenciesModel.NO_DEPENDENCIES,
                                         (ResolvableModel) content)
          if(model.preferences.isViewModePublic)
          {
            content = content.toPublicAPI() ?: content
          }

          model.resolvedDependencies = libraryStore.resolve(content.dependencies.dependencies)
        break;

        case Model.Kind.LIBRARY:
          def lvrs = [:]
          content.versionsResources.each { lvr ->
            try
            {
              lvrs[lvr] = libraryStore.loadContent(lvr)
              if(model.preferences.isViewModePublic)
                lvrs[lvr] = lvrs[lvr].toPublicAPI()
            }
            catch(NoSuchContentException e)
            {
              log.warn("could not load ${lvr} (ignored): ${e.message}")
            }
          }
          model.lvrs = lvrs
        break;

        case Model.Kind.ORGANISATION:
          def libraries = [:]
          content.libraryResources.each { library ->
            try
            {
              libraries[library] = libraryStore.loadContent(library)
              if(model.preferences.isViewModePublic)
                libraries[library] = libraries[library].toPublicAPI()
            }
            catch(NoSuchContentException e)
            {
              log.warn("could not load ${library} (ignored): ${e.message}")
            }
          }
          model.libraries = libraries
        break;

        case Model.Kind.REPOSITORY:
        def libraries = new ArrayList(content.libraries)
        libraries = libraries.sort() { l1, l2 ->
          def diff = l1.organisation.compareTo(l2.organisation)
          if(diff == 0)
            diff = l1.name.compareTo(l2.name)
          return diff
        }
        model.libraries = libraries
        model.jdks = libraryStore.getJdks()
        break;

        case Model.Kind.MANIFEST:
          def manifest = content.manifest
          def baos = new ByteArrayOutputStream()
          manifest.write(baos)
          baos.close()
          model.raw = new String(baos.toByteArray())
        break;

        default:
        break;
      }
    }
    catch (NoSuchContentException e)
    {
      response.sendError 404
      return
    }

    model.content = content

    render(view:"showContent_${content.kind}", model:model)
  }

  private def splitMethodsByType(Resource resource, allMethods)
  {
    def methods = [:]

    def constructors = []
    def staticMethods = []
    def otherMethods = []
    def inheritedMethods = []

    int count = 0

    allMethods.each { MethodModel method ->
      count++
      if(method.isConstructor())
      {
        constructors << method
      }
      else
      {
        if(method.isStatic())
          staticMethods << method
        else
          otherMethods << method
      }

      if(method.resource.parent != resource)
        inheritedMethods << method
    }

    if(constructors)
      methods.constructors = constructors
    if(staticMethods)
      methods.staticMethods = staticMethods

    // always add other methods
    if(otherMethods)
      methods.otherMethods = otherMethods

    // all inherited methods
    if(inheritedMethods)
      methods.inheritedMethods = inheritedMethods

    return methods
  }

  /**
   * TODO HIGH YP: this is a hack for bug GRAILS-4253
   */
  private URI extractURI(request, viewMode)
  {
    String uri = request.getAttribute("javax.servlet.forward.servlet_path");
    uri = uri - "/l/${viewMode}"
    if(uri.startsWith('/'))
    {
      uri = uri - '/'
    }
    try
    {
      return new URI(uri)
    }
    catch (URISyntaxException e)
    {
      log.warn("Invalid URI: ${uri}")
      return null
    }
  }
}
