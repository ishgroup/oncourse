records.each { Script sc ->
    csv << [
            "name"              : sc.name,
            "script"            : sc.script,
            "modifiedOn"        : sc.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "createdOn"         : sc.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "enabled"           : sc.enabled,
            "trigger type"      : sc.triggerType,
            "cron schedule"     : sc.cronSchedule,
            "review date"       : sc.entityClass,
            "entity event type" : sc.entityEventType,
            "description"       : sc.description,
            "system event type" : sc.systemEventType
    ]
}
