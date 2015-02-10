package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

/**
 * Courses and products can be related to other courses and products.
 * This type points to what sort of record to expect at the other end
 * of the polymorphic join.
 * 
 * @PublicApi
 */
public enum EntityRelationType implements DisplayableExtendedEnumeration<Integer> {

	/**
	 * Database value: 1
	 * @PublicApi
	 */
	COURSE(1, "Course"),

	/**
	 * Database value: 2
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
