#!/usr/local/bin/bash
ids="3184 2291"
#111, 3014, 2703, 2074, 2006, 1222, 10,3699, 19, 3219, 2396, 2956, 3861, 299, 334, 2871, 338, 3093, 12, 50, 1296, 272, 6, 3768 have been completed, 3438 tas test, removed

for id in ${ids}; do
	echo "importing collegeid = ${id}"
	time ./stagingmigrate_college.sh -c ${id} -s oncourse_staging_willow -d w2staging -h 10.100.33.2 -ustagingrestore -pneiDu9Oi
done

#### Do some miscellaneous SQL
/usr/local/bin/mysql -h 10.100.33.2 -ustagingrestore -pneiDu9Oi w2staging_college < tasmania.sql
/usr/local/bin/mysql -h 10.100.33.2 -ustagingrestore -pneiDu9Oi w2staging_college < lightbulb.sql

#### start a full solr index
cd /var/tomonCourseStaging/scripts; ./runsolrindex.sh -a full

echo "wait for the db migrations"
sleep 30

exit 0;


exit 0;
