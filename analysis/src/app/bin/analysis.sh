#!/usr/bin/env bash

#command: ./analysis.sh -deamon -db ../../webdb -output report,file -threads 4

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
JAVACMD="$JAVA_HOME/bin/java"

APP_HOME=`cd $bin/..; pwd; cd $bin`


LIB="$APP_HOME/lib" ;

CLASSPATH="$JAVA_HOME/lib/tools.jar"

#for f in $LIB/*.jar; do
#  CLASSPATH=${CLASSPATH}:$f;
#done

LOG_FILE="$APP_HOME/logs/log.txt"

CLASSPATH="${CLASSPATH}:$LIB/*"
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  APP_HOME=`cygpath --path --windows "$APP_HOME"`
fi

if [ -d "$APP_HOME/logs" ] ; then
  echo "logs is existed!"
else
  mkdir $APP_HOME/logs
fi

JAVA_LIBRARY_PATH="/opt/hadoop/cdh3/lib/native/Linux-amd64-64/"
HADOOP_OPTS="-Djava.library.path=$JAVA_LIBRARY_PATH"

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


SYS_PROPS="-Danalysis.data.dir=$APP_HOME/data/analysis"
SYS_PROPS="$SYS_PROPS -Dapp.home.dir=$APP_HOME"

#Active MQ configuration
SYS_PROPS="$SYS_PROPS -Dactivemq.data.dir=$APP_HOME/data/activemq"
SYS_PROPS="$SYS_PROPS -Dactivemq.broker.url=nio://$ACTIVEMQ_SERVER:61616?jms.useCompression=true&jms.useAsyncSend=true"

#Index Forward configuration
SYS_PROPS="$SYS_PROPS -Dindex.name=web"
SYS_PROPS="$SYS_PROPS -Dindex.type=webpage"
SYS_PROPS="$SYS_PROPS -Dindex.url=$ELASTICSEARCH_SERVER"
SYS_PROPS="$SYS_PROPS -Dcrawler.input.auto-startup=true"

# Index output options
SYS_PROPS="$SYS_PROPS -Danalysis.output.console=false"
#SYS_PROPS="$SYS_PROPS -Danalysis.output.file=true"
#SYS_PROPS="$SYS_PROPS -Danalysis.output.index=true"

# The number of threads
SYS_PROPS="$SYS_PROPS -Danalysis.number-of-thread=4"

#get arguments
#COMMAND=$1
#shift

MODE=$1
if [ "$MODE" = "-deamon" ] ; then
  shift ;
fi

if [ "$MODE" = "-deamon" ] ; then
  CLASS='org.headvances.analysis.AnalysisCommandLine'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  nohup $JAVACMD $JAVA_OPTS -cp $CLASSPATH $HADOOP_OPTS $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@" > $LOG_FILE 2>&1 < /dev/null &
else
  CLASS='org.headvances.analysis.AnalysisCommandLine'
  JAVA_OPTS="-server -XX:+UseParallelGC -Xshare:auto -Xms128m -Xmx512m"
  exec $JAVACMD $JAVA_OPTS -cp $CLASSPATH $HADOOP_OPTS $YOURKIT_PROFILE_OPTION $SYS_PROPS $CLASS "$@"
fi
