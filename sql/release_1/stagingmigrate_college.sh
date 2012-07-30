#!/usr/local/bin/bash

usage() {
    /bin/echo "Migrate old willow_college to new schema, this has multiple arguments:"
    /bin/echo "-c is the college id, -s for the source database, -d for the destination database, -h for the mysql host and -u and -p for mysql username and password"
    /bin/echo "eg ./$0 -c 10 -s willow -d w2live -h db.office -uwillow -p<pass>"
    /bin/echo "This will migrate collegeid 10"
    /bin/echo "With the db source and destination, only specific the prefix the _college, _binary, _reference, is assumed"
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
/bin/echo "s/%COLLEGEID%/${collegeid}/g" > edit.sed
/bin/echo "s/%SOURCEDB%/${sdb}/g" >> edit.sed
/bin/echo "s/%DESTINATIONDB%/${ddb}/g" >> edit.sed
/usr/bin/sed -f edit.sed dataMigration.sql > tmp.sql

/bin/echo "#run the migration"
/usr/local/bin/mysql -h ${mysqlhost} -u${mysqluser} -p${mysqlpass}   < tmp.sql

/bin/echo "#Grab blob values from table and set them into a values_string column as text"
/usr/local/bin/javac stagingMigratePreferences.java
/usr/local/bin/java -cp mysql-connector-java.jar:. stagingMigratePreferences ${collegeid}

#clean up
/bin/rm -f *.class
/bin/rm -f tmp.sql
/bin/rm -f edit.sed

