package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum CourseTextileAttributes {
	COURSE_PARAM_CODE("code:"), 
	COURSE_PARAM_SHOW_CLASSES("showclasses:"), 
	COURSE_PARAM_TAG("tag:");

	private String value;

	private CourseTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		CourseTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (CourseTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
