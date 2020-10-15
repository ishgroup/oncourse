TODAY = new Date()
TODAY.set(hourOfDay: 0, minute: 0, second: 0)

records*.sessions.flatten().findAll { s -> dateBetween(TODAY, TODAY+1, s.startDatetime) && s.courseClass.course.code.contains("KTT") }.each { Session s ->

	csv << [
		"Class"					: s.courseClass.uniqueCode + " " + s.courseClass.course.name,
		"Student First Name"	: "",
		"Student Last Name"		: "",
		"Student Contact Number": "",
		"Session Time"			: "",
		"After School Walk Bus"	: "",
		"Allergy Info"			: "",
		"Age"					: "",
		"Sign In"				: "",
		"Sign Out"				: ""
	]


	s.courseClass.successAndQueuedEnrolments.each { Enrolment e ->
		csv << [
			"Class"					: " ",
			"Student First Name"	: e.student.contact.firstName,
			"Student Last Name"		: e.student.contact.lastName,
			"Student Contact Number": e.student?.contact?.mobilePhone,
			"Session Time"			: s.startDatetime.format("dd MMMM YYYY a"),
			"After School Walk Bus"	: e.customFields.find { cf -> cf.customFieldType.key == "Bus" }?.value,
			"Allergy Info"			: e.student?.specialNeeds,
			"Age"					: e.student.contact?.age,
			"Sign In"				: "",
			"Sign Out"				: ""
		]

	}

}

def dateBetween(Date startRange, Date endRange, Date toCheck){
    return toCheck.after(startRange) && toCheck.before(endRange)
}