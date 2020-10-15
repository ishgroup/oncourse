records.each { Certificate c ->
	csv << [
			"First Name"              		: c.studentFirstName,
			"Last Name"              		: c.studentLastName,
			"Training Date"                 : c.createdOn?.format("dd/MM/yyyy"),
			"Trainer"						: c.outcomes.first().enrolment.courseClass.tutorRoles[0]?.tutor?.contact?.fullName,
			"Cert No."              		: c.certificateNumber,
			"Outcome 1"						: (c.outcomes[0]?.code ?: "") + " " + (c.outcomes[0]?.name ?: ""),
			"Outcome 2"						: (c.outcomes[1]?.code ?: "") + " " + (c.outcomes[1]?.name ?: ""),
			"Outcome 3"						: (c.outcomes[2]?.code ?: "") + " " + (c.outcomes[2]?.name ?: ""),
			"Outcome 4"						: (c.outcomes[3]?.code ?: "") + " " + (c.outcomes[3]?.name ?: ""),
			"Outcome 5"						: (c.outcomes[4]?.code ?: "") + " " + (c.outcomes[4]?.name ?: ""),
	]
}