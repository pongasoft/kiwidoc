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

package com.pongasoft.kiwidoc.index.impl;

import com.pongasoft.kiwidoc.index.api.KiwidocIndex;

import java.io.Serializable;

/**
 * @author yan@pongasoft.com
 */
public class KiwidocIndexStats implements KiwidocIndex.Stats, Serializable
{
  private static final long serialVersionUID = 1L;

  private int _libraryCount;
  private int _packageCount;
  private int _classCount;

  /**
   * Constructor
   */
  public KiwidocIndexStats(int libraryCount, int packageCount, int classCount)
  {
    _libraryCount = libraryCount;
    _packageCount = packageCount;
    _classCount = classCount;
  }

  public int getLibraryCount()
  {
    return _libraryCount;
  }

  public void setLibraryCount(int libraryCount)
  {
    _libraryCount = libraryCount;
  }

  public int getPackageCount()
  {
    return _packageCount;
  }

  public void setPackageCount(int packageCount)
  {
    _packageCount = packageCount;
  }

  public int getClassCount()
  {
    return _classCount;
  }

  public void setClassCount(int classCount)
  {
    _classCount = classCount;
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("l=").append(_libraryCount);
    sb.append(";p=").append(_packageCount);
    sb.append(";c=").append(_classCount);
    return sb.toString();
  }
}
