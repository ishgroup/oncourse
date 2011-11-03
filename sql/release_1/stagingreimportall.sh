#!/usr/local/bin/bash

ids="299 12 111 2871 2006 1323 334 3014 2703 3093 338 2291 6 1222 272 3184 50 1234 3768 2074 3438 25 1296"
#10,3699, 19, 3219, 2396, 2956, 3861 have been completed

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./stagingmigrate_college.sh -c ${id} -s oncourse_staging_willow -d w2staging -h 10.100.33.2 -ustagingrestore -pneiDu9Oi
done

#### Do some miscellaneous SQL
#Update the HomePage of CityEast with new content 
/usr/local/bin/mysql -h 10.100.33.2 -ustagingrestore -pneiDu9Oi w2staging_college < eastHomePageContent.sql

#### start a full solr index
cd /var/tomonCourseStaging/scripts; ./runsolrindex.sh -a full

echo "wait for the db migrations"
sleep 30

exit 0;


exit 0;
