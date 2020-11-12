import java.time.LocalDate

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Audit a ->
        audit(id: a.id) {
            systemUser([a.systemUser?.firstName, a.systemUser?.lastName].findAll().join(' '))
            created(a.created.format("dd/MM/yyyy"))
            entityName(a.entityIdentifier)
            entityId(a.entityId)
            action(a.action)
            message(a.message)
        }
    }
}
