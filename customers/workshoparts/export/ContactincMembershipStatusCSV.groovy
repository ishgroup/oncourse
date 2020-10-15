/*
 * Copyright ish group pty ltd. All rights reserved. https://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */

records.each { Contact c ->
	if (c.memberships.empty) {
		addRow(c)
	} else {
		def result = c.memberships.findAll { m -> ![ProductStatus.CANCELLED, ProductStatus.CREDITED].contains(m.status) }.groupBy { membership -> membership.product.name }

		result.each { entry ->
			Date date = entry.value.sort { membership -> membership.expiryDate }.reverse()[0].expiryDate
			addRow(c, entry.key, date)
		}
	}
}

def addRow(Contact c) {
	addRow(c, null, null)
}

def addRow(Contact c, String membershipName, Date expiryDate) {
	csv << [
			"title"                          : c.title,
			"lastName"                       : c.lastName,
			"firstName"                      : c.firstName,
			"allowEmail"                     : c.allowEmail,
			"membership status"              : membershipName,
			"membership expiry"              : expiryDate?.format("dd-MM-yyyy"),
			"street"                         : c.street,
			"postcode"                       : c.postcode,
			"suburb"                         : c.suburb,
			"state"                          : c.state,
			"email"                          : c.email
	]
}
