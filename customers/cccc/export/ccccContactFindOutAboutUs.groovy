/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { Contact c ->
	csv << [
		"First name"						: c.firstName,
		"Last name"							: c.lastName,
		"email"								: c?.email,
		"How did you find out about us?"	: c.customField("howDidYouFindOutAboutUs")?: ""
	]
}
