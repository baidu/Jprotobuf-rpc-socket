#!/bin/sh

# get current parent directory
# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set CATALINA_HOME if not already set
[ -z "$CURRENT_DIR" ] && CURRENT_DIR=`cd "$PRGDIR" ; pwd`

echo $CURRENT_DIR

_JAVA_CMD=
if [ -z "${JAVA_HOME}" ]; then
    _JAVA_CMD=/System/Library/Frameworks/JavaVM.framework/Home
else
    _JAVA_CMD=${JAVA_HOME}/bin/java
fi

#JAVA_OPTS="-Xms256m -Xmx256m"


"${_JAVA_CMD}" ${JAVA_OPTS} -Djava.ext.dirs="${CURRENT_DIR}/lib" -cp "${CURRENT_DIR}/bin:.:${CURRENT_DIR}/conf" com.baidu.jprotobuf.pbrpc.transport.EchoServicePerformanceMain

JAVA_OPTS=
_JAVA_CMD=
