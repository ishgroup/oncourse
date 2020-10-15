def toBeSent = new Date()

records.each { ac ->
	toBeSent = ac.releaseDate - 7
	csv << [
			"Class Code" 				: ac.courseClass.uniqueCode,
			"Class Name"				: ac.courseClass.course.name,
			"Assessment Name"			: ac.assessment.name,
			"Assessment Code"			: ac.assessment.code,
			"Assessment Tags"			: (ac.assessment.tags.size() > 0) ? ac.assessment.tags*.name.flatten().unique().join(";") : "",
			"Assessment Release Date"	: ac.releaseDate.format("dd/MM/YYYY"),
			"Assessment Due Date"		: ac.dueDate.format("dd/MM/YYYY"),
			"Location" 					: ac.courseClass?.room?.site?.name,
			"Enrolmnent Counts"			: ac.courseClass.enrolmentsCount,
			"Date to be sent" 			: toBeSent.format("dd/MM/YYYY")
	]
}

