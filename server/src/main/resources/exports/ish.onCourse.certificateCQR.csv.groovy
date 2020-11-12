csv.delimiter = '\t'
csv.writeHeader = false

csv << ["header line" : '[VERSION200]']

records.each { c ->
	 def map = [
	        "VET clientId"        : c.certificateOutcomes*.outcome*.enrolment*.vetClientID.find(),
			"student number"      : c.student.studentNumber,
			"first name"          : c.student.contact.firstName,
			"last name"           : c.student.contact.lastName,
			"birthDate"           : c.student.contact.birthDate?.format("dd/MM/YYYY"),
			"gender"              : c.student.contact.gender == null ? "@" : (c.student.contact.gender.databaseValue == 1 ? "M" : "F"),
			"nationalCode"        : c.qualification?.nationalCode,
			"isQualification"     : c.isQualification ? "Q" : "S",
			"createdOn"           : c.createdOn?.format("dd/MM/YYYY"),
			"certificateNumber"   : c.certificateNumber,
			"language"            : "English"
	]

	c.certificateOutcomes*.outcome*.module*.nationalCode.each { code ->
		map << [ "${code}" : code ]
	}

	csv << map
}
