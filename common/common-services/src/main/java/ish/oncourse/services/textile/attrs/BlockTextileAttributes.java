package ish.oncourse.services.textile.attrs;

import java.util.ArrayList;
import java.util.List;

public enum BlockTextileAttributes {
	BLOCK_PARAM_NAME("name:");
	
	private String value;

	private BlockTextileAttributes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static List<String> getAttrValues() {
		BlockTextileAttributes[] values = values();
		List<String> attrValues = new ArrayList<>(values.length);
		
		for (BlockTextileAttributes attr : values) {
			attrValues.add(attr.getValue());
		}

		return attrValues;
	}
}
