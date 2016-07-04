/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.oncourse.portal.services;

import ish.oncourse.model.Contact;

/**
 * User: akoiro
 * Date: 4/07/2016
 */
public class GetContactPhone {
	private Contact contact;
	public String get() {
		if (contact.getMobilePhoneNumber() != null)
			return contact.getMobilePhoneNumber();
		else if (contact.getHomePhoneNumber() != null)
			return contact.getHomePhoneNumber();
		else if (contact.getBusinessPhoneNumber() != null)
			return contact.getBusinessPhoneNumber();
		else if (contact.getFaxNumber() != null)
			return contact.getFaxNumber();
		else
			return null;
	}

	public static GetContactPhone valueOf(Contact contact) {
		GetContactPhone result = new GetContactPhone();
		result.contact = contact;
		return result;
	}
}
