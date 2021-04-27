xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Assessment ass ->
        assessment(id: ass.id) {
            createdOn(ass?.createdOn?.format("d-M-y HH:mm:ss"))
            modifiedOn(ass?.modifiedOn?.format("d-M-y HH:mm:ss"))
            code(ass?.code)
            name(ass?.name)
            description(ass?.description)
            active(ass?.active)
            gradingType(ass.gradingType?.typeName)
        }
    }
}
