records.each { Account account ->
    csv << [
            "Account code"       : account.accountCode,
            "createdOn"          : account.createdOn?.format("d-M-y HH:mm:ss"),
            "Account description": account.description,
            "id"                 : account.id,
            "isEnabled"          : account.isEnabled,
            "modifiedOn"         : account.modifiedOn?.format("d-M-y HH:mm:ss"),
            "Account type"       : account.type.displayName,
            "Tax"                : account.tax?.taxCode,
    ]
}
