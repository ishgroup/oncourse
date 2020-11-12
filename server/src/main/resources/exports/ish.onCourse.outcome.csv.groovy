records.each { Outcome o ->
    csv << [
            "Student name"                      : o.studentName,
            "Course"                            : o.enrolment?.courseClass?.course?.name,
            "Code"                              : o.enrolment?.courseClass?.uniqueCode,
            "National Code"                     : o.module?.nationalCode,
            "UOC name"                          : o.module?.title,
            "Status"                            : o.status,
            "Start date"                        : o.startDate?.format("yyyy-MM-dd"),
            "End date"                          : o.endDate?.format("yyyy-MM-dd"),
            "Created on"                        : o.createdOn?.format("yyyy-MM-dd HH:mm:ss"),
            "Modified on"                       : o.modifiedOn?.format("yyyy-MM-dd HH:mm:ss"),
            "Delivery mode"                     : o.deliveryMode,
            "Reportable hours"                  : o.reportableHours,
            "Hours attended"                    : o.hoursAttended,
            "Funding source"                    : o.fundingSource,
            "vetPurchasingContractID"           : o.vetPurchasingContractID,
            "vetPurchasingContractScheduleID"   : o.vetPurchasingContractScheduleID,
            "vetFundingSourceStateID"           : o.vetFundingSourceStateID,
            "specificProgramIdentifier"         : o.specificProgramIdentifier,
            "markedByTutor"                     : o.markedByTutor,
            "markedByTutorDate"                 : o.markedByTutorDate,
            "startDateOverridden"               : o.startDateOverridden,
            "endDateOverridden"                 : o.endDateOverridden
    ]
}
