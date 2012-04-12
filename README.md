Introduction
============
This is the open source version of kiwidoc, a website to search and browse javadoc. The website is no longer active, but the source code is now available for download.

Features
========

This is the list of features supported by kiwidoc.

Dynamically linked javadoc
--------------------------
When looking at standard javadoc, classes are linked together within the library. It is also possible, using javadoc, to create links to classes in other libraries, but it requires you to have the source code for those libraries during the javadoc generation phase. kiwidoc automatically links all the libraries it knows about. And, it's even dynamic: if a dependent library gets added to kiwidoc later, the link will automatically be created. As of 1.4.0, the javadoc is pre-rendered which improves the site speed drastically while still preserving the dynamicity when new libraries get added.

Searchable
----------
kiwidoc is completely searchable. You can search javadoc content as well as manifest headers.

Proximity searching
-------------------
When running a search, the current viewed resource (whether it is a class, a package, etc.) is always treated as the starting point for the search. The search is then expanded outward from that point. This provides very intuitive results that favor items 'closer' to the current resource. For example, if you are currently looking at the class `java.util.HashMap` (java/j2se/1.5) and you run a search for map, kiwidoc will automatically show you the class `java.util.Map` (java/j2se/1.5) as the first result, then the following results will be 'close' to this one (for example, other `java.util.Map` in other versions of the jdk, then it will expand to other libraries).

Typeahead searching
-------------------
The search box features a typeahead feature on class names, package names and library names for fast access. The typeahead results are sorted using the proximity searching algorithm described previously. This feature makes the site extremely easy to navigate.

Note: this feature is not yet available in the open source version!

Camel case searching
--------------------
The typeahead searching feature allows you to use only the uppercases in a class name. For example, if you are looking for the class `BufferedInputStream`, simply enter BIS.

Dynamically generated javadoc
-----------------------------
In a similar fashion to the dynamically linked feature, the content of the javadoc is generated dynamically, covering `{@inheritdoc}` tag and inherited parameter documentation.

Public View vs Private view
---------------------------
kiwidoc allows you to switch between *public* view and *private* view at any point in time. *public* view shows you the public API, while *private* view shows you additional non-public information (private methods, non exported packages, etc.).

Display dependencies
--------------------
When looking at a library you can see its dependencies (direct, transitive and optional being called out separately). Most of the information is coming from the maven repositories, but internally kiwidoc can handle dependencies as long as they are provided.

Manifest view
-------------
You can view the manifest of all the libraries indexed (provided there is one). The manifest view is OSGi aware and displays the long headers in a very readable fashion.

OSGi aware
----------
kiwidoc is aware of OSGi and will tell you whether a library is an OSGi bundle or not. Since kiwidoc automatically indexes the manifests, it is easy to find all the bundles by simply searching for `Bundle-SymbolicName`.

Facetted search
---------------
Facetted search automatically categorizes search results, allowing you to narrow your search to just one category.

Easy third party access
-----------------------
All links are following a REST style pattern making it easy for third parties to access kiwidoc information.

Scalable architecture
---------------------
Scalable architecture means that kiwidoc can be expanded to include a virtually limitless number of libraries.

Bytecode parsing
----------------
Even if the source code for a library is not available, kiwidoc can generate basic reference information directly from the bytecode (including, in most cases, parameter names).

IDE Style display
-----------------
Information is displayed in a familiar way. Icons inspired by those used in Eclipse will also help you feel right at home with kiwidoc.

Not advertising heavy
---------------------
kiwidoc is a functional reference tool, not a thinly-disguised advertising billboard. You won't see kiwidoc littered with ads like so many other sites.

Overview
========

<img src="https://github.com/pongasoft/kiwidoc/raw/master/docs/images/kiwidoc.png">

This picture represents an overview of the whole process:

* source code, byte code, javadoc, dependencies, etc... are fed into the repository builder which generates what is called the kiwidoc, which is a json representation of the javadoc and structure of the code (classes, methods, etc...). By default, this gets stored under `/export/content/pongasoft/kiwidoc/data`
* the kiwidoc is then fed into the index builder which creates the keyword index as well as the stats (note that at this moment and for licensing reasons, the open source version does not support typeahead and it will be added in a later version). By default, this gets stored under `/export/content/pongasoft/kiwidoc/caches`
* the java web server runs a java web server which uses both the kiwidoc and the index
* the final piece (in yellow on the diagram) is entirely optional but allows to generate a static version of the site so that it is much faster to navigate: the kiwidoc is fed into the static generator which uses the java web server to render the html (or in other words, the static generator is essentially "crawling" the website and capturing the output). By default, this gets stored under `/export/content/pongasoft/kiwidoc/static`. A simple front web server (like nginx or apache) can be used to serve all static pages and only delegate the search functionality to the java web server.

