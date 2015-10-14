/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A set of values for AVETMISS reporting from the version 7 standard.
 * Consult the AVETMISS documentation for more detail about these options.
 * 
 */
@API
public enum DeliveryMode implements DisplayableExtendedEnumeration<Integer> {

	/**
	 */
	@API
	NOT_SET(0, "Not Set", 0),

	/**
	 */
	@API
	CLASSROOM(1, "Classroom-based (10)", 10),

	/**
	 */
	@API
	ELECTRONIC(2, "Electronic-based (20)", 20),

	/**
	 */
	@API
	EMPLOYMENT(3, "Employment-based (30)", 30),

	/**
	 */
	@API
	OTHER(4, "Other delivery (40)", 40),

	/**
	 */
	@API
	NA(9, "Not applicable - recognition of prior learning/credit transfer (90)", 90),

	/**
	 */
	@API
	WA_LOCAL_CLASS(11, "WA: Local Class (1)", 1),

	/**
	 */
	@API
	WA_REMOTE_CLASS(12, "WA: Remote Class - Live Conferencing (2)", 2),

	/**
	 */
	@API
	WA_SELF_PACED_SCHEDULED(13, "WA: Self Paced - Scheduled (3)", 3),

	/**
	 */
	@API
	WA_SELF_PACED_UNSCHEDULED(14, "WA: Self Paced - Unscheduled (4)", 4),

	/**
	 */
	@API
	WA_EXTERNAL(15, "WA: External - Correspondence (5)", 5),

	/**
	 */
	@API
	WA_WORKPLACE(16, "WA: Workplace (6)", 6),

	/**
	 */
	@API
	WA_VIDEO_LEARNING(18, "WA: Video/Television Learning (8)", 8),

	/**
	 */
	@API
	WA_INTERNET_SITE(19, "WA: Internet Site - Online Learning (9)", 9);

	private String displayName;
	private int value;
	private int code;

	private DeliveryMode(int value, String displayName, int code) {
		this.value = value;
		this.displayName = displayName;
		this.code = code;
	}

	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	public Integer getCode() {
		return this.code;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}

}
