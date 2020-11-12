import java.time.LocalDate

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Qualification q ->
        qualification(id: q.id) {
            title(q.title)
            nationalCode(q.nationalCode)
            modifiedOn(q.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            createdOn(q.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            isAccreditedCourse(q.type?.displayName)
            level(q.level)
            nominalHours(q.nominalHours?.format("0.00"))
            reviewDate(q.reviewDate?.format("yyyy-MM-dd"))
            anzsco(q.anzsco)
            fieldOfEducation(q.fieldOfEducation)
            isOffered(q.isOffered)
            newApprenticeship(q.newApprenticeship)
        }
    }
}