Quickstart
==========

Step 1
------

At the root of the source code, enter the following command (make sure you use gradle v0.9 as defined in the `project-spec.groovy` file!):

```
gradle -Prelease=true -i package-install
```

This will generate several executables each representing one of the box in the diagram (the `-i` option will display where the executables are installed).

Step 2
------

Generating the kiwidoc can be more or less complicated so in order to bootstrap faster, you should download the pregenerated jdk 1.6 kiwidoc available on the [Downloads](https://github.com/pongasoft/kiwidoc/downloads) page and simply untar (bzip2 compressed) in the data directory (`/export/content/pongasoft/kiwidoc/data`).

Step 3
-------

Now you need to index the kiwidoc. Using the index builder tool (`com.pongasoft.tools.kiwidoc.index.builder-<version>`) enter the command:

```
./bin/index-builder.sh
```

Step 4
------

Start the kiwidoc server (a jetty server running on port 8080: `com.pongasoft.kiwidoc.server-<version>`).

```
./bin/kiwidocctl.sh start
```

Step 5
------

Using your browser you can then navigate to http://localhost:8080/java

At this stage, you have kiwidoc up and running. Here is a more detailed description of all the executables.

Generating the kiwidoc
======================

Generating the kiwidoc is the process that takes the source code, byte code, dependencies, classpath, etc... and generates a json representation of the code. The tool used for this step is `com.pongasoft.tools.kiwidoc.repository.builder-<version>`. Example:

```
  ./bin/kiwidoc.sh -v /tmp/libs.txt
```

with `/tmp/libs.txt`:

```
mvn://junit/junit/4.3
mvn://junit/junit/4.4
```

Generating the kiwidoc when the library is already in the maven central repository and contain all the proper information (like javadoc, dependencies, etc...) is by far the easiest way. You simply provide a text file where every line is a `mvn://<groupId>/<artifactId>/<version` url.

When not in the maven central repository, it is a much more complicated and involved process: you need to tell kiwidoc where and how to find all the relevant information. For this you use a groovy file instead. Example: 

```
  ./bin/kiwidoc.sh -v /tmp/libs.groovy
```

with `/tmp/libs.groovy` looking like this (this is an example using some LinkedIn open source libraries):

<pre>
def basedir = "/Volumes/pongasoft/maven-cache/com/linkedin/opensource"

def zoie="${basedir}/zoie/1.4.0"

libs["com.linkedin.opensource/zoie/1.4.0"] =
    [
      sources: "jar:file:${zoie}/src.jar!/java",
      javadoc: "jar:file:${zoie}/javadoc.jar!/doc",
      classes: "${zoie}/zoie-1.4.0.jar",
      ivy: "${zoie}/ivy.xml",
      dependencies: [directDependencies: ["commons-logging/commons-logging/1.1", "org.directwebremoting/dwr/3.0.M1",
        "fastutil/fastutil/5.0.5", "org.json/json/20090211", "junit/junit/4.5", "log4j/log4j/1.2.15",
        "org.apache.lucene/lucene-core/2.4.0", "org.apache.lucene/lucene-highlighter/2.3.0",
        "javax.servlet/servlet-api/2.5", "org.springframework/spring-webmvc/2.5.5", "org.springframework/spring/2.5.5",
        "org.mortbay.jetty/jetty/6.1.19", "org.mortbay.jetty/jetty-management/6.1.19", "org.mortbay.jetty/jetty-naming/6.1.19",
        "org.mortbay.jetty/jetty-util/6.1.19", "mx4j/mx4j/3.0.1", "mx4j/mx4j-tools/3.0.1"]]
    ]

def bobo="${basedir}/bobo-browse/2.0.5"

libs["com.linkedin.opensource/bobo-browse/2.0.5"] =
    [
      sources: "jar:file:${bobo}/src.jar!/src",
      javadoc: "jar:file:${bobo}/javadoc.jar!/doc",
      classes: "${bobo}/bobo-browse-2.0.5.jar",
      ivy: "${bobo}/ivy.xml",
      classpath: ["${zoie}/zoie-1.4.0.jar"],
      dependencies: [directDependencies: ["com.linkedin.opensource/zoie/1.4.0", "commons-logging/commons-logging/1.1",
      "fastutil/fastutil/5.0.5", "log4j/log4j/1.2.15", "org.apache.lucene/lucene-core/2.4.0"]]
    ]

def voldemort ="${basedir}/voldemort/0.51"

def voldermortLibs = []
new File("${voldemort}/lib").eachFile { voldermortLibs.add(it.canonicalPath) }

libs["com.linkedin.opensource/voldemort/0.51"] =
    [
      sources: "jar:file:${voldemort}/src.jar!/src/java",
      javadoc: "jar:file:${voldemort}/javadoc-client.jar!/client",
      classes: "${voldemort}/classes.jar",
      ivy: "${voldemort}/ivy.xml",
      classpath: voldermortLibs
    ]

</pre>

Note: the reason why kiwidoc uses javadoc as an input is because of the fact that some classes are not part of the javadoc public api. kiwidoc uses this information to determine which classes should be public vs which classes should be private!

Generating the index
====================

In order to allow for keyword and typeahead search, the kiwidoc needs to be indexed. For this you use the tool `com.pongasoft.tools.kiwidoc.index.builder-<version>`

Example:

```
./bin/index-builder.sh
2012/04/12 09:07:33.313 INFO [FSDirectoryFactory] Created empty FS directory /export/content/pongasoft/kiwidoc/caches/keyword/publicAndPrivate
2012/04/12 09:07:33.317 INFO [FSDirectoryFactory] Created empty FS directory /export/content/pongasoft/kiwidoc/caches/keyword/publicOnly
2012/04/12 09:07:33.480 INFO [IndexBuilder] Processing 2 libraries
2012/04/12 09:07:38.502 INFO [IndexBuilder] t=5s477;l=0;p=44;c=744
2012/04/12 09:07:43.504 INFO [IndexBuilder] t=10s482;l=0;p=158;c=3278
2012/04/12 09:07:48.505 INFO [IndexBuilder] t=15s483;l=0;p=244;c=5597
2012/04/12 09:07:53.506 INFO [IndexBuilder] t=20s484;l=0;p=389;c=7943
2012/04/12 09:07:58.506 INFO [IndexBuilder] t=25s484;l=0;p=536;c=10949
2012/04/12 09:08:03.507 INFO [IndexBuilder] t=30s485;l=0;p=661;c=13045
2012/04/12 09:08:08.508 INFO [IndexBuilder] t=35s486;l=0;p=792;c=15944
2012/04/12 09:08:13.508 INFO [IndexBuilder] t=40s486;l=0;p=935;c=19163
2012/04/12 09:08:15.127 INFO [IndexBuilder] Saving stats
2012/04/12 09:08:15.398 INFO [IndexBuilder] Optimizing keyword index
2012/04/12 09:08:16.507 INFO [IndexBuilder] Keyword index optimized in 1s109
2012/04/12 09:08:16.508 INFO [IndexBuilder] 43s485 l=2;p=974;c=19738
```

Starting the kiwidoc server
===========================

Once you have the kiwidoc and the index, the service can be started. For this you use the fully packaged java web server (jetty) `com.pongasoft.kiwidoc.server-<version>`. Example:

```
./bin/kiwidocctl.sh start

then to view the log file...

tail logs/java.log
```

Stopping the kiwidoc server
===========================

Stopping the server is as simple as using a different argument.

```
./bin/kiwidocctl.sh stop
```

Generating the static site
==========================

If you want to generate a static version of kiwidoc, you need to have the server up and running (see previous section) and you use the tool `com.pongasoft.tools.kiwidoc.static-generator-<version>`. Example:

```
./bin/static-generator.sh -d 5 -r / -t 4 -s
t=21m43s55;r=42846;p=42846;skp=0;ct=0;s=1.47g;sks=0;at=30;as=36.02k;ert=0;q=0;r=0;e=52
```

You can ignore the warnings. This process takes quite a bit of time as it needs to essentially crawl the entire website to generate all the pages statically.

You can then use a regular web server to serve the static pages while delegating search to the java web server.

More information
================

If you need more information/help, please check out the [google+ page](https://plus.google.com/109803936727111638144)