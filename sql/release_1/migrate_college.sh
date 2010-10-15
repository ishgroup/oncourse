usage() {
    echo "Migrate old willow_college to new schema, expects one argument to be a collegeid ie: './migrate_collge.sh 10'"
    echo "will migrate date of collegeid 10"
    exit 0;
}
if [ $# -lt 1 ]; then
    usage;
fi
sed "s/%COLLEGEID%/${1}/" dataMigration.sql > tmp.sql
mysql -h db.office -uwillow -pT7t,RMJRPGjFSq9m   < tmp.sql

#Grab blob values from table and set them into a values_string column as text
javac MigratePreferences.java
java -cp mysql-connector-java.jar:. MigratePreferences $1
rm *.class
rm tmp.sql