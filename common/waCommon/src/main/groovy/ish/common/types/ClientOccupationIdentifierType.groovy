/*
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;
import ish.oncourse.API;

/**
 * A set of values for AVETMISS reporting for Victoria only.
 * Consult the AVETMISS documentation for more detail about these options.
 *
 */
@API
public enum ClientOccupationIdentifierType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 0
	 */
	@API
	NOT_SET(0, "Not Stated", 0),

	/**
	 * Database value: 1
	 */
	@API
	MANAGER(1, "Manager (1)", 1),

	/**
	 * Database value: 2
	 */
	@API
	PROFESSIONALS(2, "Professionals (2)", 2),

	/**
	 * Database value: 3
	 */
	@API
	TECHNICIANS(3, "Technicians and Trades Workers (3)", 3),

	/**
	 * Database value: 4
	 */
	@API
	COMMUNITY(4, "Community and personal Service Workers (4)", 4),

	/**
	 * Database value: 5
	 */
	@API
	CLERICAL(5, "Clerical and Administrative Workers (5)", 5),
	
	/**
	 * Database value: 6
	 */
	@API
	SALES(6, "Sales Workers (6)", 6),

	/**
	 * Database value: 7
	 */
	@API
	MACHINERY(7, "Machinery Operators and Drivers (7)", 7),

	/**
	 * Database value: 8
	 */
	@API
	LABOURERS(8, "Labourers (8)", 8);
		

	private String displayName;
	private int value;
	private int code;

	private ClientOccupationIdentifierType(int value, String displayName, int code) {
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
