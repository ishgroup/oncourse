#!/usr/local/bin/bash
###
# a script for jurgen while testing data migrations
###


databases="w2staging_college w2staging_binary"

for database in ${databases}; do
	/usr/local/bin/mysqladmin -f -h 10.100.33.2 -ustagingrestore -pneiDu9Oi drop ${database}
	/usr/local/bin/mysqladmin -f -h 10.100.33.2 -ustagingrestore -pneiDu9Oi create ${database}
done

	echo "s/w2_binary/w2staging_binary/g" > edit1.sed
	echo "s/w2_college/w2staging_college/g" >> edit1.sed
	echo "s/w2_reference/w2staging_reference/g" >> edit1.sed

	/usr/bin/sed -f edit1.sed createSchema_college.sql > temp1.sql
	/usr/local/bin/mysql -h 10.100.33.2 -ustagingrestore -pneiDu9Oi < temp1.sql

	/bin/rm -f temp1.sql
	/bin/rm -f edit1.sed

#### add smarts here to restart apps to do the db migration before import.
cd /var/tomonCourseStaging/app/A1; ./app.sh restart

echo "wait for the db migrations"
sleep 30


exit 0;
