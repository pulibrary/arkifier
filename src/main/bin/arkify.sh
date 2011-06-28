#!/bin/sh

# Absolute path to this script, e.g. /opt/local/arkifier/arkify.sh
SCRIPT=`readlink -f $0`

# Absolute path this script is in, thus /opt/local/arkifier
DIR=`dirname $SCRIPT`

OPTS_ARG=$*

java -jar $DIR/${build.finalName}-${project.version}.jar $OPTS_ARG