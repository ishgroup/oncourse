#!/usr/bin/env sh

##############################################################################
##
##  ${applicationName} start up script
##
##############################################################################

usage() {
  echo "\$0 stop|start"
  exit 1;
}

pidfile="/var/run/onCourse/${applicationName}.pid"

# Attempt to set APP_HOME
# Resolve links: \$0 may be a link
PRG="\$0"
# Need this for relative symlinks.
while [ -h "\$PRG" ] ; do
    ls=`ls -ld "\$PRG"`
    link=`expr "\$ls" : '.*-> \\(.*\\)\$'`
    if expr "\$link" : '/.*' > /dev/null; then
        PRG="\$link"
    else
        PRG=`dirname "\$PRG"`"/\$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"\$PRG\"`/${appHomeRelativePath}" >/dev/null
APP_HOME="`pwd -P`"
cd "\$SAVED" >/dev/null

APP_NAME="${applicationName}"
APP_BASE_NAME=`basename "\$0"`

# Add default JVM options here. You can also use JAVA_OPTS and ${optsEnvironmentVar} to pass JVM options to this script.
DEFAULT_JVM_OPTS=${defaultJvmOpts}

die() {
    echo
    echo "\$*"
    echo
    exit 1
}

CLASSPATH=$classpath

# Determine the Java command to use to start the JVM.
if [ -n "\$JAVA_HOME" ] ; then
    JAVACMD="\$JAVA_HOME/bin/java"
    if [ ! -x "\$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: \$JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Collect all arguments for the java command, following the shell quoting and substitution rules
eval set -- \$DEFAULT_JVM_OPTS \$JAVA_OPTS \$${optsEnvironmentVar} <% if ( appNameSystemProperty ) { %>"\"-D${appNameSystemProperty}=\$APP_BASE_NAME\"" <% } %>-classpath "\"\$CLASSPATH\"" ${mainClassName}


stop() {
    cat $pidfile | kill
}

start() {
    exec "\$JAVACMD" "\$@" & echo $! > $pidfile
}

case "\$1" in
   stop)
    stop
    ;;
   start)
    start
    ;;
   *)
    usage
esac


