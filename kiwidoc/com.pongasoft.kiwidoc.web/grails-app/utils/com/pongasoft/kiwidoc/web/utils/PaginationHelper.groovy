
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

package com.pongasoft.kiwidoc.web.utils

/**
 * @author yan@pongasoft.com
*/
class PaginationHelper
{
  def defaultMax
  def maxMax

  def computeRange(offset, max, total)
  {
    def low = offset
    def high = low + max

    if(high > total)
      high = total

    return (low..<high)
  }

  def extractMax(params)
  {
    def max
    try
    {
      max = params.max?.toInteger()
      if(max)
      {
        if(max < 0)
          max = defaultMax;
        else
          max = Math.min(max, maxMax);
      }
      else
      {
        max = defaultMax
      }
    }
    catch (NumberFormatException e)
    {
      max = defaultMax
    }

    params.max = max

    return max;
  }

  def extractOffset(params)
  {
    def offset
    try
    {
      offset = params.offset?.toInteger()
      if(offset < 0)
        offset = 0
    }
    catch (NumberFormatException e)
    {
      offset = 0
    }

    params.offset = offset

    return offset;
  }
}