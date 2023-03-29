records.each { Certificate c ->
	csv << [
			"studentFirstName"               : c.student?.contact?.firstName,
			"studentLastName"                : c.student?.contact?.lastName,
			"studentBirthDate"               : c.student.contact.birthDate?.format("dd/MM/yyyy"),
			"studentEmail"                   : c.student.contact.email,
			"studentStreet"                  : c.student.contact.street,
			"studentSuburb"                  : c.student.contact.suburb,
			"studentState"                   : c.student.contact.state,
			"studentPostcode"                : c.student.contact.postcode,
			"studentNumber"                  : c.student.studentNumber,
			"studentUniqueLearnerIdentifier" : c.student.uniqueLearnerIndentifier,
			"certificateNumber"              : c.certificateNumber,
			"fullQualification"              : c.isQualification,
			"createdOn"                      : c.createdOn?.format("dd/MM/yyyy"),
			"printed"                        : c.printedOn?.format("dd/MM/yyyy"),
			"revokedOn"                      : c.revokedOn?.format("dd/MM/yyyy"),
			"qualificationNationalCode"      : c.qualification?.nationalCode,
			"qualificationLevel"             : c.qualification?.level,
			"qualificationTitle"             : c.qualification?.title,
			"unitCount"                      : c.certificateOutcomes.size(),
			"publicNotes"                    : c.publicNotes,
			"privateNotes"                   : c.privateNotes
	]
}
