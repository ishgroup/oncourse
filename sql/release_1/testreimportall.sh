#!/bin/bash

ids="12 111 2871 2006 1323 334 3014 2703 3093 338 2291 6 1222 272 3184 10 299 50 1234 3768 2074 3438 25 1296"
#ids="3699, 19 2956, 3219, 2396, " #Already migrated colleges.

#ids="3184 3438"

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./testmigrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2test -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

#### start a full solr index 
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourseTest; sudo ./runsolrindex.sh -a full"

echo "wait for the db migrations"
sleep 30

exit 0;
