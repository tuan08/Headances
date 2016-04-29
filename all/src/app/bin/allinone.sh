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


#Active MQ configuration
SYS_PROPS="$SYS_PROPS -Dactivemq.data.dir=$APP_HOME/data/activemq"
SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=nio://127.0.0.1:61616"

SYS_PROPS="-Dcrawler.data.dir=$APP_HOME/data/crawler"
SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dcluster.members=localhost:5700,localhost:5701,localhost:5702"

#Html Document Consumer configuration
SYS_PROPS="$SYS_PROPS -Dhtmldocument.consumer=FileHtmlDocumentConsumer"

SYS_PROPS="$SYS_PROPS -Dindex.name=web"
SYS_PROPS="$SYS_PROPS -Dindex.type=webpage"
SYS_PROPS="$SYS_PROPS -Dindex.url=127.0.0.1:9300"
SYS_PROPS="$SYS_PROPS -Danalysis.number-of-thread=1"
#SYS_PROPS="$SYS_PROPS -Dcrawler.input.auto-startup=true"


SYS_PROPS="$SYS_PROPS -Dsearch.index=web"
SYS_PROPS="$SYS_PROPS -Dsearch.type=webpage"
SYS_PROPS="$SYS_PROPS -Dsearch.url=localhost:9300"

JAVACMD="$JAVA_HOME/bin/java"

COMMAND=$1
shift

if [ "$COMMAND" = "server" ] ; then
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1536m"
  CLASS="org.headvances.all.swui.AllInOneServer"
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
elif [ "$COMMAND" = "console" ] ; then
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx768m"
  CLASS="org.headvances.all.swui.AllInOneServer"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS
else
  JAVA_OPTS="-Xshare:auto -Xms128m -Xmx1024m" 
  CLASS="org.headvances.all.swui.AllInOne"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS $OPTIONS "$@"
fi
