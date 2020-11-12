records.collectMany { CourseClass cc -> cc.sessions }.each { s ->
	csv << [
			"course-class code"	: s.courseClass.course.code + "-" + s.courseClass.code,
			"course name"		: s.courseClass.course.name,
			"student count"		: s.courseClass.validEnrolmentCount,
			"created on"	: s.createdOn.format("d/M/Y hh:mm a"),
			"modified on"	: s.modifiedOn.format("d/M/Y hh:mm a"),
			"session starts"	: s.displayStartDateTime.format("d/M/Y hh:mm a"),
			"session ends"		: s.displayEndDateTime.format("d/M/Y hh:mm a"),
			"session duration"	: s.durationInHours,
			"payable time"		: s.payableDurationInHours,
			"tutors"			: s.tutors?.contact?.fullName ?: "",
			"modules"			: s.modules?.nationalCode ?: "",
			"public notes"		: s.publicNotes,
			"private notes"		: s.privateNotes,
			"site name"       : s.room?.site?.name,
			"site address"	: s.room?.site?.street,
			"site address"	: s.room?.site?.state,
			"site suburb"      : s.room?.site?.suburb,
			"site postcode"     : s.room?.site?.postcode,
			"room"				: s.room?.name,
			"room"				: s.room?.facilities,
			"seat count"		: s.room?.seatedCapacity
				]
}
