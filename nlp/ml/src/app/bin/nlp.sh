
#!/bin/bash

cygwin=false
case "`uname`" in
    CYGWIN*) cygwin=true;;
esac

OS=`uname`

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

cd $bin

if [ "$OS" == "Linux" ] ; then
 JAVA_HOME="/opt/jdk1.6"
fi

APP_HOME=`cd $bin/..; pwd; cd $bin`
LOG_FILE="$APP_HOME/logs/log.txt"
if [ -d "$APP_HOME/logs" ] ; then
     echo "logs is existed!"
else
     mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

CLASSPATH="${CLASSPATH}:$LIB/*"

if $cygwin; then
   JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
   CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi
####################Start Profiling Config###############################
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
###################Eng Profiling Config##################################

# get arguments
COMMAND=$1
shift

PID_FILE=$bin/pid.txt

JAVACMD="$JAVA_HOME/bin/java"
JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms256m -Xmx1024m"

function do_ws(){
  CLASS='org.headvances.ml.nlp.ws.MLWS'
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS $OPTIONS "$@" > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}

function do_pos(){
  CLASS='org.headvances.ml.nlp.pos.MLPOS'
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS $OPTIONS "$@" > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}

function do_ent(){
  CLASS='org.headvances.ml.nlp.ent.MLENT'
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS $OPTIONS "$@" > $LOG_FILE 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}
# figure out which class to run
if [ "$COMMAND" = "ws" ] ; then
   do_ws $@
elif [ "$COMMAND" = "pos" ] ; then
  do_pos $@
elif [ "$COMMAND" = "ent" ] ; then
  do_ent $@ 
elif [ "$COMMAND" = "kill" ] ; then
  kill -9 \`cat $PID_FILE\` && rm $PID_FILE
else
  echo "Available Command: wstrain, wstest, wsreport"
fi
