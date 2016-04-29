#!/bin/bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

if [ "$OS" == "Linux" ] ; then
  JAVA_HOME="/opt/jdk1.7"
fi

APP_HOME=`cd $bin/..; pwd; cd $bin`
PID_FILE="$bin/webserver-pid.txt"
LOG_FILE="$APP_HOME/logs/webserver.log"
if [ -d "$APP_HOME/logs" ] ; then
  echo "Logs is exist!"
else
  mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;
CLASSPATH="$JAVA_HOME/lib/tools.jar"
CLASSPATH="${CLASSPATH}:$APP_HOME/lib/*"
WEBAPPS="$APP_HOME/webapps"
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  WEBAPPS=`cygpath --path --windows "$WEBAPPS"`
fi

SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dsearch.index=web"
SYS_PROPS="$SYS_PROPS -Dsearch.type=webpage"
SYS_PROPS="$SYS_PROPS -Dsearch.url=192.168.6.22:9300"
###########################Start Profiling Config##########################################
JPDA_TRANSPORT=dt_socket
JPDA_ADDRESS=8000

REMOTE_DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

#for linux
LD_LIBRARY_PATH="/opt/Yourkit/bin/linux-x86-64/"
#for MAC
#DYLD_LIBRARY_PATH="/Users/tuannguyen/java/YourKit/bin/mac/"
#for Window

PATH="$PATH:$LD_LIBRARY_PATH"
export LD_LIBRARY_PATH DYLD_LIBRARY_PATH

#YOURKIT_PROFILE_OPTION="$REMOTE_DEBUG -agentlib:yjpagent  -Djava.awt.headless=false"
###########################Eng Profiling Config#############################################

JAVACMD="$JAVA_HOME/bin/java"
COMMAND=$1
shift

function console() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx768m"
  CLASS="org.headvances.http.JettyWebServer"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS -webapp $WEBAPPS
}

function deamon() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1536m"
  CLASS="org.headvances.http.JettyWebServer"
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS -webapp $WEBAPPS > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}

if [ "$COMMAND" = "deamon" ] ; then
  deamon
elif [ "$COMMAND" = "kill" ] ; then
  kill -9 `cat $PID_FILE` && rm -rf $PID_FILE
else
  console
fi
