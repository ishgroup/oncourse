records.each { Contact c ->
    csv << [
            "Last name"     : c.lastName,
            "First name"        : c.firstName,
            "Email"     : c.email,
            "Company name of invoice recipient"     : c.customFields.find { cf -> cf.customFieldType.key == "EmployerName" }?.value ?: "",
            "Email Address of invoice recipient"    : c.customFields.find { cf -> cf.customFieldType.key == "ManagersEmail" }?.value ?: "",
            "Job Title"     : c.customFields.find { cf -> cf.customFieldType.key == "JobTitle" }?.value ?: "",
            "Which FACS District is your service located in?"   : c.customFields.find { cf -> cf.customFieldType.key == "FACSdistrict" }?.value ?: "",
    ]
}
