package ish.oncourse.services.textile.attrs;

public enum CourseListSortValue {

	DATE("date"),
	ALPHABETICAL("alphabetical"),
	AVAILABILITY("availability");
	
	private String name;

	private CourseListSortValue(String name) {
		this.name = name;
	}

	public static CourseListSortValue getByName(String name) {
		if (name == null) {
			return null;
		}
		for (CourseListSortValue item : values()) {
			if (name.equals(item.name)) {
				return item;
			}
		}
		return null;
	}

}
