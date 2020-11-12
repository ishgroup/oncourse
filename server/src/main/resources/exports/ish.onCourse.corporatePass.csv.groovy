records.each { CorporatePass cp ->
    csv << [
            "Modified On"                                : cp.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "Created On"                                 : cp.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "Expiry Date"                                : cp.expiryDate?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
            "Invoice Email"                              : cp.invoiceEmail,
            "Contact first name"                         : cp.contact?.firstName,
            "Contact last name"                          : cp.contact?.lastName,
    ]
}
