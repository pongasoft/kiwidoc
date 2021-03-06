
 
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

/**
 * @author yan@pongasoft.com
*/
class MiscUtils
{
  static def save(map, valuesMap)
  {
    def savedValues = [:]
    valuesMap.each { k, v ->
      savedValues[k] = map[k]
      map[k] = v
    }

    return savedValues
  }

  static def restore(map, savedValuesMap)
  {
    savedValuesMap.each { k, v ->
      map[k] = v
    }
  }

  static def saveAndRestore(map, valuesMap, closure)
  {
    def save = save(map, valuesMap)
    try
    {
      closure(map)
    }
    finally
    {
      restore(map, save)
    }
  }

}