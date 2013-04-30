package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum TextTextileAttributes {
	LABEL("label:"),
	LINES("lines:"),
	REQUIRED("required:"),
	MAXLENGTH("maxlength:");
	
	private String value;

	private TextTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		TextTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (TextTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
