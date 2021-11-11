#Replication and data upgrade
If we have a necessity to change schema and sync it with willow side.

1. Start from adjusting willow side first, pass through willow instructions first:
[   https://github.com/ishgroup/willow/blob/main/services/README.md
](https://github.com/ishgroup/willow/blob/main/services/README.md)

2. Make sure that all willow apps redy and deployed

3. Make angel side changes:
- db
- cayenne
- business logik

4. make sure that angel side changees pushed in main branche. That is necessary because
- all plugins has server-jar.jar dependency, latest snapshot are used

[  https://repo.ish.com.au/#browse/search=keyword%3Dserver-jar:ish-snapshots%3Aish.oncourse.angel%3Aserver-jar%3A99-SNAPSHOT](https://repo.ish.com.au/#browse/search=keyword%3Dserver-jar:ish-snapshots%3Aish.oncourse.angel%3Aserver-jar%3A99-SNAPSHOT)
- we build server snapshots from mian brance (runs automatically build on any change, or manually)

 [ https://build.ish.com.au/#/builders/322](https://build.ish.com.au/#/builders/322)

for testing approach you can build server-jar lacally and adjust [build.gradle](build.gradle)
to use local file:
```
    dependencies {
        //implementation group: "ish.oncourse.angel", name: "server-jar", version: "99-SNAPSHOT", changing: true
        implementation files('path/to/local/server-jar.jar')
    }
```

5. Update webservices-client lib to the latest version

[plugins/replication/build.gradle]()
```
webservicesVersion = '122' -> '123'
```

6. depends on what way are you following, implemnt new version or adjust current one:

#### Adjust current version (ok when you extending existing stub):

 - go to exact stub builde/entity updater  and extend it with pice of code. Example off adding Attendance.startDate fieeld:

[ish.oncourse.commercial.replication.builders.AttendanceStubBuilder]()
```
	stub.setStartDate(entity.getStartDate())
```

[ish.oncourse.commercial.replication.updaters.AttendanceUpdater]()
```
	entity.setStartDate(stub.getStartDate())
```


#### Implementing new rplication version
Since angel side is replication **client** so it should suppoort  only one **latest** replication version 

- replace all `v25` entries to `v26` (case sensitive) in p[lugins/replication]() direectory
- implement all required changes cpecific to new replication, for example add new entity Attendance to replication:
 
new builder and updater:
    
[ish.oncourse.commercial.replication.updaters.AttendanceUpdater]()
    
[ish.oncourse.commercial.replication.builders.AttendanceStubBuilder]()

add ccorrecponded mappings here:

[ish.oncourse.commercial.replication.updaters.AngelUpdaterImpl]()
 
[ish.oncourse.commercial.replication.builders.AngelStubBuilderImpl]()

- make sure that replication code compiles


7. push your changes in remote 

8. Mak sure your changes in main brance

9. release new plugins version here: [https://build.ish.com.au/](https://build.ish.com.au/)

10. run angel with latest replication plugin


Here we go!

> **Important note - just created plugin version is not compatible with outdated angel servers**
> **for example v26 replication needed to replicate `Attendance.startDate` field**
> **the replivation-62.jar (release of replication plugin 62 version) implemented exaclty v26 replication**
> **`Attendance.startDate` attribute was introdused in 108 version of angel**
> **replivation-62.jar compatible only with 108 version of angel and upper!!!**



