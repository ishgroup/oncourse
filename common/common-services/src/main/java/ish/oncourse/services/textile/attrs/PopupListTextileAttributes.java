package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum PopupListTextileAttributes {
	LABEL("label:"), DEFAULT("default:"), REQUIRED("required:"), OPTIONS("options:");

	private String value;

	private PopupListTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		PopupListTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);

		for (PopupListTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}

		return attrValues;
	}
}
