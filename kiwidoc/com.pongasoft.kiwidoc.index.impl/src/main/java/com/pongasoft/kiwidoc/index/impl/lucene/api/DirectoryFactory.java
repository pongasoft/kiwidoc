
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

package com.pongasoft.kiwidoc.index.impl.lucene.api;

import org.apache.lucene.store.Directory;

import java.io.IOException;

/**
 * @author yan@pongasoft.com
 */
public interface DirectoryFactory
{
  /**
   * Opens the directory
   *
   * @param create <code>true</code> if the directory needs to be created/erased
   * @return a directory
   * @throws IOException when there is a problem opening the directory */
  Directory openDirectory(boolean create) throws IOException;
}