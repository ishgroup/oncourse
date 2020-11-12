import java.time.LocalDate

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Script sc ->
        script(id: sc.id) {
            name(sc.name)
            script(sc.script)
            modifiedOn(sc.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            createdOn(sc.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            enabled(sc.enabled)
            triggerType(sc.triggerType)
            cronSchedule(sc.cronSchedule)
            entityClass(sc.entityClass)
            entityEventType(sc.entityEventType)
            description(sc.description)
            systemEventType(sc.systemEventType)
        }
    }
}
