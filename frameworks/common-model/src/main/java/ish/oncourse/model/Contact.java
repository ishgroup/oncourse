package ish.oncourse.model;

import ish.oncourse.model.auto._Contact;

public class Contact extends _Contact {

	public String getFullName() {
		StringBuilder buff = new StringBuilder();
		String familyName = getFamilyName();
		if (Boolean.TRUE.equals(getIsCompany())) {
			if (familyName != null) {
				buff.append(familyName);
			}

		} else {
			String givenName = getGivenName();
			if (givenName != null) {
				buff.append(givenName);
			}
			if (familyName != null) {
				if (buff.length() > 0) {
					buff.append(" ");
				}
				buff.append(familyName);
			}
		}
		return buff.toString();
	}
}
