xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Account acc ->
        account(id: acc.id) {
            accountCode(acc?.accountCode)
            createdOn(acc?.createdOn?.format("d-M-y HH:mm:ss"))
            description(acc?.description)
            isEnabled(acc?.isEnabled)
            modifiedOn(acc?.modifiedOn?.format("d-M-y HH:mm:ss"))
            type(acc?.type?.displayName)
            tax(acc?.tax?.taxCode)
        }
    }
}
