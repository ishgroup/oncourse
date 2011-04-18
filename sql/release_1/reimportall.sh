#!/bin/bash

ids="12 111 2871 3219 2006 1323 334 3014 2703 2956 3093 299 338 2291 6 2396 10 1222 272 3184"

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	./migrate_college.sh -c ${id} -s oncourse_realdata_willow -d w2dev -h db.office -uwillow -pT7t,RMJRPGjFSq9m
done

exit 0;
