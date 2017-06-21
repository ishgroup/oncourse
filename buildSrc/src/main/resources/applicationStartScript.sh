#!/usr/bin/env sh

##############################################################################
##
##  ${applicationName} start up script
##
##############################################################################

# There is no realpath command on OSX, so we need to fake it
command -v realpath >/dev/null 2>&1 || realpath() {
    cd \$(dirname \${1})
    pwd -P
}

PID_FILE="/var/run/onCourse/${applicationName}.pid"
BASEDIR=\$(realpath \${0})
APP_HOME=\$(dirname \${BASEDIR} | xargs dirname)
cd \$APP_HOME

# Default JVM options. You can also use JAVA_OPTS and ${optsEnvironmentVar} to pass JVM options to this script.
DEFAULT_JVM_OPTS=${defaultJvmOpts}

usage() {
    echo "\$0 stop|start"
    exit 1;
}

stop() {
    cat "\$PID_FILE" | xargs kill
}

# Tell the application to return an error on the health check so we can drain all the remaining connections
# and have haproxy remove the backend from the pool
drain() {
    cat "\$PID_FILE" | xargs kill -2
}

start() {
    CLASSPATH="${classpath}"

    # Determine the Java command to use to start the JVM.
    if [ -n "\$JAVA_HOME" ] ; then
        JAVACMD="\$JAVA_HOME/bin/java"
        if [ ! -x "\$JAVACMD" ] ; then
            echo "ERROR: JAVA_HOME is set to an invalid directory: \$JAVA_HOME"
            exit 1
        fi
    else
        JAVACMD=\$(which java)
        if [ ! -x "\$JAVACMD" ] ; then
            echo "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
            exit 1
        fi
    fi

    CMD="\$JAVACMD \\
        \$DEFAULT_JVM_OPTS \\
        \$JAVA_OPTS \\
        \$${optsEnvironmentVar} \\
        -classpath \$CLASSPATH \\
        ${mainClassName}"

    # FreeBSD has a daemon starting binary which creates the pidfile for us
    if [ -x "/usr/sbin/daemon" ] ; then
        /usr/sbin/daemon -p \$PID_FILE -f \$CMD
    else
        exec "\$JAVACMD" "\$@" & echo \$! > "\$pidfile"
    fi
}

status() {
    pid=`cat "\$PID_FILE" 2>/dev/null`
    ps -p \$pid 2>/dev/null>/dev/null
    if [ \$? -eq 0 ]; then
        echo "${applicationName} is running as pid \$pid."
    else
        echo "${applicationName} is not running."
        return 1
    fi
}

case "\$1" in
    stop)
        stop
        ;;
    start)
        start
        ;;
    status)
        status
        ;;
    drain)
        drain
        ;;
    *)
        usage
esac