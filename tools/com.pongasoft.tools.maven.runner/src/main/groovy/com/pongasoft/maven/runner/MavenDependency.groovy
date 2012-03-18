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

package com.pongasoft.maven.runner

/**
 * @author yan@pongasoft.com
 */
public class MavenDependency
{
  private static def MAVEN_REGEX = ~/^mvn:\/\/([^\/]+)\/([^\/]+)\/(.+)$/

  def static create(String dependency)
  {
    if(dependency == null)
      return null;

    def matcher = MAVEN_REGEX.matcher(dependency)

    if(matcher)
    {
      def matches = matcher[0]
      return [
        groupId: matches[1],
        artifactId: matches[2],
        version: matches[3]
      ]
    }

    return null
  }
}