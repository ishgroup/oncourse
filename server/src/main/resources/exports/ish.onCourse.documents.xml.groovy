xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Document doc ->
        document(id: doc.id) {
            createdOn(doc?.createdOn?.format("d-M-y HH:mm:ss"))
            modifiedOn(doc?.modifiedOn?.format("d-M-y HH:mm:ss"))
            fileName(doc?.currentVersion?.fileName)
            fileType(doc?.currentVersion?.mimeType)
            securityLevel(doc?.webVisibility)
            description(doc?.description)
            isShared(doc?.isShared)
            isRemoved(doc?.isRemoved)
            added(doc?.added)
            fileUUID(doc?.fileUUID)
        }
    }
}
