#!/usr/local/bin/bash

usage() {
    echo "Migrate old willow_college to new schema, this has multiple arguments:"
    echo "-c is the college id, -s for the source database, -d for the destination database, -h for the mysql host and -u and -p for mysql username and password"
    echo "eg ./$0 -c 10 -s willow -d w2live -h db.office -uwillow -p<pass>"
    echo "This will migrate collegeid 10"
    echo "With the db source and destination, only specific the prefix the _college, _binary, _reference, is assumed"
    exit 0;
}

while getopts c:s:d:h:u:p: arg
do case ${arg} in
  c) collegeid=${OPTARG};;
  s) sdb=${OPTARG};;
  d) ddb=${OPTARG};;
  h) mysqlhost=${OPTARG};;
  u) mysqluser=${OPTARG};;
  p) mysqlpass=${OPTARG};;
  ?) exerr ${usage};;
esac; done; shift $(( ${OPTIND} - 1 ))

if [ "${collegeid}" = "" ] || [ "${sdb}" = "" ] || [ "${ddb}" = "" ]; then
    usage;
fi

#replace all the variables we need to replace in the migration file
echo "s/%COLLEGEID%/${collegeid}/g" > edit.sed
echo "s/%SOURCEDB%/${sdb}/g" >> edit.sed
echo "s/%DESTINATIONDB%/${ddb}/g" >> edit.sed
sed -f edit.sed dataMigration.sql > tmp.sql

#run the migration
mysql -h ${mysqlhost} -u${mysqluser} -p${mysqlpass}   < tmp.sql

#Grab blob values from table and set them into a values_string column as text
javac liveMigratePreferences.java
java -cp mysql-connector-java.jar:. liveMigratePreferences ${collegeid}

#clean up
rm -f *.class
rm -f tmp.sql
rm -f edit.sed

#run a full solr index
cd /var/tomonCourse; ./runsolrindex.sh -a full

