package ish.common.types;

import ish.common.util.DisplayableExtendedEnumeration;

public enum EntityRelationType implements DisplayableExtendedEnumeration<Integer> {
	
	COURSE(1, "Course"),
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
