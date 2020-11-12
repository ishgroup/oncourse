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
package ish.common.types;

import ish.oncourse.API;

/**
 * For each access key, a role may be given certain access levels. This class is not an enum because
 * the values here can be added up to give layers of access. For example, VIEW (1) + EDIT (5) = 6.
 *
 */
@API
public final class Mask {
	/**
	 * No access to whatever is controlled by this key.
	 *
	 */
	@API
	public static final int NONE = 0;

	/**
	 * Records may be viewed by the holder of a key with this mask.
	 *
	 */
	@API
	public static final int VIEW = 1;

	/**
	 * New records may be created by the holder of a key with this mask.
	 *
	 */
	@API
	public static final int CREATE = 2;

	/**
	 * Records may be edited by the holder of a key with this mask.
	 *
	 */
	@API
	public static final int EDIT = 4;

	/**
	 * Records may be deleted by the holder of a key with this mask.
	 *
	 */
	@API
	public static final int DELETE = 8;

	/**
	 * Records may be printed by the holder of a key with this mask.
	 *
	 */
	@API
	public static final int PRINT = 16;

	/**
	 * Mask for all access allowed.
	 *
	 */
	@API
	public static final int ALL = 255;

	private Mask() {}

    /**
	 * produces a human readable string of masks based on a integer number.
	 *
	 * @param pCode
	 * @return mask
	 */
	public static String maskForIntCode(Integer pCode) {
		Integer code = pCode;
		if (code == null || code == 0) {
			return "NONE";
		}
		if (code == 255) {
			return "ALL";
		}

		String result = "";
		if (code - PRINT > 0) {
			result = result + " PRINT";
			code = code - PRINT;
		}
		if (code - DELETE > 0) {
			result = result + " DELETE";
			code = code - DELETE;
		}
		if (code - EDIT > 0) {
			result = result + " EDIT";
			code = code - EDIT;
		}
		if (code - CREATE > 0) {
			result = result + " CREATE";
			code = code - CREATE;
		}
		if (code - VIEW > 0) {
			result = result + " VIEW";
			// code = code - VIEW;
		}

		return result.trim();
	}
}
