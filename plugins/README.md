#How to add plugin to angel server
1. download certain (replication for example) plugin from:

   [https://repo.ish.com.au/#browse/search=keyword%3Dreplication](https://repo.ish.com.au/#browse/search=keyword%3Dreplication)

or (for developers)

build it yourself locally with `plugins:replication:build` graddle task

2. put plugin jar file into {oncoursehome}/plugins/replication-99SNNAPSHOT.jar

or (for developers)

add file dependencie into server/build.gradle
```
implemenntation files('path/to/local/replication-99SNNAPSHOT.jar')
```

3.restart server

## Add plugin as snapshot
1. Add repo.ish.com.au credentials to /home/.gradle/gradle.properties like in gradle.properties of plugins file
   (you can change them there too, but than every time you need you will set it again)
2. Build server with task server:jar and add it to plugin dependencies (if you use local server-jar)
3. Build plugin, run task plugins:replication:publish -PreleaseVersion=Your_snapshot_version_number-SNAPSHOT
4. Add on server dependency to published snapshot

#Supported features

1. Define script closure that could be used in onCourse scripts.

 Main components:
 - plugin class that implements
```
ish.oncourse.server.integration.PluginTrait
```
and annatated with:
```
@ish.oncourse.server.integration.Plugin
```
Each plugin should have uique type number, see example:
```
ish.oncourse.commercial.plugin.xero.XeroException
```
 - Script closure class that implements
```
   ish.oncourse.server.scripting.ScriptClosureTrait<T>
```
and annatated with:
```
@ish.oncourse.server.scripting.ScriptClosure
```
Each closure should have uique losure name:

```
ish.oncourse.commercial.plugin.xero.XeroScriptClosure
```
example of closure usage in script:
```
xero {
   action "journal"
   startOn startOnDate
   postJournals postJournalsInXero
}
```

2. @OnSave plugin callback

Invoke when user persist `ish.oncourse.server.cayenne.IntegrationConfiguration` object of certain pugin type.

params:
```
ish.oncourse.server.cayenne.IntegrationConfiguration configuration
java.util.Map<String,String> props
```
see eaxmple method:
```
    ish.oncourse.commercial.plugin.tcsi.TCSIIntegration.onSave()
```

4. @OnConfigure callback

Invoke when server DI container cofigure, before `ish.oncourse.server.AngelCommand` run

params:
```
com.google.injec.Binder binder
```
see example:
```
    ish.oncourse.commercial.replication.ReplicationPlugin.configure()
```



5. @OnStart plugin callback. Invoke on server start after DI is ready and fully initialized

see eaxmple method:
```
   ish.oncourse.commercial.replication.ReplicationPlugin.start()
```

6. System event listening:

see example:
```
ish.oncourse.commercial.replication.event.SystemUserEventListener
```

7. Cayenne even listening

see example:
```
   ish.oncourse.commercial.replication.lifecycle,QueueableLifecycleListener
```

8. Periodic jobs scheduling

see example:
```
ish.oncourse.commercial.replication.services.ReplicationJob
```

9. Liquibase data/model upgrade

Just put your liquibase yaml file into [/data]() folder on classpath

see example:

[plugins/replication/src/main/resources/database/replication.yml]()

10. cayenne data model extantion, custom entities

Just put your cayenne map xml file into [/cayenne]() folder on classpath

see example:

[plugins/replication/src/main/resources/cayenne/cayenne-Replication.xml]()

11. pluggable resources:

   - message templates ([/message]() folder)
   - exports ([/exports]() folder)
   - pdf reports ([/reports ]() folder)
   - groovy scripts ([/scripts]() folder)
   - imports ([/imports]() folder)

Just put defenition of your resource (*.jrxml, *.yaml, *.html, *.txt *.groovy) into corresponded folder on classpath

see example:
```
plugins/myob/src/main/resources/scripts/ish.script.integrationMYOB.groovy
plugins/myob/src/main/resources/scripts/ish.script.integrationMYOB.yaml
``