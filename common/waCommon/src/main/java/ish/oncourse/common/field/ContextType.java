/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au No copying or use of this code is allowed without permission in writing from ish.
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
	COURSE_CLASS("courseClass");

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
