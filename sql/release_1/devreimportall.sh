#!/bin/bash

ids="10 12 2871 334 111 2006 1323 3014 2703 3093 338 2291 6  1222 272 3184 299 50 1234 3768 2074 3438 25 1296"
# 19, 2956, 3699, 3219, 2396

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./devmigrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2dev -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

exit 0;

#### add smarts here run the solr reimports
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourse/; sudo ./runsolrindex.sh -a full"

echo "wait for the db migrations"
sleep 30



