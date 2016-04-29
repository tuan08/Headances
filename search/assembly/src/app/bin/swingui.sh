#!/bin/bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

OS=`uname`

PRG="$0"
PRGDIR=`dirname "$PRG"`
APP_HOME=$PRGDIR/..

bin=`pwd`

ABS_APP_HOME=$APP_HOME

if [ -d "$APP_HOME/logs" ] ; then
  echo ""
else
  mkdir $APP_HOME/logs
fi

WORKING_DIR=$APP_HOME/working
if [ -d "$APP_HOME/report" ] ; then
  echo ""
else
  mkdir $APP_HOME/report
  mkdir $WORKING_DIR
fi

LIB="$APP_HOME/lib" ;


CLASSPATH="$JAVA_HOME/lib/tools.jar"

for f in $LIB/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
CLASSPATH=${CLASSPATH}:$WORKING_DIR;

if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  ABS_APP_HOME=`cygpath --absolute --windows "$ABS_APP_HOME"`
  WORKING_DIR=`cygpath --absolute --windows "$WORKING_DIR"`
fi

JAVA_OPTS="-Xshare:auto -Xms128m -Xmx2048m" 


JAVACMD="$JAVA_HOME/bin/java"
CLASS="org.headvances.search.swui.Main"

exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS $OPTIONS "$@"
