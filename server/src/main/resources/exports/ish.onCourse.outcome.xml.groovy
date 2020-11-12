xml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

xml.data() {
    records.each { Outcome o ->
        outcome(id: o.id) {
            "id"(o.id)
            "createdOn"(o.createdOn?.format("d-M-y HH:mm:ss"))
            "modifiedOn"(o.modifiedOn?.format("d-M-y HH:mm:ss"))
            "startDate"(o.startDate?.format("yyyy-MM-dd"))
            "endDate"(o.endDate?.format("yyyy-MM-dd"))
            "course"(o.enrolment?.courseClass?.course?.name)
            "code"(o.enrolment?.courseClass?.uniqueCode)
            "nationalCode"(o.module?.nationalCode)
            "status"(o.status)
            "deliveryMode"(o.deliveryMode)
            "reportableHours"(o.reportableHours)
            "hoursAttended"(o.hoursAttended)
            "fundingSource"(o.fundingSource)
            "vetPurchasingContractId"(o.vetPurchasingContractID)
            "vetPurchasingContractScheduleId"(o.vetPurchasingContractScheduleID)
            "vetFundingSourceStateId"(o.vetFundingSourceStateID)
            "specificProgramIdentifier"(o.specificProgramIdentifier)
            "markedByTutor"(o.markedByTutor)
            "markedByTutorDate"(o.markedByTutorDate)
            "startDateOverridden"(o.startDateOverridden)
            "endDateOverridden"(o.endDateOverridden)
        }
    }
}
