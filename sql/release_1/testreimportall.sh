#!/bin/bash

ids="3014 111 2006 1323 2703 2291 6 3184 50 1234 3768 2074 25 1296"
#1222, 10,3699, 19, 3219, 2396, 2956, 3861, 299, 334, 2871, 338, 3093, 12, 272 have been completed, 3438 tas test,

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./testmigrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2test -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

#### start a full solr index 
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourseTest; sudo ./runsolrindex.sh -a full"

echo "wait for the db migrations"
sleep 30

exit 0;
