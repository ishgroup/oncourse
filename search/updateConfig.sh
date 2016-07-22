#!/bin/bash

SOLR_DIR=<need path to solr dir>
BASE_DIR="`dirname \"$0\"`"

pushd $SOLR_DIR/server/scripts/cloud-scripts
./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir $BASE_DIR/src/main/resources/solr/courses/conf -confname courses
./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir $BASE_DIR/src/main/resources/solr/tags/conf -confname tags
./zkcli.sh -zkhost localhost:2181 -cmd upconfig -confdir $BASE_DIR/src/main/resources/solr/suburbs/conf -confname suburbs
popd
