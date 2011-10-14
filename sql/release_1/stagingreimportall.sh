#!/usr/local/bin/bash

ids="299 10 12 111 2871 2006 1323 334 3014 2703 3093 338 2291 6 1222 272 3184 50 1234 3768 2074 3438 25 1296"
#3699, 19. 3219, 2396 and 2956 have been completed

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./stagingmigrate_college.sh -c ${id} -s oncourse_staging_willow -d w2staging -h 10.100.33.2 -ustagingrestore -pneiDu9Oi
done

#### start a full solr index
cd /var/tomonCourseStaging; ./runsolrindex.sh -a full

echo "wait for the db migrations"
sleep 30

exit 0;


exit 0;
