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
  JAVA_HOME="/opt/jdk1.6"
fi

APP_HOME=`cd $bin/..; pwd; cd $bin`


LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

LOG_FILE="$APP_HOME/logs/log.txt"
PID_FILE="$APP_HOME/logs/pid.txt"

CLASSPATH=${CLASSPATH}:$LIB/*;
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  APP_HOME=`cygpath --absolute --windows "$APP_HOME"`
fi

JAVA_LIBRARY_PATH="/opt/hadoop/cdh3/lib/native/Linux-amd64-64/"
HADOOP_OPTS="-Djava.library.path=$JAVA_LIBRARY_PATH"



if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

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

SYS_PROPS="-Dapp.home.dir=$APP_HOME"
SYS_PROPS="$SYS_PROPS -Dcrawler.data.dir=$APP_HOME/data/crawler"
SYS_PROPS="$SYS_PROPS -Dcrawler.scheduler.max-per-site=50"
#Crawler Fetcher configuration
SYS_PROPS="$SYS_PROPS -Dcrawler.fetcher.number-of-threads=25"


#SYS_PROPS="$SYS_PROPS -Dcluster.members=192.168.6.23"
#SYS_PROPS="$SYS_PROPS -Dcluster.members=192.168.6.12,192.168.6.13"

#Active MQ configuration
SYS_PROPS="$SYS_PROPS -Dactivemq.data.dir=$APP_HOME/data/activemq"
SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=vm://localhost?jms.useCompression=true&jms.useAsyncSend=true&jms.prefetchPolicy.queuePrefetch=5"
#SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=nio://localhost:61617?jms.useCompression=true&jms.useAsyncSend=true&jms.prefetchPolicy.queuePrefetch=5"


#Html Document Consumer configuration
SYS_PROPS="$SYS_PROPS -Dhtmldocument.store.method=dump"

JAVACMD="$JAVA_HOME/bin/java"

# get arguments
COMMAND=$1
shift

MODE=$1
if [ "$MODE" = "-deamon" ] ; then
  shift ;
fi

# figure out which class to run
if [ "$COMMAND" = "master" ] ; then
  CLASS='org.headvances.crawler.master.CrawlerMaster'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"
elif [ "$COMMAND" = "fetcher" ] ; then
  CLASS='org.headvances.crawler.CrawlerFetcher'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"  
elif [ "$COMMAND" = "crawl" ] ; then
  if [ "$MODE" = "-deamon" ] ; then
    CLASS='org.headvances.crawler.tool.Crawler'
    JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1012m $HADOOP_OPTS"
    nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $LOG_FILE 2>&1 < /dev/null &
    printf '%d' $! > $PID_FILE
  else
    CLASS='org.headvances.crawler.tool.Crawler'
    JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx1012m $HADOOP_OPTS"
    exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"
  fi
elif [ "$COMMAND" = "kill" ] ; then
  kill -9 `cat $PID_FILE` && rm -rf $PID_FILE
elif [ "$COMMAND" = "swui" ] ; then
  CLASS='org.headvances.crawler.swui.CrawlerApplicationPlugin'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"  
else
  echo "Use such commands as: ./crawler [option] [parameters]";
  echo "Opitions: master,fetcher, crawl, swui";
fi
