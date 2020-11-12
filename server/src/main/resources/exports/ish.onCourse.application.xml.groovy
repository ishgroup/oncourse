xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Application app ->
        application(id: app.id) {
            confirmationStatus(app?.confirmationStatus)
            createdOn(app?.createdOn?.format("d-M-y HH:mm:ss"))
            modifiedOn(app?.modifiedOn?.format("d-M-y HH:mm:ss"))
            enrolBy(app?.enrolBy?.format("d-M-y HH:mm:ss"))
            feeOverride(app?.feeOverride)
            reason(app?.reason)
            source(app?.source)
            status(app?.status)
            studentFirstName(app?.student?.contact?.firstName)
            studentLastName(app?.student?.contact?.lastName)
            courseCode(app?.course?.code)
            courseName(app?.course?.name)
        }
    }
}
