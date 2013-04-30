package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum RadioListTextileAttributes {
	LABEL("label:"), DEFAULT("default:"), REQUIRED("required:"), OPTIONS("options:");

	private String value;

	private RadioListTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		RadioListTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);

		for (RadioListTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}

		return attrValues;
	}
}
