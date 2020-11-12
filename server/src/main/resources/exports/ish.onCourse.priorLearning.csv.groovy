import ish.oncourse.server.cayenne.PriorLearning

records.each { PriorLearning p ->
	csv << [
			"createdOn"         			: p.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"modifiedOn"        			: p.modifiedOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"title"             			: p.title,
			"notes"      					: p.notes,
			"externalRef"					: p.externalRef,
			"outcomeIdTrainingOrg" 			: p.outcomeIdTrainingOrg,
			"studentFirstName"             	: p.student?.contact?.firstName,
			"studentLastName"               : p.student?.contact?.lastName,
			"studentBirthDate"              : p.student?.contact?.birthDate?.format("dd/MM/yyyy"),
			"studentEmail"                  : p.student?.contact?.email,
			"studentStreet"                 : p.student?.contact?.street,
			"studentSuburb"                 : p.student?.contact?.suburb,
			"studentState"                  : p.student?.contact?.state,
			"studentPostcode"               : p.student?.contact?.postcode,
			"studentNumber"                 : p.student?.studentNumber,
			"studentUniqueLearnerIdentifier": p.student?.uniqueLearnerIndentifier,
			"qualificationNationalCode"     : p.qualification?.nationalCode,
			"qualificationLevel"            : p.qualification?.level,
			"qualificationTitle"            : p.qualification?.title
	]
}
