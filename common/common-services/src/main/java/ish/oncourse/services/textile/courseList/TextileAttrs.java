package ish.oncourse.services.textile.courseList;

import java.util.ArrayList;
import java.util.List;

public enum TextileAttrs {
	
	tag("tag:"),
	limit("limit:"),
	sort("sort:"),
	order("order:"),
	style("style:"),
	showTags("showTags:");

	private String value;

	private TextileAttrs(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		TextileAttrs[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);

		for (TextileAttrs attr : values) {
			attrValues.add(attr.getValue());
		}

		return attrValues;
	}
}
