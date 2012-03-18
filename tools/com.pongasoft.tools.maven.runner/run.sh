#!/bin/sh

#
# Copyright (c) 2012 Yan Pujante
#
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

DIRNAME=`dirname "$0"`

DEBUG=
#DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"

CLASSPATH="$DIRNAME/src/main/groovy"
#CLASSPATH="$HOME/.m2/repository/com/pongasoft/com.pongasoft.maven.runner/1.0.0-SNAPSHOT/com.pongasoft.maven.runner-1.0.0-SNAPSHOT.jar"

CMD_LINE=`JAVA_OPTS="$DEBUG" groovy -cp $CLASSPATH -e "com.pongasoft.maven.runner.Runner.main(args)" - "$@"`

#echo groovy $CMD_LINE

groovy $CMD_LINE

#JAVA_OPTS="-Xmx512m" groovy -cp src/main/groovy -e "com.pongasoft.maven.runner.Runner.main(args)" - "$@"
#JAVA_OPTS="-Xmx512m" groovy -cp target/classes run.groovy "$@"
#JAVA_OPTS="-Xmx512m -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" groovy -cp src/main/groovy run.groovy "$@"
