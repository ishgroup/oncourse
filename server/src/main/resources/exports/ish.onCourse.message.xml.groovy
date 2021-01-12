xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Message msg ->
        message(id: msg.id) {
            contacts(msg.contacts*.fullName.flatten().unique().join(", "))
            createdOn(msg?.createdOn?.format("d-M-y HH:mm:ss"))
            createdBy(msg?.createdBy?.email)
            emailFrom(msg?.emailFrom ?: "")
            emailSubject(msg?.emailSubject ?: "")
            emailHtmlBody(msg?.emailHtmlBody ?: "")
            emailBody(msg?.emailBody ?: "")
            smsText(msg.smsText ?: "")
            postDescription(msg?.postDescription ?: "")
        }
    }
}
