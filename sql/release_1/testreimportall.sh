#!/bin/bash

ids="3014 111 2006 1323 2291 6 3184 1234 3768 25"

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./testmigrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2test -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

#### start a full solr index 
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourseTest; sudo ./runsolrindex.sh -a full"

echo "wait for the db migrations"
sleep 30

exit 0;
