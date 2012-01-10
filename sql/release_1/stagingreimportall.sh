#!/usr/local/bin/bash

ids="3014 111 2006 1323 2291 6 3184 1234 3768 25"
#2703, 2074, 1222, 10,3699, 19, 3219, 2396, 2956, 3861, 299, 334, 2871, 338, 3093, 12, 50, 1296, 272 have been 
completed, 3438 tas test, removed

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./stagingmigrate_college.sh -c ${id} -s oncourse_staging_willow -d w2staging -h 10.100.33.2 -ustagingrestore -pneiDu9Oi
done

#### Do some miscellaneous SQL
#Update the HomePage of CityEast with new content 
#/usr/local/bin/mysql -h 10.100.33.2 -ustagingrestore -pneiDu9Oi w2staging_college < eastHomePageContent.sql

#### start a full solr index
cd /var/tomonCourseStaging/scripts; ./runsolrindex.sh -a full

echo "wait for the db migrations"
sleep 30

exit 0;


exit 0;
