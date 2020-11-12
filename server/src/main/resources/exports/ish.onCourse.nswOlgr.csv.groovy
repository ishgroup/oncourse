records.each { Contact c ->
	csv << [
			"title"                          : c.title,
			"gender"                         : c.isMale ? "M" : (c.isMale == null) ? "" : "F",
			"lastName"                       : c.lastName,
			"firstName"                      : c.firstName,
			"middleName"                     : c.middleName,
			"birthDate"                      : c.birthDate?.format("dd/MM/yyyy"),
			"countryOfBirth"                 : c.student?.countryOfBirth?.name,
			"mobilePhone"                    : c.mobilePhone,
			"email"                          : c.email,
			"street"                         : c.street,
			"suburb"                         : c.suburb,
			"state"                          : c.state,
			"postcode"                       : c.postcode,
			"usi"                            : c.student?.usi,
			"usiStatus"                      : c.student?.usiStatus,
	]
}
