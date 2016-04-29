#!/usr/bin/env bash

cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true;;
esac

OS=`uname`

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

cd $bin

if [ "$OS" == "Linux" ] ; then
  JAVA_HOME="/opt/jdk1.7"
fi

APP_HOME=`cd $bin/..; pwd; cd $bin`

if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

#for f in $LIB/*.jar; do
#  CLASSPATH=${CLASSPATH}:$f;
#done

CLASSPATH=${CLASSPATH}:$LIB/*;

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
ACTIVEMQ_SERVER=192.168.10.1
# get arguments
COMMAND=$1
shift

PID_FILE=$bin/pid.txt

ANALYSIS_SERVER="192.168.10.2"
MASTER="192.168.10.3"
FETCHERS="192.168.10.4"
CRAWLER_MEMBERS="$MASTER $FETCHERS"
CLUSTER_MEMBERS="$ANALYSIS_SERVER $MASTER $FETCHERS"

DATA_DIR="/mnt/data/crawler"

SYS_PROPS="-Dcrawler.data.dir=$DATA_DIR"
SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dcluster.members=${CLUSTER_MEMBERS// /,}"

#Active MQ configuration
SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=nio://$ACTIVEMQ_SERVER:61636?jms.useCompression=true&jms.useAsyncSend=true"


#Crawler Fetcher configuration
SYS_PROPS="$SYS_PROPS -Dcrawler.fetcher.number-of-threads=30"


#Html Document Consumer configuration
SYS_PROPS="$SYS_PROPS -Dhtmldocument.store.method=dump"

JAVACMD="$JAVA_HOME/bin/java"

function crawler_start_master() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx700m"
  CLASS='org.headvances.crawler.master.CrawlerMaster'
  echo "$SYS_PROPS"
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $APP_HOME/logs/log.txt 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}

function crawler_start_fetcher() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx700m"
  CLASS='org.headvances.crawler.CrawlerFetcher'

  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $APP_HOME/logs/log.txt 2>&1 < /dev/null &
  printf '%d' $! > $PID_FILE
}

function crawler_cluster_start() {
  crawler_start_master
  for worker in $FETCHERS; do
    echo "###############################################################"
    echo "Start Member On  $worker"
    echo "###############################################################"
    ssh $USER@$worker "$bin/cluster.sh start-fetcher"
  done
}

function crawler_cluster_exec() {
  for worker in $CRAWLER_MEMBERS; do
    echo "#################################################################"
    echo "Execute '$@' On  $worker"
    echo "#################################################################"
    ssh $USER@$worker "cd $bin && $@"
  done
}
function crawler_cluster_sync() {
  for worker in $FETCHERS; do
    echo "###########################################################"
    echo "synchronized data with $worker"
    echo "###########################################################"
    rsync -vr --delete $APP_HOME $USER@$worker:/opt/
  done
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
function crawler_cluster_view_log() {
  LINE=$1
  if [ "$LINE" == "" ] ; then
    LINE="20"
  fi
  crawler_cluster_exec "tail -n $LINE $APP_HOME/logs/log.txt"
}

if [ "$COMMAND" = "start-master" ] ; then
  crawler_start_master
elif [ "$COMMAND" = "start-fetcher" ] ; then
  crawler_start_fetcher
elif [ "$COMMAND" = "start" ] ; then
  confirmYN "Do you wish to start the crawler cluster(Y/N)? "
  crawler_cluster_start
elif [ "$COMMAND" = "kill" ] ; then
  confirmYN "Do you want to kill this process on all the members(Y/N)?"
  crawler_cluster_exec "kill -9 \`cat $PID_FILE\` && rm $PID_FILE"
elif [ "$COMMAND" = "running" ] ; then
  crawler_cluster_exec 'ps aux | grep Crawler'
elif [ "$COMMAND" = "clean-all" ] ; then
  confirmYN "Do you wish to remove all logs, pid file and data(Y/N) ? "
  crawler_cluster_exec "rm -rf $DATA_DIR $APP_HOME/logs/* $PID_FILE";
elif [ "$COMMAND" = "clean-log" ] ; then
  crawler_cluster_exec "rm -rf $APP_HOME/logs/* $PID_FILE";
elif [ "$COMMAND" = "log" ] ; then
  crawler_cluster_view_log $@
elif [ "$COMMAND" = "loggrep" ] ; then
  crawler_cluster_exec "tail -n 1000 $APP_HOME/logs/log.txt | grep $@"
elif [ "$COMMAND" = "exception" ] ; then
  crawler_cluster_exec "tail -n 1000 $APP_HOME/logs/log.txt | grep -A 5 \"Exception\""
elif [ "$COMMAND" = "exec" ] ; then
  crawler_cluster_exec $@
elif [ "$COMMAND" = "sync" ] ; then
  confirmYN "Do you want to sync this program with the other members(Y/N)?"
  crawler_cluster_sync
else
  echo "cluster command options: "
  echo "  start     : To start the cluster "
  echo "  kill      : To kill  the process on all the members"
  echo "  running   : To list the running java process"
  echo "  clean-all : To remove the data , logs and pid files"
  echo "  clean-log : To remove logs and pid files"
  echo "  log n     : To view last n lines from log files. Default is 30"
  echo "  exception : To view exception from log files"
  echo "  exec      : To execute the shell command on all the members"
  echo "  sync      : To copy this program to the fetcher members"
fi
