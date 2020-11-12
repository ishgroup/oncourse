import java.time.LocalDate

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { CorporatePass cp ->
        CorporatePass(id: cp.id) {
            modifiedOn(cp.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            createdOn(cp.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            expiryDate(cp.expiryDate?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            invoiceEmail(cp.invoiceEmail)
            contactFirstName(cp.contact?.firstName)
            contactLastName(cp.contact?.lastName)
        }
    }
}
