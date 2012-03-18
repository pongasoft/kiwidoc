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
import com.pongasoft.kiwidoc.model.resource.LibraryResource
import com.pongasoft.kiwidoc.model.resource.LibraryVersionResource

class RootController extends BaseController
{

  KiwidocLibraryStore libraryStore

  def index = {
    def model = initModel()
    model.jdks = libraryStore.getJdks()
    model.android = servletContext.getAttribute('androidLibraries')
    render(view:"index", model: model)
  }

  def info = {
    def model = initModel()
    model.content = [resource: new LibraryResource('java', 'j2se')]

    if(params.page == '500')
      throw new Exception('generating 500 error')

    render(view: params.page, model: model)
  }

  def error = {

    switch(params.code)
    {
      case '404':
        render(view: 'error_404')
        break;

      case '503':
        render(view: 'error_503')
        break;

      default:
        request.'javax.servlet.error.status_code' = params.code
        render(view: "error_x")
        break;
    }
  }

}
