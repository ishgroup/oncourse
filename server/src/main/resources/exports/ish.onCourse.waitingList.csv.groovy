records.each { WaitingList wl ->
	csv << [
			"Course code"  : wl.course.code,
			"Course name"  : wl.course.name,
			"created"      : wl.createdOn?.format("yyyy-MM-dd'T'HH:mm:ssXXX"),
			"Student count": wl.studentCount,
			"Notes"        : wl.notes,
			"Student notes": wl.studentNotes,
			"Title"        : wl.student.contact.title,
			"First name"   : wl.student.contact.firstName,
			"Last name"    : wl.student.contact.lastName,
			"Email"        : wl.student.contact.email,
			"Street"       : wl.student.contact.street,
			"Suburb"       : wl.student.contact.suburb,
			"State"        : wl.student.contact.state,
			"Post code"    : wl.student.contact.postcode,
			"Mobile phone" : wl.student.contact.mobilePhone,
			"Home phone"   : wl.student.contact.homePhone,
			"Work phone"   : wl.student.contact.workPhone,
			"Birth date"   : wl.student.contact.birthDate?.format("yyyy-MM-dd")
	]
}
