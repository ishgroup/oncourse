records.each { Contact c ->
	csv << [
		"First name"						: c.firstName,
		"Last name"							: c.lastName,
		"email"								: c?.email,
		"How did you find out about us?"	: c.customField("howDidYouFindOutAboutUs")?: ""
	]
}