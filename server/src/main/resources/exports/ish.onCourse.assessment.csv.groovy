records.each { Assessment assessment ->
    csv << [
            "id"                    : assessment.id,
            "createdOn"             : assessment.createdOn?.format("d-M-y HH:mm:ss"),
            "modifiedOn"            : assessment.modifiedOn?.format("d-M-y HH:mm:ss"),
            "Assessment code"       : assessment.code,
            "Assessment name"       : assessment.name,
            "Assessment description": assessment.description,
            "Active"                : assessment.active,
            "Grading type"          : assessment.gradingType?.typeName
    ]
}
