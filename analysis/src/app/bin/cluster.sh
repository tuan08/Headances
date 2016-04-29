#!/usr/bin/env bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

cd $bin
. "$bin"/env.sh


ACTIVEMQ_SERVER="192.168.10.1"
ELASTICSEARCH_SERVER="192.168.10.6:9300"

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

COMMAND=$1
shift

PID_FILE=pid.txt

ANALYSIS_DATA_DIR=/mnt/data/analysis
CLUSTER_MEMBERS="192.168.10.2,192.168.10.3,192.168.10.4"

JAVACMD="$JAVA_HOME/bin/java"
JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"

SYS_PROPS="-Danalysis.data.dir=$ANALYSIS_DATA_DIR"
SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dcluster.members=${CLUSTER_MEMBERS/ /,}"

#Active MQ configuration
SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=nio://$ACTIVEMQ_SERVER:61636?jms.useCompression=true&jms.useAsyncSend=true"

#Index Forward configuration
SYS_PROPS="$SYS_PROPS -Dindex.name=web"
SYS_PROPS="$SYS_PROPS -Dindex.type=webpage"
SYS_PROPS="$SYS_PROPS -Dindex.url=$ELASTICSEARCH_SERVER"
SYS_PROPS="$SYS_PROPS -Dcrawler.input.auto-startup=true"

function analysis_cluster_start() {
  CLASS='org.headvances.analysis.AnalysisServer'
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $APP_HOME/logs/log.txt 2>&1 < /dev/null & 
  printf '%d' $! > $PID_FILE
}

function analysis_cluster_stop() {
  echo "Stop!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  kill -9  `cat $bin/$PID_FILE` && rm $bin/$PID_FILE
}

function analysis_cluster_running() {
  ps aux | grep AnalysisServer
}
function analysis_cluster_view_log() {
  LINE=$1
    if [ "$LINE" == "" ] ; then
       LINE="20"
    fi
   tail -n $LINE $APP_HOME/logs/log.txt
}

function confirmYN() {
 while true; do
       read -p "$@" yn
       case $yn in
         [Yy]* ) break;;
         [Nn]* ) exit;;
         * ) echo "Please answer yes or no.";;
       esac
 done
}

if [ "$COMMAND" = "start" ] ; then
  confirmYN "Do you wish to start the analysis server(Y/N) ? "
  analysis_cluster_start
elif [ "$COMMAND" = "kill" ] ; then
  confirmYN "Do you want to kill this process(Y/N) ? "
  analysis_cluster_stop
elif [ "$COMMAND" = "running" ] ; then
  analysis_cluster_running
elif [ "$COMMAND" = "clean-all" ] ; then
  confirmYN "Do you wish to remove all logs, pid file and data(Y/N) ? "
  rm -rf $ANALYSIS_DATA_DIR $APP_HOME/logs/* $PID_FILE
elif [ "$COMMAND" = "clean-log" ] ; then 
  rm -rf $APP_HOME/logs/* $PID_FILE
elif [ "$COMMAND" = "log" ] ; then
  analysis_cluster_view_log $@
elif [ "$COMMAND" = "loggrep" ] ; then
  tail -n 1000 $APP_HOME/logs/log.txt | grep $@
elif [ "$COMMAND" = "exception" ] ; then
  tail -n 1000 $APP_HOME/logs/log.txt | grep -A 5 "Exception" 
else
  echo "cluster command options: "
  echo "  start     : To start the analysis server"
  echo "  kill      : To kill the analysis server"
  echo "  running   : To list the running process"
  echo "  clean-all : To remove the data, log and pid file"
  echo "  clean-log : To remove logs and pid file"
  echo "  log n     : To view last n lines from log file. Default is 30"
  echo "  loggrep s : To view string s from log file"
  echo "  exception : To view exception from log file"
fi
