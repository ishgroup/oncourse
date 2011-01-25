#!/bin/bash

GRINDERPATH=./grinder
GRINDERPROPERTIES=./grinder.properties

CLASSPATH=$GRINDERPATH/lib/grinder.jar:$CLASSPATH


export CLASSPATH GRINDERPROPERTIES

java net.grinder.Grinder $GRINDERPROPERTIES


