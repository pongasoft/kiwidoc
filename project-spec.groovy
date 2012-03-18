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

spec = [
    name: 'kiwidoc',
    group: 'com.pongasoft',
    version: '2.0.0',

    versions: [
      grails: '1.3.5',
      groovy: '1.7.5',
      jetty: '7.2.2.v20101205',
      linkedinUtils: '1.8.0',
      lucene: '2.4.1',
      slf4j: '1.5.8' // to be compatible with grails 1.3.5
    ],

    // information about the build framework itself
    build: [
        type: "gradle",
        version: "0.9",
        uri: "http://gradle.artifactoryonline.com/gradle/distributions/gradle-0.9-all.zip",
        commands: [
            "snapshot": "gradle release",
            "release": "gradle -Prelease=true release"
        ]
    ]
]

/**
 * External dependencies
 */
spec.external = [
  asm: 'asm:asm:3.2',
  bnd: 'biz.aQute:bnd:0.0.401',
  commonsCli: 'commons-cli:commons-cli:1.2',
  commonsHttpClient: 'commons-httpclient:commons-httpclient:3.1',
  commonsLogging: 'commons-logging:commons-logging:1.0.4',
  commonsVfs: 'commons-vfs:commons-vfs:1.0',
  grailsSpring: "org.grails:grails-spring:${spec.versions.grails}",
  groovy: "org.codehaus.groovy:groovy:${spec.versions.groovy}",
  javaMail: 'javax.mail:mail:1.4.1',
  jerichoHtml: 'net.htmlparser.jericho:jericho-html:2.6.1',
  jettyPackage: [
    group: "org.eclipse.jetty",
    name: "jetty-distribution",
    version: spec.versions.jetty,
    ext: "tar.gz"
  ],
  json: 'org.json:json:20080701',
  junit: 'junit:junit:4.4',
  linkedinUtilsCore: "org.linkedin:org.linkedin.util-core:${spec.versions.linkedinUtils}",
  linkedinUtilsGroovy: "org.linkedin:org.linkedin.util-groovy:${spec.versions.linkedinUtils}",
  luceneCore: "org.apache.lucene:lucene-core:${spec.versions.lucene}",
  luceneHighlighter: "org.apache.lucene:lucene-highlighter:${spec.versions.lucene}",
  mavenAntTasks: 'org.apache.maven:maven-ant-tasks:2.1.1',
  slf4jLog4j: "org.slf4j:slf4j-log4j12:${spec.versions.slf4j}",
  tagsoup: 'org.ccil.cowan.tagsoup:tagsoup:1.2'
]
