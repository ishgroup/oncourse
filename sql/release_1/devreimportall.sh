#!/bin/bash

ids="3014 111 1323 2291 6 3184 1234 3768 25"

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./devmigrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2dev -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

exit 0;

#### add smarts here run the solr reimports
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourse/; sudo ./runsolrindex.sh -a full"

echo "wait for the db migrations"
sleep 30



