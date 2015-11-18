/*
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
 * Delivery mode can be set for the whole class, or individually per Outcome
 * 
 */
@API
public enum DeliveryMode implements DisplayableExtendedEnumeration<Integer> {

	/**
     * Database value: 0
	 */
	@API
	NOT_SET(0, "Not Set", 0),

	/**
     * Database value: 1
	 */
	@API
	CLASSROOM(1, "Classroom-based (10)", 10),

	/**
     * Database value: 2
	 */
	@API
	ELECTRONIC(2, "Electronic-based (20)", 20),

	/**
     * Database value: 3
	 */
	@API
	EMPLOYMENT(3, "Employment-based (30)", 30),

	/**
     * Database value: 4
	 */
	@API
	OTHER(4, "Other delivery (40)", 40),

	/**
     * Database value: 9
	 */
	@API
	NA(9, "Not applicable - recognition of prior learning/credit transfer (90)", 90),

	/**
     * Database value: 11
	 */
	@API
	WA_LOCAL_CLASS(11, "WA: Local Class (1)", 1),

	/**
     * Database value: 12
	 */
	@API
	WA_REMOTE_CLASS(12, "WA: Remote Class - Live Conferencing (2)", 2),

	/**
     * Database value: 13
	 */
	@API
	WA_SELF_PACED_SCHEDULED(13, "WA: Self Paced - Scheduled (3)", 3),

	/**
     * Database value: 14
	 */
	@API
	WA_SELF_PACED_UNSCHEDULED(14, "WA: Self Paced - Unscheduled (4)", 4),

	/**
     * Database value: 15
	 */
	@API
	WA_EXTERNAL(15, "WA: External - Correspondence (5)", 5),

	/**
     * Database value: 16
	 */
	@API
	WA_WORKPLACE(16, "WA: Workplace (6)", 6),

	/**
     * Database value: 18
	 */
	@API
	WA_VIDEO_LEARNING(18, "WA: Video/Television Learning (8)", 8),

	/**
     * Database value: 19
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
