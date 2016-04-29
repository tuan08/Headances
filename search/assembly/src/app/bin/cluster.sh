#!/bin/bash

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

APP_HOME=`cd $bin/..; pwd; cd $bin`
DATA_DIR="/mnt/data/search.engine"
PID_FILE="$bin/pid.txt"
LOG_FILE="$APP_HOME/logs/headvances.log"

if [ -f "$APP_HOME/lib/slf4j-api-1.6.0.jar" ] ; then
  rm $APP_HOME/lib/*slf*
fi
 
cp  $APP_HOME/config/elasticsearch.yml.cluster $APP_HOME/config/elasticsearch.yml


COMMAND=$1
shift


MASTER="192.168.10.6"
SLAVES="192.168.10.7 192.168.10.8"
MEMBERS="$MASTER $SLAVES"

function cluster_start() {
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1024m"

  for worker in $MEMBERS; do
    echo "#############################################################################"
    echo "Start Member On  $worker"
    echo "#############################################################################"
    echo "ssh $USER@$worker $bin/elasticsearch"
    ssh $USER@$worker "$bin/start-node.sh -p $PID_FILE"
  done
}

function cluster_stop() {
  for worker in $MEMBERS; do
    echo "#############################################################################"
    echo "Stop member On  $worker"
    echo "#############################################################################"
    ssh $USER@$worker "kill -9 \`cat $PID_FILE\` && rm $PID_FILE"
  done
}

function cluster_exec() {
  for worker in $MEMBERS; do
    echo "#############################################################################"
    echo "Execute '$@' On  $worker"
    echo "#############################################################################"
    ssh $USER@$worker "cd $bin && $@"
  done
}
function cluster_sync() {
  for worker in $SLAVES; do
    echo "###########################################################"
    echo "synchronized data with $worker"
    echo "###########################################################"
    rsync -vr --delete $APP_HOME $USER@$worker:/opt/
  done
}

function cluster_view_log() {
  LINE=$1
  if [ "$LINE" == "" ] ; then
    LINE="20"
  fi
  cluster_exec "tail -n $LINE $LOG_FILE"
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
  confirmYN "Do you wish to start the crawler cluster(Y/N)? "
  cluster_start
elif [ "$COMMAND" = "kill" ] ; then
  confirmYN "Do you want to kill this process on all the members(Y/N)?"
  cluster_exec "kill -9 \`cat $PID_FILE\` && rm $PID_FILE"
elif [ "$COMMAND" = "running" ] ; then
  cluster_exec 'ps aux | grep ElasticSearch'
elif [ "$COMMAND" = "exec" ] ; then
  cluster_exec $@

elif [ "$COMMAND" = "clean" ] ; then
  confirmYN "Do you wish to remove all logs, pid file(Y/N) ? "
  cluster_exec "rm -rf $APP_HOME/logs/* $PID_FILE";
elif [ "$COMMAND" = "clean-all" ] ; then
  confirmYN "Do you wish to remove all logs, pid file and data(Y/N) ? "
  cluster_exec "rm -rf $DATA_DIR $APP_HOME/logs/* $PID_FILE";

elif [ "$COMMAND" = "log" ] ; then
  cluster_view_log $@
elif [ "$COMMAND" = "loggrep" ] ; then
  cluster_exec "tail -n 1000 $LOG_FILE | grep $@"
elif [ "$COMMAND" = "exception" ] ; then
  cluster_exec "tail -n 1000 $LOG_FILE | grep -A 5 \"Exception\""

elif [ "$COMMAND" = "sync" ] ; then
  confirmYN "Do you want to sync this program with the other members(Y/N)?"
  cluster_sync
else
  echo "cluster command options: "
  echo "  start     : To start the cluster "
  echo "  kill      : To run the command kill -9 PID on all the members"
  echo "  running   : To list the running java process"
  echo "  exec      : To execute the shell command on all the members"
  echo "  clean     : To remove the logs and pid file"
  echo "  clean-all : To remove the data, logs and pid file"
  echo "  log n     : To view last n lines from log files. Default is 30"
  echo "  loggrep   : To grep a pattern from the log file"
  echo "  exception : To view exception from log files"
  echo "  sync      : To copy this program to the members"
fi
