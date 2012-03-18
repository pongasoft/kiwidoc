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

class PreferencesController
{
  private static final String DEVELOPPER_MODE_COOKIE = 'devmode'

  def switchDevelopperMode = {
    def developperMode = loadDevelopperMode(session)

    // switch developper mode
    developperMode = !developperMode

    saveDevelopperMode(session, developperMode)

    if(params.uri)
    {
      redirect(uri: params.uri - '/java')
    }
    else
    {
      redirect(uri: '/')
    }
  }

  // TODO HIGH YP:  cookies are not working... getting cookie with same name twice and
  // no session id cookie!
//  static def saveDevelopperMode(response, developperMode)
//  {
//    def cookie = new Cookie(DEVELOPPER_MODE_COOKIE, developperMode.toString())
//    cookie.path = '/'
//    cookie.maxAge = -1
//    response.addCookie(cookie)
//  }
//
//  static def loadDevelopperMode(request)
//  {
//    def developperMode = false
//
//    def cookie = request.cookies?.find { it.name = DEVELOPPER_MODE_COOKIE}
//
//    if(cookie?.value?.equals('true'))
//      developperMode = true
//
//    return developperMode
//  }

  static def saveDevelopperMode(session, developperMode)
  {
    session.developperMode = developperMode
  }

  static def loadDevelopperMode(session)
  {
    def developperMode = false

    if(session.developperMode)
      developperMode = true

    return developperMode
  }
}
