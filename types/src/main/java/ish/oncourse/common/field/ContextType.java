/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */
package ish.oncourse.common.field;

public enum ContextType {
	CONTACT("contact"),
	STUDENT("student"),
	COURSE("course"),
	ENROLMENT("enrolment"),
	APPLICATION("application"),
	WAITING_LIST("waitingList"),
	TAG_GROUP("tag"),
	MAILING_LIST("mailingList"),
	SURVEY("survey"),
	COURSE_CLASS("courseClass"),
	ARTICLE("article"),
	MEMBERSHIP("membership"),
	VOUCHER("voucher");

	private String identifier;

	private ContextType(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public static ContextType getByIdentifier(String identifier) {

		for (ContextType context : ContextType.values()) {
			if (context.identifier.equalsIgnoreCase(identifier)) {
				return context;
			}
		}
		return null;
	}
}
