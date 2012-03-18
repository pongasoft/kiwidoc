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

// Place your Spring DSL code here
beans = {

  /*
  nodeComparator(IndexableResource.IRComparator) {
    resourceComparator = {ResourceComparator rc -> }
  }

  kiwidocIndex(KiwidocIndexImpl) {
    exportedClassesTST = { TernarySearchTreeImpl tst ->
      nodeComparator = ref('nodeComparator')
    }
    privateClassesTST = { TernarySearchTreeImpl tst ->
      nodeComparator = ref('nodeComparator')
    }
    resourceEncoder = {NoOpResourceEncoder re -> }
  }
  */

  if(grails.util.GrailsUtil.isDevelopmentEnv())
  {
    myShutdownHook(com.pongasoft.kiwidoc.web.utils.ShutdownHook)
  }
}