records.each { Module mod ->
    csv << [
            "default qualification"                     : mod.defaultQualification?.title,
            "default qualifications national code"      : mod.defaultQualification?.nationalCode,
            "field of education"                        : mod.fieldOfEducation,
            "modifiedOn"                                : mod.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "createdOn"                                 : mod.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "is offered"                                : mod.isOffered,
            "national code"                             : mod.nationalCode,
            "training package"                          : mod.trainingPackage?.title,
            "nominal hours"                             : mod.nominalHours,
            "title"                                     : mod.title,
            "specialization"                            : mod.specialization,
            "credit points"                             : mod.creditPoints,
            "expiry days"                               : mod.expiryDays,
            "is custom"                                 : mod.isCustom,
            "type"                                      : mod.type
    ]
}
