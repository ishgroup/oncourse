/**
 * Copyright ish group pty ltd. All rights reserved. http://www.ish.com.au
 * No copying or use of this code is allowed without permission in writing from ish.
 */
package ish.common.types;

/**
 * For each access key, a role may be given certain access levels.
 * 
 * @PublicApi
 */
public final class Mask {
	/**
	 * No access to whatever is controlled by this key.
	 * 
	 * @PublicApi
	 */
	public static final int NONE = 0;
	
	/**
	 * Records may be viewed by the holder of a key with this mask.
	 * 
	 * @PublicApi
	 */
	public static final int VIEW = 1;
	
	/**
	 * New records may be created by the holder of a key with this mask.
	 * 
	 * @PublicApi
	 */
	public static final int CREATE = 2;
	
	/**
	 * Records may be edited by the holder of a key with this mask.
	 * 
	 * @PublicApi
	 */
	public static final int EDIT = 4;
	
	/**
	 * Records may be deleted by the holder of a key with this mask.
	 * 
	 * @PublicApi
	 */
	public static final int DELETE = 8;
	
	/**
	 * Records may be printed by the holder of a key with this mask.
	 * 
	 * @PublicApi
	 */
	public static final int PRINT = 16;
	
	/**
	 * Mask for all access allowed.
	 * 
	 * @PublicApi
	 */
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
