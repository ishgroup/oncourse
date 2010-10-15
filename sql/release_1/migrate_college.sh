usage() {
    echo "Migrate the old values which were blobs to strings, this expects the collegeid as the first argument, ie: './migrate_collge.sh 10'"
    exit 0;
}
if [ $# -lt 1 ]; then
    usage;
fi
javac MigratePreferences.java
java -cp mysql-connector-java.jar:. MigratePreferences $1
rm *.class