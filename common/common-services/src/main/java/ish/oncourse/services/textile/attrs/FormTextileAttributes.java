package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum FormTextileAttributes {
	NAME("name:"),
	EMAIL("email:"),
	URL("url:");
	
	private String value;

	private FormTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		FormTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (FormTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}
		
		return attrValues;
	}
}
