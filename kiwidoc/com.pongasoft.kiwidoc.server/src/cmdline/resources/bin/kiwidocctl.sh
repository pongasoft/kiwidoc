#!/bin/bash

BASEDIR=`cd $(dirname $0)/.. ; pwd`
cd $BASEDIR

JVM_OPTIONS="-Xincgc -Djava.awt.headless=true -Xmx512m -Xms128m -Xloggc:$BASEDIR/logs/gc.log -XX:+PrintGCDateStamps -Djetty.port=8080"
JAVA_OPTIONS="$JVM_OPTIONS -Dorg.linkedin.app.name=com.pongasoft.kiwidoc -Dcom.pongasoft.kiwidoc.web.config.location=file://$BASEDIR/conf/kiwidoc.groovy -Dcom.pongasoft.kiwidoc.root=$BASEDIR" $BASEDIR/@jetty.distribution@/bin/jetty.sh $@
