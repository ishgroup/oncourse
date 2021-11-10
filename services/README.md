# Replication and data model upgrade
If we have a necessity to change schema (add more columns, move existing column to different table) on willow and angel.
The algorithm below guarantee the safety migration of data and data model with no downtime and support back compatibility of old angel servers.
simultaneously support work of old and new angel versions.


Let's review an example when we need to add new attrinute 'startDate' for 'Attendance' entity.

Not null

By default all existed Attendancies should be populated with values from related session:

`Attendance.startDate = Attendance.session.startDate
`

###### **Important note - willow should not care about certain college angel version, if you feel such necessity - you might doing something wrong**

## Schema changes
1. always do willow schema changes first. Willow should always has actual data model. No matter if angel is not up to date yet
 - create nullable column via liquibase [common/cayenne-model/src/main/resources/liquibase.db.changelog.xml]()
 - release/deploy admin app to appply liquibase
 - you need to care about other apps that use willow db, 
not null columns can brak them. For example currently deployed servises app  (in production) still has old cayenne model 
and try to insert `Attendance` without `startDate` value into updated db - insert will be failed since NotNull constraint
 - do not try use 'not null default={some value}' - we just loose control of managing data,
only business logik of application should assign values (using cayenne models).
2. adjust cayenne  data model
 - add what ever you need at  [common/cayenne-model/src/main/resources/oncourse.map.xml]()
 - run `:common:cayenne-model:cgen` gradle task to rebuild cayenne classes
 - make code changes that you need in accordance with business logik. Check all willow apps that use `Attendance` entity,
use new `startDate` value where you need it. 
 - If application read `startDate` from db - make sure that you have `startDate != null` check, since all `Attendance.startDate` in db are null yet
 - If application create or modify `Attendance` record - make sure that you populate `Attendance.startDate` with actuall value. 
Willow app should not produce records with `startDate == null` any more. 
 - adjust currently supported replication Updater:
   [ish.oncourse.webservices.replication.v25.updaters.AttendanceUpdater]():
   `entity.setStartDate(entity.getSession().getStartDate())`
so it doesn't not produce null values and emulate data upgrade.
It is also required for back compatibility of angel servers that doesn't have new data yet (support old versions)

3. release and deploy all willow java apps that affected by the changes. 
Services app should be also deployed (anyway) since it guaranteed has changes of updaters

4. write another liquibase to:
 - update **all** Attendance record - populate `startDate` colummn with value from corresponded `session`
 - add `not null` constraint - we can do it safety now, all app provide not null value for that column

5. deploy admin again

That is all. The willow end absalutelly up to date

Now we need to implement sending new data from angel side
###### **Important note - alway think about currenty deployed production apps after liquibase passed. They use same database**
###### **so you can not drop/rename columns/tables in one go**
###### **You need intermidiate step in this case, like deploying app with new schema**


## replication

Since we do not add new entities (just extend existing one) into replication and do not add new soap ports - new replication version is not really needed.
We can reuse current version.

So let's review both cases.

### reuse current replication version

1. add new field to replication stub here:

   [common/webservices-client/src/main/resources/wsdl/v25_replication.wsdl]()
2. rebuild Java classes with:

   `common:webservices-client:wsdl2Java` gradlew task. Note that target replication version hadrcoded in task source code
3. push changes to remote
4. release new webservices-client.jar library here:

   [https://build.ish.com.au/#/builders/124](https://build.ish.com.au/#/builders/124)
5. update corresponded dependency with latest version:

   [build.gradle](build.gradle)

  ` ebservicesVersion = '122'` ->` webservicesVersion = '123'`
6. Adjust corresponded entity updater. in our case:

   [ish.oncourse.webservices.replication.v25.updaters.AttendanceUpdater]()

like that:
```
 if (stub.getStartDate() != null) {
      entity.setStartDate(stub.getStartDate());
   } else {
      entity.setStartDate(entity.getSession().getStartDate());
   }
```
7. release/deploy services 

So now services app support old and new angel servers.


### new replication version
In case of adding new entities to replication or adding new ports, you need to add ne replication version
1. add version name here:
   [build.gradle](build.gradle)
like that:
```
ext {
      replicationVersions = [
               23,// introduced in onCourse 9.13
               24,// introduced in onCourse 105
               25, 
               26
       ]
       referenceVersions = [
               7// introduced in onCourse 9.10
       ]
   }
```   
2. copy
   [common/webservices-client/src/main/resources/wsdl/replication25_binding.xml]() to [common/webservices-client/src/main/resources/wsdl/replication26_binding.xml]()
   [common/webservices-client/src/main/resources/wsdl/v25_replication.wsdl]() to [common/webservices-client/src/main/resources/wsdl/v26_replication.wsdl]()
3. change all `v25` entries in new files to `v26` (case sensitive) 
4. extend
   [ish.oncourse.webservices.util.SupportedVersions]()
emuneration with new V26 item
5. repeate 1-6 steps from **reuse current replication version** article.

## implement new replication version for willow

1. Pass through all steps from **new replication version** article
2. run
   
`services:generateSOAPstubs`

to update generated sources 
3. copy packages with all content inside

   [services/src/main/java/ish/oncourse/webservices/replication/v25/builders]() to [services/src/main/java/ish/oncourse/webservices/replication/v26/builders]()
   
   [services/src/main/java/ish/oncourse/webservices/replication/v25/updaters]() to [services/src/main/java/ish/oncourse/webservices/replication/v26/updaters]()

   [services/src/main/java/ish/oncourse/webservices/soap/v25]() to [services/src/main/java/ish/oncourse/webservices/soap/v26]()
4. change all `v25` entries in new packages to `v26` (case sensitive) 
5. add new `v26` endpoints here:
   
[services/src/main/resources/application-context.xml]()
6. Make all required changes in certain stubBuilder/updater entity classe (that we just created in previous step). 
Remamber that you created new replication for exact purpose, this is exact time to do it now
7.run services app and verify by opening URL:
   [https://128.0.0.1:8090/services/v25/payment](https://secure-payment.oncourse.net.au/services/v25/payment)

### implement new replication version for angel
see 

[https://github.com/ishgroup/oncourse-secret/blob/main/plugins/replication/README.md
](https://github.com/ishgroup/oncourse-secret/blob/main/plugins/replication/build.gradle)


### remove support of old replication versions 
see 
[build.gradle](build.gradle)
```
ext {
      replicationVersions = [
               23,// introduced in onCourse 9.13
               24,// introduced in onCourse 105
               25,//  introduced in onCourse 108
               26
       ]
       referenceVersions = [
               7// introduced in onCourse 9.10
       ]
   }
```  

 variable has info about whitch replication version was introdused in which angel release.
So if you can see that all your angel servers has 108 version - you can remove v23 and v24.
To do that undo all steps from 
   - new replication version
   - implement new replication version for willow

articles.
