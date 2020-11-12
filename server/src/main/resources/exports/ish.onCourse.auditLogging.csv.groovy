records.each { Audit a ->
    csv << [
            "systemUser" : [a.systemUser?.firstName, a.systemUser?.lastName].findAll().join(' '),
            "created"    : a.created.format("dd/MM/yyyy"),
            "entityName" : a.entityIdentifier,
            "entityId"   : a.entityId,
            "action"     : a.action,
            "message"    : a.message
    ]
}
