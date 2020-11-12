import java.time.LocalDate

xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Module mod ->
        module(id: mod.id) {
            defaultQualification(mod.defaultQualification?.title)
            defaultQualificationNationalCode(mod.defaultQualification?.nationalCode)
            fieldOfEducation(mod.fieldOfEducation)
            modifiedOn(mod.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            createdOn(mod.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"))
            isOffered(mod.isOffered)
            nationalCode(mod.nationalCode)
            trainingPackage(mod.trainingPackage?.title)
            nominalHours(mod.nominalHours)
            title(mod.title)
            specialization(mod.specialization)
            creditPoints(mod.creditPoints)
            expiryDays(mod.expiryDays)
            isCustom(mod.isCustom)
            type(mod.type)
        }
    }
}
