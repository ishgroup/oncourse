package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * @PublicApi
 */
public enum EntityRelationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * @PublicApi
	 */
	COURSE(1, "Course"),

	/**
	 * @PublicApi
	 */
	PRODUCT(2, "Product");

	private String displayName;
	private int value;
	
	private EntityRelationType(int value, String displayName) {
		this.value = value;
		this.displayName = displayName;
	}
	
	@Override
	public Integer getDatabaseValue() {
		return this.value;
	}

	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
}
