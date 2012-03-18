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

import com.pongasoft.kiwidoc.builder.KiwidocBuilder
import grails.spring.BeanBuilder
import org.apache.commons.vfs.impl.StandardFileSystemManager

/**
 * @author yan@pongasoft.com
 */
public class KiwidocWiring
{
  private def appContext

  def KiwidocWiring()
  {
    def bb = new BeanBuilder()

    bb.beans {

      // fileSystem
      fileSystem(StandardFileSystemManager.class) { bean ->
        bean.initMethod = 'init'
        bean.destroyMethod = 'close'
      }

      // kiwidocBuilder
      kiwidocBuilder(KiwidocBuilder.class)
    }

    appContext = bb.createApplicationContext()
  }

  def destroy()
  {
    appContext.close()
  }

  def getBean(beanName)
  {
    return appContext.getBean(beanName)
  }
}