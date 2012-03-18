
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

package com.pongasoft.kiwidoc.model;

/**
 * @author yan@pongasoft.com
 */
public class LibraryVersionStats
{
  private final int _exportedPackagesCount;
  private final int _privatePackagesCount;
  private final int _exportedClassesCount;
  private final int _privateClassesCount;

  /**
   * Constructor
   */
  public LibraryVersionStats(int exportedPackagesCount,
                             int privatePackagesCount,
                             int exportedClassesCount,
                             int privateClassesCount)
  {
    _exportedPackagesCount = exportedPackagesCount;
    _privatePackagesCount = privatePackagesCount;
    _exportedClassesCount = exportedClassesCount;
    _privateClassesCount = privateClassesCount;
  }

  public int getExportedPackagesCount()
  {
    return _exportedPackagesCount;
  }

  public int getPrivatePackagesCount()
  {
    return _privatePackagesCount;
  }

  public int getExportedClassesCount()
  {
    return _exportedClassesCount;
  }

  public int getPrivateClassesCount()
  {
    return _privateClassesCount;
  }

  public int getTotalClassesCount()
  {
    return getExportedClassesCount() + getPrivateClassesCount();
  }
}
