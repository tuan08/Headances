#!/bin/bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

OS=`uname`

APP_HOME=..
JAVA_BASE="d:/java"

if [ "$OS" == "Darwin" ] ; then
  JAVA_BASE="/Users/tuannguyen/java" 
elif [ "$OS" == "Linux" ] ; then
  JAVA_BASE="/opt" 
  JAVA_HOME="/opt/jdk1.6"
fi


if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

for f in $LIB/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done

if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi
