package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum CourseListTextileAttributes {
	
	COURSE_LIST_PARAM_TAG("tag:"),
	COURSE_LIST_PARAM_LIMIT("limit:"),
	COURSE_LIST_PARAM_SORT("sort:"),
	COURSE_LIST_PARAM_ORDER("order:");
	
	private String value;

	private CourseListTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		CourseListTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);

		for (CourseListTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}

		return attrValues;
	}
}
