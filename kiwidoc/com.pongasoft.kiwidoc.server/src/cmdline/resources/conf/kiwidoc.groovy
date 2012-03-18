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

println "Loading server configuration"

grails.serverURL = "http://localhost/java"
kiwidoc.dir = 'file:///export/content/pongasoft/kiwidoc/data'
caches.dir = 'file:///export/content/pongasoft/kiwidoc/caches'

log4j = {
    appenders {
    	rollingFile name:'file',
                  file: "${System.properties['com.pongasoft.kiwidoc.root']}/logs/java.log",
                  maxFileSize: '100KB',
                  layout: pattern(conversionPattern: '%d{MM/dd HH:mm:ss} %-5p [%c] %x - %m%n')
    }

    root {
      info 'file'
      additivity = false
    }

  error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
       'org.codehaus.groovy.grails.web.pages', //  GSP
       'org.codehaus.groovy.grails.web.sitemesh', //  layouts
       'org.codehaus.groovy.grails."web.mapping.filter', // URL mapping
       'org.codehaus.groovy.grails."web.mapping', // URL mapping
       'org.codehaus.groovy.grails.commons', // core / classloading
       'org.codehaus.groovy.grails.plugins', // plugins
       'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
       'org.springframework',
       'org.hibernate'

  info 'grails',
       'com.pongasoft'

  warn   'org.mortbay.log', 'net.htmlparser.jericho'
}
