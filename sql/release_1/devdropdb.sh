#!/bin/bash
###
# a script for jurgen while testing data migrations
###

databases="w2dev_college w2dev_binary"
#databases="w2dev_college w2dev_reference w2dev_binary"

for database in ${databases}; do
	mysqladmin5 -f -h db.office -uwillow -pT7t,RMJRPGjFSq9m drop ${database}
	mysqladmin5 -f -h db.office -uwillow -pT7t,RMJRPGjFSq9m create ${database}
done

	echo "s/w2_binary/w2dev_binary/g" > edit1.sed
	echo "s/w2_college/w2dev_college/g" >> edit1.sed
	echo "s/w2_reference/w2dev_reference/g" >> edit1.sed

	sed -f edit1.sed createSchema_college.sql > temp1.sql
	mysql -h db.office -uroot -pxqr69Gf < temp1.sql

	rm -f temp1.sql
	rm -f edit1.sed

#### add smarts here to restart apps to do the db migration before import.
ssh jurgen@freebsd.vm.ish.com.au "cd /var/onCourse/app/A1; sudo ./app.sh restart"

echo "wait for the db migrations"
sleep 30

exit 0;


