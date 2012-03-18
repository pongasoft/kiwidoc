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

class UrlMappings {
    static mappings = {

      "/l/$viewMode/$uri**" (controller: 'library', action: 'showContent')

      "/l" (controller: 'library', action: 'showContent') {
        viewMode = 'p'
      }

      "/l/p" (controller: 'library', action: 'showContent') {
        viewMode = 'p'
      }

      "/l/x" (controller: 'library', action: 'showContent') {
        viewMode = 'x'
      }

      "/s/$viewMode/$action?/$id?" (controller: 'search')
      
      "/" (controller: 'root', action:"index") {
          viewMode = 'p'
        }

      "/info/$page" (controller: 'root', action: 'info')  {
          viewMode = 'p'
        }

      "/error/$code" (controller: 'root', action: 'error')  {
          viewMode = 'p'
        }

      "/b/$action" (controller: 'block') {}

      "/admin" (controller: 'admin', action: 'index')  { }

	  "500"(view:'/root/error_x')
    "404"(view:'/root/error_404')
	}
}
